/**
 * 身体成分 API
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */
import { request } from '@/utils/request';
import type { BodyComposition, BodyCompositionDTO } from '@/types/body-composition';

/**
 * 记录身体成分
 */
export function recordBodyComposition(data: BodyCompositionDTO) {
  return request<BodyComposition>({
    url: '/api/body-composition/record',
    method: 'POST',
    data
  });
}

/**
 * 获取今日身体成分
 */
export function getTodayBodyComposition() {
  return request<BodyComposition>({
    url: '/api/body-composition/today',
    method: 'GET'
  });
}

/**
 * 获取最新身体成分
 */
export function getLatestBodyComposition() {
  return request<BodyComposition>({
    url: '/api/body-composition/latest',
    method: 'GET'
  });
}

/**
 * 获取历史身体成分列表
 */
export function getBodyCompositionHistory(limit?: number) {
  return request<BodyComposition[]>({
    url: '/api/body-composition/history',
    method: 'GET',
    data: { limit }
  });
}
