package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户每日食物摄入记录实体类
 * @author 高琼
 * @date 2026-04-13
 */
@Data
@TableName("t_food_daily_log")
public class FoodDailyLogEntity {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 记录日期 */
    private LocalDate recordDate;

    /** 餐次: breakfast-早餐,lunch-午餐,dinner-晚餐,snack-加餐 */
    private String mealType;

    /** 食物库ID(关联t_food_library) */
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
