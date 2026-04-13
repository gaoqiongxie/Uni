package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 身体成分记录实体
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */
@Data
@TableName("t_body_composition")
public class BodyCompositionEntity {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 身高(cm) */
    private BigDecimal height;

    /** 体重(kg) */
    private BigDecimal weight;

    /** BMI指数 */
    private BigDecimal bmi;

    /** 体脂率(%) */
    private BigDecimal bodyFatRate;

    /** 体脂等级: lean(偏瘦)/normal(正常)/high(偏高)/obese(肥胖) */
    private String fatLevel;

    /** 肌肉量(kg) */
    private BigDecimal muscleMass;

    /** 骨量(kg) */
    private BigDecimal boneMass;

    /** 水分率(%) */
    private BigDecimal waterRate;

    /** 内脏脂肪等级 */
    private BigDecimal visceralFat;

    /** 腰围(cm) */
    private BigDecimal waist;

    /** 颈围(cm) */
    private BigDecimal neck;

    /** 臀围(cm,女性用) */
    private BigDecimal hip;

    /** 性别: male/female */
    private String gender;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
