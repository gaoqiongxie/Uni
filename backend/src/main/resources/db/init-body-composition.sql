-- ===============================================
-- 轻瘦 App - 身体成分记录表
-- Sprint 11 - 周一体脂率 & 身体成分追踪
-- 创建时间: 2026-04-13
-- ===============================================

-- 如果表已存在则删除
DROP TABLE IF EXISTS `t_body_composition`;

-- 身体成分记录表
CREATE TABLE `t_body_composition` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `record_date` DATE NOT NULL COMMENT '记录日期',
    `height` DECIMAL(5,1) DEFAULT NULL COMMENT '身高(cm)',
    `weight` DECIMAL(5,1) NOT NULL COMMENT '体重(kg)',
    `bmi` DECIMAL(4,1) DEFAULT NULL COMMENT 'BMI指数',
    `body_fat_rate` DECIMAL(4,1) DEFAULT NULL COMMENT '体脂率(%)',
    `fat_level` VARCHAR(20) DEFAULT NULL COMMENT '体脂等级: lean(偏瘦)/normal(正常)/high(偏高)/obese(肥胖)',
    `muscle_mass` DECIMAL(5,1) DEFAULT NULL COMMENT '肌肉量(kg)',
    `bone_mass` DECIMAL(4,1) DEFAULT NULL COMMENT '骨量(kg)',
    `water_rate` DECIMAL(4,1) DEFAULT NULL COMMENT '水分率(%)',
    `visceral_fat` DECIMAL(4,1) DEFAULT NULL COMMENT '内脏脂肪等级',
    `waist` DECIMAL(5,1) DEFAULT NULL COMMENT '腰围(cm)',
    `neck` DECIMAL(5,1) DEFAULT NULL COMMENT '颈围(cm)',
    `hip` DECIMAL(5,1) DEFAULT NULL COMMENT '臀围(cm,女性用)',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别: male/female',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `record_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='身体成分记录表';

-- ===============================================
-- 初始化测试数据（可选）
-- ===============================================

-- 插入一条示例记录（用户ID=1）
-- INSERT INTO `t_body_composition` (`user_id`, `record_date`, `height`, `weight`, `bmi`, `body_fat_rate`, `fat_level`, `gender`)
-- VALUES (1, CURDATE(), 165.0, 60.0, 22.0, 25.5, 'normal', 'female');
