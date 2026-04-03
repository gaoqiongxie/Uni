import { http } from '../utils/request'
import type { WeeklyReport, MonthlyReport } from '../types/report'

export const reportApi = {
  /**
   * 获取周减脂报告
   */
  getWeeklyReport(weekOffset: number = 0) {
    return http.get<WeeklyReport>('/api/report/weekly', { weekOffset })
  },

  /**
   * 获取月减脂报告
   */
  getMonthlyReport(monthOffset: number = 0) {
    return http.get<MonthlyReport>('/api/report/monthly', { monthOffset })
  }
}
