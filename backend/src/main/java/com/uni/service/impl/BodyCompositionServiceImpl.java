package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.dto.BodyCompositionDTO;
import com.uni.entity.BodyCompositionEntity;
import com.uni.mapper.BodyCompositionMapper;
import com.uni.service.BodyCompositionService;
import com.uni.vo.BodyCompositionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 身体成分 Service 实现
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 * 
 * 海军法体脂率公式：
 * 男: 495 / (1.0324 - 0.19077 × log10(腰围 - 颈围) + 0.15456 × log10(身高)) - 450
 * 女: 495 / (1.29579 - 0.35004 × log10(腰围 + 臀围 - 颈围) + 0.22100 × log10(身高)) - 450
 */
@Service
public class BodyCompositionServiceImpl implements BodyCompositionService {

    private final BodyCompositionMapper bodyCompositionMapper;

    public BodyCompositionServiceImpl(BodyCompositionMapper bodyCompositionMapper) {
        this.bodyCompositionMapper = bodyCompositionMapper;
    }

    @Override
    public BodyCompositionVO record(BodyCompositionDTO dto, Long userId) {
        // 解析日期
        LocalDate recordDate = LocalDate.now();
        if (!StringUtils.isEmpty(dto.getRecordDate())) {
            recordDate = LocalDate.parse(dto.getRecordDate());
        }

        // 计算 BMI
        double height = dto.getHeight().doubleValue();
        double weight = dto.getWeight().doubleValue();
        double bmi = weight / Math.pow(height / 100, 2);

        // 计算体脂率
        double waist = dto.getWaist() != null ? dto.getWaist().doubleValue() : 0;
        double neck = dto.getNeck() != null ? dto.getNeck().doubleValue() : 0;
        double hip = dto.getHip() != null ? dto.getHip().doubleValue() : 0;
        String gender = dto.getGender() != null ? dto.getGender() : "male";

        double bodyFatRate = calculateBodyFatRate(gender, waist, neck, hip, height);
        String fatLevel = getFatLevel(bodyFatRate, gender);

        // 估算其他成分
        double muscleMass = estimateMuscleMass(weight, bodyFatRate);
        double boneMass = estimateBoneMass(weight);
        double waterRate = estimateWaterRate(bodyFatRate);

        // 构建实体
        BodyCompositionEntity entity = new BodyCompositionEntity();
        entity.setUserId(userId);
        entity.setRecordDate(recordDate);
        entity.setHeight(dto.getHeight());
        entity.setWeight(dto.getWeight());
        entity.setBmi(BigDecimal.valueOf(bmi).setScale(1, RoundingMode.HALF_UP));
        entity.setBodyFatRate(BigDecimal.valueOf(bodyFatRate).setScale(1, RoundingMode.HALF_UP));
        entity.setFatLevel(fatLevel);
        entity.setMuscleMass(BigDecimal.valueOf(muscleMass).setScale(1, RoundingMode.HALF_UP));
        entity.setBoneMass(BigDecimal.valueOf(boneMass).setScale(1, RoundingMode.HALF_UP));
        entity.setWaterRate(BigDecimal.valueOf(waterRate).setScale(1, RoundingMode.HALF_UP));
        entity.setWaist(dto.getWaist());
        entity.setNeck(dto.getNeck());
        entity.setHip(dto.getHip());
        entity.setGender(gender);
        entity.setVisceralFat(BigDecimal.valueOf(bodyFatRate / 5).setScale(1, RoundingMode.HALF_UP)); // 简化估算

        // 保存或更新（根据 userId + recordDate）
        LambdaQueryWrapper<BodyCompositionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BodyCompositionEntity::getUserId, userId)
               .eq(BodyCompositionEntity::getRecordDate, recordDate);
        BodyCompositionEntity existEntity = bodyCompositionMapper.selectOne(wrapper);

        if (existEntity != null) {
            entity.setId(existEntity.getId());
            bodyCompositionMapper.updateById(entity);
        } else {
            bodyCompositionMapper.insert(entity);
        }

