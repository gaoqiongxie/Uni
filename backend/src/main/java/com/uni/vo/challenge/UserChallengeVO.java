package com.uni.vo.challenge;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserChallengeVO {
    private Long id;
    private Long challengeId;
    private String title;
    private String type;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationDays;
    private Integer checkinCount;
    private Integer streakDays;
    private BigDecimal completeRate;
    private Integer daysPassed;
    private Integer daysRemaining;
}
