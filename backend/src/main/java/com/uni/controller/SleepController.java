package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.SleepService;
import com.uni.vo.sleep.SleepGoalVO;
import com.uni.vo.sleep.SleepRecordVO;
import com.uni.vo.sleep.SleepStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 睡眠控制器
 */
@RestController
@RequestMapping("/api/sleep")
@RequiredArgsConstructor
public class SleepController extends BaseController {

    private final SleepService sleepService;

    /**
     * 记录睡眠
     */
    @PostMapping("/record")
    public Result<Void> recordSleep(@RequestBody SleepRecordVO recordVO) {
        sleepService.recordSleep(getCurrentUserId(), recordVO);
        return Result.success();
    }

    /**
     * 更新睡眠记录
     */
    @PutMapping("/record/{recordId}")
    public Result<Void> updateSleep(@PathVariable Long recordId, @RequestBody SleepRecordVO recordVO) {
        sleepService.updateSleep(getCurrentUserId(), recordId, recordVO);
        return Result.success();
    }

    /**
     * 删除睡眠记录
     */
    @DeleteMapping("/record/{recordId}")
    public Result<Void> deleteSleep(@PathVariable Long recordId) {
        sleepService.deleteSleep(getCurrentUserId(), recordId);
        return Result.success();
    }

    /**
     * 获取指定日期睡眠记录
     */
    @GetMapping("/record")
    public Result<SleepRecordVO> getSleepByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(sleepService.getSleepByDate(getCurrentUserId(), date));
    }

    /**
     * 获取最近睡眠记录
     */
    @GetMapping("/recent")
    public Result<List<SleepRecordVO>> getRecentSleeps(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(sleepService.getRecentSleeps(getCurrentUserId(), days));
    }

    /**
     * 获取睡眠统计
     */
    @GetMapping("/stats")
    public Result<SleepStatsVO> getSleepStats() {
        return Result.success(sleepService.getSleepStats(getCurrentUserId()));
    }

    /**
     * 获取睡眠目标
     */
    @GetMapping("/goal")
    public Result<SleepGoalVO> getSleepGoal() {
        return Result.success(sleepService.getSleepGoal(getCurrentUserId()));
    }

    /**
     * 更新睡眠目标
     */
    @PutMapping("/goal")
    public Result<Void> updateSleepGoal(@RequestBody SleepGoalVO goalVO) {
        sleepService.updateSleepGoal(getCurrentUserId(), goalVO);
        return Result.success();
    }
}
