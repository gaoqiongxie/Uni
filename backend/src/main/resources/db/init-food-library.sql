-- 食物热量数据库表初始化脚本
-- 创建时间: 2026-04-13
-- 作者: 高琼

-- 食物库表
CREATE TABLE IF NOT EXISTS `t_food_library` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `food_name` VARCHAR(100) NOT NULL COMMENT '食物名称',
    `category` VARCHAR(50) NOT NULL COMMENT '食物分类: staple-主食,meat-肉蛋奶,vegetable-蔬菜,fruit-水果,snack-零食,drink-饮品,other-其他',
    `calories_per_100g` DECIMAL(8,2) NOT NULL COMMENT '每100克热量(千卡)',
    `protein_per_100g` DECIMAL(6,2) DEFAULT 0 COMMENT '每100克蛋白质(g)',
    `fat_per_100g` DECIMAL(6,2) DEFAULT 0 COMMENT '每100克脂肪(g)',
    `carbs_per_100g` DECIMAL(6,2) DEFAULT 0 COMMENT '每100克碳水化合物(g)',
    `unit` VARCHAR(20) DEFAULT '克' COMMENT '计量单位',
    `unit_weight` DECIMAL(6,2) DEFAULT 100 COMMENT '单位重量(g)',
    `common_portion` VARCHAR(50) COMMENT '常见份量描述',
    `is_common` TINYINT(1) DEFAULT 1 COMMENT '是否常用: 0-否,1-是',
    `icon_url` VARCHAR(500) COMMENT '食物图标URL',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_is_common` (`is_common`),
    INDEX `idx_food_name` (`food_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食物热量库表';

