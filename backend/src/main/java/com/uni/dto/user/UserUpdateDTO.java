package com.uni.dto.user;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户信息更新DTO
 */
@Data
public class UserUpdateDTO {

    /** 用户ID（由Controller从Header中注入，前端不传） */
    private Long id;

    /** 昵称 */
    private String nickname;

    /** 性别(0.未知,1.男,2.女) */
    private Integer gender;

    /** 出生日期 */
    private LocalDate birthday;

    /** 身高(cm) */
    private BigDecimal height;

    /** 目标体重(kg) */
    private BigDecimal targetWeight;

    /** 活动等级(1.久坐,2.轻度,3.中度,4.重度) */
    private Integer activityLevel;

    /** 每日热量目标(kcal) */
    private Integer calorieGoal;
}
