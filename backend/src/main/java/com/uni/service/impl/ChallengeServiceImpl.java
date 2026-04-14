package com.uni.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.dto.challenge.CheckinDTO;
import com.uni.entity.ChallengeCheckinEntity;
import com.uni.entity.ChallengeEntity;
import com.uni.entity.UserChallengeEntity;
import com.uni.mapper.ChallengeCheckinMapper;
import com.uni.mapper.ChallengeMapper;
import com.uni.mapper.UserChallengeMapper;
import com.uni.service.ChallengeService;
import com.uni.vo.challenge.ChallengeCheckinVO;
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
    public List<ChallengeVO> listChallenges() {
        List<ChallengeEntity> list = baseMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChallengeEntity>()
                        .eq(ChallengeEntity::getStatus, 1)
                        .orderByAsc(ChallengeEntity::getSortOrder));
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinChallenge(Long userId, Long challengeId) {
        ChallengeEntity challenge = baseMapper.selectById(challengeId);
        if (challenge == null) {
            throw new RuntimeException("挑战不存在");
        }

        UserChallengeEntity exist = userChallengeMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserChallengeEntity>()
                        .eq(UserChallengeEntity::getUserId, userId)
                        .eq(UserChallengeEntity::getChallengeId, challengeId));
        if (exist != null && exist.getStatus() == 1) {
            throw new RuntimeException("已参与该挑战");
        }

        UserChallengeEntity uc = new UserChallengeEntity();
        uc.setUserId(userId);
        uc.setChallengeId(challengeId);
        uc.setStatus(1);
        uc.setStartDate(LocalDate.now());
        uc.setEndDate(LocalDate.now().plusDays(challenge.getDurationDays() - 1));
        uc.setCheckinCount(0);
        uc.setStreakDays(0);
        uc.setCompleteRate(BigDecimal.ZERO);
        userChallengeMapper.insert(uc);

        baseMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ChallengeEntity>()
                .eq(ChallengeEntity::getId, challengeId)
                .setSql("joined_count = joined_count + 1"));
    }

    @Override
    public List<UserChallengeVO> getUserChallenges(Long userId) {
        List<UserChallengeEntity> list = userChallengeMapper.selectActiveByUserId(userId);
        return list.stream().map(uc -> {
            UserChallengeVO vo = new UserChallengeVO();
            BeanUtils.copyProperties(uc, vo);
            ChallengeEntity c = baseMapper.selectById(uc.getChallengeId());
            if (c != null) {
                vo.setTitle(c.getTitle());
                vo.setType(c.getType());
                vo.setDurationDays(c.getDurationDays());
            }
            vo.setTodayChecked(checkinMapper.countByUserAndChallengeAndDate(userId, uc.getChallengeId(), LocalDate.now()) > 0);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkin(Long userId, CheckinDTO dto) {
        UserChallengeEntity uc = userChallengeMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserChallengeEntity>()
                        .eq(UserChallengeEntity::getUserId, userId)
                        .eq(UserChallengeEntity::getChallengeId, dto.getChallengeId())
                        .eq(UserChallengeEntity::getStatus, 1));
        if (uc == null) {
            throw new RuntimeException("未参与该挑战");
        }

        if (checkinMapper.countByUserAndChallengeAndDate(userId, dto.getChallengeId(), LocalDate.now()) > 0) {
            throw new RuntimeException("今日已打卡");
        }

        ChallengeCheckinEntity checkin = new ChallengeCheckinEntity();
        checkin.setUserId(userId);
        checkin.setChallengeId(dto.getChallengeId());
        checkin.setUserChallengeId(uc.getId());
        checkin.setCheckinDate(LocalDate.now());
        checkin.setActualValue(dto.getActualValue());
        checkin.setNote(dto.getNote());
        checkin.setPhotoUrl(dto.getPhotoUrl());
        checkinMapper.insert(checkin);

        int checkinCount = uc.getCheckinCount() + 1;
        int streakDays = uc.getStreakDays() + 1;
        ChallengeEntity c = baseMapper.selectById(dto.getChallengeId());
        BigDecimal completeRate = BigDecimal.valueOf(checkinCount * 100)
                .divide(BigDecimal.valueOf(c.getDurationDays()), 2, RoundingMode.HALF_UP);

        userChallengeMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<UserChallengeEntity>()
                .eq(UserChallengeEntity::getId, uc.getId())
                .set(UserChallengeEntity::getCheckinCount, checkinCount)
                .set(UserChallengeEntity::getStreakDays, streakDays)
                .set(UserChallengeEntity::getCompleteRate, completeRate));
    }

    @Override
    public List<ChallengeCheckinVO> getCheckinList(Long userId, Long challengeId) {
        List<ChallengeCheckinEntity> list = checkinMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChallengeCheckinEntity>()
                        .eq(ChallengeCheckinEntity::getUserId, userId)
                        .eq(ChallengeCheckinEntity::getChallengeId, challengeId)
                        .orderByDesc(ChallengeCheckinEntity::getCheckinDate));
        return list.stream().map(c -> {
            ChallengeCheckinVO vo = new ChallengeCheckinVO();
            BeanUtils.copyProperties(c, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    private ChallengeVO convertToVO(ChallengeEntity entity) {
        ChallengeVO vo = new ChallengeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
