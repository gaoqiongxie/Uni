package com.uni.vo.social;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long id;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String content;
    private LocalDateTime createTime;
}
