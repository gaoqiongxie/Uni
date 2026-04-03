package com.uni.vo.social;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户关注VO
 */
@Data
public class UserFollowVO {

    private Long id;

    private Long userId;

    private String userNickname;

    private String userAvatar;

    private LocalDateTime createTime;
}
