package com.uni.vo.sleep;

import lombok.Data;

/**
 * 睡眠目标VO
 */
@Data
public class SleepGoalVO {

    private Integer targetDuration;

    private String targetBedTime;

    private String targetWakeTime;

    private Boolean reminderEnabled;

    private Boolean bedReminder;

    private Boolean wakeReminder;
}
