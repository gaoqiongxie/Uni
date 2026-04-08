/**
 * 睡眠质量
 */
export type SleepQuality = 1 | 2 | 3 | 4 | 5

export const SLEEP_QUALITY_OPTIONS = [
  { value: 1 as SleepQuality, label: '很差', emoji: '😫', color: '#ff4444' },
  { value: 2 as SleepQuality, label: '较差', emoji: '😔', color: '#ff8800' },
  { value: 3 as SleepQuality, label: '一般', emoji: '😐', color: '#ffcc00' },
  { value: 4 as SleepQuality, label: '较好', emoji: '🙂', color: '#66cc66' },
  { value: 5 as SleepQuality, label: '很好', emoji: '😴', color: '#44aa44' }
]

/**
 * 睡眠影响因素
 */
export const SLEEP_FACTORS = [
  { key: 'caffeine', label: '咖啡因', icon: '☕' },
  { key: 'screen', label: '电子屏幕', icon: '📱' },
  { key: 'exercise', label: '运动', icon: '🏃' },
  { key: 'stress', label: '压力', icon: '😰' },
  { key: 'late_meal', label: '晚餐过晚', icon: '🍽️' },
  { key: 'alcohol', label: '饮酒', icon: '🍺' },
  { key: 'noise', label: '噪音', icon: '🔊' },
  { key: 'temperature', label: '温度不适', icon: '🌡️' }
]

/**
 * 睡眠记录
 */
export interface SleepRecord {
  id?: number
  sleepDate: string
  bedTime: string
  wakeTime: string
  sleepDuration: number
  deepSleep?: number
  lightSleep?: number
  awakeDuration?: number
  sleepQuality: SleepQuality
  sleepQualityText?: string
  sleepScore?: number
  factors?: string
  notes?: string
  bedTimeStr?: string
  wakeTimeStr?: string
  durationStr?: string
}

/**
 * 睡眠目标
 */
export interface SleepGoal {
  targetDuration: number
  targetBedTime: string
  targetWakeTime: string
  reminderEnabled: boolean
  bedReminder: boolean
  wakeReminder: boolean
}

/**
 * 睡眠统计
 */
export interface SleepStats {
  avgDuration: number
  avgDurationStr: string
  avgQuality: number
  goalAchievementRate: number
  totalRecords: number
  recentRecords: SleepRecord[]
  weeklyStats: DailySleepStats[]
}

/**
 * 每日睡眠统计
 */
export interface DailySleepStats {
  date: string
  duration: number
  quality: number
  score: number
}

/**
 * 目标时长选项(分钟)
 */
export const TARGET_DURATION_OPTIONS = [
  { label: '6小时', value: 360 },
  { label: '6.5小时', value: 390 },
  { label: '7小时', value: 420 },
  { label: '7.5小时', value: 450 },
  { label: '8小时', value: 480 },
  { label: '8.5小时', value: 510 },
  { label: '9小时', value: 540 }
]

/**
 * 格式化睡眠时长
 */
export function formatDuration(minutes: number): string {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins > 0 ? mins + '分' : ''}`
  }
  return `${mins}分钟`
}

/**
 * 获取睡眠质量文本
 */
export function getQualityText(quality: SleepQuality): string {
  const option = SLEEP_QUALITY_OPTIONS.find(o => o.value === quality)
  return option?.label || '一般'
}

/**
 * 获取睡眠质量颜色
 */
export function getQualityColor(quality: SleepQuality): string {
  const option = SLEEP_QUALITY_OPTIONS.find(o => o.value === quality)
  return option?.color || '#999'
}
