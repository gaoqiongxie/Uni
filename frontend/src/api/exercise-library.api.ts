// 运动消耗计算 API 封装
// @author 高琼
// @date 2026-04-14

import http from '@/utils/http'
import type { ExerciseLibraryItem, CalcCaloriesRequest, CaloriesBurnedResult } from '@/types/exercise-library'

/** 获取常用运动列表 */
export const getCommonExercises = () =>
  http.get<ExerciseLibraryItem[]>('/exercise-library/common')

/** 按分类获取运动列表 */
export const getExercisesByCategory = (category: string) =>
  http.get<ExerciseLibraryItem[]>('/exercise-library/category', { params: { category } })

/** 搜索运动 */
export const searchExercises = (keyword: string) =>
  http.get<ExerciseLibraryItem[]>('/exercise-library/search', { params: { keyword } })

/** 计算运动消耗热量 */
export const calcCaloriesBurned = (data: CalcCaloriesRequest) =>
  http.post<CaloriesBurnedResult>('/exercise-library/calc-calories', data)
