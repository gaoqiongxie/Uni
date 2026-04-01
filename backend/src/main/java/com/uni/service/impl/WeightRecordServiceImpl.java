package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.common.BizCode;
import com.uni.common.BizException;
import com.uni.dto.weight.WeightRecordDTO;
import com.uni.entity.UserEntity;
import com.uni.entity.WeightRecordEntity;
import com.uni.mapper.WeightRecordMapper;
import com.uni.service.UserService;
import com.uni.service.WeightRecordService;
import com.uni.vo.weight.WeightRecordVO;
import com.uni.vo.weight.WeightStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体重记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeightRecordServiceImpl extends ServiceImpl<WeightRecordMapper, WeightRecordEntity> implements WeightRecordService {

    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WeightRecordVO createWeightRecord(Long userId, WeightRecordDTO dto) {
        // 检查当天是否已有记录
        long count = count(new LambdaQueryWrapper<WeightRecordEntity>()
                .eq(WeightRecordEntity::getUserId, userId)
                .eq(WeightRecordEntity::getRecordDate, dto.getRecordDate())
                .eq(WeightRecordEntity::getDeleteFlag, 0));
        if (count > 0) {
            throw new BizException(BizCode.WEIGHT_RECORD_EXISTS, "今日体重记录已存在");
        }

        UserEntity user = userService.getById(userId);

        WeightRecordEntity entity = new WeightRecordEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setUserId(userId);
        entity.setCreateUser(String.valueOf(userId));
        entity.setUpdateUser(String.valueOf(userId));
        entity.setDeleteFlag(0);

        save(entity);

        // 同步更新用户当前体重和BMI
        userService.updateWeight(userId, dto.getWeight());

        WeightRecordVO vo = new WeightRecordVO();
        BeanUtils.copyProperties(entity, vo);

        // 计算BMI
        if (user != null && user.getHeight() != null && user.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightM = user.getHeight().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            vo.setBmi(dto.getWeight().divide(heightM.multiply(heightM), 2, RoundingMode.HALF_UP));
        }

        // 计算体重变化
        WeightRecordEntity lastRecord = getLastRecord(userId, dto.getRecordDate());
        if (lastRecord != null) {
            vo.setWeightChange(dto.getWeight().subtract(lastRecord.getWeight()));
        }

        return vo;
    }

    @Override
    public List<WeightRecordVO> getWeightRecords(Long userId, int page, int size, String startDate, String endDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = startDate != null ? LocalDate.parse(startDate, fmt) : LocalDate.now().minusDays(90);
        LocalDate end = endDate != null ? LocalDate.parse(endDate, fmt) : LocalDate.now();

        List<WeightRecordEntity> entities = baseMapper.selectByUserIdAndDateRange(userId, start, end);
        return entities.stream().map(e -> {
            WeightRecordVO vo = new WeightRecordVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public WeightStatsVO getWeightStats(Long userId) {
        UserEntity user = userService.getById(userId);
        List<WeightRecordEntity> records = baseMapper.selectByUserIdAndDateRange(
                userId,
                LocalDate.now().minusDays(90),
                LocalDate.now()
        );

        WeightStatsVO stats = new WeightStatsVO();
        if (user != null) {
            stats.setCurrentWeight(user.getCurrentWeight());
            stats.setTargetWeight(user.getTargetWeight());
        }

        if (!records.isEmpty()) {
            stats.setStartWeight(records.get(0).getWeight());
            BigDecimal totalLoss = records.get(0).getWeight().subtract(
                    records.get(records.size() - 1).getWeight());
            stats.setTotalLoss(totalLoss);

            if (user != null && user.getTargetWeight() != null && stats.getStartWeight() != null) {
                BigDecimal totalTarget = stats.getStartWeight().subtract(user.getTargetWeight());
                if (totalTarget.compareTo(BigDecimal.ZERO) > 0) {
                    stats.setRemainingLoss(user.getCurrentWeight().subtract(user.getTargetWeight()));
                    stats.setProgress(totalLoss.divide(totalTarget, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP));
                }
            }

            // 趋势数据（取最近30条）
            UserEntity finalUser = user;
            List<WeightStatsVO.WeightTrendVO> trend = records.stream()
                    .skip(Math.max(0, records.size() - 30))
                    .map(r -> {
                        WeightStatsVO.WeightTrendVO tv = new WeightStatsVO.WeightTrendVO();
                        tv.setDate(r.getRecordDate().toString());
                        tv.setWeight(r.getWeight());
                        if (finalUser != null && finalUser.getHeight() != null &&
                                finalUser.getHeight().compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal hm = finalUser.getHeight().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                            tv.setBmi(r.getWeight().divide(hm.multiply(hm), 2, RoundingMode.HALF_UP));
                        }
                        return tv;
                    }).collect(Collectors.toList());
            stats.setTrend(trend);
        }

        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWeightRecord(Long id, Long userId) {
        WeightRecordEntity record = getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BizException(BizCode.WEIGHT_RECORD_NOT_FOUND, "记录不存在");
        }
        return removeById(id);
    }

    private WeightRecordEntity getLastRecord(Long userId, LocalDate currentDate) {
        return getOne(new LambdaQueryWrapper<WeightRecordEntity>()
                .eq(WeightRecordEntity::getUserId, userId)
                .lt(WeightRecordEntity::getRecordDate, currentDate)
                .eq(WeightRecordEntity::getDeleteFlag, 0)
                .orderByDesc(WeightRecordEntity::getRecordDate)
                .last("LIMIT 1"));
    }
}
