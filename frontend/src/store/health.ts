// store/health.ts — HealthStore (补充实现)
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { healthApi } from '../api/health.api'
import type { WeightRecord, WeightStats } from '../types/health'

export const useHealthStore = defineStore('health', () => {
  const weightList = ref<WeightRecord[]>([])
  const stats = ref<WeightStats | null>(null)

  async function loadWeightRecords(params: { page: number; pageSize: number }) {
    const data = await healthApi.getWeightList(params)
    if (params.page === 1) {
      weightList.value = data.records || []
    } else {
      weightList.value.push(...(data.records || []))
    }
    return data.records
  }

  async function addWeightRecord(dto: {
    weight: number
    recordDate: string
    note?: string
  }) {
    const record = await healthApi.addWeightRecord(dto)
    // 插入到列表头部
    weightList.value.unshift(record)
    return record
  }

  async function loadStats() {
    stats.value = await healthApi.getWeightStats()
  }

  return { weightList, stats, loadWeightRecords, addWeightRecord, loadStats }
})