        return toVO(entity);
    }

    @Override
    public BodyCompositionVO getTodayRecord(Long userId) {
        LambdaQueryWrapper<BodyCompositionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BodyCompositionEntity::getUserId, userId)
               .eq(BodyCompositionEntity::getRecordDate, LocalDate.now())
               .orderByDesc(BodyCompositionEntity::getCreateTime)
               .last("LIMIT 1");
        BodyCompositionEntity entity = bodyCompositionMapper.selectOne(wrapper);
        return entity != null ? toVO(entity) : null;
    }

    @Override
    public BodyCompositionVO getLatestRecord(Long userId) {
        LambdaQueryWrapper<BodyCompositionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BodyCompositionEntity::getUserId, userId)
               .orderByDesc(BodyCompositionEntity::getRecordDate)
               .last("LIMIT 1");
        BodyCompositionEntity entity = bodyCompositionMapper.selectOne(wrapper);
        return entity != null ? toVO(entity) : null;
    }

    @Override
    public List<BodyCompositionVO> getHistoryList(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 30; // 默认返回30条
        }
        LambdaQueryWrapper<BodyCompositionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BodyCompositionEntity::getUserId, userId)
               .orderByDesc(BodyCompositionEntity::getRecordDate)
               .last("LIMIT " + limit);
        List<BodyCompositionEntity> entities = bodyCompositionMapper.selectList(wrapper);
        return entities.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 海军法体脂率公式
     */
    @Override
    public double calculateBodyFatRate(String gender, double waist, double neck, double hip, double height) {
        if (height <= 0) {
            return 0;
        }

        if ("female".equalsIgnoreCase(gender)) {
            // 女性公式: 495 / (1.29579 - 0.35004 × log10(腰围 + 臀围 - 颈围) + 0.22100 × log10(身高)) - 450
            double circumference = waist + hip - neck;
            if (circumference <= 0) {
                return 0;
            }
            double logCircumference = Math.log10(circumference);
            double logHeight = Math.log10(height);
            double bodyFat = 495 / (1.29579 - 0.35004 * logCircumference + 0.22100 * logHeight) - 450;
            return Math.max(0, Math.min(60, bodyFat)); // 限制范围 0-60%
        } else {
            // 男性公式: 495 / (1.0324 - 0.19077 × log10(腰围 - 颈围) + 0.15456 × log10(身高)) - 450
            double circumference = waist - neck;
            if (circumference <= 0) {
                return 0;
            }
            double logCircumference = Math.log10(circumference);
            double logHeight = Math.log10(height);
            double bodyFat = 495 / (1.0324 - 0.19077 * logCircumference + 0.15456 * logHeight) - 450;
            return Math.max(0, Math.min(60, bodyFat)); // 限制范围 0-60%
        }
    }

    /**
     * 根据体脂率判断等级
     */
    @Override
    public String getFatLevel(double bodyFatRate, String gender) {
        if ("female".equalsIgnoreCase(gender)) {
            // 女性体脂率等级
            if (bodyFatRate < 18) {
                return "lean";      // 偏瘦
            } else if (bodyFatRate <= 28) {
                return "normal";    // 正常
            } else if (bodyFatRate <= 33) {
                return "high";     // 偏高
            } else {
                return "obese";    // 肥胖
            }
        } else {
            // 男性体脂率等级
            if (bodyFatRate < 10) {
                return "lean";     // 偏瘦
            } else if (bodyFatRate <= 20) {
                return "normal";   // 正常
            } else if (bodyFatRate <= 25) {
                return "high";    // 偏高
            } else {
                return "obese";    // 肥胖
            }
        }
    }

    /**
     * 估算肌肉量（简化公式：体重 × (1 - 体脂率) × 系数）
     */
    @Override
    public double estimateMuscleMass(double weight, double bodyFatRate) {
        // 肌肉量 ≈ 体重 × (1 - 体脂率/100) × 0.4
        return weight * (1 - bodyFatRate / 100) * 0.4;
    }

    /**
     * 估算骨量（简化公式：体重 × 0.05）
     */
    @Override
    public double estimateBoneMass(double weight) {
        return weight * 0.05;
    }

    /**
     * 估算水分率（简化公式：100 - 体脂率 - 肌肉率）
     */
    @Override
    public double estimateWaterRate(double bodyFatRate) {
        // 简化：水分率 ≈ 55% - 65%，体脂率越高，水分率越低
        return Math.max(45, 65 - bodyFatRate * 0.5);
    }

    /**
     * 实体转 VO
     */
    private BodyCompositionVO toVO(BodyCompositionEntity entity) {
        if (entity == null) {
            return null;
        }
        BodyCompositionVO vo = new BodyCompositionVO();
        BeanUtils.copyProperties(entity, vo);

        // 设置体脂等级中文
        vo.setFatLevelText(getFatLevelText(entity.getFatLevel()));

        return vo;
    }

    private String getFatLevelText(String fatLevel) {
        if (fatLevel == null) return "";
        switch (fatLevel) {
            case "lean": return "偏瘦";
            case "normal": return "正常";
            case "high": return "偏高";
            case "obese": return "肥胖";
            default: return "";
        }
    }
}
