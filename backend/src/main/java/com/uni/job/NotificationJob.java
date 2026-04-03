package com.uni.job;

import com.uni.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 消息通知定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationJob {

    private final NotificationService notificationService;

    /**
     * 早餐打卡提醒 (每天 8:00)
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void breakfastReminder() {
        log.info("执行早餐打卡提醒任务: {}", LocalDateTime.now());
        notificationService.sendMealReminder("BREAKFAST", LocalTime.of(8, 0));
    }

    /**
     * 午餐打卡提醒 (每天 12:00)
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void lunchReminder() {
        log.info("执行午餐打卡提醒任务: {}", LocalDateTime.now());
        notificationService.sendMealReminder("LUNCH", LocalTime.of(12, 0));
    }

    /**
     * 晚餐打卡提醒 (每天 18:00)
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void dinnerReminder() {
        log.info("执行晚餐打卡提醒任务: {}", LocalDateTime.now());
        notificationService.sendMealReminder("DINNER", LocalTime.of(18, 0));
    }

    /**
     * 运动打卡提醒 (每天 20:00)
     */
    @Scheduled(cron = "0 0 20 * * ?")
    public void exerciseReminder() {
        log.info("执行运动打卡提醒任务: {}", LocalDateTime.now());
        notificationService.sendExerciseReminder();
    }

    /**
     * 体重记录提醒 (每天 9:00)
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void weightReminder() {
        log.info("执行体重记录提醒任务: {}", LocalDateTime.now());
        notificationService.sendWeightReminder();
    }

    /**
     * 每日总结通知 (每天 22:00)
     */
    @Scheduled(cron = "0 0 22 * * ?")
    public void dailySummary() {
        log.info("执行每日总结通知任务: {}", LocalDateTime.now());
        notificationService.sendDailySummary();
    }

    /**
     * 目标达成检查 (每天 23:00)
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void goalAchievementCheck() {
        log.info("执行目标达成检查任务: {}", LocalDateTime.now());
        notificationService.checkGoalAchievement();
    }
}
