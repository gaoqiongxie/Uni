package com.uni.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uni.entity.ExerciseLibraryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 运动库Mapper接口
 * @author 高琼
 * @date 2026-04-13
 */
@Mapper
public interface ExerciseLibraryMapper extends BaseMapper<ExerciseLibraryEntity> {

    /**
     * 根据分类查询运动列表
     * @param category 分类
     * @return 运动列表
     */
    @Select("SELECT * FROM t_exercise_library WHERE category = #{category} ORDER BY is_common DESC, exercise_name")
    List<ExerciseLibraryEntity> selectByCategory(@Param("category") String category);

    /**
     * 搜索运动
     * @param keyword 关键词
     * @return 运动列表
     */
    @Select("SELECT * FROM t_exercise_library WHERE exercise_name LIKE CONCAT('%', #{keyword}, '%') ORDER BY is_common DESC, exercise_name LIMIT 50")
    List<ExerciseLibraryEntity> searchByName(@Param("keyword") String keyword);

    /**
     * 获取常用运动列表
     * @return 常用运动列表
     */
    @Select("SELECT * FROM t_exercise_library WHERE is_common = 1 ORDER BY category, exercise_name")
    List<ExerciseLibraryEntity> selectCommonExercises();
}
