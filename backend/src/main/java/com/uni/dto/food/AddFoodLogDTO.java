package com.uni.dto.food;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 添加食物记录DTO
 * @author 高琼
 * @date 2026-04-13
 */
@Data
public class AddFoodLogDTO {

    /** 记录日期 */
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    /** 餐次: breakfast-早餐,lunch-午餐,dinner-晚餐,snack-加餐 */
    @NotBlank(message = "餐次不能为空")
    private String mealType;

    /** 食物库ID(可选) */
    private Long foodId;

    /** 食物名称 */
    @NotBlank(message = "食物名称不能为空")
    private String foodName;

    /** 摄入热量(千卡) */
    @NotNull(message = "热量不能为空")
    private BigDecimal calories;

    /** 摄入量 */
    @NotNull(message = "摄入量不能为空")
    private BigDecimal amount;

    /** 单位 */
    @NotBlank(message = "单位不能为空")
    private String unit;

    /** 蛋白质(g) */
    private BigDecimal protein;

    /** 脂肪(g) */
    private BigDecimal fat;

    /** 碳水化合物(g) */
    private BigDecimal carbs;

    /** 照片URL */
    private String photoUrl;

    /** 备注 */
    private String remark;
}
