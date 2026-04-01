import http from '../utils/request'
import type {
  WeightRecordDTO,
  WeightRecordVO,
  WeightStatsVO
} from '../types/health'

export const healthApi = {
  createWeightRecord: (data: WeightRecordDTO) =>
    http.post<WeightRecordVO>('/api/weight-record', data),

  /**
   * 分页查询体重列表
   */
  getWeightList: (params: { page: number; pageSize: number }) =>
    http.get<{ records: WeightRecordVO[]; total: number }>('/api/weight-record/page', params),

  getWeightRecords: (params?: {
    page?: number
    size?: number
    startDate?: string
    endDate?: string
  }) => http.get<WeightRecordVO[]>('/api/weight-record/list', params),

  getWeightStats: () =>
    http.get<WeightStatsVO>('/api/weight-record/stats'),

  /**
   * 添加体重记录（store 调用入口）
   */
  addWeightRecord: (data: { weight: number; recordDate: string; note?: string }) =>
    http.post<WeightRecordVO>('/api/weight-record', data),

  deleteWeightRecord: (id: number) =>
    http.delete<void>(`/api/weight-record/${id}`)
}
