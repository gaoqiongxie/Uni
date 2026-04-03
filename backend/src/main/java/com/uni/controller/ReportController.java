package com.uni.controller;

import com.uni.annotation.LoginRequired;
import com.uni.common.Result;
import com.uni.service.ReportService;
import com.uni.vo.report.MonthlyReportVO;
import com.uni.vo.report.WeeklyReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 数据报表控制器
 */
@Tag(name = "数据报表", description = "周/月减脂报告")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 获取周减脂报告
     */
    @Operation(summary = "获取周减脂报告")
    @LoginRequired
    @GetMapping("/weekly")
    public Result<WeeklyReportVO> getWeeklyReport(
            @Parameter(description = "周偏移量(0=本周,-1=上周,1=下周)")
            @RequestParam(defaultValue = "0") Integer weekOffset) {
        Long userId = getCurrentUserId();
        WeeklyReportVO report = reportService.getWeeklyReport(userId, weekOffset);
        return Result.success(report);
    }

    /**
     * 获取月减脂报告
     */
    @Operation(summary = "获取月减脂报告")
    @LoginRequired
    @GetMapping("/monthly")
    public Result<MonthlyReportVO> getMonthlyReport(
            @Parameter(description = "月偏移量(0=本月,-1=上月,1=下月)")
            @RequestParam(defaultValue = "0") Integer monthOffset) {
        Long userId = getCurrentUserId();
        MonthlyReportVO report = reportService.getMonthlyReport(userId, monthOffset);
        return Result.success(report);
    }

    private Long getCurrentUserId() {
        // 从请求头或上下文中获取
        return 1L; // 简化实现
    }
}
