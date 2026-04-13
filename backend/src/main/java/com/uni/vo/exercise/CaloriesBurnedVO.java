package com.uni.vo.exercise;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 运动消耗热量计算结果VO
 * @author 高琼
 * @date 2026-04-13
 */
@Data
public class CaloriesBurnedVO {

    /** 运动ID */
    private Long exerciseId;

    /** 运动名称 */
    private String exerciseName;

    /** MET值 */
    private BigDecimal metValue;

    /** 体重(kg) */
    private BigDecimal weight;

    /** 运动时长(分钟) */
    private Integer duration;

    /** 消耗热量(千卡) */
    private BigDecimal caloriesBurned;

    /** 消耗热量描述 */
    private String caloriesDescription;
}
