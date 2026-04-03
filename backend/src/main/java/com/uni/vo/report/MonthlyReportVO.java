package com.uni.vo.report;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 月减脂报告VO
 */
@Data
public class MonthlyReportVO {

    /** 报告月份 */
    private String month;

    /** 月份开始日期 */
    private String startDate;

    /** 月份结束日期 */
    private String endDate;

    /** 总体概览 */
    private OverviewVO overview;

    /** 体重趋势 */
    private WeightTrendVO weightTrend;

    /** 热量分析 */
    private CalorieAnalysisVO calorieAnalysis;

    /** 运动分析 */
    private ExerciseAnalysisVO exerciseAnalysis;

    /** 达成率分析 */
    private AchievementRateVO achievementRate;

    /** 每周汇总 */
    private List<WeeklySummaryVO> weeklySummaries;

    /**
     * 总体概览
     */
    @Data
    public static class OverviewVO {
        /** 总减重(kg) */
        private BigDecimal totalWeightLoss;
        /** 日均热量摄入 */
        private Integer avgDailyCalorie;
        /** 总运动时长(小时) */
        private Integer totalExerciseHours;
        /** 总运动消耗(千卡) */
        private Integer totalBurn;
        /** 打卡天数 */
        private Integer checkinDays;
        /** 目标达成率 */
        private BigDecimal goalAchievementRate;
    }

    /**
     * 体重趋势
     */
    @Data
    public static class WeightTrendVO {
        /** 月初体重 */
        private BigDecimal startWeight;
        /** 月末体重 */
        private BigDecimal endWeight;
        /** 最低体重 */
        private BigDecimal minWeight;
        /** 最高体重 */
        private BigDecimal maxWeight;
        /** 平均体重 */
        private BigDecimal avgWeight;
        /** 变化趋势：下降/持平/上升 */
        private String trend;
        /** 趋势数据(用于图表) */
        private List<TrendPointVO> trendData;
    }

    /**
     * 趋势点
     */
    @Data
    public static class TrendPointVO {
        private String date;
        private BigDecimal weight;
    }

    /**
     * 热量分析
     */
    @Data
    public static class CalorieAnalysisVO {
        /** 日均摄入 */
        private Integer avgIntake;
        /** 热量目标 */
        private Integer goal;
        /** 超标天数 */
        private Integer overDays;
        /** 达标天数 */
        private Integer达标Days;
        /** 平均早餐热量 */
        private Integer avgBreakfast;
        /** 平均午餐热量 */
        private Integer avgLunch;
        /** 平均晚餐热量 */
        private Integer avgDinner;
    }

    /**
     * 运动分析
     */
    @Data
    public static class ExerciseAnalysisVO {
        /** 运动天数 */
        private Integer exerciseDays;
        /** 运动频率(%) */
        private BigDecimal exerciseFrequency;
        /** 总时长(分钟) */
        private Integer totalDuration;
        /** 日均时长 */
        private Integer avgDuration;
        /** 总消耗 */
        private Integer totalBurn;
        /** 日均消耗 */
        private Integer avgBurn;
        /** 运动类型分布 */
        private List<ExerciseTypeVO> typeDistribution;
    }

    /**
     * 运动类型分布
     */
    @Data
    public static class ExerciseTypeVO {
        private String type;
        private Integer count;
        private Integer minutes;
        private Integer burn;
        private BigDecimal percentage;
    }

    /**
     * 达成率分析
     */
    @Data
    public static class AchievementRateVO {
        /** 体重目标达成率 */
        private BigDecimal weightGoalRate;
        /** 热量目标达成率 */
        private BigDecimal calorieGoalRate;
        /** 运动目标达成率 */
        private BigDecimal exerciseGoalRate;
        /** 综合达成率 */
        private BigDecimal overallRate;
        /** 评级：优秀/良好/一般/需努力 */
        private String rating;
    }

    /**
     * 每周汇总
     */
    @Data
    public static class WeeklySummaryVO {
        private String weekRange;
        private BigDecimal weightChange;
        private Integer avgCalorie;
        private Integer exerciseMinutes;
        private Integer checkinDays;
    }
}
