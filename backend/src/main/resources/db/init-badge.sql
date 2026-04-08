-- 成就徽章系统表结构
-- 创建时间: 2026-04-08

-- 徽章定义表
CREATE TABLE IF NOT EXISTS `t_badge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '徽章ID',
    `code` VARCHAR(50) NOT NULL COMMENT '徽章编码(唯一)',
    `name` VARCHAR(100) NOT NULL COMMENT '徽章名称',
    `description` VARCHAR(255) COMMENT '徽章描述',
    `icon` VARCHAR(255) COMMENT '徽章图标URL',
    `category` VARCHAR(20) NOT NULL DEFAULT 'general' COMMENT '分类: general通用/weight体重/meal饮食/exercise运动/social社交',
    `rarity` VARCHAR(20) NOT NULL DEFAULT 'common' COMMENT '稀有度: common普通/rare稀有/epic史诗/legendary传说',
    `condition_type` VARCHAR(50) NOT NULL COMMENT '条件类型: consecutive_checkin连续打卡/weight_loss减重/exercise_count运动次数/meal_count饮食记录/first_moment首条动态/follow_count关注数/liked_count获赞数',
    `condition_value` INT NOT NULL DEFAULT 1 COMMENT '条件数值',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='徽章定义表';

-- 用户徽章关联表
CREATE TABLE IF NOT EXISTS `t_user_badge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `badge_id` BIGINT NOT NULL COMMENT '徽章ID',
    `acquire_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    `is_new` TINYINT NOT NULL DEFAULT 1 COMMENT '是否新获得: 0否 1是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_badge` (`user_id`, `badge_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_badge_id` (`badge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户徽章表';

-- 初始化徽章数据
INSERT INTO `t_badge` (`code`, `name`, `description`, `icon`, `category`, `rarity`, `condition_type`, `condition_value`, `sort_order`) VALUES
-- 连续打卡类
('checkin_3', '初出茅庐', '连续打卡3天', '🌱', 'general', 'common', 'consecutive_checkin', 3, 1),
('checkin_7', '坚持不懈', '连续打卡7天', '🌿', 'general', 'common', 'consecutive_checkin', 7, 2),
('checkin_30', '习惯养成', '连续打卡30天', '🌳', 'general', 'rare', 'consecutive_checkin', 30, 3),
('checkin_100', '百日达人', '连续打卡100天', '👑', 'general', 'epic', 'consecutive_checkin', 100, 4),
('checkin_365', '年度传奇', '连续打卡365天', '🏆', 'general', 'legendary', 'consecutive_checkin', 365, 5),

-- 体重记录类
('weight_first', '第一步', '首次记录体重', '📝', 'weight', 'common', 'weight_count', 1, 10),
('weight_10', '持续关注', '记录体重10次', '📊', 'weight', 'common', 'weight_count', 10, 11),
('weight_loss_5', '初见成效', '累计减重5斤', '🎯', 'weight', 'rare', 'weight_loss', 5, 12),
('weight_loss_10', '显著成果', '累计减重10斤', '🎉', 'weight', 'epic', 'weight_loss', 10, 13),
('weight_loss_20', '蜕变之路', '累计减重20斤', '🦋', 'weight', 'legendary', 'weight_loss', 20, 14),
('weight_goal', '目标达成', '达成减重目标', '🏁', 'weight', 'epic', 'weight_goal_achieved', 1, 15),

-- 运动类
('exercise_first', '动起来', '首次记录运动', '🏃', 'exercise', 'common', 'exercise_count', 1, 20),
('exercise_10', '运动爱好者', '记录运动10次', '⚡', 'exercise', 'common', 'exercise_count', 10, 21),
('exercise_50', '运动达人', '记录运动50次', '🔥', 'exercise', 'rare', 'exercise_count', 50, 22),
('exercise_100', '健身狂魔', '记录运动100次', '💪', 'exercise', 'epic', 'exercise_count', 100, 23),
('calories_1000', '千卡在握', '单日消耗1000千卡', '🔥', 'exercise', 'rare', 'calories_burned', 1000, 24),

-- 饮食类
('meal_first', '健康饮食', '首次记录饮食', '🥗', 'meal', 'common', 'meal_count', 1, 30),
('meal_30', '营养专家', '记录饮食30天', '🥑', 'meal', 'rare', 'meal_count', 30, 31),
('meal_100', '饮食大师', '记录饮食100天', '👨‍🍳', 'meal', 'epic', 'meal_count', 100, 32),
('calories_control', '热量掌控', '连续7天热量达标', '📏', 'meal', 'rare', 'calories_control', 7, 33),

-- 社交类
('moment_first', '分享快乐', '发布首条动态', '📢', 'social', 'common', 'first_moment', 1, 40),
('moment_10', '活跃分子', '发布10条动态', '📱', 'social', 'common', 'moment_count', 10, 41),
('follow_5', '社交达人', '关注5位好友', '👥', 'social', 'common', 'follow_count', 5, 42),
('liked_10', '受欢迎', '获得10个赞', '❤️', 'social', 'common', 'liked_count', 10, 43),
('liked_100', '人气王', '获得100个赞', '💎', 'social', 'rare', 'liked_count', 100, 44),

-- 特殊成就
('early_bird', '早起鸟', '早上6点前打卡', '🌅', 'general', 'rare', 'early_checkin', 1, 50),
('night_owl', '夜猫子', '晚上10点后打卡', '🌙', 'general', 'rare', 'late_checkin', 1, 51),
('perfect_week', '完美一周', '一周全部打卡', '📅', 'general', 'rare', 'perfect_week', 1, 52),
('comeback', '王者归来', '断签后连续打卡7天', '🚀', 'general', 'epic', 'comeback', 7, 53);
