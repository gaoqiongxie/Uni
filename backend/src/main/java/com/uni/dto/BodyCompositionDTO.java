package com.uni.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 身体成分录入请求DTO
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */
@Data
public class BodyCompositionDTO {

    /** 记录日期 */
    private String recordDate;

    /** 身高(cm) */
    private BigDecimal height;

    /** 体重(kg) */
    private BigDecimal weight;

    /** 腰围(cm) */
    private BigDecimal waist;

    /** 颈围(cm) */
    private BigDecimal neck;

    /** 臀围(cm,女性用) */
    private BigDecimal hip;

    /** 性别: male/female */
    private String gender;
}
