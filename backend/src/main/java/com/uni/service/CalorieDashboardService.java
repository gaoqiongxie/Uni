package com.uni.service;

import com.uni.vo.calorie.CalorieDailyBalanceVO;
import com.uni.vo.calorie.CalorieDashboardVO;
import com.uni.vo.calorie.CalorieTrendPointVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 热量收支看板服务接口
 * @author 高琼
 * @date 2026-04-14
 */
public interface CalorieDashboardService {

    /**
     * 获取今日热量看板
     * @param userId 用户ID
     * @return 看板数据
     */
    CalorieDashboardVO getTodayDashboard(Long userId);

    /**
     * 获取指定日期热量数据
     * @param userId 用户ID
     * @param date 日期
     * @return 当日热量收支
     */
    CalorieDailyBalanceVO getByDate(Long userId, LocalDate date);

    /**
     * 获取近7天热量趋势
     * @param userId 用户ID
     * @return 7天趋势数据
     */
    List<CalorieTrendPointVO> getWeeklyTrend(Long userId);

    /**
     * 获取近30天热量趋势
     * @param userId 用户ID
     * @return 30天趋势数据
     */
    List<CalorieTrendPointVO> getMonthlyTrend(Long userId);
}
