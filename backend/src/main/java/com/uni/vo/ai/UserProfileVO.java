package com.uni.vo.ai;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户画像VO
 */
@Data
public class UserProfileVO {

    private Long id;

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
}
