package com.uni.vo.meal;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 餐食记录VO
 */
@Data
@Accessors(chain = true)
public class MealRecordVO {

    private Long id;
    private Long userId;
    private LocalDate recordDate;
    private Integer mealType;
    private String mealTypeName;
    private LocalTime mealTime;
    private String mealContent;
    private Integer calorieEstimate;
    private Integer moodType;
    private String attachmentIds;
    private Integer isHealthy;
    private String remark;
    private LocalDateTime createTime;
    /** 图片URL列表 */
    private List<String> photos;
}
