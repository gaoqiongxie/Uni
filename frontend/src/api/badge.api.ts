import { request } from '@/utils/request'
import type { Badge, UserBadgeStats } from '@/types/badge'

/**
 * 成就徽章 API
 */

/** 获取所有徽章列表 */
export function getAllBadges() {
  return request.get<Badge[]>('/badge/list')
}

/** 获取用户已获得的徽章 */
export function getMyBadges() {
  return request.get<Badge[]>('/badge/my')
}

/** 获取用户徽章统计 */
export function getBadgeStats() {
  return request.get<UserBadgeStats>('/badge/stats')
}

/** 检查并授予徽章 */
export function checkAndGrantBadges() {
  return request.post<Badge[]>('/badge/check')
}

/** 标记徽章为已读 */
export function markBadgesAsRead() {
  return request.post<void>('/badge/read')
}

/** 获取新徽章数量 */
export function getNewBadgeCount() {
  return request.get<number>('/badge/new-count')
}
