package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.entity.ExerciseRecordEntity;
import com.uni.entity.UserGoalEntity;
import com.uni.mapper.ExerciseRecordMapper;
import com.uni.mapper.FoodDailyLogMapper;
import com.uni.mapper.UserGoalMapper;
import com.uni.service.CalorieDashboardService;
import com.uni.vo.calorie.CalorieDailyBalanceVO;
import com.uni.vo.calorie.CalorieDashboardVO;
import com.uni.vo.calorie.CalorieTrendPointVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 热量收支看板服务实现类
 * @author 高琼
 * @date 2026-04-14
 */
@Service
@RequiredArgsConstructor
public class CalorieDashboardServiceImpl implements CalorieDashboardService {

    private final FoodDailyLogMapper foodDailyLogMapper;
    private final ExerciseRecordMapper exerciseRecordMapper;
    private final UserGoalMapper userGoalMapper;

    /** 默认目标热量（如未设定目标） */
    private static final BigDecimal DEFAULT_TARGET = BigDecimal.valueOf(1400);

    @Override
    public CalorieDashboardVO getTodayDashboard(Long userId) {
        CalorieDashboardVO vo = new CalorieDashboardVO();
        vo.setToday(getByDate(userId, LocalDate.now()));
        vo.setWeekTrend(getWeeklyTrend(userId));

        // 计算周均值和累计缺口
        List<CalorieTrendPointVO> trend = vo.getWeekTrend();
        if (!trend.isEmpty()) {
            BigDecimal totalIntake = trend.stream()
                .map(CalorieTrendPointVO::getIntake)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalBurned = trend.stream()
                .map(CalorieTrendPointVO::getBurned)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            int count = trend.size();
            vo.setWeekAvgIntake(totalIntake.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP));
            vo.setWeekAvgBurned(totalBurned.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP));

