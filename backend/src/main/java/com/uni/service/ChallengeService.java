package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.entity.ChallengeEntity;
import com.uni.vo.challenge.ChallengeVO;
import com.uni.vo.challenge.UserChallengeVO;

import java.util.List;

public interface ChallengeService extends IService<ChallengeEntity> {
    List<ChallengeVO> getChallengeList();
    void joinChallenge(Long userId, Long challengeId);
    List<UserChallengeVO> getMyChallenges(Long userId);
    void checkin(Long userId, Long challengeId, String note);
}
