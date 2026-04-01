package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.dto.exercise.ExerciseRecordDTO;
import com.uni.entity.ExerciseRecordEntity;
import com.uni.vo.exercise.ExerciseRecordVO;
import com.uni.vo.exercise.ExerciseSummaryVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 运动记录服务接口
 */
public interface ExerciseRecordService extends IService<ExerciseRecordEntity> {

    /**
     * 创建运动记录
     */
    ExerciseRecordVO createExerciseRecord(ExerciseRecordDTO dto);

    /**
     * 获取用户某天的运动记录
     */
    List<ExerciseRecordVO> getUserExerciseRecordsByDate(Long userId, LocalDate recordDate);

    /**
     * 获取用户指定日期范围的运动记录（按日期分组）
     */
    Map<String, List<ExerciseRecordVO>> getUserExerciseRecordsByRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户指定日期范围的运动汇总
     */
    ExerciseSummaryVO getExerciseSummary(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 删除运动记录
     */
    boolean deleteExerciseRecord(Long id, Long userId);
}
