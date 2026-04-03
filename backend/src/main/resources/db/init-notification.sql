-- 消息通知模块DDL

-- 用户消息表
CREATE TABLE IF NOT EXISTS `t_user_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `message_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '消息类型(1.打卡提醒,2.目标达成,3.系统公告,4.周报推送)',
  `title` varchar(100) NOT NULL DEFAULT '' COMMENT '消息标题',
  `content` varchar(500) NOT NULL DEFAULT '' COMMENT '消息内容',
  `extra_data` json DEFAULT NULL COMMENT '扩展数据(JSON)',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读(0.未读,1.已读)',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_message_type` (`message_type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表';

-- 用户推送设置表
CREATE TABLE IF NOT EXISTS `t_user_push_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `meal_reminder` tinyint(1) NOT NULL DEFAULT '1' COMMENT '餐食打卡提醒(0.关闭,1.开启)',
  `meal_reminder_time` varchar(20) NOT NULL DEFAULT '08:00,12:00,18:00' COMMENT '提醒时间(逗号分隔)',
  `exercise_reminder` tinyint(1) NOT NULL DEFAULT '1' COMMENT '运动打卡提醒(0.关闭,1.开启)',
  `exercise_reminder_time` varchar(20) NOT NULL DEFAULT '20:00' COMMENT '运动提醒时间',
  `weekly_report` tinyint(1) NOT NULL DEFAULT '1' COMMENT '周报推送(0.关闭,1.开启)',
  `goal_achievement` tinyint(1) NOT NULL DEFAULT '1' COMMENT '目标达成提醒(0.关闭,1.开启)',
  `system_notice` tinyint(1) NOT NULL DEFAULT '1' COMMENT '系统公告(0.关闭,1.开启)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户推送设置表';
