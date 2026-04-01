package com.uni.vo.recipe;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 食谱VO
 */
@Data
public class RecipeVO {

    private Long id;

    /** 食谱名称 */
    private String name;

    /** 分类 */
    private String category;

    /** 分类名称 */
    private String categoryName;

    /** 食谱简介 */
    private String description;

    /** 封面图URL */
    private String coverImage;

    /** 总热量(kcal) */
    private Integer calorie;

    /** 蛋白质(g) */
    private BigDecimal protein;

    /** 脂肪(g) */
    private BigDecimal fat;

    /** 碳水化合物(g) */
    private BigDecimal carbohydrate;

    /** 膳食纤维(g) */
    private BigDecimal fiber;

    /** 烹饪时间(分钟) */
    private Integer cookingTime;

    /** 难度(1.简单,2.中等,3.困难) */
    private Integer difficulty;

    /** 难度名称 */
    private String difficultyName;

    /** 做法步骤(JSON数组解析后) */
    private List<String> steps;

    /** 标签列表 */
    private List<String> tagList;

    /** 份数 */
    private Integer servings;

    /** 收藏数 */
    private Integer likeCount;

    /** 当前用户是否已收藏 */
    private Boolean favorited;

    /** 食材列表 */
    private List<IngredientVO> ingredients;

    /**
     * 食材VO（内部类）
     */
    @Data
    public static class IngredientVO {
        private Long id;
        private String name;
        private String amount;
        private Integer calorie;
    }
}
