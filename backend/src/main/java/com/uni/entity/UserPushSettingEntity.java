package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户推送设置实体
 */
@Data
@TableName("t_user_push_setting")
public class UserPushSettingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户id */
    private Long userId;

    /** 餐食打卡提醒(0.关闭,1.开启) */
    private Integer mealReminder;

    /** 提醒时间(逗号分隔) */
    private String mealReminderTime;

    /** 运动打卡提醒(0.关闭,1.开启) */
    private Integer exerciseReminder;

    /** 运动提醒时间 */
    private String exerciseReminderTime;

    /** 周报推送(0.关闭,1.开启) */
    private Integer weeklyReport;

    /** 目标达成提醒(0.关闭,1.开启) */
    private Integer goalAchievement;

    /** 系统公告(0.关闭,1.开启) */
    private Integer systemNotice;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
