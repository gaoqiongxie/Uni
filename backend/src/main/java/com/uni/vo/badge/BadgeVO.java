package com.uni.vo.badge;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 徽章VO
 */
@Data
public class BadgeVO {

    private Long id;

    /** 徽章编码 */
    private String code;

    /** 徽章名称 */
    private String name;

    /** 徽章描述 */
    private String description;

    /** 徽章图标 */
    private String icon;

    /** 分类: general通用/weight体重/meal饮食/exercise运动/social社交 */
    private String category;

    /** 稀有度: common普通/rare稀有/epic史诗/legendary传说 */
    private String rarity;

    /** 条件类型 */
    private String conditionType;

    /** 条件数值 */
    private Integer conditionValue;

    /** 是否已获得 */
    private Boolean acquired;

    /** 获得时间 */
    private LocalDateTime acquireTime;

    /** 是否新获得 */
    private Boolean isNew;

    /** 分类名称 */
    private String categoryName;

    /** 稀有度名称 */
    private String rarityName;
}
