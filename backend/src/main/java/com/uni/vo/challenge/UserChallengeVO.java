package com.uni.vo.challenge;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserChallengeVO {
    private Long id;
    private Long challengeId;
    private String title;
    private String type;
    private Integer durationDays;
    private Integer status;
    private String statusName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer checkinCount;
    private Integer streakDays;
    private BigDecimal completeRate;
    private Boolean todayChecked;
    private LocalDateTime createTime;

    public String getStatusName() {
        if (status == null) return "";
        switch (status) {
            case 0: return "已放弃";
            case 1: return "进行中";
            case 2: return "已完成";
            default: return "未知";
        }
    }
}
