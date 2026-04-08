package com.uni.vo.sleep;

import lombok.Data;

import java.util.List;

/**
 * 睡眠统计VO
 */
@Data
public class SleepStatsVO {

    private Integer avgDuration;

    private String avgDurationStr;

    private Double avgQuality;

    private Integer goalAchievementRate;

    private Integer totalRecords;

    private List<SleepRecordVO> recentRecords;

    private List<DailySleepStats> weeklyStats;

    @Data
    public static class DailySleepStats {
        private String date;
        private Integer duration;
        private Integer quality;
        private Integer score;
    }
}
