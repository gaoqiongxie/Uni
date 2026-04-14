package com.uni.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.entity.ChallengeCheckinEntity;
import com.uni.entity.ChallengeEntity;
import com.uni.entity.UserChallengeEntity;
import com.uni.mapper.ChallengeCheckinMapper;
import com.uni.mapper.ChallengeMapper;
import com.uni.mapper.UserChallengeMapper;
import com.uni.service.ChallengeService;
import com.uni.vo.challenge.ChallengeVO;
import com.uni.vo.challenge.UserChallengeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImpl extends ServiceImpl<ChallengeMapper, ChallengeEntity> implements ChallengeService {

    @Autowired
    private UserChallengeMapper userChallengeMapper;
    @Autowired
    private ChallengeCheckinMapper checkinMapper;

    @Override
    public List<ChallengeVO> getChallengeList() {
        List<ChallengeEntity> list = lambdaQuery().eq(ChallengeEntity::getStatus, 1)
                .orderByAsc(ChallengeEntity::getSortOrder).list();
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinChallenge(Long userId, Long challengeId) {
        ChallengeEntity challenge = getById(challengeId);
        if (challenge == null) return;

        UserChallengeEntity userChallenge = new UserChallengeEntity();
        userChallenge.setUserId(userId);
        userChallenge.setChallengeId(challengeId);
        userChallenge.setStatus(1);
        userChallenge.setStartDate(LocalDate.now());
        userChallenge.setEndDate(LocalDate.now().plusDays(challenge.getDurationDays()));
        userChallenge.setCheckinCount(0);
        userChallenge.setStreakDays(0);
        userChallenge.setCompleteRate(BigDecimal.ZERO);
        userChallengeMapper.insert(userChallenge);

        lambdaUpdate().setSql("joined_count = joined_count + 1").eq(ChallengeEntity::getId, challengeId).update();
    }

    @Override
    public List<UserChallengeVO> getMyChallenges(Long userId) {
        List<UserChallengeEntity> list = userChallengeMapper.selectActiveByUserId(userId);
        return list.stream().map(uc -> {
            UserChallengeVO vo = new UserChallengeVO();
            BeanUtils.copyProperties(uc, vo);
            ChallengeEntity challenge = getById(uc.getChallengeId());
            if (challenge != null) {
                vo.setTitle(challenge.getTitle());
                vo.setDurationDays(challenge.getDurationDays());
                vo.setType(challenge.getType());
            }
            long daysPassed = ChronoUnit.DAYS.between(uc.getStartDate(), LocalDate.now()) + 1;
            vo.setDaysPassed((int) Math.max(0, daysPassed));
            vo.setDaysRemaining((int) ChronoUnit.DAYS.between(LocalDate.now(), uc.getEndDate()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkin(Long userId, Long challengeId, String note) {
        UserChallengeEntity uc = userChallengeMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserChallengeEntity>()
                        .eq(UserChallengeEntity::getUserId, userId)
                        .eq(UserChallengeEntity::getChallengeId, challengeId)
                        .eq(UserChallengeEntity::getStatus, 1));
        if (uc == null) return;

        LocalDate today = LocalDate.now();
        if (checkinMapper.countByUserAndChallengeAndDate(userId, challengeId, today) > 0) return;

        ChallengeCheckinEntity checkin = new ChallengeCheckinEntity();
        checkin.setUserId(userId);
        checkin.setChallengeId(challengeId);
        checkin.setUserChallengeId(uc.getId());
        checkin.setCheckinDate(today);
        checkin.setNote(note);
        checkinMapper.insert(checkin);

        int newCount = uc.getCheckinCount() + 1;
        int newStreak = uc.getStreakDays() + 1;
        BigDecimal rate = BigDecimal.valueOf(newCount * 100)
                .divide(BigDecimal.valueOf(uc.getEndDate().toEpochDay() - uc.getStartDate().toEpochDay() + 1), 2, RoundingMode.HALF_UP);

        userChallengeMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UserChallengeEntity>()
                .eq(UserChallengeEntity::getId, uc.getId())
                .set(UserChallengeEntity::getCheckinCount, newCount)
                .set(UserChallengeEntity::getStreakDays, newStreak)
                .set(UserChallengeEntity::getCompleteRate, rate));
    }

    private ChallengeVO convertToVO(ChallengeEntity entity) {
        ChallengeVO vo = new ChallengeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
