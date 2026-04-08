package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.entity.BadgeEntity;
import com.uni.entity.UserBadgeEntity;
import com.uni.mapper.BadgeMapper;
import com.uni.mapper.UserBadgeMapper;
import com.uni.service.BadgeService;
import com.uni.service.GoalCheckinService;
import com.uni.service.WeightRecordService;
import com.uni.vo.badge.BadgeVO;
import com.uni.vo.badge.UserBadgeStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 徽章服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeMapper badgeMapper;
    private final UserBadgeMapper userBadgeMapper;
    private final WeightRecordService weightRecordService;
    private final GoalCheckinService goalCheckinService;

    // 分类名称映射
    private static final Map<String, String> CATEGORY_NAMES = Map.of(
        "general", "通用",
        "weight", "体重",
        "meal", "饮食",
        "exercise", "运动",
        "social", "社交"
    );

    // 稀有度名称映射
    private static final Map<String, String> RARITY_NAMES = Map.of(
        "common", "普通",
        "rare", "稀有",
        "epic", "史诗",
        "legendary", "传说"
    );

    @Override
    public List<BadgeVO> getAllBadges(Long userId) {
        // 获取所有徽章
        List<BadgeEntity> allBadges = badgeMapper.selectList(
            new LambdaQueryWrapper<BadgeEntity>()
                .eq(BadgeEntity::getStatus, 1)
                .orderByAsc(BadgeEntity::getSortOrder)
        );

        // 获取用户已获得的徽章ID
        List<Long> acquiredBadgeIds = userBadgeMapper.selectBadgeIdsByUserId(userId);
        Set<Long> acquiredSet = new HashSet<>(acquiredBadgeIds);

        // 获取用户徽章详情（包含获得时间、是否新获得）
        Map<Long, UserBadgeEntity> userBadgeMap = new HashMap<>();
        if (!acquiredBadgeIds.isEmpty()) {
            List<UserBadgeEntity> userBadges = userBadgeMapper.selectList(
                new LambdaQueryWrapper<UserBadgeEntity>()
                    .eq(UserBadgeEntity::getUserId, userId)
            );
            userBadgeMap = userBadges.stream()
                .collect(Collectors.toMap(UserBadgeEntity::getBadgeId, ub -> ub));
        }

        // 组装VO
        List<BadgeVO> result = new ArrayList<>();
        for (BadgeEntity badge : allBadges) {
            BadgeVO vo = convertToVO(badge);
            vo.setAcquired(acquiredSet.contains(badge.getId()));
            
            if (vo.getAcquired() && userBadgeMap.containsKey(badge.getId())) {
                UserBadgeEntity ub = userBadgeMap.get(badge.getId());
                vo.setAcquireTime(ub.getAcquireTime());
                vo.setIsNew(ub.getIsNew() == 1);
            }
            
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<BadgeVO> getUserBadges(Long userId) {
        // 获取用户已获得的徽章
        List<Long> badgeIds = userBadgeMapper.selectBadgeIdsByUserId(userId);
        if (badgeIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<BadgeEntity> badges = badgeMapper.selectList(
            new LambdaQueryWrapper<BadgeEntity>()
                .in(BadgeEntity::getId, badgeIds)
                .eq(BadgeEntity::getStatus, 1)
                .orderByDesc(BadgeEntity::getSortOrder)
        );

        Map<Long, UserBadgeEntity> userBadgeMap = userBadgeMapper.selectList(
            new LambdaQueryWrapper<UserBadgeEntity>()
                .eq(UserBadgeEntity::getUserId, userId)
        ).stream().collect(Collectors.toMap(UserBadgeEntity::getBadgeId, ub -> ub));

        return badges.stream().map(badge -> {
            BadgeVO vo = convertToVO(badge);
            vo.setAcquired(true);
            UserBadgeEntity ub = userBadgeMap.get(badge.getId());
            if (ub != null) {
                vo.setAcquireTime(ub.getAcquireTime());
                vo.setIsNew(ub.getIsNew() == 1);
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public UserBadgeStatsVO getUserBadgeStats(Long userId) {
        UserBadgeStatsVO stats = new UserBadgeStatsVO();
        
        // 总徽章数
        long totalBadges = badgeMapper.selectCount(
            new LambdaQueryWrapper<BadgeEntity>().eq(BadgeEntity::getStatus, 1)
        );
        stats.setTotalBadges((int) totalBadges);

        // 已获得徽章数
        List<Long> acquiredIds = userBadgeMapper.selectBadgeIdsByUserId(userId);
        stats.setAcquiredCount(acquiredIds.size());

        // 新徽章数
        stats.setNewCount(userBadgeMapper.selectNewBadgeCount(userId));

        // 各分类统计
        Map<String, UserBadgeStatsVO.CategoryStat> categoryStats = new LinkedHashMap<>();
        String[] categories = {"general", "weight", "meal", "exercise", "social"};
        
        for (String cat : categories) {
            UserBadgeStatsVO.CategoryStat stat = new UserBadgeStatsVO.CategoryStat();
            stat.setCode(cat);
            stat.setName(CATEGORY_NAMES.getOrDefault(cat, cat));
            
            // 该分类总徽章数
            long catTotal = badgeMapper.selectCount(
                new LambdaQueryWrapper<BadgeEntity>()
                    .eq(BadgeEntity::getCategory, cat)
                    .eq(BadgeEntity::getStatus, 1)
            );
            stat.setTotal((int) catTotal);
            
            // 该分类已获得数
            if (!acquiredIds.isEmpty()) {
                List<BadgeEntity> catBadges = badgeMapper.selectList(
                    new LambdaQueryWrapper<BadgeEntity>()
                        .eq(BadgeEntity::getCategory, cat)
                        .eq(BadgeEntity::getStatus, 1)
                        .in(BadgeEntity::getId, acquiredIds)
                );
                stat.setAcquired(catBadges.size());
            } else {
                stat.setAcquired(0);
            }
            
            categoryStats.put(cat, stat);
        }
        stats.setCategoryStats(categoryStats);

        // 最近获得的徽章（5个）
        List<UserBadgeEntity> recentUserBadges = userBadgeMapper.selectList(
            new LambdaQueryWrapper<UserBadgeEntity>()
                .eq(UserBadgeEntity::getUserId, userId)
                .orderByDesc(UserBadgeEntity::getAcquireTime)
                .last("LIMIT 5")
        );
        
        if (!recentUserBadges.isEmpty()) {
            List<Long> recentIds = recentUserBadges.stream()
                .map(UserBadgeEntity::getBadgeId)
                .collect(Collectors.toList());
            
            Map<Long, BadgeEntity> badgeMap = badgeMapper.selectList(
                new LambdaQueryWrapper<BadgeEntity>()
                    .in(BadgeEntity::getId, recentIds)
            ).stream().collect(Collectors.toMap(BadgeEntity::getId, b -> b));

            Map<Long, UserBadgeEntity> userBadgeMap = recentUserBadges.stream()
                .collect(Collectors.toMap(UserBadgeEntity::getBadgeId, ub -> ub));

            List<BadgeVO> recentBadges = recentIds.stream()
                .map(id -> {
                    BadgeEntity badge = badgeMap.get(id);
                    if (badge == null) return null;
                    BadgeVO vo = convertToVO(badge);
                    vo.setAcquired(true);
                    UserBadgeEntity ub = userBadgeMap.get(id);
                    vo.setAcquireTime(ub.getAcquireTime());
                    vo.setIsNew(ub.getIsNew() == 1);
                    return vo;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            stats.setRecentBadges(recentBadges);
        } else {
            stats.setRecentBadges(new ArrayList<>());
        }

        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BadgeVO> checkAndGrantBadges(Long userId) {
        List<BadgeVO> newBadges = new ArrayList<>();
        
        // 获取用户未获得的徽章
        List<BadgeEntity> unacquiredBadges = badgeMapper.selectUnacquiredBadges(userId);
        
        for (BadgeEntity badge : unacquiredBadges) {
            boolean shouldGrant = checkBadgeCondition(userId, badge);
            if (shouldGrant) {
                // 授予徽章
                UserBadgeEntity userBadge = new UserBadgeEntity();
                userBadge.setUserId(userId);
                userBadge.setBadgeId(badge.getId());
                userBadge.setAcquireTime(LocalDateTime.now());
                userBadge.setIsNew(1);
                userBadgeMapper.insert(userBadge);
                
                BadgeVO vo = convertToVO(badge);
                vo.setAcquired(true);
                vo.setAcquireTime(userBadge.getAcquireTime());
                vo.setIsNew(true);
                newBadges.add(vo);
                
                log.info("用户 {} 获得新徽章: {}", userId, badge.getName());
            }
        }
        
        return newBadges;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markBadgesAsRead(Long userId) {
        userBadgeMapper.markAsRead(userId);
    }

    @Override
    public List<BadgeVO> checkBadgesByCondition(Long userId, String conditionType, Integer currentValue) {
        List<BadgeVO> newBadges = new ArrayList<>();
        
        // 获取该条件类型下用户未获得的徽章
        List<BadgeEntity> badges = badgeMapper.selectByConditionType(conditionType);
        List<Long> acquiredIds = userBadgeMapper.selectBadgeIdsByUserId(userId);
        
        for (BadgeEntity badge : badges) {
            if (acquiredIds.contains(badge.getId())) continue;
            
            if (currentValue >= badge.getConditionValue()) {
                // 授予徽章
                UserBadgeEntity userBadge = new UserBadgeEntity();
                userBadge.setUserId(userId);
                userBadge.setBadgeId(badge.getId());
                userBadge.setAcquireTime(LocalDateTime.now());
                userBadge.setIsNew(1);
                userBadgeMapper.insert(userBadge);
                
                BadgeVO vo = convertToVO(badge);
                vo.setAcquired(true);
                vo.setAcquireTime(userBadge.getAcquireTime());
                vo.setIsNew(true);
                newBadges.add(vo);
            }
        }
        
        return newBadges;
    }

    @Override
    public Integer getNewBadgeCount(Long userId) {
        return userBadgeMapper.selectNewBadgeCount(userId);
    }

    /**
     * 检查徽章条件
     */
    private boolean checkBadgeCondition(Long userId, BadgeEntity badge) {
        String conditionType = badge.getConditionType();
        Integer conditionValue = badge.getConditionValue();
        
        switch (conditionType) {
            case "consecutive_checkin":
                return checkConsecutiveCheckin(userId, conditionValue);
            case "weight_count":
                return checkWeightCount(userId, conditionValue);
            case "weight_loss":
                return checkWeightLoss(userId, conditionValue);
            case "exercise_count":
                return checkExerciseCount(userId, conditionValue);
            case "meal_count":
                return checkMealCount(userId, conditionValue);
            // 其他条件类型...
            default:
                return false;
        }
    }

    private boolean checkConsecutiveCheckin(Long userId, Integer days) {
        // 这里需要查询连续打卡天数
        // 简化实现，实际应该查询t_goal_checkin表
        return false;
    }

    private boolean checkWeightCount(Long userId, Integer count) {
        long weightCount = weightRecordService.count(
            new LambdaQueryWrapper<>()
        );
        return weightCount >= count;
    }

    private boolean checkWeightLoss(Long userId, Integer kg) {
        // 查询体重变化
        // 简化实现
        return false;
    }

    private boolean checkExerciseCount(Long userId, Integer count) {
        // 查询运动记录数
        return false;
    }

    private boolean checkMealCount(Long userId, Integer count) {
        // 查询饮食记录天数
        return false;
    }

    private BadgeVO convertToVO(BadgeEntity entity) {
        BadgeVO vo = new BadgeVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setIcon(entity.getIcon());
        vo.setCategory(entity.getCategory());
        vo.setRarity(entity.getRarity());
        vo.setConditionType(entity.getConditionType());
        vo.setConditionValue(entity.getConditionValue());
        vo.setCategoryName(CATEGORY_NAMES.getOrDefault(entity.getCategory(), entity.getCategory()));
        vo.setRarityName(RARITY_NAMES.getOrDefault(entity.getRarity(), entity.getRarity()));
        return vo;
    }
}
