package com.uni.vo.sleep;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 睡眠记录VO
 */
@Data
public class SleepRecordVO {

    private Long id;

    private LocalDate sleepDate;

    private LocalDateTime bedTime;

    private LocalDateTime wakeTime;

    private Integer sleepDuration;

    private Integer deepSleep;

    private Integer lightSleep;

    private Integer awakeDuration;

    private Integer sleepQuality;

    private String sleepQualityText;

    private Integer sleepScore;

    private String factors;

    private String notes;

    private String bedTimeStr;

    private String wakeTimeStr;

    private String durationStr;
}
