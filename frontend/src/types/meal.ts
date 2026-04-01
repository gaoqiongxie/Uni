// 餐食相关类型定义

export interface MealRecordDTO {
  recordDate: string
  mealType: number
  mealTime: string
  mealContent: string
  calorieEstimate?: number
  moodType?: number
  attachmentIds?: string
  recordSource?: number
  isHealthy?: number
  remark?: string
}

export interface MealRecordVO {
  id: number
  userId: number
  recordDate: string
  mealType: number
  mealTypeName?: string
  mealTime: string
  mealContent: string
  calorieEstimate: number
  moodType: number
  isHealthy: number
  remark?: string
  createTime: string
  photos?: string[]
}

export interface CalendarDayVO {
  date: string
  checkIn: boolean
  mealCount: number
}

export interface MealCalendarVO {
  year: number
  month: number
  totalDays: number
  checkInDays: number
  checkInRate: number
  currentStreak: number
  maxStreak: number
  calendar: CalendarDayVO[]
}
