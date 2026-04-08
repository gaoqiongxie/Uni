package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.entity.*;
import com.uni.mapper.*;
import com.uni.service.AiAssistantService;
import com.uni.vo.ai.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AI智能助手服务实现
 * 基于规则引擎的轻量级AI分析
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAssistantServiceImpl implements AiAssistantService {

    private final UserProfileMapper userProfileMapper;
    private final AiChatHistoryMapper chatHistoryMapper;
    private final PersonalizedPlanMapper planMapper;
    private final UserMapper userMapper;
    private final WeightRecordMapper weightRecordMapper;
    private final MealRecordMapper mealRecordMapper;
    private final ExerciseRecordMapper exerciseRecordMapper;
    private final ObjectMapper objectMapper;

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        UserProfileEntity entity = userProfileMapper.selectByUserId(userId);
        if (entity == null) {
            return null;
        }
        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserProfile(Long userId, UserProfileVO vo) {
        UserProfileEntity entity = userProfileMapper.selectByUserId(userId);
        
        if (entity != null) {
            BeanUtils.copyProperties(vo, entity, "id", "userId", "createdAt");
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setIsCompleted(1);
            userProfileMapper.updateById(entity);
        } else {
            entity = new UserProfileEntity();
            BeanUtils.copyProperties(vo, entity);
            entity.setUserId(userId);
            entity.setIsCompleted(1);
            entity.setDeleted(0);
            userProfileMapper.insert(entity);
        }
    }

    @Override
    public boolean hasCompletedProfile(Long userId) {
        UserProfileEntity entity = userProfileMapper.selectByUserId(userId);
        return entity != null && entity.getIsCompleted() != null && entity.getIsCompleted() == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiChatMessageVO chatWithAi(Long userId, String sessionId, String message) {
        // 保存用户消息
        AiChatHistoryEntity userMsg = new AiChatHistoryEntity();
        userMsg.setUserId(userId);
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setContent(message);
        userMsg.setMessageType("text");
        userMsg.setCreatedAt(LocalDateTime.now());
        userMsg.setDeleted(0);
        chatHistoryMapper.insert(userMsg);

        // 生成AI回复
        String aiResponse = generateAiResponse(userId, message);

        // 保存AI回复
        AiChatHistoryEntity aiMsg = new AiChatHistoryEntity();
        aiMsg.setUserId(userId);
        aiMsg.setSessionId(sessionId);
        aiMsg.setRole("assistant");
        aiMsg.setContent(aiResponse);
        aiMsg.setMessageType("text");
        aiMsg.setCreatedAt(LocalDateTime.now());
        aiMsg.setDeleted(0);
        chatHistoryMapper.insert(aiMsg);

        AiChatMessageVO vo = new AiChatMessageVO();
        vo.setRole("assistant");
        vo.setContent(aiResponse);
        vo.setMessageType("text");
        vo.setCreatedAt(aiMsg.getCreatedAt());
        return vo;
    }

    @Override
    public List<AiChatMessageVO> getChatHistory(Long userId, String sessionId) {
        List<AiChatHistoryEntity> list = chatHistoryMapper.selectBySession(userId, sessionId);
        List<AiChatMessageVO> result = new ArrayList<>();
        for (AiChatHistoryEntity entity : list) {
            AiChatMessageVO vo = new AiChatMessageVO();
            vo.setId(entity.getId());
            vo.setRole(entity.getRole());
            vo.setContent(entity.getContent());
            vo.setMessageType(entity.getMessageType());
            vo.setCreatedAt(entity.getCreatedAt());
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PersonalizedPlanVO generatePlan(Long userId, AiPlanRequestVO request) {
        UserProfileEntity profile = userProfileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new RuntimeException("请先完善个人信息");
        }

        // 基于规则生成个性化计划
        Map<String, Object> planContent = new HashMap<>();
        String planTitle;
        String aiAnalysis;

        switch (request.getPlanType()) {
            case "diet":
                planContent = generateDietPlan(profile);
                planTitle = "个性化饮食计划";
                aiAnalysis = generateDietAnalysis(profile);
                break;
            case "exercise":
                planContent = generateExercisePlan(profile);
                planTitle = "个性化运动计划";
                aiAnalysis = generateExerciseAnalysis(profile);
                break;
            case "sleep":
                planContent = generateSleepPlan(profile);
                planTitle = "睡眠改善计划";
                aiAnalysis = generateSleepAnalysis(profile);
                break;
            default:
                planContent = generateComprehensivePlan(profile);
                planTitle = "综合减脂计划";
                aiAnalysis = generateComprehensiveAnalysis(profile);
        }

        // 计算目标值
        int dailyCalorie = calculateDailyCalorie(profile);
        int dailyWater = calculateDailyWater(profile);
        BigDecimal dailySleep = new BigDecimal("7.5");
        int weeklyExercise = calculateWeeklyExercise(profile);

        // 保存计划
        PersonalizedPlanEntity plan = new PersonalizedPlanEntity();
        plan.setUserId(userId);
        plan.setPlanType(request.getPlanType());
        plan.setPlanTitle(planTitle);
        try {
            plan.setPlanContent(objectMapper.writeValueAsString(planContent));
        } catch (Exception e) {
            plan.setPlanContent("{}");
        }
        plan.setDailyCalorieTarget(dailyCalorie);
        plan.setDailyWaterTarget(dailyWater);
        plan.setDailySleepTarget(dailySleep);
        plan.setWeeklyExerciseDays(weeklyExercise);
        plan.setAiAnalysis(aiAnalysis);
        plan.setIsActive(1);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setDeleted(0);
        planMapper.insert(plan);

        PersonalizedPlanVO vo = new PersonalizedPlanVO();
        BeanUtils.copyProperties(plan, vo);
        vo.setPlanContent(planContent);
        return vo;
    }

    @Override
    public List<PersonalizedPlanVO> getUserPlans(Long userId) {
        List<PersonalizedPlanEntity> list = planMapper.selectActivePlans(userId);
        List<PersonalizedPlanVO> result = new ArrayList<>();
        for (PersonalizedPlanEntity entity : list) {
            PersonalizedPlanVO vo = new PersonalizedPlanVO();
            BeanUtils.copyProperties(entity, vo);
            try {
                vo.setPlanContent(objectMapper.readValue(entity.getPlanContent(), Map.class));
            } catch (Exception e) {
                vo.setPlanContent(new HashMap<>());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public String getDailyTip(Long userId) {
        // 基于用户数据生成每日建议
        UserProfileEntity profile = userProfileMapper.selectByUserId(userId);
        
        // 获取最近数据
        LocalDate today = LocalDate.now();
        
        // 根据时间和用户状态生成建议
        List<String> tips = new ArrayList<>();
        
        if (profile != null) {
            // 基于目标进度的建议
            if (profile.getCurrentWeight() != null && profile.getTargetWeight() != null) {
                BigDecimal diff = profile.getCurrentWeight().subtract(profile.getTargetWeight());
                if (diff.compareTo(new BigDecimal("5")) > 0) {
                    tips.add("您的减脂目标很明确！建议每周减重0.5-1kg，保持健康节奏。");
                }
            }
            
            // 基于活动量的建议
            if ("sedentary".equals(profile.getActivityLevel())) {
                tips.add("您平时久坐较多，建议每小时起身活动5分钟，积少成多！");
            }
            
            // 基于睡眠的建议
            if (profile.getSleepHours() != null && profile.getSleepHours().compareTo(new BigDecimal("7")) < 0) {
                tips.add("睡眠不足会影响代谢，建议今晚提前30分钟上床休息。");
            }
        }
        
        // 通用建议
        tips.add("记得多喝水！每天至少8杯水，帮助代谢和排毒。");
        tips.add("每餐先吃蔬菜，再吃蛋白质，最后吃主食，有助于控制血糖。");
        tips.add("细嚼慢咽，每口咀嚼20次以上，更容易产生饱腹感。");
        
        // 随机返回一条
        Random random = new Random();
        return tips.get(random.nextInt(tips.size()));
    }

    @Override
    public String analyzeUserData(Long userId) {
        UserProfileEntity profile = userProfileMapper.selectByUserId(userId);
        if (profile == null) {
            return "请先完善您的个人信息，我才能为您分析哦~";
        }

        StringBuilder analysis = new StringBuilder();
        analysis.append("📊 **您的专属分析报告**\n\n");
        
        // BMI分析
        if (profile.getHeight() != null && profile.getCurrentWeight() != null) {
            BigDecimal heightM = profile.getHeight().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = profile.getCurrentWeight().divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP);
            analysis.append("**BMI指数**: ").append(bmi).append(" ");
            if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                analysis.append("(偏瘦)\n");
            } else if (bmi.compareTo(new BigDecimal("24")) < 0) {
                analysis.append("(正常)\n");
            } else if (bmi.compareTo(new BigDecimal("28")) < 0) {
                analysis.append("(超重)\n");
            } else {
                analysis.append("(肥胖)\n");
            }
        }
        
        // 目标分析
        if (profile.getCurrentWeight() != null && profile.getTargetWeight() != null) {
            BigDecimal needLose = profile.getCurrentWeight().subtract(profile.getTargetWeight());
            analysis.append("**目标减重**: ").append(needLose).append("kg\n");
            
            // 预计时间
            BigDecimal weeks = needLose.divide(new BigDecimal("0.75"), 0, RoundingMode.UP);
            analysis.append("**预计周期**: 约").append(weeks).append("周（按每周减0.75kg计算）\n");
        }
        
        // 基础代谢
        if (profile.getGender() != null && profile.getAge() != null && 
            profile.getHeight() != null && profile.getCurrentWeight() != null) {
            BigDecimal bmr = calculateBMR(profile);
            analysis.append("**基础代谢**: 约").append(bmr.intValue()).append("kcal/天\n");
        }
        
        analysis.append("\n💡 **我的建议**\n");
        analysis.append(generatePersonalizedAdvice(profile));
        
        return analysis.toString();
    }

    // ========== 私有方法 ==========

    private String generateAiResponse(Long userId, String message) {
        String lowerMsg = message.toLowerCase();
        
        // 关键词匹配回复
        if (lowerMsg.contains("计划") || lowerMsg.contains("方案")) {
            return "我可以为您生成个性化的减脂计划！请告诉我您更关注哪方面：\n1. 饮食计划 🥗\n2. 运动计划 🏃\n3. 睡眠改善 💤\n4. 综合计划 📋";
        }
        
        if (lowerMsg.contains("吃") || lowerMsg.contains("食") || lowerMsg.contains("餐")) {
            return "饮食是减脂的关键！建议您：\n• 每餐七分饱，细嚼慢咽\n• 多吃蛋白质和蔬菜\n• 少吃精制碳水\n• 多喝水，餐前喝一杯\n\n需要我为您制定详细的饮食计划吗？";
        }
        
        if (lowerMsg.contains("运动") || lowerMsg.contains("锻炼") || lowerMsg.contains("练")) {
            return "运动让减脂更高效！建议：\n• 每周3-5次，每次30-60分钟\n• 有氧+力量结合\n• 选择自己喜欢的运动\n• 循序渐进，不要急于求成\n\n想获取适合您的运动计划吗？";
        }
        
        if (lowerMsg.contains("体重") || lowerMsg.contains("瘦") || lowerMsg.contains("减")) {
            return "健康减脂是场马拉松，不是短跑！\n\n建议您：\n1. 设定合理目标（每周0.5-1kg）\n2. 记录每日体重变化\n3. 关注围度而不仅是体重\n4. 保持耐心，相信过程\n\n需要查看您的数据分析报告吗？";
        }
        
        if (lowerMsg.contains("你好") || lowerMsg.contains("在吗")) {
            return "你好！我是您的智能减脂助手 🤖\n\n我可以帮您：\n• 分析身体数据\n• 制定个性化计划\n• 解答减脂疑问\n• 每日打卡提醒\n\n今天想聊点什么？";
        }
        
        // 默认回复
        return "收到！作为您的减脂助手，我建议您：\n\n1. 坚持记录每日数据\n2. 保持规律作息\n3. 多喝水，少熬夜\n4. 遇到平台期不要气馁\n\n有什么具体问题随时问我哦！💪";
    }

    private Map<String, Object> generateDietPlan(UserProfileEntity profile) {
        Map<String, Object> plan = new HashMap<>();
        
        int calorieTarget = calculateDailyCalorie(profile);
        
        plan.put("dailyCalorie", calorieTarget);
        plan.put("mealDistribution", Map.of(
            "breakfast", "30% (" + (int)(calorieTarget * 0.3) + "kcal)",
            "lunch", "40% (" + (int)(calorieTarget * 0.4) + "kcal)",
            "dinner", "25% (" + (int)(calorieTarget * 0.25) + "kcal)",
            "snack", "5% (" + (int)(calorieTarget * 0.05) + "kcal)"
        ));
        
        plan.put("guidelines", Arrays.asList(
            "每餐先吃蔬菜，再吃蛋白质，最后吃主食",
            "细嚼慢咽，每餐20分钟以上",
            "多喝水，餐前喝一杯水",
            "少油少盐少糖",
            "晚餐在7点前完成"
        ));
        
        plan.put("sampleMenu", Map.of(
            "breakfast", "燕麦粥 + 鸡蛋 + 牛奶",
            "lunch", "糙米饭 + 鸡胸肉 + 蔬菜",
            "dinner", "蔬菜沙拉 + 豆腐 + 少量主食",
            "snack", "水果或坚果（少量）"
        ));
        
        return plan;
    }

    private Map<String, Object> generateExercisePlan(UserProfileEntity profile) {
        Map<String, Object> plan = new HashMap<>();
        
        int days = calculateWeeklyExercise(profile);
        String level = profile.getExerciseExperience();
        
        plan.put("weeklyDays", days);
        plan.put("duration", "30-45分钟/次");
        plan.put("intensity", "中等强度");
        
        List<Map<String, String>> schedule = new ArrayList<>();
        schedule.add(Map.of("day", "周一", "type", "有氧运动", "activity", "快走或慢跑30分钟"));
        schedule.add(Map.of("day", "周二", "type", "力量训练", "activity", "自重训练：深蹲、俯卧撑、平板支撑"));
        schedule.add(Map.of("day", "周三", "type", "休息", "activity", "轻度拉伸或散步"));
        schedule.add(Map.of("day", "周四", "type", "有氧运动", "activity", "游泳或骑车40分钟"));
        schedule.add(Map.of("day", "周五", "type", "力量训练", "activity", "哑铃训练或阻力带训练"));
        schedule.add(Map.of("day", "周六", "type", "有氧运动", "activity", "跳绳或健身操30分钟"));
        schedule.add(Map.of("day", "周日", "type", "休息", "activity", "瑜伽或完全休息"));
        
        plan.put("schedule", schedule);
        
        plan.put("tips", Arrays.asList(
            "运动前热身5-10分钟",
            "运动后拉伸放松",
            "循序渐进，不要急于求成",
            "选择自己喜欢的运动",
            "保持规律，贵在坚持"
        ));
        
        return plan;
    }

    private Map<String, Object> generateSleepPlan(UserProfileEntity profile) {
        Map<String, Object> plan = new HashMap<>();
        
        plan.put("targetHours", "7-8小时");
        plan.put("bedtime", "22:30-23:00");
        plan.put("waketime", "06:30-07:00");
        
        plan.put("bedtimeRoutine", Arrays.asList(
            "21:30 停止进食",
            "21:45 放下手机，调暗灯光",
            "22:00 温水泡脚或冲澡",
            "22:15 阅读或冥想放松",
            "22:30 上床准备入睡"
        ));
        
        plan.put("sleepHygiene", Arrays.asList(
            "保持卧室凉爽、安静、黑暗",
            "睡前2小时不喝咖啡因",
            "睡前不看电子屏幕",
            "保持规律作息，周末也不例外",
            "如果20分钟睡不着，起床做些放松的事"
        ));
        
        return plan;
    }

    private Map<String, Object> generateComprehensivePlan(UserProfileEntity profile) {
        Map<String, Object> plan = new HashMap<>();
        
        plan.put("diet", generateDietPlan(profile));
        plan.put("exercise", generateExercisePlan(profile));
        plan.put("sleep", generateSleepPlan(profile));
        
        plan.put("dailyGoals", Map.of(
            "calorie", calculateDailyCalorie(profile) + "kcal",
            "water", calculateDailyWater(profile) + "ml",
            "sleep", "7-8小时",
            "exercise", calculateWeeklyExercise(profile) + "天",
            "steps", "8000-10000步"
        ));
        
        plan.put("weeklyChecklist", Arrays.asList(
            "记录体重变化",
            "完成计划运动天数",
            "坚持饮食记录",
            "保证充足睡眠",
            "拍照记录体型变化"
        ));
        
        return plan;
    }

    private String generateDietAnalysis(UserProfileEntity profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("根据您的身体数据和生活习惯，我为您制定了这份饮食计划。");
        
        if (profile.getActivityLevel() != null) {
            switch (profile.getActivityLevel()) {
                case "sedentary":
                    sb.append("由于您平时活动量较少，建议严格控制热量摄入，多吃高纤维食物增加饱腹感。");
                    break;
                case "active":
                case "very_active":
                    sb.append("考虑到您的活动量较大，计划中保留了足够的热量支持您的运动需求。");
                    break;
                default:
                    sb.append("这份计划适合您的日常活动水平，营养均衡且易于执行。");
            }
        }
        
        if (profile.getCookingSkill() != null && profile.getCookingSkill() <= 2) {
            sb.append("食谱选择了简单易做的菜品，即使是烹饪新手也能轻松上手。");
        }
        
        return sb.toString();
    }

    private String generateExerciseAnalysis(UserProfileEntity profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("这份运动计划根据您的运动经验量身定制。");
        
        if ("beginner".equals(profile.getExerciseExperience())) {
            sb.append("作为运动新手，计划从低强度开始，帮助您逐步建立运动习惯，避免运动损伤。");
        } else if ("advanced".equals(profile.getExerciseExperience())) {
            sb.append("基于您丰富的运动经验，计划中包含了更具挑战性的训练内容。");
        }
        
        if (profile.getHasGymAccess() != null && profile.getHasGymAccess() == 0) {
            sb.append("考虑到您没有健身房条件，计划以居家运动为主，无需专业器械。");
        }
        
        return sb.toString();
    }

    private String generateSleepAnalysis(UserProfileEntity profile) {
        return "良好的睡眠是减脂成功的重要保障。这份睡眠计划帮助您建立规律的作息，提高睡眠质量，让身体在夜间更好地恢复和代谢。";
    }

    private String generateComprehensiveAnalysis(UserProfileEntity profile) {
        return "这是一份为您量身定制的综合减脂计划，涵盖饮食、运动和睡眠三个方面。三管齐下，科学减脂，让您在健康的前提下达成目标！";
    }

    private String generatePersonalizedAdvice(UserProfileEntity profile) {
        List<String> advices = new ArrayList<>();
        
        if ("sedentary".equals(profile.getActivityLevel())) {
            advices.add("• 您平时久坐较多，建议每小时起身活动5分钟");
        }
        
        if (profile.getSleepHours() != null && profile.getSleepHours().compareTo(new BigDecimal("7")) < 0) {
            advices.add("• 睡眠不足会影响减脂效果，建议增加睡眠时间");
        }
        
        if (profile.getStressLevel() != null && profile.getStressLevel() >= 4) {
            advices.add("• 压力较大时容易情绪化进食，建议学习放松技巧");
        }
        
        if (advices.isEmpty()) {
            advices.add("• 您的整体状况不错，保持当前的生活方式！");
            advices.add("• 坚持记录数据，及时调整计划");
        }
        
        return String.join("\n", advices);
    }

    private int calculateDailyCalorie(UserProfileEntity profile) {
        BigDecimal bmr = calculateBMR(profile);
        double activityMultiplier = 1.2; // 默认久坐
        
        if (profile.getActivityLevel() != null) {
            switch (profile.getActivityLevel()) {
                case "light": activityMultiplier = 1.375; break;
                case "moderate": activityMultiplier = 1.55; break;
                case "active": activityMultiplier = 1.725; break;
                case "very_active": activityMultiplier = 1.9; break;
            }
        }
        
        BigDecimal tdee = bmr.multiply(new BigDecimal(activityMultiplier));
        // 减脂热量缺口 300-500
        return tdee.subtract(new BigDecimal("400")).intValue();
    }

    private BigDecimal calculateBMR(UserProfileEntity profile) {
        // Mifflin-St Jeor 公式
        if (profile.getGender() == null || profile.getAge() == null || 
            profile.getHeight() == null || profile.getCurrentWeight() == null) {
            return new BigDecimal("1500");
        }
        
        BigDecimal weight = profile.getCurrentWeight();
        BigDecimal height = profile.getHeight();
        int age = profile.getAge();
        
        BigDecimal bmr;
        if (profile.getGender() == 1) { // 男
            bmr = new BigDecimal("10").multiply(weight)
                .add(new BigDecimal("6.25").multiply(height))
                .subtract(new BigDecimal("5").multiply(new BigDecimal(age)))
                .add(new BigDecimal("5"));
        } else { // 女
            bmr = new BigDecimal("10").multiply(weight)
                .add(new BigDecimal("6.25").multiply(height))
                .subtract(new BigDecimal("5").multiply(new BigDecimal(age)))
                .subtract(new BigDecimal("161"));
        }
        
        return bmr;
    }

    private int calculateDailyWater(UserProfileEntity profile) {
        if (profile.getCurrentWeight() == null) {
            return 2000;
        }
        // 体重(kg) * 35ml
        return profile.getCurrentWeight().multiply(new BigDecimal("35")).intValue();
    }

    private int calculateWeeklyExercise(UserProfileEntity profile) {
        if ("beginner".equals(profile.getExerciseExperience())) {
            return 3;
        } else if ("advanced".equals(profile.getExerciseExperience())) {
            return 5;
        }
        return 4;
    }
}
