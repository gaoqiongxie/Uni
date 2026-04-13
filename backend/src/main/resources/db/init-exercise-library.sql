-- 运动库表初始化脚本
-- 创建时间: 2026-04-13
-- 作者: 高琼

-- 运动库表
CREATE TABLE IF NOT EXISTS `t_exercise_library` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exercise_name` VARCHAR(100) NOT NULL COMMENT '运动名称',
    `category` VARCHAR(50) NOT NULL COMMENT '运动分类: aerobic-有氧运动,strength-力量训练,flexibility-柔韧性训练,sports-球类运动,daily-日常活动,other-其他',
    `met_value` DECIMAL(4,2) NOT NULL COMMENT 'MET值(代谢当量)',
    `intensity` VARCHAR(20) COMMENT '强度: low-低,moderate-中,high-高',
    `description` VARCHAR(500) COMMENT '运动描述',
    `icon_url` VARCHAR(500) COMMENT '运动图标URL',
    `is_common` TINYINT(1) DEFAULT 1 COMMENT '是否常用: 0-否,1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_is_common` (`is_common`),
    INDEX `idx_exercise_name` (`exercise_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动库表';

-- 插入常用运动数据
INSERT INTO `t_exercise_library` (`exercise_name`, `category`, `met_value`, `intensity`, `description`, `is_common`) VALUES
-- 有氧运动
('快走', 'aerobic', 3.50, 'low', '中等速度步行，适合日常锻炼', 1),
('慢跑', 'aerobic', 7.00, 'moderate', '轻松跑步，保持呼吸平稳', 1),
('快跑', 'aerobic', 11.50, 'high', '高强度跑步，心率较高', 1),
('游泳', 'aerobic', 8.00, 'moderate', '全身运动，对关节友好', 1),
('骑自行车', 'aerobic', 7.50, 'moderate', '户外或室内骑行', 1),
('跳绳', 'aerobic', 10.00, 'high', '高效燃脂运动', 1),
('椭圆机', 'aerobic', 5.00, 'moderate', '健身房常用有氧器械', 1),
('划船机', 'aerobic', 7.00, 'moderate', '全身性有氧训练', 1),
('健身操', 'aerobic', 6.50, 'moderate', '跟随音乐节奏的有氧运动', 1),
('爬楼梯', 'aerobic', 8.00, 'moderate', '利用楼梯进行有氧训练', 1),
('跳舞', 'aerobic', 5.00, 'moderate', '各类舞蹈运动', 1),
('瑜伽', 'aerobic', 2.50, 'low', '身心放松的轻度运动', 1),
('普拉提', 'aerobic', 3.00, 'low', '核心训练与柔韧性结合', 1),
('动感单车', 'aerobic', 8.50, 'high', '室内高强度骑行课程', 1),
('有氧搏击', 'aerobic', 10.00, 'high', '结合拳击动作的有氧运动', 1),

-- 力量训练
('深蹲', 'strength', 5.00, 'moderate', '下肢力量训练经典动作', 1),
('俯卧撑', 'strength', 4.00, 'moderate', '上肢与核心力量训练', 1),
('仰卧起坐', 'strength', 3.50, 'moderate', '腹肌训练经典动作', 1),
('平板支撑', 'strength', 3.50, 'moderate', '核心稳定性训练', 1),
('哑铃训练', 'strength', 4.50, 'moderate', '使用哑铃的力量训练', 1),
('杠铃训练', 'strength', 6.00, 'high', '大重量力量训练', 1),
('引体向上', 'strength', 5.00, 'high', '背部与上肢力量训练', 1),
('硬拉', 'strength', 6.00, 'high', '全身性力量训练', 1),
('卧推', 'strength', 5.00, 'high', '胸部力量训练', 1),
('器械训练', 'strength', 4.00, 'moderate', '健身房固定器械训练', 1),

-- 球类运动
('篮球', 'sports', 6.50, 'high', '团队球类运动', 1),
('足球', 'sports', 7.00, 'high', '户外团队运动', 1),
('羽毛球', 'sports', 5.50, 'moderate', '室内/室外球类运动', 1),
('乒乓球', 'sports', 4.00, 'moderate', '室内小球运动', 1),
('网球', 'sports', 7.00, 'high', '需要较大场地的球类运动', 1),
('排球', 'sports', 4.00, 'moderate', '团队球类运动', 1),

-- 日常活动
('散步', 'daily', 2.50, 'low', '轻松步行，日常活动', 1),
('站立办公', 'daily', 1.50, 'low', '站立工作消耗', 1),
('家务劳动', 'daily', 2.50, 'low', '打扫卫生、整理房间', 1),
('园艺', 'daily', 3.50, 'low', '种植、修剪花草', 1),
('遛狗', 'daily', 3.00, 'low', '带宠物散步', 1),
('购物', 'daily', 2.30, 'low', '逛街购物', 1),
('上下楼梯', 'daily', 4.00, 'moderate', '日常上下楼梯', 1),
('带孩子', 'daily', 2.50, 'low', '照顾婴幼儿活动', 1),

-- 其他
('HIIT', 'other', 11.00, 'high', '高强度间歇训练', 1),
('登山', 'other', 6.50, 'high', '户外登山运动', 1),
('滑雪', 'other', 7.00, 'high', '冬季户外运动', 1),
('滑冰', 'other', 7.00, 'moderate', '室内/室外滑冰', 1),
('攀岩', 'other', 8.00, 'high', '室内/室外攀岩', 1);
