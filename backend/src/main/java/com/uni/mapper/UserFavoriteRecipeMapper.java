package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.UserFavoriteRecipeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 用户收藏食谱 Mapper
 */
@Mapper
public interface UserFavoriteRecipeMapper extends BaseMapper<UserFavoriteRecipeEntity> {

    /**
     * 查询用户收藏的食谱ID列表
     */
    @Select("SELECT recipe_id FROM t_user_favorite_recipe WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Long> selectRecipeIdsByUserId(@Param("userId") Long userId);
}
