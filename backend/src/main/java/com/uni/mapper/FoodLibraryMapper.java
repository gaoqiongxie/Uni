package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.FoodLibraryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 食物库Mapper接口
 * @author 高琼
 * @date 2026-04-13
 */
@Mapper
public interface FoodLibraryMapper extends BaseMapper<FoodLibraryEntity> {

    /**
     * 根据分类查询食物列表
     * @param category 分类
     * @return 食物列表
     */
    @Select("SELECT * FROM t_food_library WHERE category = #{category} ORDER BY is_common DESC, food_name")
    List<FoodLibraryEntity> selectByCategory(@Param("category") String category);

    /**
     * 搜索食物
     * @param keyword 关键词
     * @return 食物列表
     */
    @Select("SELECT * FROM t_food_library WHERE food_name LIKE CONCAT('%', #{keyword}, '%') ORDER BY is_common DESC, food_name LIMIT 50")
    List<FoodLibraryEntity> searchByName(@Param("keyword") String keyword);

    /**
     * 获取常用食物列表
     * @return 常用食物列表
     */
    @Select("SELECT * FROM t_food_library WHERE is_common = 1 ORDER BY category, food_name")
    List<FoodLibraryEntity> selectCommonFoods();
}
