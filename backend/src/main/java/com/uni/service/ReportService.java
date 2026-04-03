package com.uni.service;

import com.uni.vo.report.MonthlyReportVO;
import com.uni.vo.report.WeeklyReportVO;

/**
 * 数据报表服务
 */
public interface ReportService {

    /**
     * 获取周减脂报告
     * @param userId 用户ID
     * @param weekOffset 周偏移量(0=本周, -1=上周, 1=下周)
     * @return 周报告
     */
    WeeklyReportVO getWeeklyReport(Long userId, Integer weekOffset);

    /**
     * 获取月减脂报告
     * @param userId 用户ID
     * @param monthOffset 月偏移量(0=本月, -1=上月, 1=下月)
     * @return 月报告
     */
    MonthlyReportVO getMonthlyReport(Long userId, Integer monthOffset);
}
