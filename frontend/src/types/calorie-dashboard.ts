// 热量收支看板 TypeScript 类型定义
// @author 高琼
// @date 2026-04-14

/** 每日热量收支汇总 */
export interface CalorieDailyBalance {
  date: string
  /** 目标热量(千卡) */
  targetCalories: number
  /** 摄入热量(千卡) */
  intakeCalories: number
  /** 运动消耗热量(千卡) */
  burnedCalories: number
  /** 净摄入 = 摄入 - 运动消耗 */
  netCalories: number
  /** 热量缺口 = 净摄入 - 目标 (负数=赤字) */
  calorieGap: number
  /** 基础代谢(千卡) */
  bmr: number
  /** 蛋白质摄入(g) */
  proteinIntake: number
  /** 脂肪摄入(g) */
  fatIntake: number
  /** 碳水摄入(g) */
  carbsIntake: number
}

/** 周/月趋势数据点 */
export interface CalorieTrendPoint {
  date: string
  label: string
  intake: number
  burned: number
  net: number
}

/** 热量看板 VO */
export interface CalorieDashboardVO {
  today: CalorieDailyBalance
  weekTrend: CalorieTrendPoint[]
  weekAvgIntake: number
  weekAvgBurned: number
  weekCumulativeGap: number
  streakDays: number  // 连续达标天数
}

/** 营养素分布 */
export interface NutrientDistribution {
  protein: number
  fat: number
  carbs: number
  proteinPercent: number
  fatPercent: number
  carbsPercent: number
}
