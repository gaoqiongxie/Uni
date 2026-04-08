package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 徽章定义实体
 */
@Data
@TableName("t_badge")
public class BadgeEntity {

    @TableId(type = IdType.AUTO)
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

    /** 排序 */
    private Integer sortOrder;

    /** 状态: 0禁用 1启用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
