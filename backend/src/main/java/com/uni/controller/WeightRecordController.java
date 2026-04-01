package com.uni.controller;

import com.uni.common.PageResult;
import com.uni.common.Result;
import com.uni.dto.weight.WeightRecordDTO;
import com.uni.service.WeightRecordService;
import com.uni.vo.weight.WeightRecordVO;
import com.uni.vo.weight.WeightStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 体重记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/weight-record")
@RequiredArgsConstructor
@Tag(name = "体重记录", description = "体重记录管理")
public class WeightRecordController {

    private final WeightRecordService weightRecordService;

    @Operation(summary = "创建体重记录")
    @PostMapping
    public Result<WeightRecordVO> create(@RequestHeader("X-User-Id") Long userId,
                                          @RequestBody @Valid WeightRecordDTO dto) {
        log.info("创建体重记录: userId={}, date={}", userId, dto.getRecordDate());
        return Result.success(weightRecordService.createWeightRecord(userId, dto));
    }

    @Operation(summary = "获取体重记录列表")
    @GetMapping("/list")
    public Result<List<WeightRecordVO>> list(@RequestHeader("X-User-Id") Long userId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String startDate,
                                              @RequestParam(required = false) String endDate) {
        return Result.success(weightRecordService.getWeightRecords(userId, page, size, startDate, endDate));
    }

    @Operation(summary = "分页查询体重记录")
    @GetMapping("/page")
    public Result<PageResult<WeightRecordVO>> page(@RequestHeader("X-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) String startDate,
                                                    @RequestParam(required = false) String endDate) {
        return Result.success(weightRecordService.getWeightRecordsPage(userId, page, size, startDate, endDate));
    }

    @Operation(summary = "获取体重统计")
    @GetMapping("/stats")
    public Result<WeightStatsVO> stats(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(weightRecordService.getWeightStats(userId));
    }

    @Operation(summary = "删除体重记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable Long id) {
        weightRecordService.deleteWeightRecord(id, userId);
        return Result.success();
    }
}
