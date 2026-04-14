package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_challenge_checkin")
public class ChallengeCheckinEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long challengeId;
    private Long userChallengeId;
    private LocalDate checkinDate;
    private BigDecimal actualValue;
    private String note;
    private String photoUrl;
    private LocalDateTime createTime;
}
