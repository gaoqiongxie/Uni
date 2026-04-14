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
    private String typeName;
    private BigDecimal targetValue;
    private String targetUnit;
    private Integer durationDays;
    private String coverUrl;
    private Integer joinedCount;
    private LocalDateTime createTime;

    public String getTypeName() {
        if (type == null) return "";
        switch (type) {
            case "weight": return "体重";
            case "exercise": return "运动";
            case "meal": return "饮食";
            case "water": return "饮水";
            case "custom": return "自定义";
            default: return type;
        }
    }
}
