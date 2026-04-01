package com.uni.vo.exercise;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 运动汇总VO（日/周/月维度）
 */
@Data
@Accessors(chain = true)
public class ExerciseSummaryVO {

    /** 总运动次数 */
    private int totalCount;

    /** 总运动时长(分钟) */
    private int totalDuration;

    /** 总消耗热量(kcal) */
    private int totalCalorieBurn;

    /** 总运动距离(km) */
    private BigDecimal totalDistance;

    /** 平均每次运动时长(分钟) */
    private BigDecimal avgDuration;

    /** 平均每次消耗热量(kcal) */
    private BigDecimal avgCalorieBurn;
}
