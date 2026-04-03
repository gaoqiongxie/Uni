package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动态点赞实体
 */
@Data
@TableName("t_moment_like")
public class MomentLikeEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long momentId;

    private Long userId;

    private LocalDateTime createTime;
}
