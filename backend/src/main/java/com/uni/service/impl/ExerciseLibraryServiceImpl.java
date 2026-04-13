package com.uni.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.entity.ExerciseLibraryEntity;
import com.uni.mapper.ExerciseLibraryMapper;
import com.uni.service.ExerciseLibraryService;
import com.uni.vo.exercise.ExerciseLibraryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运动库服务实现类
 * @author 高琼
 * @date 2026-04-13
 */
@Service
public class ExerciseLibraryServiceImpl extends ServiceImpl<ExerciseLibraryMapper, ExerciseLibraryEntity> implements ExerciseLibraryService {

    @Override
    public List<ExerciseLibraryVO> getExercisesByCategory(String category) {
        List<ExerciseLibraryEntity> list = baseMapper.selectByCategory(category);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<ExerciseLibraryVO> searchExercises(String keyword) {
        List<ExerciseLibraryEntity> list = baseMapper.searchByName(keyword);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<ExerciseLibraryVO> getCommonExercises() {
        List<ExerciseLibraryEntity> list = baseMapper.selectCommonExercises();
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateCaloriesBurned(BigDecimal metValue, BigDecimal weight, Integer duration) {
        // 热量消耗 = MET × 体重(kg) × 时长(小时)
        // 时长从分钟转换为小时
        BigDecimal hours = BigDecimal.valueOf(duration).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
        return metValue.multiply(weight).multiply(hours).setScale(2, RoundingMode.HALF_UP);
    }

    private ExerciseLibraryVO convertToVO(ExerciseLibraryEntity entity) {
        ExerciseLibraryVO vo = new ExerciseLibraryVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
