import { http } from '../utils/http'
import type { UserGoalDTO, UserGoalVO } from '../types/goal'

/** 创建目标 */
export const createGoal = (data: UserGoalDTO) =>
  http.post<UserGoalVO>('/api/goal', data)

/** 获取当前进行中目标 */
export const getActiveGoal = () =>
  http.get<UserGoalVO>('/api/goal/active')

/** 目标历史列表 */
export const listGoals = () =>
  http.get<UserGoalVO[]>('/api/goal/list')

/** 放弃目标 */
export const abandonGoal = (id: number) =>
  http.post<boolean>(`/api/goal/${id}/abandon`)

/** 标记目标达成 */
export const achieveGoal = (id: number) =>
  http.post<boolean>(`/api/goal/${id}/achieve`)
