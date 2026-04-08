package com.uni.vo.ai;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 个性化减肥计划VO
 */
@Data
public class PersonalizedPlanVO {

    private Long id;
    private String planType;
    private String planTitle;
    private Object planContent;
    private Integer dailyCalorieTarget;
    private Integer dailyWaterTarget;
    private BigDecimal dailySleepTarget;
    private Integer weeklyExerciseDays;
    private String aiAnalysis;
    private Integer isActive;
    private LocalDateTime createdAt;
}
