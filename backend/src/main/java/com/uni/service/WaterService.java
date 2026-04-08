package com.uni.service;

import com.uni.vo.water.WaterDailyStatsVO;
import com.uni.vo.water.WaterRecordVO;
import com.uni.vo.water.WaterReminderSettingVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮水服务接口
 */
public interface WaterService {

    /**
     * 记录饮水
     */
    void recordWater(Long userId, Integer amount);

    /**
     * 删除饮水记录
     */
    void deleteRecord(Long userId, Long recordId);

    /**
     * 获取今日饮水统计
     */
    WaterDailyStatsVO getTodayStats(Long userId);

    /**
     * 获取指定日期饮水记录
     */
    List<WaterRecordVO> getRecordsByDate(Long userId, LocalDate date);

    /**
     * 获取饮水提醒设置
     */
    WaterReminderSettingVO getReminderSetting(Long userId);

    /**
     * 更新饮水提醒设置
     */
    void updateReminderSetting(Long userId, WaterReminderSettingVO settingVO);

    /**
     * 获取最近7天饮水统计
     */
    List<WaterDailyStatsVO> getWeeklyStats(Long userId);
}
