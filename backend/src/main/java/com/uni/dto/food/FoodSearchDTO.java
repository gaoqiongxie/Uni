package com.uni.dto.food;

import lombok.Data;

/**
 * 食物搜索DTO
 * @author 高琼
 * @date 2026-04-13
 */
@Data
public class FoodSearchDTO {

    /** 关键词 */
    private String keyword;

    /** 分类 */
    private String category;

    /** 是否只查常用 */
    private Boolean commonOnly;
}
