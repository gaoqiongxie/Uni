-- ============================================
-- 主题设置表
-- ============================================

CREATE TABLE IF NOT EXISTS `t_user_theme` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `theme_mode` TINYINT NOT NULL DEFAULT 0 COMMENT '主题模式: 0-跟随系统, 1-浅色, 2-深色',
    `primary_color` VARCHAR(20) DEFAULT '#667eea' COMMENT '主题色',
    `font_size` TINYINT DEFAULT 1 COMMENT '字体大小: 0-小, 1-中, 2-大',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户主题设置表';

-- 初始化数据（可选：为现有用户创建默认设置）
-- INSERT INTO t_user_theme (user_id, theme_mode) SELECT id, 0 FROM t_user;
