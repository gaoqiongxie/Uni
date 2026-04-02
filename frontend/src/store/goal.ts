import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as goalApi from '../api/goal.api'
import type { UserGoalVO, UserGoalDTO } from '../types/goal'

export const useGoalStore = defineStore('goal', () => {
  const activeGoal = ref<UserGoalVO | null>(null)
  const goalHistory = ref<UserGoalVO[]>([])
  const loading = ref(false)

  /** 加载当前目标 */
  async function loadActiveGoal() {
    loading.value = true
    try {
      activeGoal.value = await goalApi.getActiveGoal()
    } catch (e: any) {
      activeGoal.value = null
    } finally {
      loading.value = false
    }
  }

  /** 加载目标历史 */
  async function loadGoalHistory() {
    try {
      goalHistory.value = await goalApi.listGoals()
    } catch (e: any) {
      goalHistory.value = []
    }
  }

  /** 创建新目标 */
  async function createGoal(dto: UserGoalDTO) {
    const result = await goalApi.createGoal(dto)
    activeGoal.value = result
    // 重新加载历史
    await loadGoalHistory()
    return result
  }

  /** 放弃目标 */
  async function abandonGoal(id: number) {
    await goalApi.abandonGoal(id)
    activeGoal.value = null
    await loadGoalHistory()
  }

  /** 标记达成 */
  async function achieveGoal(id: number) {
    await goalApi.achieveGoal(id)
    await loadActiveGoal()
    await loadGoalHistory()
  }

  return {
    activeGoal,
    goalHistory,
    loading,
    loadActiveGoal,
    loadGoalHistory,
    createGoal,
    abandonGoal,
    achieveGoal
  }
})
