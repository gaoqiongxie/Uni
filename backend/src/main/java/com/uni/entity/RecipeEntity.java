package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 食谱表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_recipe")
public class RecipeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 食谱名称 */
    private String name;

    /** 分类(早餐/午餐/晚餐/加餐/低卡/高蛋白) */
    private String category;

    /** 食谱简介 */
    private String description;

    /** 封面图URL */
    private String coverImage;

    /** 总热量(kcal) */
    private Integer calorie;

    /** 蛋白质(g) */
    private java.math.BigDecimal protein;

    /** 脂肪(g) */
    private java.math.BigDecimal fat;

    /** 碳水化合物(g) */
    private java.math.BigDecimal carbohydrate;

    /** 膳食纤维(g) */
    private java.math.BigDecimal fiber;

    /** 烹饪时间(分钟) */
    private Integer cookingTime;

    /** 难度(1.简单,2.中等,3.困难) */
    private Integer difficulty;

    /** 做法步骤(JSON数组) */
    private String steps;

    /** 标签(逗号分隔) */
    private String tags;

    /** 份数 */
    private Integer servings;

    /** 收藏数 */
    private Integer likeCount;

    /** 状态(0.下架,1.上架) */
    private Integer status;

    /** 创建人Ad */
    private String createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 修改人Ad */
    private String updateUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime editTime;

    @TableLogic
    private Integer deleteFlag;
}
