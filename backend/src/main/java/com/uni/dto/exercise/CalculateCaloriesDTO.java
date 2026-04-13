package com.uni.dto.exercise;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 计算运动消耗DTO
 * @author 高琼
 * @date 2026-04-13
 */
@Data
public class CalculateCaloriesDTO {

    /** 运动ID */
    @NotNull(message = "运动ID不能为空")
    private Long exerciseId;

    /** 体重(kg) */
    @NotNull(message = "体重不能为空")
    private BigDecimal weight;

    /** 运动时长(分钟) */
    @NotNull(message = "运动时长不能为空")
    private Integer duration;
}
