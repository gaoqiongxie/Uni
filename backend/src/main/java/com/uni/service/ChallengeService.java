package com.uni.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uni.dto.challenge.CheckinDTO;
import com.uni.entity.ChallengeEntity;
import com.uni.vo.challenge.ChallengeCheckinVO;
import com.uni.vo.challenge.ChallengeVO;
import com.uni.vo.challenge.UserChallengeVO;

import java.util.List;

public interface ChallengeService extends IService<ChallengeEntity> {
    List<ChallengeVO> listChallenges();
    void joinChallenge(Long userId, Long challengeId);
    List<UserChallengeVO> getUserChallenges(Long userId);
    void checkin(Long userId, CheckinDTO dto);
    List<ChallengeCheckinVO> getCheckinList(Long userId, Long challengeId);
}
