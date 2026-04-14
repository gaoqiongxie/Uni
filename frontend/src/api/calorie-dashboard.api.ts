// 热量收支看板 API 封装
// @author 高琼
// @date 2026-04-14

import http from '@/utils/http'
import type { CalorieDashboardVO, CalorieDailyBalance, CalorieTrendPoint } from '@/types/calorie-dashboard'

/** 获取今日热量看板数据 */
export const getTodayCalorieDashboard = () =>
  http.get<CalorieDashboardVO>('/calorie-dashboard/today')

/** 获取指定日期热量数据 */
export const getCalorieByDate = (date: string) =>
  http.get<CalorieDailyBalance>('/calorie-dashboard/date', { params: { date } })

/** 获取最近7天热量趋势 */
export const getWeeklyTrend = () =>
  http.get<CalorieTrendPoint[]>('/calorie-dashboard/weekly-trend')

/** 获取最近30天热量趋势 */
export const getMonthlyTrend = () =>
  http.get<CalorieTrendPoint[]>('/calorie-dashboard/monthly-trend')
