-- 创建数据库
CREATE DATABASE IF NOT EXISTS `uni_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `uni_db`;

-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `username` varchar(50) NOT NULL DEFAULT '' COMMENT '用户名',
  `phone` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码(加密存储)',
  `nickname` varchar(50) NOT NULL DEFAULT '' COMMENT '昵称',
  `avatar_url` varchar(500) NOT NULL DEFAULT '' COMMENT '头像URL',
  `gender` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别(0.未知,1.男,2.女)',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `height` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '身高(cm)',
  `current_weight` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '当前体重(kg)',
  `target_weight` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '目标体重(kg)',
  `bmi` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT 'BMI指数',
  `activity_level` tinyint(1) NOT NULL DEFAULT '1' COMMENT '活动等级(1.久坐,2.轻度活动,3.中度活动,4.重度活动)',
  `calorie_goal` int(11) NOT NULL DEFAULT '1400' COMMENT '每日热量目标(kcal)',
  `user_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户状态(1.正常,2.冻结,3.注销)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_phone` (`phone`),
  KEY `idx_username` (`username`),
  KEY `idx_status` (`user_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 体重记录表
CREATE TABLE IF NOT EXISTS `t_weight_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `record_date` date NOT NULL COMMENT '记录日期',
  `weight` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '体重(kg)',
  `body_fat_rate` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT '体脂率(%)',
  `muscle_rate` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT '肌肉率(%)',
  `water_rate` decimal(4,2) NOT NULL DEFAULT '0.00' COMMENT '水分率(%)',
  `waist_circumference` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '腰围(cm)',
  `hip_circumference` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '臀围(cm)',
  `record_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录类型(1.手动录入,2.智能设备同步)',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_date` (`user_id`, `record_date`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体重记录表';

-- 餐食打卡表
CREATE TABLE IF NOT EXISTS `t_meal_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `record_date` date NOT NULL COMMENT '记录日期',
  `meal_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '餐食类型(1.早餐,2.午餐,3.晚餐,4.加餐)',
  `meal_time` time NOT NULL COMMENT '用餐时间',
  `meal_content` varchar(500) NOT NULL DEFAULT '' COMMENT '餐食内容描述',
  `calorie_estimate` int(11) NOT NULL DEFAULT '0' COMMENT '热量估算(kcal)',
  `mood_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '心情类型(0.正常,1.开心,2.疲惫,3.压力大,4.其他)',
  `attachment_ids` varchar(500) NOT NULL DEFAULT '' COMMENT '附件id列表(逗号分隔)',
  `record_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录来源(1.手动录入,2.拍照识别)',
  `is_healthy` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否健康(0.不健康,1.健康)',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_meal_type` (`meal_type`),
  KEY `idx_user_date_type` (`user_id`, `record_date`, `meal_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐食打卡表';

-- 附件表
CREATE TABLE IF NOT EXISTS `t_attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `file_name` varchar(255) NOT NULL DEFAULT '' COMMENT '文件名',
  `file_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件大小(字节)',
  `file_type` varchar(50) NOT NULL DEFAULT '' COMMENT '文件类型',
  `file_extension` varchar(20) NOT NULL DEFAULT '' COMMENT '文件扩展名',
  `storage_path` varchar(500) NOT NULL DEFAULT '' COMMENT '存储路径',
  `oss_url` varchar(500) NOT NULL DEFAULT '' COMMENT 'OSS访问URL',
  `thumbnail_url` varchar(500) NOT NULL DEFAULT '' COMMENT '缩略图URL',
  `attachment_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '附件类型(1.餐食图片,2.体重照片,3.运动照片,4.其他)',
  `upload_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '上传来源(1.手机拍照,2.相册选择)',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_attachment_type` (`attachment_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件表';

-- 运动记录表
CREATE TABLE IF NOT EXISTS `t_exercise_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `record_date` date NOT NULL COMMENT '记录日期',
  `exercise_type` varchar(50) NOT NULL DEFAULT '' COMMENT '运动类型(跑步/游泳/瑜伽/力量训练/骑行/跳绳/散步/其他)',
  `duration` int(11) NOT NULL DEFAULT '0' COMMENT '运动时长(分钟)',
  `calorie_burn` int(11) NOT NULL DEFAULT '0' COMMENT '消耗热量(kcal)',
  `distance` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '运动距离(km)',
  `heart_rate_avg` int(11) NOT NULL DEFAULT '0' COMMENT '平均心率(bpm)',
  `heart_rate_max` int(11) NOT NULL DEFAULT '0' COMMENT '最大心率(bpm)',
  `intensity` tinyint(1) NOT NULL DEFAULT '2' COMMENT '运动强度(1.低强度,2.中等强度,3.高强度)',
  `feeling` tinyint(1) NOT NULL DEFAULT '0' COMMENT '运动感受(0.一般,1.轻松,2.适中,3.吃力,4.很累)',
  `attachment_ids` varchar(500) NOT NULL DEFAULT '' COMMENT '附件id列表(逗号分隔)',
  `record_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '记录来源(1.手动录入,2.智能设备同步)',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_exercise_type` (`exercise_type`),
  KEY `idx_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动记录表';

-- 食谱表
CREATE TABLE IF NOT EXISTS `t_recipe` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(100) NOT NULL COMMENT '食谱名称',
  `category` varchar(50) NOT NULL DEFAULT '' COMMENT '分类(早餐/午餐/晚餐/加餐/低卡/高蛋白)',
  `description` varchar(500) NOT NULL DEFAULT '' COMMENT '食谱简介',
  `cover_image` varchar(500) NOT NULL DEFAULT '' COMMENT '封面图URL',
  `calorie` int(11) NOT NULL DEFAULT '0' COMMENT '总热量(kcal)',
  `protein` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '蛋白质(g)',
  `fat` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '脂肪(g)',
  `carbohydrate` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '碳水化合物(g)',
  `fiber` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '膳食纤维(g)',
  `cooking_time` int(11) NOT NULL DEFAULT '0' COMMENT '烹饪时间(分钟)',
  `difficulty` tinyint(1) NOT NULL DEFAULT '1' COMMENT '难度(1.简单,2.中等,3.困难)',
  `steps` text COMMENT '做法步骤(JSON数组)',
  `tags` varchar(500) NOT NULL DEFAULT '' COMMENT '标签(逗号分隔,如减脂,高蛋白)',
  `servings` int(11) NOT NULL DEFAULT '1' COMMENT '份数',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态(0.下架,1.上架)',
  `create_user` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人Ad',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) NOT NULL DEFAULT '' COMMENT '修改人Ad',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记(0.未删除,1.已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_calorie` (`calorie`),
  KEY `idx_status` (`status`),
  KEY `idx_tags` (`tags`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱表';

-- 食材表(食谱-食材关联)
CREATE TABLE IF NOT EXISTS `t_recipe_ingredient` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `recipe_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '食谱id',
  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '食材名称',
  `amount` varchar(50) NOT NULL DEFAULT '' COMMENT '用量(如200g、2个)',
  `calorie` int(11) NOT NULL DEFAULT '0' COMMENT '食材热量(kcal)',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_recipe_id` (`recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食材表';

-- 用户收藏食谱表
CREATE TABLE IF NOT EXISTS `t_user_favorite_recipe` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `recipe_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '食谱id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_recipe` (`user_id`, `recipe_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_recipe_id` (`recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏食谱表';
