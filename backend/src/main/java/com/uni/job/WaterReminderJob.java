package com.uni.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uni.entity.UserEntity;
import com.uni.entity.WaterRecordEntity;
import com.uni.entity.WaterReminderSettingEntity;
import com.uni.mapper.UserMapper;
import com.uni.mapper.WaterRecordMapper;
import com.uni.mapper.WaterReminderSettingMapper;
import com.uni.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 饮水提醒定时任务
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WaterReminderJob {

    private final WaterReminderSettingMapper reminderSettingMapper;
    private final WaterRecordMapper waterRecordMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    /**
     * 每30分钟检查一次饮水提醒
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void checkWaterReminder() {
        log.info("开始执行饮水提醒检查任务");

        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        // 获取所有开启提醒的用户设置
        LambdaQueryWrapper<WaterReminderSettingEntity> settingWrapper = new LambdaQueryWrapper<>();
        settingWrapper.eq(WaterReminderSettingEntity::getReminderEnabled, true);
        List<WaterReminderSettingEntity> settings = reminderSettingMapper.selectList(settingWrapper);

        for (WaterReminderSettingEntity setting : settings) {
            try {
                // 检查是否在提醒时间段内
                LocalTime startTime = LocalTime.parse(setting.getStartTime());
                LocalTime endTime = LocalTime.parse(setting.getEndTime());

                if (now.isBefore(startTime) || now.isAfter(endTime)) {
                    continue;
                }

                // 检查是否需要提醒（距离上次记录是否超过间隔时间）
                if (!shouldRemind(setting, today)) {
                    continue;
                }

                // 检查今日是否已完成目标
                if (isDailyGoalReached(setting, today)) {
                    continue;
                }

                // 发送提醒
                sendWaterReminder(setting.getUserId());

            } catch (Exception e) {
                log.error("饮水提醒处理失败, userId: {}", setting.getUserId(), e);
            }
        }

        log.info("饮水提醒检查任务完成");
    }

    /**
     * 判断是否需要提醒
     */
    private boolean shouldRemind(WaterReminderSettingEntity setting, LocalDate date) {
        // 获取当日最后一次饮水记录
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        LambdaQueryWrapper<WaterRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterRecordEntity::getUserId, setting.getUserId())
                .between(WaterRecordEntity::getRecordTime, startOfDay, endOfDay)
                .orderByDesc(WaterRecordEntity::getRecordTime)
                .last("LIMIT 1");

        WaterRecordEntity lastRecord = waterRecordMapper.selectOne(wrapper);

        if (lastRecord == null) {
            // 今天还没有记录，需要提醒
            return true;
        }

        // 检查距离上次记录是否超过间隔时间
        LocalDateTime lastRecordTime = lastRecord.getRecordTime();
        LocalDateTime nextReminderTime = lastRecordTime.plusMinutes(setting.getReminderInterval());

        return LocalDateTime.now().isAfter(nextReminderTime);
    }

    /**
     * 检查是否已完成今日目标
     */
    private boolean isDailyGoalReached(WaterReminderSettingEntity setting, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        LambdaQueryWrapper<WaterRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterRecordEntity::getUserId, setting.getUserId())
                .between(WaterRecordEntity::getRecordTime, startOfDay, endOfDay);

        List<WaterRecordEntity> records = waterRecordMapper.selectList(wrapper);

        int totalAmount = records.stream()
                .mapToInt(WaterRecordEntity::getAmount)
                .sum();

        return totalAmount >= setting.getDailyGoal();
    }

    /**
     * 发送饮水提醒
     */
    private void sendWaterReminder(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }

        String title = "💧 该喝水啦";
        String content = "记得补充水分哦，保持身体健康！点击记录今日饮水~";

        notificationService.sendNotification(userId, title, content, "WATER_REMINDER", null);

        log.info("已发送饮水提醒给用户: {}", userId);
    }

    /**
     * 每天早上8点发送早安饮水提醒
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void morningWaterReminder() {
        log.info("开始执行早安饮水提醒");

        LambdaQueryWrapper<WaterReminderSettingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterReminderSettingEntity::getReminderEnabled, true);
        List<WaterReminderSettingEntity> settings = reminderSettingMapper.selectList(wrapper);

        for (WaterReminderSettingEntity setting : settings) {
            try {
                String title = "🌅 早安！开启活力一天";
                String content = "早上好！起床后喝一杯温水，帮助身体排毒，开启美好的一天~";
                notificationService.sendNotification(setting.getUserId(), title, content, "WATER_REMINDER", null);
            } catch (Exception e) {
                log.error("早安饮水提醒发送失败, userId: {}", setting.getUserId(), e);
            }
        }

        log.info("早安饮水提醒发送完成");
    }
}
