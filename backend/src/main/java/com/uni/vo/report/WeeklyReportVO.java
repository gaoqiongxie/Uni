package com.uni.vo.report;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 周减脂报告VO
 */
@Data
public class WeeklyReportVO {

    /** 报告周期 */
    private String weekRange;

    /** 周开始日期 */
    private String startDate;

    /** 周结束日期 */
    private String endDate;

    /** 体重变化 */
    private WeightChangeVO weightChange;

    /** 热量统计 */
    private CalorieStatsVO calorieStats;

    /** 运动汇总 */
    private ExerciseSummaryVO exerciseSummary;

    /** 打卡统计 */
    private CheckinStatsVO checkinStats;

    /** 每日数据 */
    private List<DailyDataVO> dailyDataList;

    /**
     * 体重变化
     */
    @Data
    public static class WeightChangeVO {
        /** 周初体重 */
        private BigDecimal startWeight;
        /** 周末体重 */
        private BigDecimal endWeight;
        /** 变化量 */
        private BigDecimal change;
        /** 变化百分比 */
        private BigDecimal changePercent;
        /** 变化方向：-1下降 0持平 1上升 */
        private Integer direction;
    }

    /**
     * 热量统计
     */
    @Data
    public static class CalorieStatsVO {
        /** 日均摄入 */
        private Integer avgIntake;
        /** 热量目标 */
        private Integer goal;
        /** 达标天数 */
        private Integer达标Days;
        /** 总摄入 */
        private Integer totalIntake;
    }

    /**
     * 运动汇总
     */
    @Data
    public static class ExerciseSummaryVO {
        /** 运动天数 */
        private Integer exerciseDays;
        /** 总时长(分钟) */
        private Integer totalDuration;
        /** 总消耗(千卡) */
        private Integer totalBurn;
        /** 日均消耗 */
        private Integer avgBurn;
        /** 最常运动类型 */
        private String topExerciseType;
    }

    /**
     * 打卡统计
     */
    @Data
    public static class CheckinStatsVO {
        /** 打卡天数 */
        private Integer checkinDays;
        /** 餐食打卡次数 */
        private Integer mealCheckins;
        /** 运动打卡次数 */
        private Integer exerciseCheckins;
        /** 连续打卡天数 */
        private Integer streakDays;
    }

    /**
     * 每日数据
     */
    @Data
    public static class DailyDataVO {
        /** 日期 */
        private String date;
        /** 星期 */
        private String weekday;
        /** 体重 */
        private BigDecimal weight;
        /** 热量摄入 */
        private Integer calorieIntake;
        /** 运动时长 */
        private Integer exerciseMinutes;
        /** 运动消耗 */
        private Integer calorieBurn;
        /** 是否达标 */
        private Boolean goalAchieved;
    }
}
