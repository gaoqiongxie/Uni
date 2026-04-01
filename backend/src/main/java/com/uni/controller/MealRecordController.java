package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.meal.MealRecordDTO;
import com.uni.service.MealRecordService;
import com.uni.vo.meal.MealCalendarVO;
import com.uni.vo.meal.MealRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 餐食打卡控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/meal-record")
@RequiredArgsConstructor
@Tag(name = "餐食打卡", description = "餐食记录管理")
public class MealRecordController {

    private final MealRecordService mealRecordService;

    @Operation(summary = "创建餐食打卡记录")
    @PostMapping
    public Result<MealRecordVO> create(@RequestHeader("X-User-Id") Long userId,
                                        @RequestBody @Valid MealRecordDTO dto) {
        dto.setUserId(userId);
        log.info("餐食打卡: userId={}, date={}, type={}", userId, dto.getRecordDate(), dto.getMealType());
        return Result.success(mealRecordService.createMealRecord(dto));
    }

    @Operation(summary = "获取某天的餐食记录")
    @GetMapping("/date/{date}")
    public Result<List<MealRecordVO>> getByDate(@RequestHeader("X-User-Id") Long userId,
                                                 @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(mealRecordService.getUserMealRecordsByDate(userId, date));
    }

    @Operation(summary = "获取打卡日历")
    @GetMapping("/calendar")
    public Result<MealCalendarVO> calendar(@RequestHeader("X-User-Id") Long userId,
                                            @RequestParam int year,
                                            @RequestParam int month) {
        return Result.success(mealRecordService.getMealCalendar(userId, year, month));
    }

    @Operation(summary = "删除餐食记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable Long id) {
        mealRecordService.deleteMealRecord(id, userId);
        return Result.success();
    }
}
