import { request } from '@/utils/request'
import type { UserProfile, AiChatMessage, PersonalizedPlan, GeneratePlanRequest } from '@/types/ai-assistant'

/**
 * 获取用户画像
 */
export function getUserProfile() {
  return request<UserProfile>({
    url: '/api/ai/profile',
    method: 'GET',
  })
}

/**
 * 保存用户画像
 */
export function saveUserProfile(data: UserProfile) {
  return request<void>({
    url: '/api/ai/profile/save',
    method: 'POST',
    data,
  })
}

/**
 * 检查是否完成画像
 */
export function checkProfileCompleted() {
  return request<boolean>({
    url: '/api/ai/profile/check',
    method: 'GET',
  })
}

/**
 * 与AI对话
 */
export function chatWithAi(sessionId: string, message: string) {
  return request<AiChatMessage>({
    url: '/api/ai/chat',
    method: 'POST',
    params: { sessionId, message },
  })
}

/**
 * 获取对话历史
 */
export function getChatHistory(sessionId: string) {
  return request<AiChatMessage[]>({
    url: '/api/ai/chat/history',
    method: 'GET',
    params: { sessionId },
  })
}

/**
 * 生成个性化计划
 */
export function generatePlan(data: GeneratePlanRequest) {
  return request<PersonalizedPlan>({
    url: '/api/ai/plan/generate',
    method: 'POST',
    data,
  })
}

/**
 * 获取用户的计划列表
 */
export function getUserPlans() {
  return request<PersonalizedPlan[]>({
    url: '/api/ai/plan/list',
    method: 'GET',
  })
}

/**
 * 获取今日AI建议
 */
export function getDailyTip() {
  return request<string>({
    url: '/api/ai/daily-tip',
    method: 'GET',
  })
}

/**
 * 分析用户数据
 */
export function analyzeUserData() {
  return request<string>({
    url: '/api/ai/analyze',
    method: 'GET',
  })
}

/**
 * 创建新会话
 */
export function newSession() {
  return request<string>({
    url: '/api/ai/session/new',
    method: 'GET',
  })
}
