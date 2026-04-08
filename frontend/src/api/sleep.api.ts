import { request } from '@/utils/request'
import type { SleepRecord, SleepGoal, SleepStats } from '@/types/sleep'

/**
 * 睡眠API
 */
export const sleepApi = {
  /**
   * 记录睡眠
   */
  recordSleep(data: SleepRecord) {
    return request.post<void>('/api/sleep/record', data)
  },

  /**
   * 更新睡眠记录
   */
  updateSleep(recordId: number, data: SleepRecord) {
    return request.put<void>(`/api/sleep/record/${recordId}`, data)
  },

  /**
   * 删除睡眠记录
   */
  deleteSleep(recordId: number) {
    return request.delete<void>(`/api/sleep/record/${recordId}`)
  },

  /**
   * 获取指定日期睡眠记录
   */
  getSleepByDate(date: string) {
    return request.get<SleepRecord>('/api/sleep/record', {
      params: { date }
    })
  },

  /**
   * 获取最近睡眠记录
   */
  getRecentSleeps(days: number = 7) {
    return request.get<SleepRecord[]>('/api/sleep/recent', {
      params: { days }
    })
  },

  /**
   * 获取睡眠统计
   */
  getSleepStats() {
    return request.get<SleepStats>('/api/sleep/stats')
  },

  /**
   * 获取睡眠目标
   */
  getSleepGoal() {
    return request.get<SleepGoal>('/api/sleep/goal')
  },

  /**
   * 更新睡眠目标
   */
  updateSleepGoal(goal: SleepGoal) {
    return request.put<void>('/api/sleep/goal', goal)
  }
}
