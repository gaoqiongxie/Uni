-- 饮水记录表
CREATE TABLE IF NOT EXISTS `t_water_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `amount` INT NOT NULL DEFAULT 0 COMMENT '饮水量(ml)',
  `drink_time` TIME NOT NULL COMMENT '饮水时间',
  `drink_type` VARCHAR(20) DEFAULT 'water' COMMENT '饮品类型: water-水, tea-茶, coffee-咖啡, juice-果汁, milk-牛奶, other-其他',
  `remark` VARCHAR(100) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `record_date`),
  KEY `idx_user_time` (`user_id`, `drink_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮水记录表';

-- 饮水提醒设置表
CREATE TABLE IF NOT EXISTS `t_water_reminder_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否开启提醒: 0-关闭, 1-开启',
  `daily_goal` INT NOT NULL DEFAULT 2000 COMMENT '每日目标饮水量(ml)',
  `reminder_interval` INT NOT NULL DEFAULT 60 COMMENT '提醒间隔(分钟)',
  `start_time` TIME NOT NULL DEFAULT '08:00:00' COMMENT '提醒开始时间',
  `end_time` TIME NOT NULL DEFAULT '22:00:00' COMMENT '提醒结束时间',
  `reminder_sound` TINYINT NOT NULL DEFAULT 1 COMMENT '提醒声音: 0-关闭, 1-开启',
  `reminder_vibration` TINYINT NOT NULL DEFAULT 1 COMMENT '提醒震动: 0-关闭, 1-开启',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮水提醒设置表';

-- 初始化数据: 默认饮品类型配置
INSERT INTO `t_water_record` (`user_id`, `record_date`, `amount`, `drink_time`, `drink_type`, `remark`, `create_time`) VALUES
(1, CURDATE(), 250, '08:30:00', 'water', '早起一杯水', NOW()),
(1, CURDATE(), 200, '10:00:00', 'tea', '上午茶', NOW())
ON DUPLICATE KEY UPDATE `update_time` = NOW();
