-- 轻瘦应用 - 减脂食谱初始化数据
-- 共25道食谱，涵盖早餐、午餐、晚餐、加餐、低卡、高蛋白分类
-- 执行前请确保 t_recipe 和 t_recipe_ingredient 表已创建

-- ==================== 早餐食谱 ====================

-- 1. 燕麦蓝莓酸奶杯
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('燕麦蓝莓酸奶杯', '早餐', '富含膳食纤维和抗氧化物质，低卡饱腹，开启元气满满的一天', '', 280, 12.50, 6.20, 42.00, 8.50, 5, 1, '["准备即食燕麦片40g放入杯中","倒入无糖希腊酸奶150g","加入新鲜蓝莓50g","撒上少许奇亚籽","搅拌均匀即可食用"]', '低卡,高蛋白,快手早餐', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '即食燕麦片', '40g', 140, 1),
(@recipe_id, '无糖希腊酸奶', '150g', 90, 2),
(@recipe_id, '新鲜蓝莓', '50g', 35, 3),
(@recipe_id, '奇亚籽', '5g', 15, 4);

-- 2. 全麦鸡蛋三明治
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('全麦鸡蛋三明治', '早餐', '经典减脂早餐，优质碳水+优质蛋白的完美组合', '', 320, 18.00, 10.50, 38.00, 6.00, 10, 1, '["全麦面包2片放入烤箱烤至微脆","鸡蛋煮熟切片","生菜洗净沥干","番茄切片","将鸡蛋、生菜、番茄夹在面包中","对角切开即可"]', '高蛋白,低GI,经典', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '全麦面包', '2片(60g)', 160, 1),
(@recipe_id, '鸡蛋', '1个(50g)', 70, 2),
(@recipe_id, '生菜', '2片', 5, 3),
(@recipe_id, '番茄', '半个(50g)', 10, 4),
(@recipe_id, '低脂沙拉酱', '10g', 15, 5);

-- 3. 紫薯牛奶粥
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('紫薯牛奶粥', '早餐', '紫薯富含花青素，搭配牛奶补充蛋白质，温暖养胃', '', 240, 10.00, 3.50, 42.00, 5.50, 20, 1, '["紫薯去皮切小块","将紫薯和燕麦一起放入锅中","加入适量清水煮15分钟","倒入脱脂牛奶搅拌均匀","小火煮2分钟即可"]', '低卡,养胃,抗氧化', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '紫薯', '100g', 80, 1),
(@recipe_id, '燕麦片', '30g', 105, 2),
(@recipe_id, '脱脂牛奶', '200ml', 70, 3);

-- 4. 牛油果鸡蛋吐司
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('牛油果鸡蛋吐司', '早餐', '健康脂肪+优质蛋白，网红减脂早餐，颜值与营养并存', '', 350, 15.00, 18.00, 32.00, 8.00, 8, 1, '["全麦面包烤至金黄酥脆","牛油果捣成泥，加少许盐和柠檬汁","鸡蛋煎成太阳蛋","将牛油果泥涂抹在面包上","放上煎蛋，撒黑胡椒即可"]', '健康脂肪,网红,高蛋白', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '全麦面包', '2片(60g)', 160, 1),
(@recipe_id, '牛油果', '半个(50g)', 80, 2),
(@recipe_id, '鸡蛋', '1个(50g)', 70, 3),
(@recipe_id, '橄榄油', '5g', 40, 4);

-- 5. 蔬菜蛋白煎饼
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('蔬菜蛋白煎饼', '早餐', '高蛋白低碳水，适合生酮或低碳饮食的减脂人群', '', 220, 22.00, 12.00, 6.00, 2.50, 15, 2, '["蛋白和全蛋分离，只取蛋白","菠菜切碎，番茄切丁","蛋白中加入蔬菜丁和少许盐","平底锅刷油，倒入蛋液","小火煎至两面金黄即可"]', '高蛋白,低碳水,生酮友好', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '鸡蛋白', '3个(90g)', 50, 1),
(@recipe_id, '菠菜', '50g', 15, 2),
(@recipe_id, '番茄', '50g', 10, 3),
(@recipe_id, '橄榄油', '10g', 80, 4),
(@recipe_id, '全麦面粉', '20g', 65, 5);

