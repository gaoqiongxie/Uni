package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 睡眠目标设置实体
 */
@Data
@TableName("t_sleep_goal")
public class SleepGoalEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer targetDuration;

    private String targetBedTime;

    private String targetWakeTime;

    private Integer reminderEnabled;

    private Integer bedReminder;

    private Integer wakeReminder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
