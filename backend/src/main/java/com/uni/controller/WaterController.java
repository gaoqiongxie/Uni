package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.WaterService;
import com.uni.vo.water.WaterDailyStatsVO;
import com.uni.vo.water.WaterRecordVO;
import com.uni.vo.water.WaterReminderSettingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮水控制器
 */
@RestController
@RequestMapping("/api/water")
@RequiredArgsConstructor
public class WaterController extends BaseController {

    private final WaterService waterService;

    /**
     * 记录饮水
     */
    @PostMapping("/record")
    public Result<Void> recordWater(@RequestParam Integer amount) {
        waterService.recordWater(getCurrentUserId(), amount);
        return Result.success();
    }

    /**
     * 删除饮水记录
     */
    @DeleteMapping("/record/{recordId}")
    public Result<Void> deleteRecord(@PathVariable Long recordId) {
        waterService.deleteRecord(getCurrentUserId(), recordId);
        return Result.success();
    }

    /**
     * 获取今日饮水统计
     */
    @GetMapping("/today")
    public Result<WaterDailyStatsVO> getTodayStats() {
        return Result.success(waterService.getTodayStats(getCurrentUserId()));
    }

    /**
     * 获取指定日期饮水记录
     */
    @GetMapping("/records")
    public Result<List<WaterRecordVO>> getRecordsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(waterService.getRecordsByDate(getCurrentUserId(), date));
    }

    /**
     * 获取饮水提醒设置
     */
    @GetMapping("/setting")
    public Result<WaterReminderSettingVO> getReminderSetting() {
        return Result.success(waterService.getReminderSetting(getCurrentUserId()));
    }

    /**
     * 更新饮水提醒设置
     */
    @PutMapping("/setting")
    public Result<Void> updateReminderSetting(@RequestBody WaterReminderSettingVO settingVO) {
        waterService.updateReminderSetting(getCurrentUserId(), settingVO);
        return Result.success();
    }

    /**
     * 获取最近7天饮水统计
     */
    @GetMapping("/weekly")
    public Result<List<WaterDailyStatsVO>> getWeeklyStats() {
        return Result.success(waterService.getWeeklyStats(getCurrentUserId()));
    }
}
