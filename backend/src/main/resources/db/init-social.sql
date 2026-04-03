-- ============================================================
-- 社交功能表结构
-- 动态/关注/点赞/评论
-- ============================================================

-- 用户动态表
CREATE TABLE IF NOT EXISTS `t_moment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '发布用户ID',
  `content` text COMMENT '动态内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片URL，逗号分隔',
  `location` varchar(200) DEFAULT NULL COMMENT '位置信息',
  `related_type` varchar(50) DEFAULT NULL COMMENT '关联类型：WEIGHT/MEAL/EXERCISE/GOAL',
  `related_id` bigint DEFAULT NULL COMMENT '关联记录ID',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `is_public` tinyint DEFAULT '1' COMMENT '是否公开：0-仅自己 1-公开 2-好友可见',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-删除 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户动态表';

-- 用户关注表
CREATE TABLE IF NOT EXISTS `t_user_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `follow_user_id` bigint NOT NULL COMMENT '被关注用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`, `follow_user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_follow_user_id` (`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- 动态点赞表
CREATE TABLE IF NOT EXISTS `t_moment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `moment_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_moment_user` (`moment_id`, `user_id`),
  KEY `idx_moment_id` (`moment_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞表';

-- 动态评论表
CREATE TABLE IF NOT EXISTS `t_moment_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `moment_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID，0为一级评论',
  `reply_user_id` bigint DEFAULT NULL COMMENT '回复用户ID',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-删除 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_moment_id` (`moment_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态评论表';
