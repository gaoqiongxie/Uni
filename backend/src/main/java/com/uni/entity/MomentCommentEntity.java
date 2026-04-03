package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动态评论实体
 */
@Data
@TableName("t_moment_comment")
public class MomentCommentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long momentId;

    private Long userId;

    private String content;

    private Long parentId;

    private Long replyUserId;

    private Integer likeCount;

    private Integer status;

    private LocalDateTime createTime;
}
