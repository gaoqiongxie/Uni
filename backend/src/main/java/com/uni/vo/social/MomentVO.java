package com.uni.vo.social;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态VO
 */
@Data
public class MomentVO {

    private Long id;

    private Long userId;

    private String userNickname;

    private String userAvatar;

    private String content;

    private List<String> images;

    private String location;

    private String relatedType;

    private Long relatedId;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean isLiked;

    private Integer isPublic;

    private LocalDateTime createTime;
}
