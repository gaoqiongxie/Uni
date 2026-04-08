package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 饮水提醒设置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_water_reminder_setting")
public class WaterReminderSettingEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否开启提醒: 0-关闭, 1-开启
     */
    private Integer enabled;

    /**
     * 每日目标饮水量(ml)
     */
    private Integer dailyGoal;

    /**
     * 提醒间隔(分钟)
     */
    private Integer reminderInterval;

    /**
     * 提醒开始时间
     */
    private LocalTime startTime;

    /**
     * 提醒结束时间
     */
    private LocalTime endTime;

    /**
     * 提醒声音: 0-关闭, 1-开启
     */
    private Integer reminderSound;

    /**
     * 提醒震动: 0-关闭, 1-开启
     */
    private Integer reminderVibration;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
