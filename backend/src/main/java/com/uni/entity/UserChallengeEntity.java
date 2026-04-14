package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_user_challenge")
public class UserChallengeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long challengeId;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer checkinCount;
    private Integer streakDays;
    private BigDecimal completeRate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
