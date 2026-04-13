package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食物库实体类
 * @author 高琼
 * @date 2026-04-13
 */
@Data
@TableName("t_food_library")
public class FoodLibraryEntity {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 食物名称 */
    private String foodName;

    /** 食物分类: staple-主食,meat-肉蛋奶,vegetable-蔬菜,fruit-水果,snack-零食,drink-饮品,other-其他 */
    private String category;

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

    /** 是否常用: 0-否,1-是 */
    private Integer isCommon;

    /** 食物图标URL */
    private String iconUrl;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