            BigDecimal target = vo.getToday().getTargetCalories();
            BigDecimal cumGap = trend.stream()
                .map(t -> t.getNet().subtract(target))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setWeekCumulativeGap(cumGap.setScale(1, RoundingMode.HALF_UP));
        }

        // 计算连续达标天数
        vo.setStreakDays(calcStreakDays(userId));
        return vo;
    }

    @Override
    public CalorieDailyBalanceVO getByDate(Long userId, LocalDate date) {
        CalorieDailyBalanceVO vo = new CalorieDailyBalanceVO();
        vo.setDate(date.toString());

        // 获取目标热量
        BigDecimal targetCalories = getUserTargetCalories(userId);
        vo.setTargetCalories(targetCalories);

        // 获取当日摄入热量
        BigDecimal intake = foodDailyLogMapper.sumCaloriesByDate(userId, date);
        intake = intake == null ? BigDecimal.ZERO : intake;
        vo.setIntakeCalories(intake.setScale(1, RoundingMode.HALF_UP));

        // 获取当日运动消耗
        BigDecimal burned = getExerciseBurnedByDate(userId, date);
        vo.setBurnedCalories(burned.setScale(1, RoundingMode.HALF_UP));

        // 净摄入 = 摄入 - 运动消耗
        BigDecimal net = intake.subtract(burned);
        vo.setNetCalories(net.setScale(1, RoundingMode.HALF_UP));

        // 热量缺口 = 净摄入 - 目标（负数为赤字，有利于减脂）
        BigDecimal gap = net.subtract(targetCalories);
        vo.setCalorieGap(gap.setScale(1, RoundingMode.HALF_UP));

        // 营养素
        BigDecimal[] nutrients = getNutrientsByDate(userId, date);
        vo.setProteinIntake(nutrients[0].setScale(1, RoundingMode.HALF_UP));
        vo.setFatIntake(nutrients[1].setScale(1, RoundingMode.HALF_UP));
        vo.setCarbsIntake(nutrients[2].setScale(1, RoundingMode.HALF_UP));

        // 完成度
        if (targetCalories.compareTo(BigDecimal.ZERO) > 0) {
            int percent = net.divide(targetCalories, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).intValue();
            vo.setCompletionPercent(Math.min(percent, 100));
        } else {
            vo.setCompletionPercent(0);
        }

        // 状态描述
        vo.setStatusDesc(buildStatusDesc(gap));
        return vo;
    }

    @Override
    public List<CalorieTrendPointVO> getWeeklyTrend(Long userId) {
        return buildTrend(userId, 7);
    }

    @Override
    public List<CalorieTrendPointVO> getMonthlyTrend(Long userId) {
        return buildTrend(userId, 30);
    }

    // ---- 私有方法 ----

    private BigDecimal getUserTargetCalories(Long userId) {
        try {
            LambdaQueryWrapper<UserGoalEntity> qw = new LambdaQueryWrapper<UserGoalEntity>()
                .eq(UserGoalEntity::getUserId, userId)
                .eq(UserGoalEntity::getStatus, 1)
                .orderByDesc(UserGoalEntity::getCreateTime)
                .last("LIMIT 1");
            UserGoalEntity goal = userGoalMapper.selectOne(qw);
            if (goal != null && goal.getDailyCalorieTarget() != null) {
                return goal.getDailyCalorieTarget();
            }
        } catch (Exception ignored) {}
        return DEFAULT_TARGET;
    }

    private BigDecimal getExerciseBurnedByDate(Long userId, LocalDate date) {
        try {
            LambdaQueryWrapper<ExerciseRecordEntity> qw = new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getUserId, userId)
                .eq(ExerciseRecordEntity::getRecordDate, date);
            List<ExerciseRecordEntity> records = exerciseRecordMapper.selectList(qw);
            return records.stream()
                .filter(r -> r.getCaloriesBurned() != null)
                .map(ExerciseRecordEntity::getCaloriesBurned)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal[] getNutrientsByDate(Long userId, LocalDate date) {
        // 从 food_daily_log 聚合当日营养素
        try {
            var logs = foodDailyLogMapper.selectByUserAndDate(userId, date);
            BigDecimal protein = logs.stream()
                .filter(l -> l.getProtein() != null)
                .map(l -> l.getProtein())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal fat = logs.stream()
                .filter(l -> l.getFat() != null)
                .map(l -> l.getFat())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal carbs = logs.stream()
                .filter(l -> l.getCarbs() != null)
                .map(l -> l.getCarbs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new BigDecimal[]{ protein, fat, carbs };
        } catch (Exception e) {
            return new BigDecimal[]{ BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        }
    }

    private List<CalorieTrendPointVO> buildTrend(Long userId, int days) {
        List<CalorieTrendPointVO> result = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d");
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            BigDecimal intake = foodDailyLogMapper.sumCaloriesByDate(userId, date);
            intake = intake == null ? BigDecimal.ZERO : intake.setScale(1, RoundingMode.HALF_UP);
            BigDecimal burned = getExerciseBurnedByDate(userId, date).setScale(1, RoundingMode.HALF_UP);
            BigDecimal net = intake.subtract(burned).setScale(1, RoundingMode.HALF_UP);

            CalorieTrendPointVO point = new CalorieTrendPointVO();
            point.setDate(date.toString());
            point.setLabel(date.format(fmt));
            point.setIntake(intake);
            point.setBurned(burned);
            point.setNet(net);
            result.add(point);
        }
        return result;
    }

    private int calcStreakDays(Long userId) {
        int streak = 0;
        BigDecimal target = getUserTargetCalories(userId);
        for (int i = 0; i < 30; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            BigDecimal intake = foodDailyLogMapper.sumCaloriesByDate(userId, date);
            if (intake == null || intake.compareTo(BigDecimal.ZERO) == 0) break;
            BigDecimal burned = getExerciseBurnedByDate(userId, date);
            BigDecimal net = intake.subtract(burned);
            if (net.compareTo(target) <= 0) streak++;
            else break;
        }
        return streak;
    }

    private String buildStatusDesc(BigDecimal gap) {
        double g = gap.doubleValue();
        if (g < -500) return "热量赤字较大，适量补充营养";
        if (g < -200) return "热量赤字良好，继续保持！";
        if (g < 0)    return "轻微赤字，状态不错";
        if (g < 200)  return "基本达标，再加把劲";
        if (g < 400)  return "稍有超标，注意饮食";
        return "热量超标，明天加强运动";
    }
}
