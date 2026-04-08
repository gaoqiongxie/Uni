import { request } from '@/utils/request'
import type { WaterDailyStats, WaterRecord, WaterReminderSetting } from '@/types/water'

/**
 * 饮水API
 */
export const waterApi = {
  /**
   * 记录饮水
   */
  recordWater(amount: number) {
    return request.post<void>('/api/water/record', null, {
      params: { amount }
    })
  },

  /**
   * 删除饮水记录
   */
  deleteRecord(recordId: number) {
    return request.delete<void>(`/api/water/record/${recordId}`)
  },

  /**
   * 获取今日饮水统计
   */
  getTodayStats() {
    return request.get<WaterDailyStats>('/api/water/today')
  },

  /**
   * 获取指定日期饮水记录
   */
  getRecordsByDate(date: string) {
    return request.get<WaterRecord[]>('/api/water/records', {
      params: { date }
    })
  },

  /**
   * 获取饮水提醒设置
   */
  getReminderSetting() {
    return request.get<WaterReminderSetting>('/api/water/setting')
  },

  /**
   * 更新饮水提醒设置
   */
  updateReminderSetting(setting: WaterReminderSetting) {
    return request.put<void>('/api/water/setting', setting)
  },

  /**
   * 获取最近7天饮水统计
   */
  getWeeklyStats() {
    return request.get<WaterDailyStats[]>('/api/water/weekly')
  }
}
