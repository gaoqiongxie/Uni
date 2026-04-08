package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI对话记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_ai_chat_history")
public class AiChatHistoryEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String sessionId;
    private String role;
    private String content;
    private String messageType;
    private String relatedData;
    private LocalDateTime createdAt;
    private Integer deleted;
}
