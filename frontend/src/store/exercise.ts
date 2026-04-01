import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { exerciseApi } from '../api/exercise.api'
import type { ExerciseRecordDTO, ExerciseRecordVO, ExerciseSummaryVO } from '../types/exercise'

export const useExerciseStore = defineStore('exercise', () => {
  const todayExercises = ref<ExerciseRecordVO[]>([])
  // recordsData: { [dateStr]: ExerciseRecordVO[] }
  const recordsData = reactive<Record<string, ExerciseRecordVO[]>>({})
  const summary = ref<ExerciseSummaryVO | null>(null)
  const loading = ref(false)

  /** 创建运动记录 */
  async function createExerciseRecord(data: ExerciseRecordDTO) {
    loading.value = true
    try {
      const result = await exerciseApi.create(data)
      todayExercises.value.push(result)
      if (!recordsData[data.recordDate]) recordsData[data.recordDate] = []
      recordsData[data.recordDate].push(result)
      return result
    } finally {
      loading.value = false
    }
  }

  /** 加载某天的运动记录 */
  async function loadExercisesByDate(date: string) {
    loading.value = true
    try {
      const records = await exerciseApi.getByDate(date)
      todayExercises.value = records
      recordsData[date] = records
    } finally {
      loading.value = false
    }
  }

  /** 加载日期范围的运动记录 */
  async function loadExercisesByRange(params: { startDate: string; endDate: string }) {
    loading.value = true
    try {
      const data = await exerciseApi.getByRange(params)
      Object.assign(recordsData, data)
      return data
    } finally {
      loading.value = false
    }
  }

  /** 加载运动汇总 */
  async function loadSummary(params: { startDate: string; endDate: string }) {
    summary.value = await exerciseApi.getSummary(params)
    return summary.value
  }

  /** 删除运动记录 */
  async function deleteExerciseRecord(id: number) {
    await exerciseApi.delete(id)
    todayExercises.value = todayExercises.value.filter(r => r.id !== id)
    for (const date of Object.keys(recordsData)) {
      recordsData[date] = recordsData[date].filter(r => r.id !== id)
    }
  }

  return {
    todayExercises,
    recordsData,
    summary,
    loading,
    createExerciseRecord,
    loadExercisesByDate,
    loadExercisesByRange,
    loadSummary,
    deleteExerciseRecord
  }
})
