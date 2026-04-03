package com.uni.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uni.common.Result;
import com.uni.entity.UserMessageEntity;
import com.uni.entity.UserPushSettingEntity;
import com.uni.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息通知控制器
 */
@Tag(name = "消息通知", description = "消息通知相关接口")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "获取消息列表", description = "分页获取用户消息列表")
    @GetMapping("/list")
    public Result<Page<UserMessageEntity>> getMessageList(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "消息类型") @RequestParam(required = false) String type,
            @Parameter(description = "是否已读") @RequestParam(required = false) Integer isRead,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size) {
        Page<UserMessageEntity> result = notificationService.getMessageList(userId, type, isRead, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    @Operation(summary = "标记消息已读")
    @PostMapping("/read/{messageId}")
    public Result<Void> markAsRead(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        notificationService.markAsRead(messageId);
        return Result.success();
    }

    @Operation(summary = "标记所有消息已读")
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        notificationService.deleteMessage(messageId);
        return Result.success();
    }

    @Operation(summary = "获取推送设置")
    @GetMapping("/push-setting")
    public Result<UserPushSettingEntity> getPushSetting(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        UserPushSettingEntity setting = notificationService.getPushSetting(userId);
        return Result.success(setting);
    }

    @Operation(summary = "更新推送设置")
    @PutMapping("/push-setting")
    public Result<Void> updatePushSetting(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @RequestBody UserPushSettingEntity setting) {
        notificationService.updatePushSetting(userId, setting);
        return Result.success();
    }

    @Operation(summary = "发送测试消息", description = "用于测试消息推送功能")
    @PostMapping("/send-test")
    public Result<Void> sendTestMessage(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "消息标题") @RequestParam String title,
            @Parameter(description = "消息内容") @RequestParam String content) {
        notificationService.sendMessage(userId, "SYSTEM", title, content, null);
        return Result.success();
    }
}
