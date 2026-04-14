package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_challenge")
public class ChallengeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String type;
    private BigDecimal targetValue;
    private String targetUnit;
    private Integer durationDays;
    private String coverUrl;
    private Integer status;
    private Integer sortOrder;
    private Integer joinedCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
