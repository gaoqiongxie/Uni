package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体重记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_weight_record")
public class WeightRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户id */
    private Long userId;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 体重(kg) */
    private BigDecimal weight;

    /** 体脂率(%) */
    private BigDecimal bodyFatRate;

    /** 肌肉率(%) */
    private BigDecimal muscleRate;

    /** 水分率(%) */
    private BigDecimal waterRate;

    /** 腰围(cm) */
    private BigDecimal waistCircumference;

    /** 臀围(cm) */
    private BigDecimal hipCircumference;

    /** 记录类型(1.手动录入,2.智能设备同步) */
    private Integer recordType;

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
