package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.RecipeIngredientEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 食材 Mapper
 */
@Mapper
public interface RecipeIngredientMapper extends BaseMapper<RecipeIngredientEntity> {

    /**
     * 根据食谱id查询食材列表（按排序）
     */
    @Select("SELECT * FROM t_recipe_ingredient WHERE recipe_id = #{recipeId} ORDER BY sort_order ASC")
    List<RecipeIngredientEntity> selectByRecipeId(@Param("recipeId") Long recipeId);
}
