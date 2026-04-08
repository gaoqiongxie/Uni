package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.entity.SleepGoalEntity;
import com.uni.entity.SleepRecordEntity;
import com.uni.mapper.SleepGoalMapper;
import com.uni.mapper.SleepRecordMapper;
import com.uni.service.SleepService;
import com.uni.vo.sleep.SleepGoalVO;
import com.uni.vo.sleep.SleepRecordVO;
import com.uni.vo.sleep.SleepStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 睡眠服务实现
 */
@Service
@RequiredArgsConstructor
public class SleepServiceImpl implements SleepService {

    private final SleepRecordMapper sleepRecordMapper;
    private final SleepGoalMapper sleepGoalMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordSleep(Long userId, SleepRecordVO recordVO) {
        SleepRecordEntity entity = new SleepRecordEntity();
        entity.setUserId(userId);
        entity.setSleepDate(recordVO.getSleepDate());
        entity.setBedTime(recordVO.getBedTime());
        entity.setWakeTime(recordVO.getWakeTime());

        // 计算睡眠时长
        int duration = (int) Duration.between(recordVO.getBedTime(), recordVO.getWakeTime()).toMinutes();
        if (duration < 0) {
            duration += 24 * 60; // 跨天
        }
        entity.setSleepDuration(duration);

        entity.setDeepSleep(recordVO.getDeepSleep());
        entity.setLightSleep(recordVO.getLightSleep());
        entity.setAwakeDuration(recordVO.getAwakeDuration());
        entity.setSleepQuality(recordVO.getSleepQuality());
        entity.setFactors(recordVO.getFactors());
        entity.setNotes(recordVO.getNotes());

        // 计算睡眠评分
        entity.setSleepScore(calculateSleepScore(duration, recordVO.getSleepQuality()));

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        sleepRecordMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSleep(Long userId, Long recordId, SleepRecordVO recordVO) {
        SleepRecordEntity entity = sleepRecordMapper.selectById(recordId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            throw new RuntimeException("记录不存在");
        }

        entity.setBedTime(recordVO.getBedTime());
        entity.setWakeTime(recordVO.getWakeTime());

        int duration = (int) Duration.between(recordVO.getBedTime(), recordVO.getWakeTime()).toMinutes();
        if (duration < 0) {
            duration += 24 * 60;
        }
        entity.setSleepDuration(duration);

        entity.setDeepSleep(recordVO.getDeepSleep());
        entity.setLightSleep(recordVO.getLightSleep());
        entity.setAwakeDuration(recordVO.getAwakeDuration());
        entity.setSleepQuality(recordVO.getSleepQuality());
        entity.setFactors(recordVO.getFactors());
        entity.setNotes(recordVO.getNotes());
        entity.setSleepScore(calculateSleepScore(duration, recordVO.getSleepQuality()));
        entity.setUpdatedAt(LocalDateTime.now());

        sleepRecordMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSleep(Long userId, Long recordId) {
        LambdaQueryWrapper<SleepRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SleepRecordEntity::getId, recordId)
                .eq(SleepRecordEntity::getUserId, userId);
        sleepRecordMapper.delete(wrapper);
    }

    @Override
    public SleepRecordVO getSleepByDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<SleepRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SleepRecordEntity::getUserId, userId)
                .eq(SleepRecordEntity::getSleepDate, date);

