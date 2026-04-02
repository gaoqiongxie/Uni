package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.common.BizCode;
import com.uni.common.BizException;
import com.uni.dto.goal.UserGoalDTO;
import com.uni.entity.UserEntity;
import com.uni.entity.UserGoalEntity;
import com.uni.mapper.UserGoalMapper;
import com.uni.service.UserGoalService;
import com.uni.service.UserService;
import com.uni.vo.goal.UserGoalVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户目标服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserGoalServiceImpl extends ServiceImpl<UserGoalMapper, UserGoalEntity> implements UserGoalService {

    private static final Map<Integer, String> GOAL_TYPE_MAP = Map.of(
            1, "减重", 2, "维持", 3, "增肌"
    );
    private static final Map<Integer, String> STATUS_MAP = Map.of(
            0, "已放弃", 1, "进行中", 2, "已达成"
    );

    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserGoalVO createGoal(Long userId, UserGoalDTO dto) {
        // 将当前进行中目标置为放弃
        update(new LambdaUpdateWrapper<UserGoalEntity>()
                .eq(UserGoalEntity::getUserId, userId)
                .eq(UserGoalEntity::getStatus, 1)
                .set(UserGoalEntity::getStatus, 0));

        UserEntity user = userService.getById(userId);
        if (user == null) {
            throw new BizException(BizCode.USER_NOT_FOUND, "用户不存在");
        }

        UserGoalEntity entity = new UserGoalEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setUserId(userId);
        entity.setStartWeight(user.getCurrentWeight() != null ? user.getCurrentWeight() : BigDecimal.ZERO);
        entity.setStartDate(LocalDate.now());
        entity.setStatus(1);
        entity.setCreateUser(String.valueOf(userId));
        entity.setUpdateUser(String.valueOf(userId));
        entity.setDeleteFlag(0);

        // 若未传热量目标，自动根据体重差计算推荐值
        if (entity.getCalorieGoal() == null || entity.getCalorieGoal() <= 0) {
            entity.setCalorieGoal(calcRecommendCalorie(user, dto.getTargetWeight()));
        }

        save(entity);

        // 同步更新用户目标体重和热量目标
        userService.updateGoal(userId, dto.getTargetWeight(), entity.getCalorieGoal());

        return toVO(entity, user);
    }

    @Override
    public UserGoalVO getActiveGoal(Long userId) {
        UserGoalEntity entity = baseMapper.selectActiveGoal(userId);
        if (entity == null) return null;
        UserEntity user = userService.getById(userId);
        return toVO(entity, user);
    }

    @Override
    public List<UserGoalVO> listGoals(Long userId) {
        List<UserGoalEntity> list = list(new LambdaQueryWrapper<UserGoalEntity>()
                .eq(UserGoalEntity::getUserId, userId)
                .eq(UserGoalEntity::getDeleteFlag, 0)
                .orderByDesc(UserGoalEntity::getCreateTime));
        UserEntity user = userService.getById(userId);
        return list.stream().map(e -> toVO(e, user)).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean abandonGoal(Long goalId, Long userId) {
        UserGoalEntity entity = getById(goalId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            throw new BizException(BizCode.PARAM_ERROR, "目标不存在");
        }
        if (entity.getStatus() != 1) {
            throw new BizException(BizCode.PARAM_ERROR, "目标已结束");
        }
        return update(new LambdaUpdateWrapper<UserGoalEntity>()
                .eq(UserGoalEntity::getId, goalId)
                .set(UserGoalEntity::getStatus, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean achieveGoal(Long goalId, Long userId) {
        UserGoalEntity entity = getById(goalId);
        if (entity == null || !entity.getUserId().equals(userId)) {
            throw new BizException(BizCode.PARAM_ERROR, "目标不存在");
        }
        if (entity.getStatus() != 1) {
            throw new BizException(BizCode.PARAM_ERROR, "目标已结束");
        }
        return update(new LambdaUpdateWrapper<UserGoalEntity>()
                .eq(UserGoalEntity::getId, goalId)
                .set(UserGoalEntity::getStatus, 2)
                .set(UserGoalEntity::getAchievedDate, LocalDate.now()));
    }

    // ─────────────────── 私有方法 ───────────────────

    private UserGoalVO toVO(UserGoalEntity entity, UserEntity user) {
        UserGoalVO vo = new UserGoalVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setGoalTypeName(GOAL_TYPE_MAP.getOrDefault(entity.getGoalType(), "未知"));
        vo.setStatusName(STATUS_MAP.getOrDefault(entity.getStatus(), "未知"));

        LocalDate today = LocalDate.now();
        long persist = ChronoUnit.DAYS.between(entity.getStartDate(), today) + 1;
        vo.setPersistDays((int) persist);

        if (entity.getTargetDate() != null && entity.getTargetDate().isAfter(today)) {
            vo.setRemainDays((int) ChronoUnit.DAYS.between(today, entity.getTargetDate()));
        } else {
            vo.setRemainDays(0);
        }

        if (user != null && user.getCurrentWeight() != null) {
            vo.setCurrentWeight(user.getCurrentWeight());
            BigDecimal gap = entity.getStartWeight().subtract(entity.getTargetWeight());
            BigDecimal lost = entity.getStartWeight().subtract(user.getCurrentWeight());
            vo.setWeightGap(gap);
            vo.setWeightLost(lost);
            // 进度百分比
            if (gap.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal pct = lost.divide(gap, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);
                vo.setProgressPercent(pct.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100)));
            } else {
                vo.setProgressPercent(BigDecimal.valueOf(100));
            }
        }

        return vo;
    }

    /**
     * 根据 BMR + 活动系数推荐每日热量缺口目标
     * 简化计算：目标热量 = TDEE - 400 kcal，最低 1200
     */
    private int calcRecommendCalorie(UserEntity user, BigDecimal targetWeight) {
        if (user.getHeight() == null || user.getCurrentWeight() == null) return 1400;
        // Mifflin-St Jeor BMR
        double h = user.getHeight().doubleValue();
        double w = user.getCurrentWeight().doubleValue();
        // 默认女性（减脂应用主力用户）
        double bmr = 10 * w + 6.25 * h - 5 * 30 - 161;
        // 轻度活动 1.375
        double tdee = bmr * 1.375;
        int recommend = (int) Math.max(1200, tdee - 400);
        return recommend;
    }
}
