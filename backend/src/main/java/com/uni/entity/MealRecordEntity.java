package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 餐食打卡表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_meal_record")
public class MealRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户id */
    private Long userId;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 餐食类型(1.早餐,2.午餐,3.晚餐,4.加餐) */
    private Integer mealType;

    /** 用餐时间 */
    private LocalTime mealTime;

    /** 餐食内容描述 */
    private String mealContent;

    /** 热量估算(kcal) */
    private Integer calorieEstimate;

    /** 心情类型(0.正常,1.开心,2.疲惫,3.压力大,4.其他) */
    private Integer moodType;

    /** 附件id列表(逗号分隔) */
    private String attachmentIds;

    /** 记录来源(1.手动录入,2.拍照识别) */
    private Integer recordSource;

    /** 是否健康(0.不健康,1.健康) */
    private Integer isHealthy;

    /** 备注 */
    private String remark;

    /** 创建人Ad */
    private String createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 修改人Ad */
    private String updateUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime editTime;

    @TableLogic
    private Integer deleteFlag;
}
