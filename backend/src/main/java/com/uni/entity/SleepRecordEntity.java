package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 睡眠记录实体
 */
@Data
@TableName("t_sleep_record")
public class SleepRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate sleepDate;

    private LocalDateTime bedTime;

    private LocalDateTime wakeTime;

    private Integer sleepDuration;

    private Integer deepSleep;

    private Integer lightSleep;

    private Integer awakeDuration;

    private Integer sleepQuality;

    private Integer sleepScore;

    private String factors;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
