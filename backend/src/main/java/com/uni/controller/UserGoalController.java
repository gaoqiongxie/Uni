package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.goal.UserGoalDTO;
import com.uni.service.UserGoalService;
import com.uni.vo.goal.UserGoalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户目标 Controller
 */
@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService userGoalService;

    /**
     * 创建目标
     */
    @PostMapping
    public Result<UserGoalVO> create(@RequestBody UserGoalDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userGoalService.createGoal(userId, dto));
    }

    /**
     * 获取当前进行中目标（含进度统计）
     */
    @GetMapping("/active")
    public Result<UserGoalVO> getActive(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userGoalService.getActiveGoal(userId));
    }

    /**
     * 目标历史列表
     */
    @GetMapping("/list")
    public Result<List<UserGoalVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userGoalService.listGoals(userId));
    }

    /**
     * 放弃目标
     */
    @PostMapping("/{id}/abandon")
    public Result<Boolean> abandon(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userGoalService.abandonGoal(id, userId));
    }

    /**
     * 标记目标达成
     */
    @PostMapping("/{id}/achieve")
    public Result<Boolean> achieve(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userGoalService.achieveGoal(id, userId));
    }
}
