package com.uni.dto.goal;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建/更新目标 DTO
 */
@Data
public class UserGoalDTO {

    /** 目标类型(1.减重,2.维持,3.增肌) */
    private Integer goalType;

    /** 目标体重(kg) */
    private BigDecimal targetWeight;

    /** 每日热量目标(kcal) */
    private Integer calorieGoal;

    /** 每周运动天数 */
    private Integer exerciseDaysPerWeek;

    /** 每日运动时长(分钟) */
    private Integer exerciseMinutesPerDay;

    /** 目标截止日期 */
    private LocalDate targetDate;

    /** 动力宣言 */
    private String remark;
}
