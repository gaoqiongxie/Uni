package com.uni.vo.challenge;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChallengeVO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private BigDecimal targetValue;
    private String targetUnit;
    private Integer durationDays;
    private String coverUrl;
    private Integer joinedCount;
    private LocalDateTime createTime;
}
