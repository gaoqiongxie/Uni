package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户消息实体
 */
@Data
@TableName("t_user_message")
public class UserMessageEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户id */
    private Long userId;

    /** 消息类型(1.打卡提醒,2.目标达成,3.系统公告,4.周报推送) */
    private Integer messageType;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 扩展数据(JSON) */
    private String extraData;

    /** 是否已读(0.未读,1.已读) */
    private Integer isRead;

    /** 阅读时间 */
    private LocalDateTime readTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
