package com.uni.vo.calorie;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 热量收支看板VO
 * @author 高琼
 * @date 2026-04-14
 */
@Data
public class CalorieDashboardVO {

    /** 今日数据 */
    private CalorieDailyBalanceVO today;

    /** 近7天趋势 */
    private List<CalorieTrendPointVO> weekTrend;

    /** 周平均摄入(千卡) */
    private BigDecimal weekAvgIntake;

    /** 周平均消耗(千卡) */
    private BigDecimal weekAvgBurned;

    /** 本周累计热量缺口(千卡) */
    private BigDecimal weekCumulativeGap;

    /** 连续达标天数 */
    private Integer streakDays;
}
