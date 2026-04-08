package com.uni.vo.water;

import lombok.Data;

import java.util.List;

/**
 * 每日饮水统计VO
 */
@Data
public class WaterDailyStatsVO {

    /**
     * 当日饮水总量(ml)
     */
    private Integer totalAmount;

    /**
     * 每日目标(ml)
     */
    private Integer dailyGoal;

    /**
     * 完成百分比
     */
    private Integer completionRate;

    /**
     * 今日饮水记录列表
     */
    private List<WaterRecordVO> records;

    /**
     * 连续达标天数
     */
    private Integer consecutiveDays;

    /**
     * 是否已达标
     */
    private Boolean goalAchieved;
}
