// 运动消耗计算 TypeScript 类型定义
// @author 高琼
// @date 2026-04-14

/** 运动强度 */
export type ExerciseIntensity = 'low' | 'moderate' | 'high'

/** 运动分类 */
export type ExerciseCategory = 'aerobic' | 'strength' | 'flexibility' | 'sports' | 'daily' | 'other'

/** 运动库项目 */
export interface ExerciseLibraryItem {
  id: number
  exerciseName: string
  category: ExerciseCategory
  categoryName: string
  metValue: number
  intensity: ExerciseIntensity
  intensityName: string
  description: string
  iconUrl?: string
  isCommon: number
}

/** 运动消耗热量计算请求 */
export interface CalcCaloriesRequest {
  exerciseId: number
  weight: number       // 体重 kg
  duration: number     // 运动时长 分钟
}

/** 运动消耗热量计算结果 */
export interface CaloriesBurnedResult {
  exerciseId: number
  exerciseName: string
  metValue: number
  weight: number
  duration: number
  caloriesBurned: number
  caloriesDescription: string
}

/** 搜索运动请求 */
export interface SearchExerciseRequest {
  keyword?: string
  category?: ExerciseCategory
}

/** 分类展示项 */
export interface CategoryOption {
  value: string
  label: string
  icon: string
}

export const EXERCISE_CATEGORIES: CategoryOption[] = [
  { value: 'all',         label: '全部',     icon: '🏅' },
  { value: 'aerobic',     label: '有氧运动', icon: '🏃' },
  { value: 'strength',    label: '力量训练', icon: '💪' },
  { value: 'sports',      label: '球类运动', icon: '⚽' },
  { value: 'daily',       label: '日常活动', icon: '🚶' },
  { value: 'flexibility', label: '柔韧性',   icon: '🧘' },
  { value: 'other',       label: '其他',     icon: '🎯' },
]

export const INTENSITY_CONFIG: Record<ExerciseIntensity, { label: string; color: string }> = {
  low:      { label: '低强度', color: '#52c41a' },
  moderate: { label: '中等强度', color: '#faad14' },
  high:     { label: '高强度', color: '#ff4d4f' },
}
