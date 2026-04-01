package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.exercise.ExerciseRecordDTO;
import com.uni.service.ExerciseRecordService;
import com.uni.vo.exercise.ExerciseRecordVO;
import com.uni.vo.exercise.ExerciseSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 运动记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/exercise-record")
@RequiredArgsConstructor
@Tag(name = "运动记录", description = "运动记录管理")
public class ExerciseRecordController {

    private final ExerciseRecordService exerciseRecordService;

    @Operation(summary = "创建运动记录")
    @PostMapping
    public Result<ExerciseRecordVO> create(@RequestHeader("X-User-Id") Long userId,
                                            @RequestBody @Valid ExerciseRecordDTO dto) {
        dto.setUserId(userId);
        log.info("运动记录: userId={}, date={}, type={}, duration={}min",
                userId, dto.getRecordDate(), dto.getExerciseType(), dto.getDuration());
        return Result.success(exerciseRecordService.createExerciseRecord(dto));
    }

    @Operation(summary = "获取某天的运动记录")
    @GetMapping("/date/{date}")
    public Result<List<ExerciseRecordVO>> getByDate(@RequestHeader("X-User-Id") Long userId,
                                                     @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(exerciseRecordService.getUserExerciseRecordsByDate(userId, date));
    }

    @Operation(summary = "获取日期范围的运动记录")
    @GetMapping("/range")
    public Result<Map<String, List<ExerciseRecordVO>>> getByRange(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(exerciseRecordService.getUserExerciseRecordsByRange(userId, startDate, endDate));
    }

    @Operation(summary = "获取运动汇总统计")
    @GetMapping("/summary")
    public Result<ExerciseSummaryVO> getSummary(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(exerciseRecordService.getExerciseSummary(userId, startDate, endDate));
    }

    @Operation(summary = "删除运动记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable Long id) {
        exerciseRecordService.deleteExerciseRecord(id, userId);
        return Result.success();
    }
}
