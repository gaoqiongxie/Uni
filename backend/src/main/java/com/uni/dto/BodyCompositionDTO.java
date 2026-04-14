package com.uni.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 身体成分录入请求DTO
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */
@Data
public class BodyCompositionDTO {

    /** 记录日期 (yyyy-MM-dd，不填则取今日) */
    private String recordDate;

    /** 身高(cm) - 必填 */
    @NotNull(message = "身高不能为空")
    @DecimalMin(value = "100", message = "身高不能低于100cm")
    @DecimalMax(value = "250", message = "身高不能超过250cm")
    private BigDecimal height;

    /** 体重(kg) - 必填 */
    @NotNull(message = "体重不能为空")
    @DecimalMin(value = "20", message = "体重不能低于20kg")
    @DecimalMax(value = "300", message = "体重不能超过300kg")
    private BigDecimal weight;

    /** 腰围(cm) - 用于计算体脂率 */
    @DecimalMin(value = "40", message = "腰围数值异常")
    @DecimalMax(value = "200", message = "腰围数值异常")
    private BigDecimal waist;

    /** 颈围(cm) - 用于计算体脂率 */
    @DecimalMin(value = "20", message = "颈围数值异常")
    @DecimalMax(value = "80", message = "颈围数值异常")
    private BigDecimal neck;

    /** 臀围(cm,女性用) - 女性用于计算体脂率 */
    @DecimalMin(value = "50", message = "臀围数值异常")
    @DecimalMax(value = "200", message = "臀围数值异常")
    private BigDecimal hip;

    /** 性别: male/female，不填默认 male */
    private String gender;
}
