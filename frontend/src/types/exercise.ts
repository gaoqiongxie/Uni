// 运动记录相关类型定义

export interface ExerciseRecordVO {
  id: number
  userId: number
  recordDate: string
  exerciseType: string
  duration: number
  calorieBurn: number
  distance?: number
  heartRateAvg?: number
  heartRateMax?: number
  intensity: number
  intensityName: string
  feeling: number
  feelingName: string
  remark?: string
  createTime?: string
  photos?: string[]
}

export interface ExerciseRecordDTO {
  recordDate: string
  exerciseType: string
  duration: number
  calorieBurn?: number
  distance?: number
  heartRateAvg?: number
  heartRateMax?: number
  intensity?: number
  feeling?: number
  attachmentIds?: string
  recordSource?: number
  remark?: string
}

export interface ExerciseSummaryVO {
  totalCount: number
  totalDuration: number
  totalCalorieBurn: number
  totalDistance: number
  avgDuration: number
  avgCalorieBurn: number
}

/** 运动类型预设列表 */
export const EXERCISE_TYPES = [
  { label: '跑步', value: '跑步', icon: '🏃' },
  { label: '游泳', value: '游泳', icon: '🏊' },
  { label: '瑜伽', value: '瑜伽', icon: '🧘' },
  { label: '力量训练', value: '力量训练', icon: '💪' },
  { label: '骑行', value: '骑行', icon: '🚴' },
  { label: '跳绳', value: '跳绳', icon: '⏭' },
  { label: '散步', value: '散步', icon: '🚶' },
  { label: '其他', value: '其他', icon: '⛹' }
] as const

/** 运动强度列表 */
export const INTENSITY_OPTIONS = [
  { label: '低强度', value: 1 },
  { label: '中等强度', value: 2 },
  { label: '高强度', value: 3 }
] as const

/** 运动感受列表 */
export const FEELING_OPTIONS = [
  { label: '一般', value: 0 },
  { label: '轻松', value: 1 },
  { label: '适中', value: 2 },
  { label: '吃力', value: 3 },
  { label: '很累', value: 4 }
] as const
