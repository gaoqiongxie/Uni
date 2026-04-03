package com.uni.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uni.common.Result;
import com.uni.service.SocialService;
import com.uni.vo.social.MomentCommentVO;
import com.uni.vo.social.MomentVO;
import com.uni.vo.social.UserFollowVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交功能控制器
 */
@Tag(name = "社交功能", description = "动态发布、关注、点赞评论")
@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    // ==================== 动态相关 ====================

    @Operation(summary = "发布动态")
    @PostMapping("/moment")
    public Result<Long> publishMoment(@RequestAttribute Long userId,
                                       @RequestParam String content,
                                       @RequestParam(required = false) String images,
                                       @RequestParam(required = false) Integer shareWeight,
                                       @RequestParam(required = false) Integer shareExercise) {
        Long momentId = socialService.publishMoment(userId, content, images, shareWeight, shareExercise);
        return Result.success(momentId);
    }

    @Operation(summary = "删除动态")
    @DeleteMapping("/moment/{momentId}")
    public Result<Void> deleteMoment(@RequestAttribute Long userId,
                                      @PathVariable Long momentId) {
        socialService.deleteMoment(userId, momentId);
        return Result.success();
    }

    @Operation(summary = "获取动态列表(社区)")
    @GetMapping("/moments")
    public Result<Page<MomentVO>> getMomentList(@RequestAttribute Long userId,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "20") Integer size) {
        Page<MomentVO> result = socialService.getMomentList(userId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取我关注的动态")
    @GetMapping("/moments/following")
    public Result<Page<MomentVO>> getFollowingMoments(@RequestAttribute Long userId,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "20") Integer size) {
        Page<MomentVO> result = socialService.getFollowingMoments(userId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取用户动态")
    @GetMapping("/user/{targetUserId}/moments")
    public Result<Page<MomentVO>> getUserMoments(@RequestAttribute Long userId,
                                                  @PathVariable Long targetUserId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        Page<MomentVO> result = socialService.getUserMoments(userId, targetUserId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取动态详情")
    @GetMapping("/moment/{momentId}")
    public Result<MomentVO> getMomentDetail(@RequestAttribute Long userId,
                                             @PathVariable Long momentId) {
        MomentVO result = socialService.getMomentDetail(userId, momentId);
        return Result.success(result);
    }

    // ==================== 点赞相关 ====================

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/moment/{momentId}/like")
    public Result<Boolean> toggleLike(@RequestAttribute Long userId,
                                       @PathVariable Long momentId) {
        Boolean liked = socialService.toggleLike(userId, momentId);
        return Result.success(liked);
    }

    // ==================== 评论相关 ====================

    @Operation(summary = "发表评论")
    @PostMapping("/moment/{momentId}/comment")
    public Result<Long> addComment(@RequestAttribute Long userId,
                                    @PathVariable Long momentId,
                                    @RequestParam String content,
                                    @RequestParam(required = false) Long parentId) {
        Long commentId = socialService.addComment(userId, momentId, content, parentId);
        return Result.success(commentId);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/{commentId}")
    public Result<Void> deleteComment(@RequestAttribute Long userId,
                                       @PathVariable Long commentId) {
        socialService.deleteComment(userId, commentId);
        return Result.success();
    }

    @Operation(summary = "获取评论列表")
    @GetMapping("/moment/{momentId}/comments")
    public Result<List<MomentCommentVO>> getMomentComments(@RequestAttribute Long userId,
                                                            @PathVariable Long momentId) {
        List<MomentCommentVO> result = socialService.getMomentComments(momentId);
        return Result.success(result);
    }

    // ==================== 关注相关 ====================

    @Operation(summary = "关注/取消关注")
    @PostMapping("/user/{targetUserId}/follow")
    public Result<Boolean> toggleFollow(@RequestAttribute Long userId,
                                         @PathVariable Long targetUserId) {
        Boolean following = socialService.toggleFollow(userId, targetUserId);
        return Result.success(following);
    }

    @Operation(summary = "获取关注列表")
    @GetMapping("/user/{targetUserId}/following")
    public Result<List<UserFollowVO>> getFollowingList(@RequestAttribute Long userId,
                                                        @PathVariable Long targetUserId) {
        List<UserFollowVO> result = socialService.getFollowingList(userId, targetUserId);
        return Result.success(result);
    }

    @Operation(summary = "获取粉丝列表")
    @GetMapping("/user/{targetUserId}/followers")
    public Result<List<UserFollowVO>> getFollowerList(@RequestAttribute Long userId,
                                                       @PathVariable Long targetUserId) {
        List<UserFollowVO> result = socialService.getFollowerList(userId, targetUserId);
        return Result.success(result);
    }

    @Operation(summary = "检查是否已关注")
    @GetMapping("/user/{targetUserId}/is-following")
    public Result<Boolean> isFollowing(@RequestAttribute Long userId,
                                        @PathVariable Long targetUserId) {
        Boolean result = socialService.isFollowing(userId, targetUserId);
        return Result.success(result);
    }

    @Operation(summary = "获取用户统计")
    @GetMapping("/user/{targetUserId}/stats")
    public Result<UserFollowVO> getUserStats(@RequestAttribute Long userId,
                                              @PathVariable Long targetUserId) {
        UserFollowVO result = socialService.getUserStats(userId, targetUserId);
        return Result.success(result);
    }
}
