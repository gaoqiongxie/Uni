package com.uni.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 身体成分返回视图
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */
@Data
public class BodyCompositionVO {

    /** 主键ID */
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

    /** 体脂等级描述 */
    private String fatLevel;

    /** 体脂等级中文 */
    private String fatLevelText;

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

    /** 臀围(cm) */
    private BigDecimal hip;

    /** 性别 */
    private String gender;

    /** 创建时间 */
    private LocalDateTime createTime;
}
