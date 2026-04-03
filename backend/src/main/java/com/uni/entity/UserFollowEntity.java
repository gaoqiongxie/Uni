package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户关注实体
 */
@Data
@TableName("t_user_follow")
public class UserFollowEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long followUserId;

    private LocalDateTime createTime;
}
