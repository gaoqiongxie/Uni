package com.uni.vo.calorie;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 每日热量收支VO
 * @author 高琼
 * @date 2026-04-14
 */
@Data
public class CalorieDailyBalanceVO {

    /** 日期 */
    private String date;

    /** 目标热量(千卡) */
    private BigDecimal targetCalories;

    /** 摄入热量(千卡) */
    private BigDecimal intakeCalories;

    /** 运动消耗热量(千卡) */
    private BigDecimal burnedCalories;

    /** 净摄入 = 摄入 - 运动消耗 */
    private BigDecimal netCalories;

    /** 热量缺口 = 净摄入 - 目标 */
    private BigDecimal calorieGap;

    /** 基础代谢(千卡) */
    private BigDecimal bmr;

    /** 蛋白质摄入(g) */
    private BigDecimal proteinIntake;

    /** 脂肪摄入(g) */
    private BigDecimal fatIntake;

    /** 碳水摄入(g) */
    private BigDecimal carbsIntake;

    /** 完成度百分比(0-100) */
    private Integer completionPercent;

    /** 状态描述 */
    private String statusDesc;
}
