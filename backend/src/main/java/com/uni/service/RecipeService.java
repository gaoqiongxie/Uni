package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.entity.RecipeEntity;
import com.uni.vo.recipe.RecipeVO;

import java.util.List;

/**
 * 食谱服务接口
 */
public interface RecipeService extends IService<RecipeEntity> {

    /**
     * 推荐食谱列表（分页 + 分类筛选 + 热量范围）
     */
    List<RecipeVO> recommendRecipes(Long userId, String category, Integer maxCalorie, int page, int size);

    /**
     * 获取食谱详情（含食材）
     */
    RecipeVO getRecipeDetail(Long userId, Long recipeId);

    /**
     * 搜索食谱
     */
    List<RecipeVO> searchRecipes(Long userId, String keyword, int page, int size);

    /**
     * 收藏/取消收藏
     */
    boolean toggleFavorite(Long userId, Long recipeId);

    /**
     * 获取用户收藏列表
     */
    List<RecipeVO> getFavoriteRecipes(Long userId, int page, int size);
}
