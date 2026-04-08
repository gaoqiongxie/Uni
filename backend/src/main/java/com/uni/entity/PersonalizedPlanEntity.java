package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 个性化减肥计划实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_personalized_plan")
public class PersonalizedPlanEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String planType;
    private String planTitle;
    private String planContent;
    private Integer dailyCalorieTarget;
    private Integer dailyWaterTarget;
    private BigDecimal dailySleepTarget;
    private Integer weeklyExerciseDays;
    private String aiAnalysis;
    private Integer isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