-- ==================== 午餐食谱 ====================

-- 6. 鸡胸肉藜麦沙拉
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('鸡胸肉藜麦沙拉', '午餐', '超级食物藜麦搭配低脂鸡胸肉，营养均衡饱腹感强', '', 420, 38.00, 12.00, 38.00, 7.00, 25, 2, '["藜麦提前浸泡30分钟，煮15分钟沥干","鸡胸肉用盐和黑胡椒腌制10分钟","平底锅煎鸡胸肉至两面金黄","黄瓜、小番茄、生菜洗净切块","将所有食材混合，淋上油醋汁"]', '高蛋白,超级食物,饱腹感', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '鸡胸肉', '150g', 165, 1),
(@recipe_id, '藜麦', '50g(干重)', 180, 2),
(@recipe_id, '黄瓜', '100g', 16, 3),
(@recipe_id, '小番茄', '80g', 20, 4),
(@recipe_id, '生菜', '50g', 8, 5),
(@recipe_id, '油醋汁', '15ml', 30, 6);

-- 7. 香煎三文鱼配芦笋
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('香煎三文鱼配芦笋', '午餐', '富含Omega-3脂肪酸，优质蛋白，减脂期间的好选择', '', 450, 35.00, 28.00, 8.00, 4.00, 20, 2, '["三文鱼用厨房纸吸干水分","两面撒盐和黑胡椒","平底锅刷橄榄油，中火煎三文鱼","每面煎3-4分钟至金黄","芦笋焯水2分钟","摆盘，挤上柠檬汁即可"]', '高蛋白,健康脂肪,Omega-3', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '三文鱼排', '150g', 300, 1),
(@recipe_id, '芦笋', '150g', 45, 2),
(@recipe_id, '橄榄油', '10g', 80, 3),
(@recipe_id, '柠檬', '半个', 10, 4);

-- 8. 牛肉西兰花糙米饭
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('牛肉西兰花糙米饭', '午餐', '经典中式减脂餐，牛肉补铁，西兰花补充维生素C', '', 480, 32.00, 15.00, 52.00, 8.00, 30, 2, '["糙米提前浸泡2小时，煮熟备用","牛肉切片，用生抽和料酒腌制","西兰花切小朵，焯水备用","热锅少油，爆炒牛肉至变色","加入西兰花翻炒，调味","盛在糙米饭上即可"]', '高蛋白,中式,补铁', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '瘦牛肉', '120g', 180, 1),
(@recipe_id, '糙米', '80g(干重)', 280, 2),
(@recipe_id, '西兰花', '150g', 50, 3),
(@recipe_id, '橄榄油', '8g', 70, 4);

-- 9. 清蒸鲈鱼
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('清蒸鲈鱼', '午餐', '低脂高蛋白，清蒸保留营养，适合减脂期的优质蛋白来源', '', 320, 42.00, 8.00, 12.00, 3.00, 25, 2, '["鲈鱼处理干净，两面划几刀","鱼身抹盐和料酒，腌制10分钟","盘底铺姜片和葱段","水开后蒸8-10分钟","倒掉蒸鱼水，淋上蒸鱼豉油","撒上葱丝，浇热油即可"]', '高蛋白,低脂,清蒸', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '鲈鱼', '300g', 240, 1),
(@recipe_id, '姜丝', '10g', 3, 2),
(@recipe_id, '葱', '20g', 6, 3),
(@recipe_id, '蒸鱼豉油', '15ml', 10, 4),
(@recipe_id, '食用油', '5g', 40, 5),
(@recipe_id, '杂粮饭', '100g', 120, 6);

