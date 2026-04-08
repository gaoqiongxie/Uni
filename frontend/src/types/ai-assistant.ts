/**
 * AI智能助手类型定义
 */

/** 用户画像 */
export interface UserProfile {
  id?: number
  gender: number
  age: number
  height: number
  currentWeight: number
  targetWeight: number
  bodyType: string
  healthConditions: string
  allergies: string
  dietaryRestrictions: string
  activityLevel: string
  sleepHours: number
  stressLevel: number
  workSchedule: string
  foodPreferences: string
  foodDislikes: string
  cuisinePreference: string
  cookingSkill: number
  mealPrepTime: number
  exerciseExperience: string
  preferredExercise: string
  exerciseTime: string
  hasGymAccess: number
  targetTimeline: string
  primaryGoal: string
  motivation: string
  pastAttempts: string
  biggestChallenge: string
  isCompleted?: number
}

/** AI对话消息 */
export interface AiChatMessage {
  id?: number
  role: 'user' | 'assistant'
  content: string
  messageType: string
  relatedData?: any
  createdAt?: string
}

/** 个性化计划 */
export interface PersonalizedPlan {
  id?: number
  planType: string
  planTitle: string
  planContent: any
  dailyCalorieTarget?: number
  dailyWaterTarget?: number
  dailySleepTarget?: number
  weeklyExerciseDays?: number
  aiAnalysis?: string
  isActive?: number
  createdAt?: string
}

/** 生成计划请求 */
export interface GeneratePlanRequest {
  planType: string
  additionalRequirements?: string
}

/** 体型选项 */
export const BODY_TYPE_OPTIONS = [
  { value: 'slim', label: '偏瘦', desc: 'BMI < 18.5' },
  { value: 'normal', label: '正常', desc: 'BMI 18.5-23.9' },
  { value: 'slightly_overweight', label: '微胖', desc: 'BMI 24-27.9' },
  { value: 'overweight', label: '超重', desc: 'BMI 28-30' },
  { value: 'obese', label: '肥胖', desc: 'BMI > 30' },
]

/** 活动量选项 */
export const ACTIVITY_LEVEL_OPTIONS = [
  { value: 'sedentary', label: '久坐', desc: '几乎不运动，办公室工作' },
  { value: 'light', label: '轻度活动', desc: '每周运动1-2次' },
  { value: 'moderate', label: '中度活动', desc: '每周运动3-4次' },
  { value: 'active', label: '高度活跃', desc: '每周运动5-6次' },
  { value: 'very_active', label: '非常活跃', desc: '每天运动或体力劳动' },
]

/** 运动经验选项 */
export const EXERCISE_EXPERIENCE_OPTIONS = [
  { value: 'beginner', label: '新手', desc: '很少或从未规律运动' },
  { value: 'intermediate', label: '中级', desc: '有一定运动基础' },
  { value: 'advanced', label: '高级', desc: '经常运动，经验丰富' },
]

/** 目标时间选项 */
export const TIMELINE_OPTIONS = [
  { value: '1month', label: '1个月', desc: '快速见效' },
  { value: '3months', label: '3个月', desc: '稳步减脂' },
  { value: '6months', label: '6个月', desc: '健康塑形' },
  { value: '1year', label: '1年', desc: '彻底改变' },
]

/** 主要目标选项 */
export const PRIMARY_GOAL_OPTIONS = [
  { value: 'weight_loss', label: '减重', desc: '降低体重数字' },
  { value: 'body_fat', label: '减脂', desc: '降低体脂率' },
  { value: 'muscle_gain', label: '增肌', desc: '增加肌肉量' },
  { value: 'health', label: '健康', desc: '改善健康状况' },
  { value: 'energy', label: '精力充沛', desc: '提升日常精力' },
]

/** 最大挑战选项 */
export const CHALLENGE_OPTIONS = [
  { value: 'time', label: '没时间', desc: '工作学习太忙' },
  { value: 'willpower', label: '没毅力', desc: '难以坚持' },
  { value: 'knowledge', label: '不懂方法', desc: '不知道怎么做' },
  { value: 'hunger', label: '容易饿', desc: '控制不了食欲' },
  { value: 'social', label: '社交应酬', desc: '聚餐太多' },
]

/** 计划类型选项 */
export const PLAN_TYPE_OPTIONS = [
  { value: 'diet', label: '饮食计划', icon: '🥗', desc: '科学饮食方案' },
  { value: 'exercise', label: '运动计划', icon: '🏃', desc: '个性化运动安排' },
  { value: 'sleep', label: '睡眠改善', icon: '💤', desc: '提升睡眠质量' },
  { value: 'comprehensive', label: '综合计划', icon: '📋', desc: '全方位减脂方案' },
]

/**
 * 计算BMI
 */
export function calculateBMI(weight: number, height: number): number {
  const heightM = height / 100
  return Math.round((weight / (heightM * heightM)) * 10) / 10
}

/**
 * 获取BMI分类
 */
export function getBMICategory(bmi: number): string {
  if (bmi < 18.5) return '偏瘦'
  if (bmi < 24) return '正常'
  if (bmi < 28) return '超重'
  return '肥胖'
}

/**
 * 计算基础代谢率(BMR)
 */
export function calculateBMR(gender: number, weight: number, height: number, age: number): number {
  if (gender === 1) {
    // 男: 10W + 6.25H - 5A + 5
    return Math.round(10 * weight + 6.25 * height - 5 * age + 5)
  } else {
    // 女: 10W + 6.25H - 5A - 161
    return Math.round(10 * weight + 6.25 * height - 5 * age - 161)
  }
}

/**
 * 计算每日热量需求
 */
export function calculateTDEE(bmr: number, activityLevel: string): number {
  const multipliers: Record<string, number> = {
    sedentary: 1.2,
    light: 1.375,
    moderate: 1.55,
    active: 1.725,
    very_active: 1.9,
  }
  return Math.round(bmr * (multipliers[activityLevel] || 1.2))
}
