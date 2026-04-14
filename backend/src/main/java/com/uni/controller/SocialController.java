package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.social.CommentDTO;
import com.uni.dto.social.PublishMomentDTO;
import com.uni.service.SocialService;
import com.uni.vo.social.CommentVO;
import com.uni.vo.social.MomentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social")
@Validated
public class SocialController {

    @Autowired
    private SocialService socialService;

    @PostMapping("/friend/add")
    public Result<Void> addFriend(@RequestAttribute("userId") Long userId,
                                   @RequestParam Long friendId) {
        socialService.addFriend(userId, friendId);
        return Result.success();
    }

    @PostMapping("/friend/remove")
    public Result<Void> removeFriend(@RequestAttribute("userId") Long userId,
                                      @RequestParam Long friendId) {
        socialService.removeFriend(userId, friendId);
        return Result.success();
    }

    @GetMapping("/friend/list")
    public Result<List<Long>> getFriendList(@RequestAttribute("userId") Long userId) {
        return Result.success(socialService.getFriendIds(userId));
    }

    @PostMapping("/moment/publish")
    public Result<Void> publishMoment(@RequestAttribute("userId") Long userId,
                                       @RequestBody @Validated PublishMomentDTO dto) {
        socialService.publishMoment(userId, dto.getContent(), dto.getImages(), dto.getType());
        return Result.success();
    }

    @GetMapping("/moment/list")
    public Result<List<MomentVO>> getFriendMoments(@RequestAttribute("userId") Long userId,
                                                    @RequestParam(defaultValue = "20") int limit) {
        return Result.success(socialService.getFriendMoments(userId, limit));
    }

    @PostMapping("/like")
    public Result<Void> like(@RequestAttribute("userId") Long userId,
                              @RequestParam String targetType,
                              @RequestParam Long targetId) {
        socialService.like(userId, targetType, targetId);
        return Result.success();
    }

    @PostMapping("/unlike")
    public Result<Void> unlike(@RequestAttribute("userId") Long userId,
                                @RequestParam String targetType,
                                @RequestParam Long targetId) {
        socialService.unlike(userId, targetType, targetId);
        return Result.success();
    }

    @PostMapping("/comment")
    public Result<Void> comment(@RequestAttribute("userId") Long userId,
                                 @RequestParam String targetType,
                                 @RequestBody @Validated CommentDTO dto) {
        socialService.comment(userId, targetType, dto.getTargetId(), dto.getContent());
        return Result.success();
    }

    @GetMapping("/comment/list")
    public Result<List<CommentVO>> getComments(@RequestParam String targetType,
                                                @RequestParam Long targetId) {
        return Result.success(socialService.getComments(targetType, targetId));
    }
}