-- 10. 虾仁豆腐蒸蛋
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('虾仁豆腐蒸蛋', '午餐', '嫩滑鲜美，高蛋白低脂，老少皆宜的减脂餐', '', 280, 28.00, 12.00, 10.00, 2.00, 20, 2, '["嫩豆腐切块铺在盘底","鸡蛋打散，加1.5倍温水搅匀","蛋液过筛倒入豆腐上","水开后蒸5分钟","铺上虾仁再蒸3分钟","淋上生抽和香油，撒葱花"]', '高蛋白,低脂,嫩滑', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '虾仁', '100g', 90, 1),
(@recipe_id, '嫩豆腐', '150g', 120, 2),
(@recipe_id, '鸡蛋', '2个(100g)', 140, 3),
(@recipe_id, '香油', '5g', 45, 4);

-- ==================== 晚餐食谱 ====================

-- 11. 凉拌鸡丝黄瓜
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('凉拌鸡丝黄瓜', '晚餐', '清爽开胃，低卡高蛋白，夏日减脂晚餐首选', '', 220, 28.00, 8.00, 8.00, 2.50, 15, 1, '["鸡胸肉煮熟撕成细丝","黄瓜切丝，胡萝卜切丝","蒜末、生抽、醋、香油调成汁","将所有食材混合","淋上调味汁拌匀即可"]', '低卡,高蛋白,清爽', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '鸡胸肉', '120g', 140, 1),
(@recipe_id, '黄瓜', '150g', 24, 2),
(@recipe_id, '胡萝卜', '50g', 20, 3),
(@recipe_id, '香油', '5g', 45, 4);

-- 12. 番茄蛋花汤配全麦面包
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('番茄蛋花汤配全麦面包', '晚餐', '经典家常减脂晚餐，简单易做，温暖饱腹', '', 260, 14.00, 8.00, 34.00, 5.00, 15, 1, '["番茄切块，鸡蛋打散备用","锅中少油炒香番茄出汁","加入适量清水煮开","淋入蛋液，快速搅散","加盐调味，撒葱花","搭配全麦面包食用"]', '低卡,家常,简单', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '番茄', '200g', 30, 1),
(@recipe_id, '鸡蛋', '1个(50g)', 70, 2),
(@recipe_id, '全麦面包', '1片(30g)', 80, 3),
(@recipe_id, '橄榄油', '5g', 40, 4);

-- 13. 蒜蓉蒸茄子
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('蒜蓉蒸茄子', '晚餐', '低卡饱腹，茄子吸油少，搭配蒜蓉香气十足', '', 180, 6.00, 8.00, 22.00, 8.00, 20, 1, '["茄子切条，泡盐水10分钟","挤干水分，摆入盘中","铺上蒜蓉和少许剁椒","水开后蒸10分钟","淋上生抽和香油即可"]', '低卡,素食,饱腹感', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '茄子', '300g', 66, 1),
(@recipe_id, '大蒜', '20g', 28, 2),
(@recipe_id, '生抽', '10ml', 5, 3),
(@recipe_id, '香油', '5g', 45, 4);

-- 14. 冬瓜虾仁汤
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('冬瓜虾仁汤', '晚餐', '冬瓜利尿消肿，虾仁补充蛋白，晚餐轻食首选', '', 150, 18.00, 4.00, 10.00, 3.00, 15, 1, '["冬瓜去皮去瓤切片","虾仁去虾线洗净","锅中清水煮开，放入冬瓜","冬瓜透明后加入虾仁","虾仁变色后加盐调味","撒葱花出锅"]', '低卡,利尿消肿,轻食', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '冬瓜', '300g', 36, 1),
(@recipe_id, '虾仁', '80g', 72, 2),
(@recipe_id, '香油', '3g', 27, 3);

-- 15. 西葫芦鸡蛋饼
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('西葫芦鸡蛋饼', '晚餐', '低碳水高蛋白，西葫芦代替面粉，减脂期晚餐好选择', '', 200, 16.00, 12.00, 8.00, 2.50, 15, 1, '["西葫芦擦丝，加少许盐腌制5分钟","挤干水分","打入鸡蛋，加少许面粉","搅拌均匀成糊状","平底锅刷油，倒入面糊","小火煎至两面金黄"]', '低碳水,高蛋白,快手', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '西葫芦', '200g', 34, 1),
(@recipe_id, '鸡蛋', '2个(100g)', 140, 2),
(@recipe_id, '全麦面粉', '20g', 65, 3),
(@recipe_id, '橄榄油', '8g', 70, 4);

