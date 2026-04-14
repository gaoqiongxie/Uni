-- 打卡挑战活动相关表初始化脚本
-- 创建时间: 2026-04-14
-- 作者: 高琼

-- 挑战活动表
CREATE TABLE IF NOT EXISTS `t_challenge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(100) NOT NULL COMMENT '挑战标题',
    `description` VARCHAR(500) COMMENT '挑战描述',
    `type` VARCHAR(30) NOT NULL COMMENT '挑战类型: weight-体重,exercise-运动,meal-饮食,water-饮水,custom-自定义',
    `target_value` DECIMAL(8,2) COMMENT '目标值',
    `target_unit` VARCHAR(20) COMMENT '目标单位',
    `duration_days` INT NOT NULL COMMENT '挑战天数',
    `cover_url` VARCHAR(500) COMMENT '封面图URL',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-下线,1-上线',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `joined_count` INT DEFAULT 0 COMMENT '参与人数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挑战活动表';

-- 用户参与挑战表
CREATE TABLE IF NOT EXISTS `t_user_challenge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `challenge_id` BIGINT NOT NULL COMMENT '挑战ID',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-放弃,1-进行中,2-已完成',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `checkin_count` INT DEFAULT 0 COMMENT '已打卡天数',
    `streak_days` INT DEFAULT 0 COMMENT '连续打卡天数',
    `complete_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '完成率(%)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_challenge` (`user_id`, `challenge_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_challenge_id` (`challenge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户参与挑战表';

-- 挑战打卡记录表
CREATE TABLE IF NOT EXISTS `t_challenge_checkin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `challenge_id` BIGINT NOT NULL COMMENT '挑战ID',
    `user_challenge_id` BIGINT NOT NULL COMMENT '用户挑战ID',
    `checkin_date` DATE NOT NULL COMMENT '打卡日期',
    `actual_value` DECIMAL(8,2) COMMENT '实际完成值',
    `note` VARCHAR(300) COMMENT '打卡备注',
    `photo_url` VARCHAR(500) COMMENT '打卡图片',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_challenge_date` (`user_id`, `challenge_id`, `checkin_date`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_checkin_date` (`checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挑战打卡记录表';

-- 初始化挑战活动数据
INSERT INTO `t_challenge` (`title`, `description`, `type`, `target_value`, `target_unit`, `duration_days`, `status`, `sort_order`) VALUES
('21天轻断食挑战', '坚持21天，每天控制摄入在1400千卡以内，养成健康饮食习惯', 'meal', 1400.00, 'kcal', 21, 1, 1),
('30天每日运动挑战', '30天不间断，每天至少运动30分钟，让运动成为习惯', 'exercise', 30.00, '分钟', 30, 1, 2),
('7天饮水达标挑战', '连续7天每天喝水2000ml以上，培养健康饮水习惯', 'water', 2000.00, 'ml', 7, 1, 3),
('14天减重1kg挑战', '通过合理饮食和运动，14天减重1公斤', 'weight', 1.00, 'kg', 14, 1, 4),
('100天健康打卡', '坚持100天，每天打卡记录饮食，培养健康意识', 'meal', NULL, NULL, 100, 1, 5),
('每日万步挑战', '每天步行10000步，增强心肺功能', 'exercise', 10000.00, '步', 30, 1, 6);
