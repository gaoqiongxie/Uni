package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.CalorieDashboardService;
import com.uni.util.JwtUtil;
import com.uni.vo.calorie.CalorieDailyBalanceVO;
import com.uni.vo.calorie.CalorieDashboardVO;
import com.uni.vo.calorie.CalorieTrendPointVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

/**
 * 热量收支看板Controller
 * @author 高琼
 * @date 2026-04-14
 */
@RestController
@RequestMapping("/api/calorie-dashboard")
@RequiredArgsConstructor
public class CalorieDashboardController {

    private final CalorieDashboardService calorieDashboardService;
    private final JwtUtil jwtUtil;

    @GetMapping("/today")
    public Result<CalorieDashboardVO> getTodayDashboard(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return Result.success(calorieDashboardService.getTodayDashboard(userId));
    }

    @GetMapping("/date")
    public Result<CalorieDailyBalanceVO> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return Result.success(calorieDashboardService.getByDate(userId, date));
    }

    @GetMapping("/weekly-trend")
    public Result<List<CalorieTrendPointVO>> getWeeklyTrend(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return Result.success(calorieDashboardService.getWeeklyTrend(userId));
    }

    @GetMapping("/monthly-trend")
    public Result<List<CalorieTrendPointVO>> getMonthlyTrend(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return Result.success(calorieDashboardService.getMonthlyTrend(userId));
    }
}
