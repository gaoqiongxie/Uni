package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.entity.ExerciseLibraryEntity;
import com.uni.vo.exercise.ExerciseLibraryVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 运动库服务接口
 * @author 高琼
 * @date 2026-04-13
 */
public interface ExerciseLibraryService extends IService<ExerciseLibraryEntity> {

    /**
     * 根据分类获取运动列表
     * @param category 分类
     * @return 运动列表
     */
    List<ExerciseLibraryVO> getExercisesByCategory(String category);

    /**
     * 搜索运动
     * @param keyword 关键词
     * @return 运动列表
     */
    List<ExerciseLibraryVO> searchExercises(String keyword);

    /**
     * 获取常用运动列表
     * @return 常用运动列表
     */
    List<ExerciseLibraryVO> getCommonExercises();

    /**
     * 计算运动消耗热量
     * @param metValue MET值
     * @param weight 体重(kg)
     * @param duration 运动时长(分钟)
     * @return 消耗热量(千卡)
     */
    BigDecimal calculateCaloriesBurned(BigDecimal metValue, BigDecimal weight, Integer duration);
}
