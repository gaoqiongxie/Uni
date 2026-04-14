package com.uni.controller;

import com.uni.common.Result;
import com.uni.dto.challenge.CheckinDTO;
import com.uni.dto.challenge.JoinChallengeDTO;
import com.uni.service.ChallengeService;
import com.uni.vo.challenge.ChallengeCheckinVO;
import com.uni.vo.challenge.ChallengeVO;
import com.uni.vo.challenge.UserChallengeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge")
@Validated
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @GetMapping("/list")
    public Result<List<ChallengeVO>> listChallenges() {
        return Result.success(challengeService.listChallenges());
    }

    @PostMapping("/join")
    public Result<Void> joinChallenge(@RequestAttribute("userId") Long userId,
                                       @RequestBody @Validated JoinChallengeDTO dto) {
        challengeService.joinChallenge(userId, dto.getChallengeId());
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<UserChallengeVO>> getMyChallenges(@RequestAttribute("userId") Long userId) {
        return Result.success(challengeService.getUserChallenges(userId));
    }

    @PostMapping("/checkin")
    public Result<Void> checkin(@RequestAttribute("userId") Long userId,
                                 @RequestBody @Validated CheckinDTO dto) {
        challengeService.checkin(userId, dto);
        return Result.success();
    }

    @GetMapping("/checkin/list")
    public Result<List<ChallengeCheckinVO>> getCheckinList(@RequestAttribute("userId") Long userId,
                                                            @RequestParam Long challengeId) {
        return Result.success(challengeService.getCheckinList(userId, challengeId));
    }
}
