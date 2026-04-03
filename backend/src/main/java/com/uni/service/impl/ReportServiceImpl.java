package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.entity.*;
import com.uni.mapper.*;
import com.uni.service.ReportService;
import com.uni.vo.report.MonthlyReportVO;
import com.uni.vo.report.WeeklyReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据报表服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final WeightRecordMapper weightRecordMapper;
    private final MealRecordMapper mealRecordMapper;
    private final ExerciseRecordMapper exerciseRecordMapper;
    private final UserGoalMapper userGoalMapper;
    private final UserMapper userMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] WEEKDAYS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Override
    public WeeklyReportVO getWeeklyReport(Long userId, Integer weekOffset) {
        // 计算周起止日期
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.plusWeeks(weekOffset).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        WeeklyReportVO report = new WeeklyReportVO();
        report.setWeekRange(weekStart.format(DATE_FORMATTER) + " ~ " + weekEnd.format(DATE_FORMATTER));
        report.setStartDate(weekStart.format(DATE_FORMATTER));
        report.setEndDate(weekEnd.format(DATE_FORMATTER));

        // 查询用户目标
        UserGoalEntity goal = userGoalMapper.selectOne(
            new LambdaQueryWrapper<UserGoalEntity>()
                .eq(UserGoalEntity::getUserId, userId)
                .eq(UserGoalEntity::getStatus, 1)
                .orderByDesc(UserGoalEntity::getCreateTime)
                .last("LIMIT 1")
        );

        // 体重变化
        report.setWeightChange(calculateWeightChange(userId, weekStart, weekEnd));

        // 热量统计
        report.setCalorieStats(calculateCalorieStats(userId, weekStart, weekEnd, goal));

        // 运动汇总
        report.setExerciseSummary(calculateExerciseSummary(userId, weekStart, weekEnd));

        // 打卡统计
        report.setCheckinStats(calculateCheckinStats(userId, weekStart, weekEnd));

        // 每日数据
        report.setDailyDataList(buildDailyData(userId, weekStart, weekEnd, goal));

        return report;
    }

    @Override
    public MonthlyReportVO getMonthlyReport(Long userId, Integer monthOffset) {
        // 计算月起止日期
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.plusMonths(monthOffset).withDayOfMonth(1);
        LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());

        MonthlyReportVO report = new MonthlyReportVO();
        report.setMonth(monthStart.format(DateTimeFormatter.ofPattern("yyyy年MM月")));
        report.setStartDate(monthStart.format(DATE_FORMATTER));
        report.setEndDate(monthEnd.format(DATE_FORMATTER));

        // 查询用户和目标
        UserEntity user = userMapper.selectById(userId);
        UserGoalEntity goal = userGoalMapper.selectOne(
            new LambdaQueryWrapper<UserGoalEntity>()
                .eq(UserGoalEntity::getUserId, userId)
                .eq(UserGoalEntity::getStatus, 1)
                .orderByDesc(UserGoalEntity::getCreateTime)
                .last("LIMIT 1")
        );

        // 总体概览
        report.setOverview(buildOverview(userId, monthStart, monthEnd, user, goal));

        // 体重趋势
        report.setWeightTrend(buildWeightTrend(userId, monthStart, monthEnd));

        // 热量分析
        report.setCalorieAnalysis(buildCalorieAnalysis(userId, monthStart, monthEnd, goal));

        // 运动分析
        report.setExerciseAnalysis(buildExerciseAnalysis(userId, monthStart, monthEnd));

        // 达成率分析
        report.setAchievementRate(buildAchievementRate(userId, monthStart, monthEnd, user, goal));

        // 每周汇总
        report.setWeeklySummaries(buildWeeklySummaries(userId, monthStart, monthEnd));

        return report;
    }

    // ========== 周报告计算方法 ==========

    private WeeklyReportVO.WeightChangeVO calculateWeightChange(Long userId, LocalDate start, LocalDate end) {
        List<WeightRecordEntity> records = weightRecordMapper.selectList(
            new LambdaQueryWrapper<WeightRecordEntity>()
                .eq(WeightRecordEntity::getUserId, userId)
                .ge(WeightRecordEntity::getRecordDate, start)
                .le(WeightRecordEntity::getRecordDate, end)
                .eq(WeightRecordEntity::getDeleteFlag, 0)
                .orderByAsc(WeightRecordEntity::getRecordDate)
        );

        WeeklyReportVO.WeightChangeVO vo = new WeeklyReportVO.WeightChangeVO();
        if (records.isEmpty()) {
            vo.setStartWeight(BigDecimal.ZERO);
            vo.setEndWeight(BigDecimal.ZERO);
            vo.setChange(BigDecimal.ZERO);
            vo.setChangePercent(BigDecimal.ZERO);
            vo.setDirection(0);
            return vo;
        }

        BigDecimal startWeight = records.get(0).getWeight();
        BigDecimal endWeight = records.get(records.size() - 1).getWeight();
        BigDecimal change = endWeight.subtract(startWeight);
        BigDecimal changePercent = startWeight.compareTo(BigDecimal.ZERO) > 0
            ? change.multiply(new BigDecimal("100")).divide(startWeight, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        vo.setStartWeight(startWeight);
        vo.setEndWeight(endWeight);
        vo.setChange(change.abs());
        vo.setChangePercent(changePercent.abs());
        vo.setDirection(change.compareTo(BigDecimal.ZERO));

        return vo;
    }

    private WeeklyReportVO.CalorieStatsVO calculateCalorieStats(Long userId, LocalDate start, LocalDate end, UserGoalEntity goal) {
        List<MealRecordEntity> meals = mealRecordMapper.selectList(
            new LambdaQueryWrapper<MealRecordEntity>()
                .eq(MealRecordEntity::getUserId, userId)
                .ge(MealRecordEntity::getRecordDate, start)
                .le(MealRecordEntity::getRecordDate, end)
                .eq(MealRecordEntity::getDeleteFlag, 0)
        );

        Map<LocalDate, Integer> dailyCalories = meals.stream()
            .collect(Collectors.groupingBy(
                MealRecordEntity::getRecordDate,
                Collectors.summingInt(MealRecordEntity::getCalorie)
            ));

        int totalIntake = dailyCalories.values().stream().mapToInt(Integer::intValue).sum();
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        int avgIntake = days > 0 ? totalIntake / days : 0;

        int calorieGoal = goal != null ? goal.getCalorieGoal() : 1400;
        long达标Days = dailyCalories.values().stream()
            .filter(c -> c <= calorieGoal)
            .count();

        WeeklyReportVO.CalorieStatsVO vo = new WeeklyReportVO.CalorieStatsVO();
        vo.setAvgIntake(avgIntake);
        vo.setGoal(calorieGoal);
        vo.set达标Days((int)达标Days);
        vo.setTotalIntake(totalIntake);

        return vo;
    }

    private WeeklyReportVO.ExerciseSummaryVO calculateExerciseSummary(Long userId, LocalDate start, LocalDate end) {
        List<ExerciseRecordEntity> exercises = exerciseRecordMapper.selectList(
            new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getUserId, userId)
                .ge(ExerciseRecordEntity::getRecordDate, start)
                .le(ExerciseRecordEntity::getRecordDate, end)
                .eq(ExerciseRecordEntity::getDeleteFlag, 0)
        );

        int exerciseDays = (int) exercises.stream()
            .map(ExerciseRecordEntity::getRecordDate)
            .distinct()
            .count();

        int totalDuration = exercises.stream().mapToInt(ExerciseRecordEntity::getDuration).sum();
        int totalBurn = exercises.stream().mapToInt(ExerciseRecordEntity::getCalorieBurned).sum();
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        int avgBurn = days > 0 ? totalBurn / days : 0;

        // 最常运动类型
        String topType = exercises.stream()
            .collect(Collectors.groupingBy(ExerciseRecordEntity::getExerciseName, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("-");

        WeeklyReportVO.ExerciseSummaryVO vo = new WeeklyReportVO.ExerciseSummaryVO();
        vo.setExerciseDays(exerciseDays);
        vo.setTotalDuration(totalDuration);
        vo.setTotalBurn(totalBurn);
        vo.setAvgBurn(avgBurn);
        vo.setTopExerciseType(topType);

        return vo;
    }

    private WeeklyReportVO.CheckinStatsVO calculateCheckinStats(Long userId, LocalDate start, LocalDate end) {
        // 餐食打卡天数
        long mealDays = mealRecordMapper.selectList(
            new LambdaQueryWrapper<MealRecordEntity>()
                .eq(MealRecordEntity::getUserId, userId)
                .ge(MealRecordEntity::getRecordDate, start)
                .le(MealRecordEntity::getRecordDate, end)
                .eq(MealRecordEntity::getDeleteFlag, 0)
                .select(MealRecordEntity::getRecordDate)
        ).stream().map(MealRecordEntity::getRecordDate).distinct().count();

        // 运动打卡天数
        long exerciseDays = exerciseRecordMapper.selectList(
            new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getUserId, userId)
                .ge(ExerciseRecordEntity::getRecordDate, start)
                .le(ExerciseRecordEntity::getRecordDate, end)
                .eq(ExerciseRecordEntity::getDeleteFlag, 0)
                .select(ExerciseRecordEntity::getRecordDate)
        ).stream().map(ExerciseRecordEntity::getRecordDate).distinct().count();

        WeeklyReportVO.CheckinStatsVO vo = new WeeklyReportVO.CheckinStatsVO();
        vo.setCheckinDays((int) Math.max(mealDays, exerciseDays));
        vo.setMealCheckins((int) mealDays);
        vo.setExerciseCheckins((int) exerciseDays);
        vo.setStreakDays(calculateStreakDays(userId)); // 简化为当前连续天数

        return vo;
    }

    private List<WeeklyReportVO.DailyDataVO> buildDailyData(Long userId, LocalDate start, LocalDate end, UserGoalEntity goal) {
        List<WeeklyReportVO.DailyDataVO> list = new ArrayList<>();
        int calorieGoal = goal != null ? goal.getCalorieGoal() : 1400;

        // 查询所有相关数据
        Map<LocalDate, BigDecimal> weightMap = weightRecordMapper.selectList(
            new LambdaQueryWrapper<WeightRecordEntity>()
                .eq(WeightRecordEntity::getUserId, userId)
                .ge(WeightRecordEntity::getRecordDate, start)
                .le(WeightRecordEntity::getRecordDate, end)
                .eq(WeightRecordEntity::getDeleteFlag, 0)
        ).stream().collect(Collectors.toMap(WeightRecordEntity::getRecordDate, WeightRecordEntity::getWeight));

        Map<LocalDate, Integer> mealMap = mealRecordMapper.selectList(
            new LambdaQueryWrapper<MealRecordEntity>()
                .eq(MealRecordEntity::getUserId, userId)
                .ge(MealRecordEntity::getRecordDate, start)
                .le(MealRecordEntity::getRecordDate, end)
                .eq(MealRecordEntity::getDeleteFlag, 0)
        ).stream().collect(Collectors.groupingBy(MealRecordEntity::getRecordDate,
            Collectors.summingInt(MealRecordEntity::getCalorie)));

        Map<LocalDate, List<ExerciseRecordEntity>> exerciseMap = exerciseRecordMapper.selectList(
            new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getUserId, userId)
                .ge(ExerciseRecordEntity::getRecordDate, start)
                .le(ExerciseRecordEntity::getRecordDate, end)
                .eq(ExerciseRecordEntity::getDeleteFlag, 0)
        ).stream().collect(Collectors.groupingBy(ExerciseRecordEntity::getRecordDate));

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            WeeklyReportVO.DailyDataVO vo = new WeeklyReportVO.DailyDataVO();
            vo.setDate(date.format(DATE_FORMATTER));
            vo.setWeekday(WEEKDAYS[date.getDayOfWeek().getValue() - 1]);
            vo.setWeight(weightMap.getOrDefault(date, null));
            vo.setCalorieIntake(mealMap.getOrDefault(date, 0));

            List<ExerciseRecordEntity> exercises = exerciseMap.getOrDefault(date, Collections.emptyList());
            vo.setExerciseMinutes(exercises.stream().mapToInt(ExerciseRecordEntity::getDuration).sum());
            vo.setCalorieBurn(exercises.stream().mapToInt(ExerciseRecordEntity::getCalorieBurned).sum());
            vo.setGoalAchieved(vo.getCalorieIntake() > 0 && vo.getCalorieIntake() <= calorieGoal);

            list.add(vo);
        }

        return list;
    }

    // ========== 月报告计算方法 ==========

    private MonthlyReportVO.OverviewVO buildOverview(Long userId, LocalDate start, LocalDate end, UserEntity user, UserGoalEntity goal) {
        // 体重变化
        List<WeightRecordEntity> weights = weightRecordMapper.selectList(
            new LambdaQueryWrapper<WeightRecordEntity>()
                .eq(WeightRecordEntity::getUserId, userId)
                .ge(WeightRecordEntity::getRecordDate, start)
                .le(WeightRecordEntity::getRecordDate, end)
                .eq(WeightRecordEntity::getDeleteFlag, 0)
                .orderByAsc(WeightRecordEntity::getRecordDate)
        );

        BigDecimal weightLoss = BigDecimal.ZERO;
        if (weights.size() >= 2) {
            weightLoss = weights.get(0).getWeight().subtract(weights.get(weights.size() - 1).getWeight());
        }

        // 热量统计
        List<MealRecordEntity> meals = mealRecordMapper.selectList(
            new LambdaQueryWrapper<MealRecordEntity>()
                .eq(MealRecordEntity::getUserId, userId)
                .ge(MealRecordEntity::getRecordDate, start)
                .le(MealRecordEntity::getRecordDate, end)
                .eq(MealRecordEntity::getDeleteFlag, 0)
        );

        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        int totalCalorie = meals.stream().mapToInt(MealRecordEntity::getCalorie).sum();
        int avgDailyCalorie = days > 0 ? totalCalorie / days : 0;

        // 运动统计
        List<ExerciseRecordEntity> exercises = exerciseRecordMapper.selectList(
            new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getUserId, userId)
                .ge(ExerciseRecordEntity::getRecordDate, start)
                .le(ExerciseRecordEntity::getRecordDate, end)
                .eq(ExerciseRecordEntity::getDeleteFlag, 0)
        );

        int totalMinutes = exercises.stream().mapToInt(ExerciseRecordEntity::getDuration).sum();
        int totalBurn = exercises.stream().mapToInt(ExerciseRecordEntity::getCalorieBurned).sum();

        // 打卡天数
        long checkinDays = meals.stream().map(MealRecordEntity::getRecordDate).distinct().count();

        MonthlyReportVO.OverviewVO vo = new MonthlyReportVO.OverviewVO();
        vo.setTotalWeightLoss(weightLoss);
        vo.setAvgDailyCalorie(avgDailyCalorie);
        vo.setTotalExerciseHours(totalMinutes / 60);
        vo.setTotalBurn(totalBurn);
        vo.setCheckinDays((int) checkinDays);
        vo.setGoalAchievementRate(new BigDecimal("75.00")); // 简化计算

        return vo;
    }

    private MonthlyReportVO.WeightTrendVO buildWeightTrend(Long userId, LocalDate start, LocalDate end) {
        List<WeightRecordEntity> records = weightRecordMapper.selectList(
            new LambdaQueryWrapper<WeightRecordEntity>()
                .eq(WeightRecordEntity::getUserId, userId)
                .ge(WeightRecordEntity::getRecordDate, start)
                .le(WeightRecordEntity::getRecordDate, end)
                .eq(WeightRecordEntity::getDeleteFlag, 0)
                .orderByAsc(WeightRecordEntity::getRecordDate)
        );

        MonthlyReportVO.WeightTrendVO vo = new MonthlyReportVO.WeightTrendVO();
        if (records.isEmpty()) {
            vo.setStartWeight(BigDecimal.ZERO);
            vo.setEndWeight(BigDecimal.ZERO);
            vo.setMinWeight(BigDecimal.ZERO);
            vo.setMaxWeight(BigDecimal.ZERO);
            vo.setAvgWeight(BigDecimal.ZERO);
            vo.setTrend("-");
            vo.setTrendData(Collections.emptyList());
            return vo;
        }

        vo.setStartWeight(records.get(0).getWeight());
        vo.setEndWeight(records.get(records.size() - 1).getWeight());
        vo.setMinWeight(records.stream().map(WeightRecordEntity::getWeight).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        vo.setMaxWeight(records.stream().map(WeightRecordEntity::getWeight).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));

        BigDecimal avg = records.stream()
            .map(WeightRecordEntity::getWeight)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(new BigDecimal(records.size()), 2, RoundingMode.HALF_UP);
        vo.setAvgWeight(avg);

        BigDecimal change = vo.getEndWeight().subtract(vo.getStartWeight());
        vo.setTrend(change.compareTo(BigDecimal.ZERO) < 0 ? "下降" : change.compareTo(BigDecimal.ZERO) > 0 ? "上升" : "持平");

        vo.setTrendData(records.stream().map(r -> {
            MonthlyReportVO.TrendPointVO point = new MonthlyReportVO.TrendPointVO();
            point.setDate(r.getRecordDate().format(DATE_FORMATTER));
            point.setWeight(r.getWeight());
            return point;
        }).collect(Collectors.toList()));

        return vo;
    }

    private MonthlyReportVO.CalorieAnalysisVO buildCalorieAnalysis(Long userId, LocalDate start, LocalDate end, UserGoalEntity goal) {
        List<MealRecordEntity> meals = mealRecordMapper.selectList(
            new LambdaQueryWrapper<MealRecordEntity>()
                .eq(MealRecordEntity::getUserId, userId)
                .ge(MealRecordEntity::getRecordDate, start)
                .le(MealRecordEntity::getRecordDate, end)
                .eq(MealRecordEntity::getDeleteFlag, 0)
        );

        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        int total = meals.stream().mapToInt(MealRecordEntity::getCalorie).sum();
        int avg = days > 0 ? total / days : 0;

        int calorieGoal = goal != null ? goal.getCalorieGoal() : 1400;

        Map<LocalDate, Integer> dailyMap = meals.stream()
            .collect(Collectors.groupingBy(MealRecordEntity::getRecordDate,
                Collectors.summingInt(MealRecordEntity::getCalorie)));

        long overDays = dailyMap.values().stream().filter(c -> c > calorieGoal).count();
        long达标Days = dailyMap.values().stream().filter(c -> c <= calorieGoal && c > 0).count();

        // 分餐统计
        Map<Integer, Double> mealTypeAvg = meals.stream()
            .collect(Collectors.groupingBy(MealRecordEntity::getMealType,
                Collectors.averagingInt(MealRecordEntity::getCalorie)));

        MonthlyReportVO.CalorieAnalysisVO vo = new MonthlyReportVO.CalorieAnalysisVO();
        vo.setAvgIntake(avg);
        vo.setGoal(calorieGoal);
        vo.setOverDays((int) overDays);
        vo.set达标Days((int)达标Days);
        vo.setAvgBreakfast(mealTypeAvg.getOrDefault(1, 0.0).intValue());
        vo.setAvgLunch(mealTypeAvg.getOrDefault(2, 0.0).intValue());
        vo.setAvgDinner(mealTypeAvg.getOrDefault(3, 0.0).intValue());

        return vo;
    }

    private MonthlyReportVO.ExerciseAnalysisVO buildExerciseAnalysis(Long userId, LocalDate start, LocalDate end) {
        List<ExerciseRecordEntity> exercises = exerciseRecordMapper.selectList(
            new LambdaQueryWrapper<ExerciseRecordEntity>()
                .eq(ExerciseRecordEntity::getUserId, userId)
                .ge(ExerciseRecordEntity::getRecordDate, start)
                .le(ExerciseRecordEntity::getRecordDate, end)
                .eq(ExerciseRecordEntity::getDeleteFlag, 0)
        );

        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        int exerciseDays = (int) exercises.stream().map(ExerciseRecordEntity::getRecordDate).distinct().count();
        BigDecimal frequency = days > 0
            ? new BigDecimal(exerciseDays * 100).divide(new BigDecimal(days), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        int totalDuration = exercises.stream().mapToInt(ExerciseRecordEntity::getDuration).sum();
        int totalBurn = exercises.stream().mapToInt(ExerciseRecordEntity::getCalorieBurned).sum();

        // 类型分布
        Map<String, List<ExerciseRecordEntity>> typeMap = exercises.stream()
            .collect(Collectors.groupingBy(ExerciseRecordEntity::getExerciseName));

        int totalCount = exercises.size();
        List<MonthlyReportVO.ExerciseTypeVO> typeList = typeMap.entrySet().stream().map(e -> {
            MonthlyReportVO.ExerciseTypeVO vo = new MonthlyReportVO.ExerciseTypeVO();
            vo.setType(e.getKey());
            vo.setCount(e.getValue().size());
            vo.setMinutes(e.getValue().stream().mapToInt(ExerciseRecordEntity::getDuration).sum());
            vo.setBurn(e.getValue().stream().mapToInt(ExerciseRecordEntity::getCalorieBurned).sum());
            vo.setPercentage(totalCount > 0
                ? new BigDecimal(e.getValue().size() * 100).divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
            return vo;
        }).collect(Collectors.toList());

        MonthlyReportVO.ExerciseAnalysisVO vo = new MonthlyReportVO.ExerciseAnalysisVO();
        vo.setExerciseDays(exerciseDays);
        vo.setExerciseFrequency(frequency);
        vo.setTotalDuration(totalDuration);
        vo.setAvgDuration(days > 0 ? totalDuration / days : 0);
        vo.setTotalBurn(totalBurn);
        vo.setAvgBurn(days > 0 ? totalBurn / days : 0);
        vo.setTypeDistribution(typeList);

        return vo;
    }

    private MonthlyReportVO.AchievementRateVO buildAchievementRate(Long userId, LocalDate start, LocalDate end, UserEntity user, UserGoalEntity goal) {
        // 简化计算，实际应根据目标完成情况计算
        MonthlyReportVO.AchievementRateVO vo = new MonthlyReportVO.AchievementRateVO();
        vo.setWeightGoalRate(new BigDecimal("80.00"));
        vo.setCalorieGoalRate(new BigDecimal("75.00"));
        vo.setExerciseGoalRate(new BigDecimal("70.00"));
        vo.setOverallRate(new BigDecimal("75.00"));
        vo.setRating("良好");
        return vo;
    }

    private List<MonthlyReportVO.WeeklySummaryVO> buildWeeklySummaries(Long userId, LocalDate start, LocalDate end) {
        List<MonthlyReportVO.WeeklySummaryVO> list = new ArrayList<>();

        LocalDate weekStart = start.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        while (!weekStart.isAfter(end)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(end)) weekEnd = end;

            MonthlyReportVO.WeeklySummaryVO vo = new MonthlyReportVO.WeeklySummaryVO();
            vo.setWeekRange(weekStart.format(DATE_FORMATTER) + "~" + weekEnd.format(DATE_FORMATTER));

            // 体重变化
            List<WeightRecordEntity> weights = weightRecordMapper.selectList(
                new LambdaQueryWrapper<WeightRecordEntity>()
                    .eq(WeightRecordEntity::getUserId, userId)
                    .ge(WeightRecordEntity::getRecordDate, weekStart)
                    .le(WeightRecordEntity::getRecordDate, weekEnd)
                    .eq(WeightRecordEntity::getDeleteFlag, 0)
                    .orderByAsc(WeightRecordEntity::getRecordDate)
            );

            if (weights.size() >= 2) {
                vo.setWeightChange(weights.get(weights.size() - 1).getWeight().subtract(weights.get(0).getWeight()));
            } else {
                vo.setWeightChange(BigDecimal.ZERO);
            }

            // 平均热量
            List<MealRecordEntity> meals = mealRecordMapper.selectList(
                new LambdaQueryWrapper<MealRecordEntity>()
                    .eq(MealRecordEntity::getUserId, userId)
                    .ge(MealRecordEntity::getRecordDate, weekStart)
                    .le(MealRecordEntity::getRecordDate, weekEnd)
                    .eq(MealRecordEntity::getDeleteFlag, 0)
            );

            int days = (int) java.time.temporal.ChronoUnit.DAYS.between(weekStart, weekEnd) + 1;
            int totalCalorie = meals.stream().mapToInt(MealRecordEntity::getCalorie).sum();
            vo.setAvgCalorie(days > 0 ? totalCalorie / days : 0);

            // 运动时长
            List<ExerciseRecordEntity> exercises = exerciseRecordMapper.selectList(
                new LambdaQueryWrapper<ExerciseRecordEntity>()
                    .eq(ExerciseRecordEntity::getUserId, userId)
                    .ge(ExerciseRecordEntity::getRecordDate, weekStart)
                    .le(ExerciseRecordEntity::getRecordDate, weekEnd)
                    .eq(ExerciseRecordEntity::getDeleteFlag, 0)
            );
            vo.setExerciseMinutes(exercises.stream().mapToInt(ExerciseRecordEntity::getDuration).sum());

            // 打卡天数
            vo.setCheckinDays((int) meals.stream().map(MealRecordEntity::getRecordDate).distinct().count());

            list.add(vo);
            weekStart = weekStart.plusWeeks(1);
        }

        return list;
    }

    private int calculateStreakDays(Long userId) {
        // 简化实现：计算最近连续打卡天数
        LocalDate today = LocalDate.now();
        int streak = 0;

        for (int i = 0; i < 365; i++) {
            LocalDate date = today.minusDays(i);
            boolean hasRecord = mealRecordMapper.selectCount(
                new LambdaQueryWrapper<MealRecordEntity>()
                    .eq(MealRecordEntity::getUserId, userId)
                    .eq(MealRecordEntity::getRecordDate, date)
                    .eq(MealRecordEntity::getDeleteFlag, 0)
            ) > 0;

            if (hasRecord) {
                streak++;
            } else if (i > 0) {
                break;
            }
        }

        return streak;
    }
}
