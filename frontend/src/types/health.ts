// 健康相关类型定义

export interface WeightRecordDTO {
  weight: number
  bodyFatRate?: number
  muscleRate?: number
  waterRate?: number
  waistCircumference?: number
  hipCircumference?: number
  recordDate: string
  recordType?: number
  remark?: string
}

export interface WeightRecordVO {
  id: number
  userId: number
  recordDate: string
  weight: number
  bodyFatRate?: number
  muscleRate?: number
  waterRate?: number
  waistCircumference?: number
  hipCircumference?: number
  bmi?: number
  weightChange?: number
  recordType: number
  remark?: string
  createTime: string
}

export interface WeightTrendVO {
  date: string
  weight: number
  bmi?: number
}

export interface WeightStatsVO {
  currentWeight?: number
  targetWeight?: number
  startWeight?: number
  totalLoss?: number
  remainingLoss?: number
  progress?: number
  weeklyChange?: number
  monthlyChange?: number
  trend: WeightTrendVO[]
}