        SleepRecordEntity entity = sleepRecordMapper.selectOne(wrapper);
        return entity != null ? convertToVO(entity) : null;
    }

    @Override
    public List<SleepRecordVO> getRecentSleeps(Long userId, Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<SleepRecordEntity> entities = sleepRecordMapper.selectByDateRange(userId, startDate, endDate);
        return entities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public SleepStatsVO getSleepStats(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        List<SleepRecordEntity> records = sleepRecordMapper.selectByDateRange(userId, startDate, endDate);

        SleepStatsVO stats = new SleepStatsVO();

        if (records.isEmpty()) {
            stats.setAvgDuration(0);
            stats.setAvgDurationStr("0小时0分");
            stats.setAvgQuality(0.0);
            stats.setGoalAchievementRate(0);
            stats.setTotalRecords(0);
            stats.setRecentRecords(new ArrayList<>());
            stats.setWeeklyStats(new ArrayList<>());
            return stats;
        }

        // 计算平均值
        int totalDuration = records.stream().mapToInt(SleepRecordEntity::getSleepDuration).sum();
        int avgDuration = totalDuration / records.size();
        double avgQuality = records.stream().mapToInt(SleepRecordEntity::getSleepQuality).average().orElse(0);

        // 计算目标达成率
        SleepGoalEntity goal = getOrCreateGoal(userId);
        long achievedDays = records.stream()
                .filter(r -> r.getSleepDuration() >= goal.getTargetDuration())
                .count();
        int achievementRate = (int) (achievedDays * 100 / records.size());

        stats.setAvgDuration(avgDuration);
        stats.setAvgDurationStr(formatDuration(avgDuration));
        stats.setAvgQuality(avgQuality);
        stats.setGoalAchievementRate(achievementRate);
        stats.setTotalRecords(records.size());
        stats.setRecentRecords(records.stream().map(this::convertToVO).collect(Collectors.toList()));

        // 生成周统计
        List<SleepStatsVO.DailySleepStats> weeklyStats = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            SleepRecordEntity record = records.stream()
                    .filter(r -> r.getSleepDate().equals(date))
                    .findFirst()
                    .orElse(null);

            SleepStatsVO.DailySleepStats dayStats = new SleepStatsVO.DailySleepStats();
            dayStats.setDate(date.toString());
            dayStats.setDuration(record != null ? record.getSleepDuration() : 0);
            dayStats.setQuality(record != null ? record.getSleepQuality() : 0);
            dayStats.setScore(record != null ? record.getSleepScore() : 0);
            weeklyStats.add(dayStats);
        }
        stats.setWeeklyStats(weeklyStats);

        return stats;
    }

    @Override
    public SleepGoalVO getSleepGoal(Long userId) {
        SleepGoalEntity goal = getOrCreateGoal(userId);
        return convertToGoalVO(goal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSleepGoal(Long userId, SleepGoalVO goalVO) {
        SleepGoalEntity goal = getOrCreateGoal(userId);

        goal.setTargetDuration(goalVO.getTargetDuration());
        goal.setTargetBedTime(goalVO.getTargetBedTime());
        goal.setTargetWakeTime(goalVO.getTargetWakeTime());
        goal.setReminderEnabled(goalVO.getReminderEnabled() ? 1 : 0);
        goal.setBedReminder(goalVO.getBedReminder() ? 1 : 0);
        goal.setWakeReminder(goalVO.getWakeReminder() ? 1 : 0);
        goal.setUpdatedAt(LocalDateTime.now());

        sleepGoalMapper.updateById(goal);
    }

    private SleepGoalEntity getOrCreateGoal(Long userId) {
        SleepGoalEntity goal = sleepGoalMapper.selectByUserId(userId);

        if (goal == null) {
            goal = new SleepGoalEntity();
            goal.setUserId(userId);
            goal.setTargetDuration(480); // 默认8小时
            goal.setTargetBedTime("23:00");
            goal.setTargetWakeTime("07:00");
            goal.setReminderEnabled(1);
            goal.setBedReminder(1);
            goal.setWakeReminder(1);
            goal.setCreatedAt(LocalDateTime.now());
            goal.setUpdatedAt(LocalDateTime.now());
            sleepGoalMapper.insert(goal);
        }

        return goal;
    }

    private Integer calculateSleepScore(int duration, Integer quality) {
        // 基础分：根据时长 0-60分
        int durationScore;
        if (duration >= 480) {
            durationScore = 60;
        } else if (duration >= 420) {
            durationScore = 50;
        } else if (duration >= 360) {
            durationScore = 40;
        } else if (duration >= 300) {
            durationScore = 30;
        } else {
            durationScore = 20;
        }

        // 质量分：根据质量 0-40分
        int qualityScore = (quality != null ? quality : 3) * 8;

        return Math.min(100, durationScore + qualityScore);
    }

    private SleepRecordVO convertToVO(SleepRecordEntity entity) {
        SleepRecordVO vo = new SleepRecordVO();
        vo.setId(entity.getId());
        vo.setSleepDate(entity.getSleepDate());
        vo.setBedTime(entity.getBedTime());
        vo.setWakeTime(entity.getWakeTime());
        vo.setSleepDuration(entity.getSleepDuration());
        vo.setDeepSleep(entity.getDeepSleep());
        vo.setLightSleep(entity.getLightSleep());
        vo.setAwakeDuration(entity.getAwakeDuration());
        vo.setSleepQuality(entity.getSleepQuality());
        vo.setSleepQualityText(getQualityText(entity.getSleepQuality()));
        vo.setSleepScore(entity.getSleepScore());
        vo.setFactors(entity.getFactors());
        vo.setNotes(entity.getNotes());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        vo.setBedTimeStr(entity.getBedTime().format(timeFormatter));
        vo.setWakeTimeStr(entity.getWakeTime().format(timeFormatter));
        vo.setDurationStr(formatDuration(entity.getSleepDuration()));

        return vo;
    }

    private SleepGoalVO convertToGoalVO(SleepGoalEntity entity) {
        SleepGoalVO vo = new SleepGoalVO();
        vo.setTargetDuration(entity.getTargetDuration());
        vo.setTargetBedTime(entity.getTargetBedTime());
        vo.setTargetWakeTime(entity.getTargetWakeTime());
        vo.setReminderEnabled(entity.getReminderEnabled() == 1);
        vo.setBedReminder(entity.getBedReminder() == 1);
        vo.setWakeReminder(entity.getWakeReminder() == 1);
        return vo;
    }

    private String getQualityText(Integer quality) {
        if (quality == null) return "一般";
        switch (quality) {
            case 1: return "很差";
            case 2: return "较差";
            case 3: return "一般";
            case 4: return "较好";
            case 5: return "很好";
            default: return "一般";
        }
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0) {
            return hours + "小时" + (mins > 0 ? mins + "分" : "");
        }
        return mins + "分钟";
    }
}
