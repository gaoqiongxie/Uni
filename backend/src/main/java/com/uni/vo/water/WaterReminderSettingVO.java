package com.uni.vo.water;

import lombok.Data;

/**
 * 饮水提醒设置VO
 */
@Data
public class WaterReminderSettingVO {

    private Long id;
    private Long userId;
    private Boolean enabled;
    private Integer dailyGoal;
    private Integer reminderInterval;
    private String startTime;
    private String endTime;
    private Boolean reminderSound;
    private Boolean reminderVibration;
}
