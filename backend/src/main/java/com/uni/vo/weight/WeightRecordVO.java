package com.uni.vo.weight;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体重记录VO
 */
@Data
@Accessors(chain = true)
public class WeightRecordVO {

    private Long id;
    private Long userId;
    private LocalDate recordDate;
    private BigDecimal weight;
    private BigDecimal bodyFatRate;
    private BigDecimal muscleRate;
    private BigDecimal waterRate;
    private BigDecimal waistCircumference;
    private BigDecimal hipCircumference;
    /** 本次BMI */
    private BigDecimal bmi;
    /** 与上次相比体重变化 */
    private BigDecimal weightChange;
    private Integer recordType;
    private String remark;
    private LocalDateTime createTime;
}
