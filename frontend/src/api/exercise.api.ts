import http from '../utils/request'
import type { ExerciseRecordDTO, ExerciseRecordVO, ExerciseSummaryVO } from '../types/exercise'

export const exerciseApi = {
  /** 创建运动记录 */
  create: (data: ExerciseRecordDTO) =>
    http.post<ExerciseRecordVO>('/api/exercise-record', data),

  /** 获取某天的运动记录 */
  getByDate: (date: string) =>
    http.get<ExerciseRecordVO[]>(`/api/exercise-record/date/${date}`),

  /** 获取日期范围的运动记录 */
  getByRange: (params: { startDate: string; endDate: string }) =>
    http.get<Record<string, ExerciseRecordVO[]>>('/api/exercise-record/range', params),

  /** 获取运动汇总统计 */
  getSummary: (params: { startDate: string; endDate: string }) =>
    http.get<ExerciseSummaryVO>('/api/exercise-record/summary', params),

  /** 删除运动记录 */
  delete: (id: number) =>
    http.delete<void>(`/api/exercise-record/${id}`)
}
