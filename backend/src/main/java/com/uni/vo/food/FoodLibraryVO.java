package com.uni.vo.food;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 食物库VO
 * @author 高琼
 * @date 2026-04-13
 */
@Data
public class FoodLibraryVO {

    /** 食物ID */
    private Long id;

    /** 食物名称 */
    private String foodName;

    /** 食物分类 */
    private String category;

    /** 分类名称 */
    private String categoryName;

    /** 每100克热量(千卡) */
    private BigDecimal caloriesPer100g;

    /** 每100克蛋白质(g) */
    private BigDecimal proteinPer100g;

    /** 每100克脂肪(g) */
    private BigDecimal fatPer100g;

    /** 每100克碳水化合物(g) */
    private BigDecimal carbsPer100g;

    /** 计量单位 */
    private String unit;

    /** 单位重量(g) */
    private BigDecimal unitWeight;

    /** 常见份量描述 */
    private String commonPortion;

    /** 是否常用 */
    private Integer isCommon;

    /** 食物图标URL */
    private String iconUrl;
}
