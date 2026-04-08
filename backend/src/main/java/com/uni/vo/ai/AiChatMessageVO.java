package com.uni.vo.ai;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI对话消息VO
 */
@Data
public class AiChatMessageVO {

    private Long id;
    private String role;
    private String content;
    private String messageType;
    private Object relatedData;
    private LocalDateTime createdAt;
}
