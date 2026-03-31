package com.uni.dto.weight;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 体重记录DTO
 */
@Data
public class WeightRecordDTO {

    @NotNull(message = "体重不能为空")
    @DecimalMin(value = "20.0", message = "体重最小20kg")
    @DecimalMax(value = "300.0", message = "体重最大300kg")
    private BigDecimal weight;

    /** 体脂率(%) */
    @DecimalMin(value = "5.0", message = "体脂率最小5%")
    @DecimalMax(value = "60.0", message = "体脂率最大60%")
    private BigDecimal bodyFatRate;

    /** 肌肉率(%) */
    private BigDecimal muscleRate;

    /** 水分率(%) */
    private BigDecimal waterRate;

    /** 腰围(cm) */
    private BigDecimal waistCircumference;

    /** 臀围(cm) */
    private BigDecimal hipCircumference;

    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    /** 记录类型(1.手动录入,2.智能设备同步) */
    private Integer recordType = 1;

    /** 备注 */
    private String remark;
}
