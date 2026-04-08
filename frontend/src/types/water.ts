/**
 * 饮水记录
 */
export interface WaterRecord {
  id: number
  amount: number
  recordTime: string
}

/**
 * 每日饮水统计
 */
export interface WaterDailyStats {
  date: string
  totalAmount: number
  dailyGoal: number
  completionRate: number
  recordCount: number
  records: WaterRecord[]
}

/**
 * 饮水提醒设置
 */
export interface WaterReminderSetting {
  dailyGoal: number
  reminderEnabled: boolean
  reminderInterval: number
  startTime: string
  endTime: string
}

/**
 * 快捷饮水量选项
 */
export const QUICK_AMOUNT_OPTIONS = [
  { label: '小杯', amount: 150, icon: '💧' },
  { label: '中杯', amount: 250, icon: '💧' },
  { label: '大杯', amount: 350, icon: '💧' },
  { label: '瓶装', amount: 500, icon: '🥤' },
  { label: '运动', amount: 600, icon: '🏃' },
  { label: '自定义', amount: 0, icon: '⚡' }
]

/**
 * 每日目标选项
 */
export const DAILY_GOAL_OPTIONS = [1500, 2000, 2500, 3000, 3500]

/**
 * 提醒间隔选项(分钟)
 */
export const REMINDER_INTERVAL_OPTIONS = [
  { label: '30分钟', value: 30 },
  { label: '1小时', value: 60 },
  { label: '1.5小时', value: 90 },
  { label: '2小时', value: 120 }
]
