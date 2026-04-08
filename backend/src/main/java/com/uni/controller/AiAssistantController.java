package com.uni.controller;

import com.uni.common.Result;
import com.uni.service.AiAssistantService;
import com.uni.util.UserContext;
import com.uni.vo.ai.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * AI智能助手控制器
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI智能助手", description = "智能减脂助手相关接口")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    @GetMapping("/profile")
    @Operation(summary = "获取用户画像")
    public Result<UserProfileVO> getUserProfile() {
        Long userId = UserContext.getCurrentUserId();
        UserProfileVO profile = aiAssistantService.getUserProfile(userId);
        return Result.success(profile);
    }

    @PostMapping("/profile/save")
    @Operation(summary = "保存用户画像")
    public Result<Void> saveUserProfile(@RequestBody UserProfileVO vo) {
        Long userId = UserContext.getCurrentUserId();
        aiAssistantService.saveUserProfile(userId, vo);
        return Result.success();
    }

    @GetMapping("/profile/check")
    @Operation(summary = "检查是否完成画像")
    public Result<Boolean> checkProfile() {
        Long userId = UserContext.getCurrentUserId();
        boolean completed = aiAssistantService.hasCompletedProfile(userId);
        return Result.success(completed);
    }

    @PostMapping("/chat")
    @Operation(summary = "与AI对话")
    public Result<AiChatMessageVO> chat(
            @RequestParam String sessionId,
            @RequestParam String message) {
        Long userId = UserContext.getCurrentUserId();
        AiChatMessageVO response = aiAssistantService.chatWithAi(userId, sessionId, message);
        return Result.success(response);
    }

    @GetMapping("/chat/history")
    @Operation(summary = "获取对话历史")
    public Result<List<AiChatMessageVO>> getChatHistory(@RequestParam String sessionId) {
        Long userId = UserContext.getCurrentUserId();
        List<AiChatMessageVO> history = aiAssistantService.getChatHistory(userId, sessionId);
        return Result.success(history);
    }

    @PostMapping("/plan/generate")
    @Operation(summary = "生成个性化计划")
    public Result<PersonalizedPlanVO> generatePlan(@RequestBody AiPlanRequestVO request) {
        Long userId = UserContext.getCurrentUserId();
        PersonalizedPlanVO plan = aiAssistantService.generatePlan(userId, request);
        return Result.success(plan);
    }

    @GetMapping("/plan/list")
    @Operation(summary = "获取用户的计划列表")
    public Result<List<PersonalizedPlanVO>> getUserPlans() {
        Long userId = UserContext.getCurrentUserId();
        List<PersonalizedPlanVO> plans = aiAssistantService.getUserPlans(userId);
        return Result.success(plans);
    }

    @GetMapping("/daily-tip")
    @Operation(summary = "获取今日AI建议")
    public Result<String> getDailyTip() {
        Long userId = UserContext.getCurrentUserId();
        String tip = aiAssistantService.getDailyTip(userId);
        return Result.success(tip);
    }

    @GetMapping("/analyze")
    @Operation(summary = "分析用户数据")
    public Result<String> analyzeUserData() {
        Long userId = UserContext.getCurrentUserId();
        String analysis = aiAssistantService.analyzeUserData(userId);
        return Result.success(analysis);
    }

    @GetMapping("/session/new")
    @Operation(summary = "创建新会话")
    public Result<String> newSession() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        return Result.success(sessionId);
    }
}
