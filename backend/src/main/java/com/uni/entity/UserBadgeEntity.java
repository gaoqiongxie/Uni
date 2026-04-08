package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户徽章关联实体
 */
@Data
@TableName("t_user_badge")
public class UserBadgeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 徽章ID */
    private Long badgeId;

    /** 获得时间 */
    private LocalDateTime acquireTime;

    /** 是否新获得: 0否 1是 */
    private Integer isNew;

    /** 创建时间 */
    private LocalDateTime createTime;
}