-- ==================== 加餐食谱 ====================

-- 16. 希腊酸奶坚果杯
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('希腊酸奶坚果杯', '加餐', '健康脂肪+优质蛋白，下午茶加餐，控制食欲', '', 180, 12.00, 10.00, 10.00, 2.00, 3, 1, '["准备无糖希腊酸奶100g","加入混合坚果10g","可加少许蜂蜜调味","搅拌均匀即可"]', '健康脂肪,加餐,控食欲', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '无糖希腊酸奶', '100g', 60, 1),
(@recipe_id, '混合坚果', '10g', 60, 2),
(@recipe_id, '蜂蜜', '10g', 30, 3),
(@recipe_id, '蓝莓', '30g', 20, 4);

-- 17. 苹果花生酱片
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('苹果花生酱片', '加餐', '经典美式健康加餐，苹果脆甜配花生酱香浓', '', 150, 4.00, 8.00, 18.00, 4.00, 3, 1, '["苹果洗净切片","无糖花生酱10g涂抹在苹果片上","可撒少许肉桂粉","即食或冷藏后食用"]', '健康脂肪,加餐,经典', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '苹果', '1个(150g)', 78, 1),
(@recipe_id, '无糖花生酱', '10g', 60, 2);

-- 18. 水煮蛋配小番茄
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('水煮蛋配小番茄', '加餐', '最简加餐组合，蛋白质+维生素，运动前后补充', '', 120, 10.00, 7.00, 4.00, 1.50, 10, 1, '["鸡蛋冷水下锅，水开后煮8分钟","捞出过凉水剥壳","小番茄洗净","摆盘，可撒少许黑胡椒"]', '高蛋白,简单,运动加餐', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '鸡蛋', '1个(50g)', 70, 1),
(@recipe_id, '小番茄', '100g', 25, 2);

-- 19. 黄瓜条配鹰嘴豆泥
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('黄瓜条配鹰嘴豆泥', '加餐', '中东风味健康加餐，鹰嘴豆富含植物蛋白和纤维', '', 140, 6.00, 6.00, 14.00, 5.00, 5, 1, '["黄瓜切成条状","鹰嘴豆泥30g盛入小碗","用黄瓜条蘸食鹰嘴豆泥","可撒少许红椒粉装饰"]', '素食,植物蛋白,异域风', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '黄瓜', '150g', 24, 1),
(@recipe_id, '鹰嘴豆泥', '30g', 70, 2);

-- 20. 蛋白粉香蕉奶昔
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('蛋白粉香蕉奶昔', '加餐', '健身后快速补充蛋白质，香蕉提供快速碳水', '', 220, 25.00, 3.00, 22.00, 3.00, 3, 1, '["香蕉1根切块","加入脱脂牛奶200ml","加入蛋白粉1勺","用搅拌机打匀","倒入杯中即可"]', '高蛋白,运动恢复,快手', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '香蕉', '1根(100g)', 90, 1),
(@recipe_id, '脱脂牛奶', '200ml', 70, 2),
(@recipe_id, '乳清蛋白粉', '25g', 100, 3);

-- ==================== 低卡特辑 ====================

-- 21. 魔芋丝拌黄瓜
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('魔芋丝拌黄瓜', '低卡', '超低卡路里，魔芋饱腹感极强，减脂期救命菜', '', 80, 2.00, 3.00, 12.00, 6.00, 10, 1, '["魔芋丝焯水2分钟，过凉水沥干","黄瓜切丝","胡萝卜切丝","蒜末、生抽、醋、香油调汁","拌匀即可食用"]', '超低卡,饱腹感,救命菜', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '魔芋丝', '200g', 20, 1),
(@recipe_id, '黄瓜', '100g', 16, 2),
(@recipe_id, '胡萝卜', '50g', 20, 3),
(@recipe_id, '香油', '3g', 27, 4);

