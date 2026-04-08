package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户画像实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_profile")
public class UserProfileEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    // 基本信息
    private Integer gender;
    private Integer age;
    private BigDecimal height;
    private BigDecimal currentWeight;
    private BigDecimal targetWeight;

    // 身体状况
    private String bodyType;
    private String healthConditions;
    private String allergies;
    private String dietaryRestrictions;

    // 生活习惯
    private String activityLevel;
    private BigDecimal sleepHours;
    private Integer stressLevel;
    private String workSchedule;

    // 饮食偏好
    private String foodPreferences;
    private String foodDislikes;
    private String cuisinePreference;
    private Integer cookingSkill;
    private Integer mealPrepTime;

    // 运动情况
    private String exerciseExperience;
    private String preferredExercise;
    private String exerciseTime;
    private Integer hasGymAccess;

    // 减肥目标
    private String targetTimeline;
    private String primaryGoal;
    private String motivation;
    private String pastAttempts;
    private String biggestChallenge;

    // 状态
    private Integer isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
