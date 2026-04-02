/** 目标类型 */
export const GOAL_TYPES = [
  { value: 1, label: '减重', icon: '⬇️', desc: '逐步减轻体重，塑造健康体型' },
  { value: 2, label: '维持', icon: '🔄', desc: '保持现有体重，养成健康习惯' },
  { value: 3, label: '增肌', icon: '💪', desc: '增加肌肉量，提升体能表现' }
] as const

/** 目标状态 */
export const GOAL_STATUS = {
  ABANDONED: 0,
  ACTIVE: 1,
  ACHIEVED: 2
} as const

/** 创建目标 DTO */
export interface UserGoalDTO {
  goalType: number
  targetWeight: number
  calorieGoal?: number
  exerciseDaysPerWeek: number
  exerciseMinutesPerDay: number
  targetDate?: string
  remark?: string
}

/** 目标 VO */
export interface UserGoalVO {
  id: number
  goalType: number
  goalTypeName: string
  startWeight: number
  targetWeight: number
  weightGap: number
  calorieGoal: number
  exerciseDaysPerWeek: number
  exerciseMinutesPerDay: number
  startDate: string
  targetDate: string | null
  status: number
  statusName: string
  achievedDate: string | null
  remark: string
  // 进度
  currentWeight: number
  weightLost: number
  progressPercent: number
  persistDays: number
  remainDays: number
  checkinDays: number | null
  checkinRate: number | null
}
