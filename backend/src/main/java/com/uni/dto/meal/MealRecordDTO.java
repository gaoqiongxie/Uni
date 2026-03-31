package com.uni.dto.meal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 餐食打卡DTO
 */
@Data
public class MealRecordDTO {

    /** 用户ID（由Controller从Header中注入） */
    private Long userId;

    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    @NotNull(message = "餐食类型不能为空")
    private Integer mealType;

    @NotNull(message = "用餐时间不能为空")
    private LocalTime mealTime;

    @NotBlank(message = "餐食内容不能为空")
    private String mealContent;

    /** 热量估算(kcal) */
    private Integer calorieEstimate = 0;

    /** 心情类型 */
    private Integer moodType = 0;

    /** 附件id列表(逗号分隔) */
    private String attachmentIds;

    /** 记录来源(1.手动录入,2.拍照识别) */
    private Integer recordSource = 1;

    /** 是否健康(0.不健康,1.健康) */
    private Integer isHealthy = 1;

    /** 备注 */
    private String remark;
}
