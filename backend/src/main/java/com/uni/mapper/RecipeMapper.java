package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.RecipeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 食谱 Mapper
 */
@Mapper
public interface RecipeMapper extends BaseMapper<RecipeEntity> {

    /**
     * 搜索食谱（按名称或标签模糊匹配）
     */
    @Select("<script>" +
            "SELECT * FROM t_recipe WHERE delete_flag = 0 AND status = 1 " +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') OR tags LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY like_count DESC LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    List<RecipeEntity> searchRecipes(@Param("keyword") String keyword,
                                     @Param("limit") int limit,
                                     @Param("offset") int offset);
}
