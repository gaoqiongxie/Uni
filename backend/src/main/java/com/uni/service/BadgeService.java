package com.uni.service;

import com.uni.vo.badge.BadgeVO;
import com.uni.vo.badge.UserBadgeStatsVO;

import java.util.List;

/**
 * 徽章服务接口
 */
public interface BadgeService {

    /**
     * 获取所有徽章列表
     */
    List<BadgeVO> getAllBadges(Long userId);

    /**
     * 获取用户已获得的徽章
     */
    List<BadgeVO> getUserBadges(Long userId);

    /**
     * 获取用户徽章统计
     */
    UserBadgeStatsVO getUserBadgeStats(Long userId);

    /**
     * 检查并授予徽章
     * @return 新获得的徽章列表
     */
    List<BadgeVO> checkAndGrantBadges(Long userId);

    /**
     * 标记新徽章为已读
     */
    void markBadgesAsRead(Long userId);

    /**
     * 检查特定条件类型的徽章
     */
    List<BadgeVO> checkBadgesByCondition(Long userId, String conditionType, Integer currentValue);

    /**
     * 获取新徽章数量
     */
    Integer getNewBadgeCount(Long userId);
}
