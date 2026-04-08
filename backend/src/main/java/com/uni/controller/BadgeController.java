package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.BadgeService;
import com.uni.vo.badge.BadgeVO;
import com.uni.vo.badge.UserBadgeStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 徽章控制器
 */
@Tag(name = "成就徽章", description = "徽章获取、展示、统计")
@RestController
@RequestMapping("/api/badge")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @Operation(summary = "获取所有徽章列表")
    @GetMapping("/list")
    public Result<List<BadgeVO>> getAllBadges(@RequestAttribute Long userId) {
        List<BadgeVO> badges = badgeService.getAllBadges(userId);
        return Result.success(badges);
    }

    @Operation(summary = "获取用户已获得的徽章")
    @GetMapping("/my")
    public Result<List<BadgeVO>> getMyBadges(@RequestAttribute Long userId) {
        List<BadgeVO> badges = badgeService.getUserBadges(userId);
        return Result.success(badges);
    }

    @Operation(summary = "获取用户徽章统计")
    @GetMapping("/stats")
    public Result<UserBadgeStatsVO> getBadgeStats(@RequestAttribute Long userId) {
        UserBadgeStatsVO stats = badgeService.getUserBadgeStats(userId);
        return Result.success(stats);
    }

    @Operation(summary = "检查并授予徽章")
    @PostMapping("/check")
    public Result<List<BadgeVO>> checkAndGrantBadges(@RequestAttribute Long userId) {
        List<BadgeVO> newBadges = badgeService.checkAndGrantBadges(userId);
        return Result.success(newBadges);
    }

    @Operation(summary = "标记徽章为已读")
    @PostMapping("/read")
    public Result<Void> markBadgesAsRead(@RequestAttribute Long userId) {
        badgeService.markBadgesAsRead(userId);
        return Result.success();
    }

    @Operation(summary = "获取新徽章数量")
    @GetMapping("/new-count")
    public Result<Integer> getNewBadgeCount(@RequestAttribute Long userId) {
        Integer count = badgeService.getNewBadgeCount(userId);
        return Result.success(count);
    }
}