-- 用户每日食物摄入记录表
CREATE TABLE IF NOT EXISTS `t_food_daily_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `record_date` DATE NOT NULL COMMENT '记录日期',
    `meal_type` VARCHAR(20) NOT NULL COMMENT '餐次: breakfast-早餐,lunch-午餐,dinner-晚餐,snack-加餐',
    `food_id` BIGINT COMMENT '食物库ID(关联t_food_library)',
    `food_name` VARCHAR(100) NOT NULL COMMENT '食物名称',
    `calories` DECIMAL(8,2) NOT NULL COMMENT '摄入热量(千卡)',
    `amount` DECIMAL(8,2) NOT NULL COMMENT '摄入量',
    `unit` VARCHAR(20) NOT NULL COMMENT '单位',
    `protein` DECIMAL(6,2) DEFAULT 0 COMMENT '蛋白质(g)',
    `fat` DECIMAL(6,2) DEFAULT 0 COMMENT '脂肪(g)',
    `carbs` DECIMAL(6,2) DEFAULT 0 COMMENT '碳水化合物(g)',
    `photo_url` VARCHAR(500) COMMENT '照片URL',
    `remark` VARCHAR(200) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_record_date` (`record_date`),
    INDEX `idx_meal_type` (`meal_type`),
    INDEX `idx_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户每日食物摄入记录表';

-- 插入常用食物数据
INSERT INTO `t_food_library` (`food_name`, `category`, `calories_per_100g`, `protein_per_100g`, `fat_per_100g`, `carbs_per_100g`, `unit`, `unit_weight`, `common_portion`, `is_common`) VALUES
-- 主食类
('米饭', 'staple', 116.00, 2.60, 0.30, 25.90, '碗', 150.00, '1碗(150g)', 1),
('馒头', 'staple', 223.00, 7.00, 1.10, 47.00, '个', 100.00, '1个(100g)', 1),
('面条', 'staple', 137.00, 4.50, 0.70, 28.50, '碗', 200.00, '1碗(200g)', 1),
('燕麦片', 'staple', 377.00, 13.50, 6.70, 66.90, '勺', 30.00, '1勺(30g)', 1),
('全麦面包', 'staple', 246.00, 8.50, 3.50, 43.00, '片', 35.00, '1片(35g)', 1),
('玉米', 'staple', 112.00, 4.00, 1.20, 22.80, '根', 200.00, '1根(200g)', 1),
('红薯', 'staple', 86.00, 1.60, 0.10, 20.10, '个', 150.00, '1个(150g)', 1),
('紫薯', 'staple', 82.00, 1.30, 0.20, 18.00, '个', 150.00, '1个(150g)', 1),
('小米粥', 'staple', 46.00, 1.40, 0.70, 8.40, '碗', 250.00, '1碗(250g)', 1),
('饺子', 'staple', 218.00, 8.50, 9.80, 24.00, '个', 20.00, '1个(20g)', 1),

-- 肉蛋奶类
('鸡蛋', 'meat', 144.00, 13.30, 8.80, 2.80, '个', 50.00, '1个(50g)', 1),
('鸡胸肉', 'meat', 133.00, 19.40, 5.00, 2.50, '块', 150.00, '1块(150g)', 1),
('牛肉', 'meat', 125.00, 20.20, 4.20, 2.00, '块', 100.00, '1块(100g)', 1),
('猪瘦肉', 'meat', 143.00, 20.30, 6.20, 1.50, '块', 100.00, '1块(100g)', 1),
('鱼肉', 'meat', 113.00, 18.50, 4.00, 0.00, '块', 150.00, '1块(150g)', 1),
('虾仁', 'meat', 93.00, 20.40, 0.70, 0.00, '份', 100.00, '1份(100g)', 1),
('牛奶', 'meat', 54.00, 3.00, 3.20, 3.40, '杯', 250.00, '1杯(250ml)', 1),
('酸奶', 'meat', 72.00, 2.50, 2.70, 9.00, '盒', 100.00, '1盒(100g)', 1),
('豆腐', 'meat', 76.00, 8.10, 3.70, 3.40, '块', 150.00, '1块(150g)', 1),
('火腿肠', 'meat', 212.00, 11.30, 18.00, 2.00, '根', 40.00, '1根(40g)', 1),

-- 蔬菜类
('西红柿', 'vegetable', 18.00, 0.90, 0.20, 3.90, '个', 150.00, '1个(150g)', 1),
('黄瓜', 'vegetable', 16.00, 0.80, 0.20, 2.90, '根', 150.00, '1根(150g)', 1),
('菠菜', 'vegetable', 23.00, 2.90, 0.40, 3.60, '份', 100.00, '1份(100g)', 1),
('西兰花', 'vegetable', 34.00, 2.80, 0.40, 7.00, '份', 100.00, '1份(100g)', 1),
('胡萝卜', 'vegetable', 41.00, 0.90, 0.20, 9.60, '根', 100.00, '1根(100g)', 1),
('白菜', 'vegetable', 13.00, 1.50, 0.10, 2.40, '份', 100.00, '1份(100g)', 1),
('芹菜', 'vegetable', 14.00, 0.80, 0.10, 3.90, '份', 100.00, '1份(100g)', 1),
('茄子', 'vegetable', 23.00, 1.00, 0.20, 5.50, '个', 200.00, '1个(200g)', 1),
('青椒', 'vegetable', 22.00, 1.00, 0.20, 5.40, '个', 80.00, '1个(80g)', 1),
('生菜', 'vegetable', 13.00, 1.30, 0.30, 2.00, '份', 100.00, '1份(100g)', 1),

-- 水果类
('苹果', 'fruit', 52.00, 0.30, 0.20, 13.80, '个', 200.00, '1个(200g)', 1),
('香蕉', 'fruit', 89.00, 1.10, 0.30, 22.80, '根', 120.00, '1根(120g)', 1),
('橙子', 'fruit', 47.00, 0.90, 0.10, 11.80, '个', 150.00, '1个(150g)', 1),
('草莓', 'fruit', 32.00, 0.70, 0.30, 7.70, '份', 100.00, '1份(100g)', 1),
('蓝莓', 'fruit', 57.00, 0.70, 0.30, 14.50, '盒', 125.00, '1盒(125g)', 1),
('西瓜', 'fruit', 30.00, 0.60, 0.20, 7.60, '块', 200.00, '1块(200g)', 1),
('葡萄', 'fruit', 69.00, 0.70, 0.20, 18.10, '串', 150.00, '1串(150g)', 1),
('猕猴桃', 'fruit', 61.00, 1.10, 0.50, 14.50, '个', 100.00, '1个(100g)', 1),
('火龙果', 'fruit', 51.00, 1.10, 0.40, 11.30, '个', 300.00, '1个(300g)', 1),
('梨', 'fruit', 57.00, 0.40, 0.10, 15.50, '个', 200.00, '1个(200g)', 1),

-- 零食类
('坚果', 'snack', 607.00, 20.00, 54.00, 20.00, '把', 30.00, '1把(30g)', 1),
('薯片', 'snack', 536.00, 7.00, 35.00, 53.00, '包', 50.00, '1包(50g)', 1),
('巧克力', 'snack', 546.00, 4.90, 31.00, 61.00, '块', 20.00, '1块(20g)', 1),
('饼干', 'snack', 502.00, 7.00, 25.00, 64.00, '片', 15.00, '1片(15g)', 1),
('蛋糕', 'snack', 371.00, 3.20, 15.00, 54.00, '块', 80.00, '1块(80g)', 1),

-- 饮品类
('可乐', 'drink', 42.00, 0.00, 0.00, 10.60, '罐', 330.00, '1罐(330ml)', 1),
('橙汁', 'drink', 45.00, 0.70, 0.20, 10.40, '杯', 250.00, '1杯(250ml)', 1),
('咖啡(美式)', 'drink', 2.00, 0.30, 0.00, 0.30, '杯', 250.00, '1杯(250ml)', 1),
('咖啡(拿铁)', 'drink', 43.00, 2.80, 2.30, 3.50, '杯', 250.00, '1杯(250ml)', 1),
('绿茶', 'drink', 1.00, 0.10, 0.00, 0.20, '杯', 250.00, '1杯(250ml)', 1),
('啤酒', 'drink', 43.00, 0.50, 0.00, 3.60, '瓶', 500.00, '1瓶(500ml)', 1);
