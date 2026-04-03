// 周减脂报告
export interface WeeklyReport {
  weekRange: string
  startDate: string
  endDate: string
  weightChange: {
    startWeight: number
    endWeight: number
    change: number
    changePercent: number
    direction: number // -1下降 0持平 1上升
  }
  calorieStats: {
    avgIntake: number
    goal: number
    达标Days: number
    totalIntake: number
  }
  exerciseSummary: {
    exerciseDays: number
    totalDuration: number
    totalBurn: number
    avgBurn: number
    topExerciseType: string
  }
  checkinStats: {
    checkinDays: number
    mealCheckins: number
    exerciseCheckins: number
    streakDays: number
  }
  dailyDataList: DailyData[]
}

export interface DailyData {
  date: string
  weekday: string
  weight: number
  calorieIntake: number
  exerciseMinutes: number
  calorieBurn: number
  goalAchieved: boolean
}

// 月减脂报告
export interface MonthlyReport {
  month: string
  startDate: string
  endDate: string
  overview: {
    totalWeightLoss: number
    avgDailyCalorie: number
    totalExerciseHours: number
    totalBurn: number
    checkinDays: number
    goalAchievementRate: number
  }
  weightTrend: {
    startWeight: number
    endWeight: number
    minWeight: number
    maxWeight: number
    avgWeight: number
    trend: string
    trendData: TrendPoint[]
  }
  calorieAnalysis: {
    avgIntake: number
    goal: number
    overDays: number
    达标Days: number
    avgBreakfast: number
    avgLunch: number
    avgDinner: number
  }
  exerciseAnalysis: {
    exerciseDays: number
    exerciseFrequency: number
    totalDuration: number
    avgDuration: number
    totalBurn: number
    avgBurn: number
    typeDistribution: ExerciseType[]
  }
  achievementRate: {
    weightGoalRate: number
    calorieGoalRate: number
    exerciseGoalRate: number
    overallRate: number
    rating: string
  }
  weeklySummaries: WeeklySummary[]
}

export interface TrendPoint {
  date: string
  weight: number
}

export interface ExerciseType {
  type: string
  count: number
  minutes: number
  burn: number
  percentage: number
}

export interface WeeklySummary {
  weekRange: string
  weightChange: number
  avgCalorie: number
  exerciseMinutes: number
  checkinDays: number
}
