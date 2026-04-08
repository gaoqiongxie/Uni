package com.uni.vo.badge;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户徽章统计VO
 */
@Data
public class UserBadgeStatsVO {

    /** 总徽章数 */
    private Integer totalBadges;

    /** 已获得徽章数 */
    private Integer acquiredCount;

    /** 新获得徽章数 */
    private Integer newCount;

    /** 各分类统计 */
    private Map<String, CategoryStat> categoryStats;

    /** 最近获得的徽章 */
    private List<BadgeVO> recentBadges;

    @Data
    public static class CategoryStat {
        /** 分类编码 */
        private String code;
        /** 分类名称 */
        private String name;
        /** 已获得数 */
        private Integer acquired;
        /** 总数 */
        private Integer total;
    }
}