-- 22. 生菜包鸡胸肉
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('生菜包鸡胸肉', '低卡', '用生菜代替面皮，超低碳水，大口满足无负担', '', 160, 26.00, 4.00, 6.00, 3.00, 15, 1, '["鸡胸肉煮熟撕成丝","生菜洗净沥干","黄瓜、胡萝卜切条","用生菜叶包裹鸡肉和蔬菜","可蘸低卡酱料食用"]', '超低卡,低碳水,创意', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '鸡胸肉', '100g', 120, 1),
(@recipe_id, '生菜', '100g', 15, 2),
(@recipe_id, '黄瓜条', '50g', 8, 3),
(@recipe_id, '胡萝卜条', '30g', 12, 4);

-- 23. 紫菜蛋花汤
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('紫菜蛋花汤', '低卡', '超低热量，晚餐首选，暖胃又饱腹', '', 90, 8.00, 4.00, 6.00, 2.00, 10, 1, '["紫菜撕碎放入碗中","鸡蛋打散备用","清水煮开，淋入蛋液","蛋液凝固后加入紫菜","加盐、香油调味","撒葱花出锅"]', '超低卡,暖胃,简单', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '紫菜', '5g', 15, 1),
(@recipe_id, '鸡蛋', '1个(50g)', 70, 2),
(@recipe_id, '香油', '3g', 27, 3);

-- ==================== 高蛋白特辑 ====================

-- 24. 黑椒牛排配蔬菜
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('黑椒牛排配蔬菜', '高蛋白', '优质红肉蛋白，增肌减脂首选，满足感爆棚', '', 520, 48.00, 32.00, 12.00, 4.00, 25, 2, '["牛排室温回温，用厨房纸吸干","两面撒盐和黑胡椒","平底锅烧热，放入牛排","每面煎2-3分钟","静置5分钟锁住肉汁","搭配焯水蔬菜食用"]', '高蛋白,增肌,满足感', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '牛排(瘦肉)', '200g', 400, 1),
(@recipe_id, '西兰花', '100g', 35, 2),
(@recipe_id, '胡萝卜', '50g', 20, 3),
(@recipe_id, '橄榄油', '10g', 80, 4);

-- 25. 金枪鱼沙拉
INSERT INTO `t_recipe` (`name`, `category`, `description`, `cover_image`, `calorie`, `protein`, `fat`, `carbohydrate`, `fiber`, `cooking_time`, `difficulty`, `steps`, `tags`, `servings`, `like_count`, `status`, `create_user`, `create_time`) VALUES
('金枪鱼沙拉', '高蛋白', '深海鱼优质蛋白，搭配蔬菜清爽不腻', '', 380, 35.00, 18.00, 15.00, 5.00, 10, 1, '["水浸金枪鱼罐头沥干","生菜撕成小块","黄瓜切片，小番茄对半","洋葱切丝泡水去辣","所有食材混合","淋上油醋汁拌匀"]', '高蛋白,深海鱼,清爽', 1, 0, 1, 'system', NOW());
SET @recipe_id = LAST_INSERT_ID();
INSERT INTO `t_recipe_ingredient` (`recipe_id`, `name`, `amount`, `calorie`, `sort_order`) VALUES
(@recipe_id, '水浸金枪鱼', '150g', 180, 1),
(@recipe_id, '生菜', '80g', 12, 2),
(@recipe_id, '黄瓜', '80g', 13, 3),
(@recipe_id, '小番茄', '80g', 20, 4),
(@recipe_id, '洋葱', '30g', 12, 5),
(@recipe_id, '油醋汁', '15ml', 30, 6);

-- ==================== 数据汇总 ====================
-- 早餐: 5道 (280-350 kcal)
-- 午餐: 5道 (280-480 kcal)
-- 晚餐: 5道 (150-260 kcal)
-- 加餐: 5道 (120-220 kcal)
-- 低卡: 3道 (80-160 kcal)
-- 高蛋白: 2道 (380-520 kcal)
-- 总计: 25道食谱
