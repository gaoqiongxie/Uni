package com.uni.vo.food;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 食物摄入记录VO
 * @author 高琼
 * @date 2026-04-13
 */
@Data
public class FoodDailyLogVO {

    /** 记录ID */
    private Long id;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 餐次 */
    private String mealType;

    /** 餐次名称 */
    private String mealTypeName;

    /** 食物ID */
    private Long foodId;

    /** 食物名称 */
    private String foodName;

    /** 摄入热量(千卡) */
    private BigDecimal calories;

    /** 摄入量 */
    private BigDecimal amount;

    /** 单位 */
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

    /** 创建时间 */
    private LocalDateTime createTime;
}
