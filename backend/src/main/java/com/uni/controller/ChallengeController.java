package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.ChallengeService;
import com.uni.vo.challenge.ChallengeVO;
import com.uni.vo.challenge.UserChallengeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @GetMapping("/list")
    public Result<List<ChallengeVO>> list() {
        return Result.success(challengeService.getChallengeList());
    }

    @PostMapping("/join/{challengeId}")
    public Result<Void> join(@PathVariable Long challengeId, @RequestAttribute("userId") Long userId) {
        challengeService.joinChallenge(userId, challengeId);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<UserChallengeVO>> myChallenges(@RequestAttribute("userId") Long userId) {
        return Result.success(challengeService.getMyChallenges(userId));
    }

    @PostMapping("/checkin/{challengeId}")
    public Result<Void> checkin(@PathVariable Long challengeId, @RequestAttribute("userId") Long userId, @RequestParam(required = false) String note) {
        challengeService.checkin(userId, challengeId, note);
        return Result.success();
    }
}
