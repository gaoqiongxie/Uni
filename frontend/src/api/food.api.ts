/**
 * 食物相关API
 * @author 高琼
 * @date 2026-04-13
 */

import { http } from '@/utils/http';
import type {
  FoodLibrary,
  FoodDailyLog,
  DailyCalorieSummary,
  AddFoodLogParams,
  SearchFoodParams,
  MealType
} from '@/types/food';

/** 获取食物分类 */
export const getFoodCategories = () => {
  return http.get<{ code: string; name: string }[]>('/api/food/categories');
};

/** 根据分类获取食物列表 */
export const getFoodsByCategory = (category: string) => {
  return http.get<FoodLibrary[]>(`/api/food/library/category/${category}`);
};

/** 搜索食物 */
export const searchFoods = (params: SearchFoodParams) => {
  return http.post<FoodLibrary[]>('/api/food/library/search', params);
};

/** 获取常用食物 */
export const getCommonFoods = () => {
  return http.get<FoodLibrary[]>('/api/food/library/common');
};

/** 添加食物记录 */
export const addFoodLog = (params: AddFoodLogParams) => {
  return http.post<number>('/api/food/log', params);
};

/** 删除食物记录 */
export const deleteFoodLog = (logId: number) => {
  return http.delete<void>(`/api/food/log/${logId}`);
};

/** 获取某餐的食物记录 */
export const getMealRecords = (date: string, mealType: MealType) => {
  return http.get<FoodDailyLog[]>('/api/food/log/meal', { date, mealType });
};

/** 获取每日热量汇总 */
export const getDailySummary = (date: string) => {
  return http.get<DailyCalorieSummary>('/api/food/summary/daily', { date });
};

/** 计算食物热量 */
export const calculateCalories = (foodId: number, amount: number) => {
  return http.get<number>('/api/food/calculate', { foodId, amount });
};
