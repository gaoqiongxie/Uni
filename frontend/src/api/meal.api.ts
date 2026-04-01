import http from '../utils/request'
import type {
  MealRecordDTO,
  MealRecordVO,
  MealCalendarVO
} from '../types/meal'

export const mealApi = {
  createMealRecord: (data: MealRecordDTO) =>
    http.post<MealRecordVO>('/api/meal-record', data),

  getMealRecordsByDate: (date: string) =>
    http.get<MealRecordVO[]>(`/api/meal-record/date/${date}`),

  getMealCalendar: (year: number, month: number) =>
    http.get<MealCalendarVO>('/api/meal-record/calendar', { year, month }),

  /**
   * 获取指定区间的打卡数据，返回 { [dateStr]: MealRecordVO[] }
   */
  getMealCalendarRange: (params: { startDate: string; endDate: string }) =>
    http.get<Record<string, MealRecordVO[]>>('/api/meal-record/calendar/range', params),

  deleteMealRecord: (id: number) =>
    http.delete<void>(`/api/meal-record/${id}`)
}
