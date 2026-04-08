package com.uni.service;

import com.uni.vo.sleep.SleepGoalVO;
import com.uni.vo.sleep.SleepRecordVO;
import com.uni.vo.sleep.SleepStatsVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 睡眠服务接口
 */
public interface SleepService {

    /**
     * 记录睡眠
     */
    void recordSleep(Long userId, SleepRecordVO recordVO);

    /**
     * 更新睡眠记录
     */
    void updateSleep(Long userId, Long recordId, SleepRecordVO recordVO);

    /**
     * 删除睡眠记录
     */
    void deleteSleep(Long userId, Long recordId);

    /**
     * 获取指定日期睡眠记录
     */
    SleepRecordVO getSleepByDate(Long userId, LocalDate date);

    /**
     * 获取最近睡眠记录
     */
    List<SleepRecordVO> getRecentSleeps(Long userId, Integer days);

    /**
     * 获取睡眠统计
     */
    SleepStatsVO getSleepStats(Long userId);

    /**
     * 获取睡眠目标
     */
    SleepGoalVO getSleepGoal(Long userId);

    /**
     * 更新睡眠目标
     */
    void updateSleepGoal(Long userId, SleepGoalVO goalVO);
}
