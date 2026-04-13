/**
 * 食物相关类型定义
 * @author 高琼
 * @date 2026-04-13
 */

/** 食物分类 */
export type FoodCategory = 'staple' | 'meat' | 'vegetable' | 'fruit' | 'snack' | 'drink' | 'other';

/** 餐次类型 */
export type MealType = 'breakfast' | 'lunch' | 'dinner' | 'snack';

/** 食物库 */
export interface FoodLibrary {
  id: number;
  foodName: string;
  category: FoodCategory;
  categoryName: string;
  caloriesPer100g: number;
  proteinPer100g: number;
  fatPer100g: number;
  carbsPer100g: number;
  unit: string;
  unitWeight: number;
  commonPortion: string;
  isCommon: number;
  iconUrl?: string;
}

/** 食物摄入记录 */
export interface FoodDailyLog {
  id: number;
  recordDate: string;
  mealType: MealType;
  mealTypeName: string;
  foodId?: number;
  foodName: string;
  calories: number;
  amount: number;
  unit: string;
  protein: number;
  fat: number;
  carbs: number;
  photoUrl?: string;
  remark?: string;
  createTime: string;
}

/** 每日热量汇总 */
export interface DailyCalorieSummary {
  date: string;
  breakfastCalories: number;
  lunchCalories: number;
  dinnerCalories: number;
  snackCalories: number;
  totalCalories: number;
  totalProtein: number;
  totalFat: number;
  totalCarbs: number;
  mealRecords: Record<MealType, FoodDailyLog[]>;
  nutritionRatio: NutritionRatio;
}

/** 营养素占比 */
export interface NutritionRatio {
  proteinRatio: number;
  fatRatio: number;
  carbsRatio: number;
}

/** 食物分类选项 */
export interface FoodCategoryOption {
  code: FoodCategory;
  name: string;
  icon: string;
}

/** 添加食物记录参数 */
export interface AddFoodLogParams {
  recordDate: string;
  mealType: MealType;
  foodId?: number;
  foodName: string;
  calories: number;
  amount: number;
  unit: string;
  protein?: number;
  fat?: number;
  carbs?: number;
  photoUrl?: string;
  remark?: string;
}

/** 搜索食物参数 */
export interface SearchFoodParams {
  keyword?: string;
  category?: FoodCategory;
  commonOnly?: boolean;
}

/** 餐次配置 */
export const MEAL_CONFIG: Record<MealType, { name: string; icon: string; color: string }> = {
  breakfast: { name: '早餐', icon: '🌅', color: '#FF9500' },
  lunch: { name: '午餐', icon: '☀️', color: '#34C759' },
  dinner: { name: '晚餐', icon: '🌙', color: '#5856D6' },
  snack: { name: '加餐', icon: '🍿', color: '#FF2D55' }
};

/** 食物分类配置 */
export const FOOD_CATEGORIES: FoodCategoryOption[] = [
  { code: 'staple', name: '主食', icon: '🍚' },
  { code: 'meat', name: '肉蛋奶', icon: '🥩' },
  { code: 'vegetable', name: '蔬菜', icon: '🥬' },
  { code: 'fruit', name: '水果', icon: '🍎' },
  { code: 'snack', name: '零食', icon: '🍪' },
  { code: 'drink', name: '饮品', icon: '🥤' },
  { code: 'other', name: '其他', icon: '🍽️' }
];
