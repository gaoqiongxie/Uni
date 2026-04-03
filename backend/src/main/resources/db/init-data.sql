-- 轻瘦App 初始化数据
-- 执行顺序: 先执行 init.sql 创建表，再执行此文件导入数据

USE `uni_db`;

-- 插入示例用户 (密码: 123456，加密后)
-- 注意: 生产环境请使用真实加密密码
INSERT INTO `t_user` (`username`, `phone`, `password`, `nickname`, `gender`, `height`, `current_weight`, `target_weight`, `bmi`, `calorie_goal`, `user_status`, `create_user`, `create_time`) VALUES
('demo', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '轻瘦用户', 2, 165.00, 65.00, 55.00, 23.90, 1400, 1, 'system', NOW());

-- 插入体重记录示例数据
INSERT INTO `t_weight_record` (`user_id`, `weight`, `record_date`, `remark`, `create_user`, `create_time`) VALUES
(1, 65.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY), '开始记录', 'system', NOW()),
(1, 64.50, DATE_SUB(CURDATE(), INTERVAL 23 DAY), '', 'system', NOW()),
(1, 64.20, DATE_SUB(CURDATE(), INTERVAL 16 DAY), '坚持运动', 'system', NOW()),
(1, 63.80, DATE_SUB(CURDATE(), INTERVAL 9 DAY), '', 'system', NOW()),
(1, 63.50, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '效果很好', 'system', NOW()),
(1, 63.20, CURDATE(), '继续加油', 'system', NOW());

-- 插入餐食记录示例数据
INSERT INTO `t_meal_record` (`user_id`, `meal_type`, `record_date`, `food_name`, `calorie`, `image_url`, `remark`, `create_user`, `create_time`) VALUES
(1, 1, CURDATE(), '燕麦牛奶粥', 280, '', '早餐', 'system', NOW()),
(1, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '全麦面包+鸡蛋', 320, '', '', 'system', NOW()),
(1, 2, CURDATE(), '鸡胸肉沙拉', 450, '', '午餐', 'system', NOW()),
(1, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '糙米饭+清蒸鱼', 520, '', '', 'system', NOW()),
(1, 3, CURDATE(), '蔬菜汤', 180, '', '晚餐', 'system', NOW()),
(1, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '豆腐+青菜', 220, '', '', 'system', NOW());

-- 插入运动记录示例数据
INSERT INTO `t_exercise_record` (`user_id`, `exercise_type`, `exercise_name`, `duration`, `calorie_burned`, `record_date`, `intensity`, `remark`, `create_user`, `create_time`) VALUES
(1, 1, '慢跑', 30, 280, CURDATE(), 2, '早晨跑步', 'system', NOW()),
(1, 1, '快走', 20, 120, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, '饭后散步', 'system', NOW()),
(1, 2, '瑜伽', 45, 180, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 1, '睡前放松', 'system', NOW()),
(1, 3, '跳绳', 15, 200, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 3, '高强度', 'system', NOW());

-- 插入目标记录示例数据
INSERT INTO `t_user_goal` (`user_id`, `goal_type`, `start_weight`, `target_weight`, `calorie_goal`, `exercise_days_per_week`, `exercise_minutes_per_day`, `start_date`, `target_date`, `status`, `remark`, `create_user`, `create_time`) VALUES
(1, 1, 65.00, 55.00, 1400, 4, 30, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 1, '健康减脂，遇见更好的自己', 'system', NOW());
