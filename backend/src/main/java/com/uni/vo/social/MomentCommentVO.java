package com.uni.vo.social;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态评论VO
 */
@Data
public class MomentCommentVO {

    private Long id;

    private Long momentId;

    private Long userId;

    private String userNickname;

    private String userAvatar;

    private String content;

    private Long parentId;

    private Long replyUserId;

    private String replyUserNickname;

    private Integer likeCount;

    private LocalDateTime createTime;

    private List<MomentCommentVO> replies;
}
