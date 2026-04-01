package com.uni.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.common.BizCode;
import com.uni.common.BizException;
import com.uni.dto.exercise.ExerciseRecordDTO;
import com.uni.entity.ExerciseRecordEntity;
import com.uni.mapper.ExerciseRecordMapper;
import com.uni.service.ExerciseRecordService;
import com.uni.service.FileService;
import com.uni.vo.exercise.ExerciseRecordVO;
import com.uni.vo.exercise.ExerciseSummaryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运动记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseRecordServiceImpl extends ServiceImpl<ExerciseRecordMapper, ExerciseRecordEntity> implements ExerciseRecordService {

    private static final String[] INTENSITY_NAMES = {"", "低强度", "中等强度", "高强度"};
    private static final String[] FEELING_NAMES = {"一般", "轻松", "适中", "吃力", "很累"};

    private final FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExerciseRecordVO createExerciseRecord(ExerciseRecordDTO dto) {
        ExerciseRecordEntity entity = new ExerciseRecordEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateUser(String.valueOf(dto.getUserId()));
        entity.setUpdateUser(String.valueOf(dto.getUserId()));
        entity.setDeleteFlag(0);

        save(entity);

        return toVO(entity);
    }

    @Override
    public List<ExerciseRecordVO> getUserExerciseRecordsByDate(Long userId, LocalDate recordDate) {
        List<ExerciseRecordEntity> entities = baseMapper.selectByUserIdAndDate(userId, recordDate);
        return entities.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<ExerciseRecordVO>> getUserExerciseRecordsByRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<ExerciseRecordEntity> entities = baseMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
        return entities.stream()
                .map(this::toVO)
                .collect(Collectors.groupingBy(
                        vo -> vo.getRecordDate().toString(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    @Override
    public ExerciseSummaryVO getExerciseSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        List<ExerciseRecordEntity> entities = baseMapper.selectByUserIdAndDateRange(userId, startDate, endDate);

        ExerciseSummaryVO summary = new ExerciseSummaryVO();
        summary.setTotalCount(entities.size());
        summary.setTotalDuration(entities.stream().mapToInt(e -> e.getDuration() != null ? e.getDuration() : 0).sum());
        summary.setTotalCalorieBurn(entities.stream().mapToInt(e -> e.getCalorieBurn() != null ? e.getCalorieBurn() : 0).sum());
        summary.setTotalDistance(entities.stream()
                .map(e -> e.getDistance() != null ? e.getDistance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        if (!entities.isEmpty()) {
            summary.setAvgDuration(BigDecimal.valueOf(summary.getTotalDuration())
                    .divide(BigDecimal.valueOf(entities.size()), 1, RoundingMode.HALF_UP));
            summary.setAvgCalorieBurn(BigDecimal.valueOf(summary.getTotalCalorieBurn())
                    .divide(BigDecimal.valueOf(entities.size()), 1, RoundingMode.HALF_UP));
        } else {
            summary.setAvgDuration(BigDecimal.ZERO);
            summary.setAvgCalorieBurn(BigDecimal.ZERO);
        }

        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExerciseRecord(Long id, Long userId) {
        ExerciseRecordEntity record = getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BizException(BizCode.NOT_FOUND, "运动记录不存在");
        }
        return removeById(id);
    }

    private String getIntensityName(Integer intensity) {
        if (intensity == null || intensity < 1 || intensity >= INTENSITY_NAMES.length) {
            return "中等强度";
        }
        return INTENSITY_NAMES[intensity];
    }

    private String getFeelingName(Integer feeling) {
        if (feeling == null || feeling < 0 || feeling >= FEELING_NAMES.length) {
            return "一般";
        }
        return FEELING_NAMES[feeling];
    }

    /**
     * Entity 转 VO（含图片URL填充）
     */
    private ExerciseRecordVO toVO(ExerciseRecordEntity entity) {
        ExerciseRecordVO vo = new ExerciseRecordVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setIntensityName(getIntensityName(entity.getIntensity()));
        vo.setFeelingName(getFeelingName(entity.getFeeling()));
        // 填充图片URL列表
        if (entity.getAttachmentIds() != null && !entity.getAttachmentIds().isBlank()) {
            vo.setPhotos(fileService.getFileUrls(entity.getAttachmentIds()));
        }
        return vo;
    }
}
