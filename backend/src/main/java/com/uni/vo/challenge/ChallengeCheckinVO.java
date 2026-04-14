package com.uni.vo.challenge;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ChallengeCheckinVO {
    private Long id;
    private Long challengeId;
    private LocalDate checkinDate;
    private BigDecimal actualValue;
    private String note;
    private String photoUrl;
    private LocalDateTime createTime;
}
