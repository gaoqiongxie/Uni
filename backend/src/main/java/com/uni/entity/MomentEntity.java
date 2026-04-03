package com.uni.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户动态实体
 */
@Data
@TableName("t_moment")
public class MomentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String content;

    private String images;

    private String location;

    private String relatedType;

    private Long relatedId;

    private Integer likeCount;

    private Integer commentCount;

    private Integer isPublic;

    private Integer status;

    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
