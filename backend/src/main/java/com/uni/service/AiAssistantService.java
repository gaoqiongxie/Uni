package com.uni.service;

import com.uni.vo.ai.*;

import java.util.List;

/**
 * AI智能助手服务接口
 */
public interface AiAssistantService {

    /**
     * 获取用户画像
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 保存用户画像
     */
    void saveUserProfile(Long userId, UserProfileVO vo);

    /**
     * 检查用户是否已完成画像
     */
    boolean hasCompletedProfile(Long userId);

    /**
     * 发送消息给AI
     */
    AiChatMessageVO chatWithAi(Long userId, String sessionId, String message);

    /**
     * 获取对话历史
     */
    List<AiChatMessageVO> getChatHistory(Long userId, String sessionId);

    /**
     * 生成个性化减肥计划
     */
    PersonalizedPlanVO generatePlan(Long userId, AiPlanRequestVO request);

    /**
     * 获取用户的计划列表
     */
    List<PersonalizedPlanVO> getUserPlans(Long userId);

    /**
     * 获取今日AI建议
     */
    String getDailyTip(Long userId);

    /**
     * 分析用户数据并给出建议
     */
    String analyzeUserData(Long userId);
}
