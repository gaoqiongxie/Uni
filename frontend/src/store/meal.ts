import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { mealApi } from '../api/meal.api'
import { fileApi } from '../api/file.api'
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
   * 支持先上传图片，再将 attachmentIds 传给后端
   */
  async function addMealRecord(data: {
    mealDate: string
    mealType: number
    foodName: string
    amount?: string
    calorieEstimate?: number
    imagePath?: string    // 本地临时图片路径（会先上传到服务器）
    attachmentIds?: string // 已有的附件ID
  }) {
    // 如果有本地图片，先上传
    let attachmentIds = data.attachmentIds || ''
    if (data.imagePath) {
      try {
        const fileVO = await fileApi.uploadImage(data.imagePath, 1, 2)
        attachmentIds = String(fileVO.id)
      } catch (e) {
        console.warn('图片上传失败，继续提交餐食记录', e)
      }
    }

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
      calorieEstimate: data.calorieEstimate,
      recordSource: data.imagePath ? 2 : 1 // 有图片则为拍照识别
    }
    if (attachmentIds) {
      dto.attachmentIds = attachmentIds
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
