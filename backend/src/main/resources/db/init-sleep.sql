-- 睡眠记录表
CREATE TABLE IF NOT EXISTS `t_sleep_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `sleep_date` DATE NOT NULL COMMENT '睡眠日期',
    `bed_time` DATETIME NOT NULL COMMENT '上床时间',
    `wake_time` DATETIME NOT NULL COMMENT '起床时间',
    `sleep_duration` INT NOT NULL COMMENT '睡眠时长(分钟)',
    `deep_sleep` INT DEFAULT NULL COMMENT '深睡时长(分钟)',
    `light_sleep` INT DEFAULT NULL COMMENT '浅睡时长(分钟)',
    `awake_duration` INT DEFAULT 0 COMMENT '清醒时长(分钟)',
    `sleep_quality` TINYINT DEFAULT 3 COMMENT '睡眠质量 1-很差 2-较差 3-一般 4-较好 5-很好',
    `sleep_score` INT DEFAULT NULL COMMENT '睡眠评分 0-100',
    `factors` VARCHAR(255) DEFAULT NULL COMMENT '影响因素,逗号分隔: caffeine,screen,exercise,stress,late_meal',
    `notes` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sleep_date` (`user_id`, `sleep_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_sleep_date` (`sleep_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='睡眠记录表';

-- 睡眠目标设置表
CREATE TABLE IF NOT EXISTS `t_sleep_goal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_duration` INT NOT NULL DEFAULT 480 COMMENT '目标睡眠时长(分钟),默认8小时',
    `target_bed_time` VARCHAR(10) DEFAULT '23:00' COMMENT '目标就寝时间',
    `target_wake_time` VARCHAR(10) DEFAULT '07:00' COMMENT '目标起床时间',
    `reminder_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否开启提醒 0-否 1-是',
    `bed_reminder` TINYINT NOT NULL DEFAULT 1 COMMENT '就寝提醒 0-否 1-是',
    `wake_reminder` TINYINT NOT NULL DEFAULT 1 COMMENT '起床提醒 0-否 1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='睡眠目标设置表';
