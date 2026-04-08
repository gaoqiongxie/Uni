-- ============================================
-- AI智能助手相关表
-- ============================================

-- 用户画像表
CREATE TABLE IF NOT EXISTS `t_user_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    
    -- 基本信息
    `gender` TINYINT COMMENT '性别: 0-女, 1-男',
    `age` INT COMMENT '年龄',
    `height` DECIMAL(5,2) COMMENT '身高(cm)',
    `current_weight` DECIMAL(5,2) COMMENT '当前体重(kg)',
    `target_weight` DECIMAL(5,2) COMMENT '目标体重(kg)',
    
    -- 身体状况
    `body_type` VARCHAR(20) COMMENT '体型: slim-偏瘦, normal-正常, slightly_overweight-微胖, overweight-超重, obese-肥胖',
    `health_conditions` VARCHAR(500) COMMENT '健康状况(多选): diabetes-糖尿病, hypertension-高血压, thyroid-甲状腺问题, pcod-多囊卵巢, none-无',
    `allergies` VARCHAR(200) COMMENT '食物过敏',
    `dietary_restrictions` VARCHAR(200) COMMENT '饮食限制: vegetarian-素食, vegan-纯素, halal-清真, kosher-犹太洁食',
    
    -- 生活习惯
    `activity_level` VARCHAR(20) COMMENT '活动量: sedentary-久坐, light-轻度活动, moderate-中度活动, active-高度活跃, very_active-非常活跃',
    `sleep_hours` DECIMAL(3,1) COMMENT '平均睡眠时长(小时)',
    `stress_level` TINYINT COMMENT '压力水平: 1-5',
    `work_schedule` VARCHAR(20) COMMENT '工作作息: regular-规律, shift-轮班, night-夜班, irregular-不规律',
    
    -- 饮食偏好
    `food_preferences` VARCHAR(500) COMMENT '食物偏好(喜欢)',
    `food_dislikes` VARCHAR(500) COMMENT '食物厌恶',
    `cuisine_preference` VARCHAR(200) COMMENT '菜系偏好: chinese-中餐, western-西餐, japanese-日料, korean-韩餐, thai-泰餐',
    `cooking_skill` TINYINT COMMENT '烹饪水平: 1-5',
    `meal_prep_time` TINYINT COMMENT '备餐时间: 1-15分钟, 2-30分钟, 3-1小时, 4-不限制',
    
    -- 运动情况
    `exercise_experience` VARCHAR(20) COMMENT '运动经验: beginner-新手, intermediate-中级, advanced-高级',
    `preferred_exercise` VARCHAR(200) COMMENT '喜欢的运动类型',
    `exercise_time` VARCHAR(50) COMMENT '可运动时间',
    `has_gym_access` TINYINT COMMENT '是否有健身房: 0-否, 1-是',
    
    -- 减肥目标
    `target_timeline` VARCHAR(20) COMMENT '目标时间: 1month-1个月, 3months-3个月, 6months-6个月, 1year-1年',
    `primary_goal` VARCHAR(50) COMMENT '主要目标: weight_loss-减重, body_fat-减脂, muscle_gain-增肌, health-健康, energy-精力充沛',
    `motivation` VARCHAR(500) COMMENT '减肥动力',
    `past_attempts` VARCHAR(500) COMMENT '过往尝试经历',
    `biggest_challenge` VARCHAR(200) COMMENT '最大挑战: time-没时间, willpower-没毅力, knowledge-不懂方法, hunger-容易饿, social-社交应酬',
    
    -- 完成状态
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成画像: 0-否, 1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

-- AI对话记录表
CREATE TABLE IF NOT EXISTS `t_ai_chat_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: user-用户, assistant-AI',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `message_type` VARCHAR(20) DEFAULT 'text' COMMENT '消息类型: text-文字, plan-计划, tip-建议, reminder-提醒',
    `related_data` JSON COMMENT '关联数据(如生成的计划)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (`id`),
    KEY `idx_user_session` (`user_id`, `session_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录表';

-- 个性化减肥计划表
CREATE TABLE IF NOT EXISTS `t_personalized_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `plan_type` VARCHAR(20) NOT NULL COMMENT '计划类型: diet-饮食, exercise-运动, sleep-睡眠, comprehensive-综合',
    `plan_title` VARCHAR(100) NOT NULL COMMENT '计划标题',
    `plan_content` JSON NOT NULL COMMENT '计划内容',
    `daily_calorie_target` INT COMMENT '每日热量目标(kcal)',
    `daily_water_target` INT COMMENT '每日饮水目标(ml)',
    `daily_sleep_target` DECIMAL(3,1) COMMENT '每日睡眠目标(小时)',
    `weekly_exercise_days` TINYINT COMMENT '每周运动天数',
    `ai_analysis` TEXT COMMENT 'AI分析说明',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否启用: 0-否, 1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_plan_type` (`plan_type`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个性化减肥计划表';

-- AI每日建议表
CREATE TABLE IF NOT EXISTS `t_ai_daily_tip` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `tip_date` DATE NOT NULL COMMENT '日期',
    `tip_type` VARCHAR(20) COMMENT '建议类型: diet-饮食, exercise-运动, motivation-激励, sleep-睡眠, lifestyle-生活方式',
    `tip_content` TEXT NOT NULL COMMENT '建议内容',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0-否, 1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `tip_date`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI每日建议表';
