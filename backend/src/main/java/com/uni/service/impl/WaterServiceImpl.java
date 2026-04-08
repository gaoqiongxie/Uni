package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.entity.WaterRecordEntity;
import com.uni.entity.WaterReminderSettingEntity;
import com.uni.mapper.WaterRecordMapper;
import com.uni.mapper.WaterReminderSettingMapper;
import com.uni.service.WaterService;
import com.uni.vo.water.WaterDailyStatsVO;
import com.uni.vo.water.WaterRecordVO;
import com.uni.vo.water.WaterReminderSettingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 饮水服务实现
 */
@Service
@RequiredArgsConstructor
public class WaterServiceImpl implements WaterService {

    private final WaterRecordMapper waterRecordMapper;
    private final WaterReminderSettingMapper reminderSettingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordWater(Long userId, Integer amount) {
        WaterRecordEntity record = new WaterRecordEntity();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setRecordTime(LocalDateTime.now());
        waterRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecord(Long userId, Long recordId) {
        LambdaQueryWrapper<WaterRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterRecordEntity::getId, recordId)
                .eq(WaterRecordEntity::getUserId, userId);
        waterRecordMapper.delete(wrapper);
    }

    @Override
    public WaterDailyStatsVO getTodayStats(Long userId) {
        LocalDate today = LocalDate.now();
        return getDailyStats(userId, today);
    }

    private WaterDailyStatsVO getDailyStats(Long userId, LocalDate date) {
        // 获取设置
        WaterReminderSettingEntity setting = getOrCreateSetting(userId);

        // 获取当日记录
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        LambdaQueryWrapper<WaterRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterRecordEntity::getUserId, userId)
                .between(WaterRecordEntity::getRecordTime, startOfDay, endOfDay)
                .orderByDesc(WaterRecordEntity::getRecordTime);

        List<WaterRecordEntity> records = waterRecordMapper.selectList(wrapper);

        // 计算总量
        int totalAmount = records.stream()
                .mapToInt(WaterRecordEntity::getAmount)
                .sum();

        // 计算完成度
        int completionRate = setting.getDailyGoal() > 0
                ? Math.min(100, (int) ((totalAmount * 100L) / setting.getDailyGoal()))
                : 0;

        // 转换VO
        List<WaterRecordVO> recordVOs = records.stream()
                .map(this::convertToRecordVO)
                .collect(Collectors.toList());

        WaterDailyStatsVO stats = new WaterDailyStatsVO();
        stats.setDate(date);
        stats.setTotalAmount(totalAmount);
        stats.setDailyGoal(setting.getDailyGoal());
        stats.setCompletionRate(completionRate);
        stats.setRecordCount(records.size());
        stats.setRecords(recordVOs);

        return stats;
    }

    @Override
    public List<WaterRecordVO> getRecordsByDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        LambdaQueryWrapper<WaterRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterRecordEntity::getUserId, userId)
                .between(WaterRecordEntity::getRecordTime, startOfDay, endOfDay)
                .orderByDesc(WaterRecordEntity::getRecordTime);

        return waterRecordMapper.selectList(wrapper).stream()
                .map(this::convertToRecordVO)
                .collect(Collectors.toList());
    }

    @Override
    public WaterReminderSettingVO getReminderSetting(Long userId) {
        WaterReminderSettingEntity setting = getOrCreateSetting(userId);
        return convertToSettingVO(setting);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReminderSetting(Long userId, WaterReminderSettingVO settingVO) {
        WaterReminderSettingEntity setting = getOrCreateSetting(userId);

        setting.setDailyGoal(settingVO.getDailyGoal());
        setting.setReminderEnabled(settingVO.getReminderEnabled());
        setting.setReminderInterval(settingVO.getReminderInterval());
        setting.setStartTime(settingVO.getStartTime());
        setting.setEndTime(settingVO.getEndTime());
        setting.setUpdatedAt(LocalDateTime.now());

        reminderSettingMapper.updateById(setting);
    }

    @Override
    public List<WaterDailyStatsVO> getWeeklyStats(Long userId) {
        List<WaterDailyStatsVO> statsList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            statsList.add(getDailyStats(userId, date));
        }

        return statsList;
    }

    private WaterReminderSettingEntity getOrCreateSetting(Long userId) {
        LambdaQueryWrapper<WaterReminderSettingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterReminderSettingEntity::getUserId, userId);

        WaterReminderSettingEntity setting = reminderSettingMapper.selectOne(wrapper);

        if (setting == null) {
            setting = new WaterReminderSettingEntity();
            setting.setUserId(userId);
            setting.setDailyGoal(2000); // 默认2000ml
            setting.setReminderEnabled(true);
            setting.setReminderInterval(60); // 默认60分钟
            setting.setStartTime("08:00");
            setting.setEndTime("22:00");
            setting.setCreatedAt(LocalDateTime.now());
            setting.setUpdatedAt(LocalDateTime.now());
            reminderSettingMapper.insert(setting);
        }

        return setting;
    }

    private WaterRecordVO convertToRecordVO(WaterRecordEntity entity) {
        WaterRecordVO vo = new WaterRecordVO();
        vo.setId(entity.getId());
        vo.setAmount(entity.getAmount());
        vo.setRecordTime(entity.getRecordTime());
        return vo;
    }

    private WaterReminderSettingVO convertToSettingVO(WaterReminderSettingEntity entity) {
        WaterReminderSettingVO vo = new WaterReminderSettingVO();
        vo.setDailyGoal(entity.getDailyGoal());
        vo.setReminderEnabled(entity.getReminderEnabled());
        vo.setReminderInterval(entity.getReminderInterval());
        vo.setStartTime(entity.getStartTime());
        vo.setEndTime(entity.getEndTime());
        return vo;
    }
}
