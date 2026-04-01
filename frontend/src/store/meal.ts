import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { mealApi } from '../api/meal.api'
import type { MealRecordDTO, MealRecordVO, MealCalendarVO } from '../types/meal'

export const useMealStore = defineStore('meal', () => {
  const todayMeals = ref<MealRecordVO[]>([])
  const calendar = ref<MealCalendarVO | null>(null)
  // calendarData: { [dateStr]: MealRecordVO[] }
  const calendarData = reactive<Record<string, MealRecordVO[]>>({})
  const loading = ref(false)

  async function createMealRecord(data: MealRecordDTO) {
    loading.value = true
    try {
      const result = await mealApi.createMealRecord(data)
      todayMeals.value.push(result)
      return result
    } finally {
      loading.value = false
    }
  }

  /**
   * 添加餐食记录（页面调用统一入口）
   * 页面传入 foodName/amount，这里转换为后端需要的 mealContent/mealTime
   */
  async function addMealRecord(data: {
    mealDate: string
    mealType: number
    foodName: string
    amount?: string
    calorieEstimate?: number
    imageUrl?: string
  }) {
    // 拼接 mealContent：foodName + amount
    let mealContent = data.foodName
    if (data.amount) {
      mealContent += ` (${data.amount})`
    }
    const dto: MealRecordDTO = {
      recordDate: data.mealDate,
      mealType: data.mealType,
      mealTime: new Date().toTimeString().substring(0, 5), // HH:mm
      mealContent: mealContent,
      calorieEstimate: data.calorieEstimate
    }
    const result = await mealApi.createMealRecord(dto)
    todayMeals.value.push(result)
    // 同步 calendarData
    if (!calendarData[data.mealDate]) calendarData[data.mealDate] = []
    calendarData[data.mealDate].push(result)
    return result
  }

  async function loadTodayMeals(date: string) {
    loading.value = true
    try {
      const meals = await mealApi.getMealRecordsByDate(date)
      todayMeals.value = meals
      calendarData[date] = meals
    } finally {
      loading.value = false
    }
  }

  async function loadCalendar(year: number, month: number) {
    calendar.value = await mealApi.getMealCalendar(year, month)
  }

  /**
   * 加载日历区间数据，填充 calendarData
   */
  async function loadCalendarData(params: { startDate: string; endDate: string }) {
    const data = await mealApi.getMealCalendarRange(params)
    // data: { [dateStr]: MealRecordVO[] }
    Object.assign(calendarData, data)
    return data
  }

  async function deleteMealRecord(id: number) {
    await mealApi.deleteMealRecord(id)
    todayMeals.value = todayMeals.value.filter(r => r.id !== id)
    // 从 calendarData 中同步删除
    for (const date of Object.keys(calendarData)) {
      calendarData[date] = calendarData[date].filter(r => r.id !== id)
    }
  }

  return {
    todayMeals,
    calendar,
    calendarData,
    loading,
    createMealRecord,
    addMealRecord,
    loadTodayMeals,
    loadCalendar,
    loadCalendarData,
    deleteMealRecord
  }
})
