package com.uni.vo.meal;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡日历VO
 */
@Data
@Accessors(chain = true)
public class MealCalendarVO {

    private Integer year;
    private Integer month;
    private Integer totalDays;
    private Integer checkInDays;
    private Double checkInRate;
    private Integer currentStreak;
    private Integer maxStreak;
    private List<DayVO> calendar;

    @Data
    @Accessors(chain = true)
    public static class DayVO {
        private LocalDate date;
        private Boolean checkIn;
        private Integer mealCount;
    }
}
