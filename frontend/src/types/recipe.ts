/** 食材VO */
export interface IngredientVO {
  id: number
  name: string
  amount: string
  calorie: number
}

/** 食谱VO */
export interface RecipeVO {
  id: number
  name: string
  category: string
  categoryName: string
  description: string
  coverImage: string
  calorie: number
  protein: number
  fat: number
  carbohydrate: number
  fiber: number
  cookingTime: number
  difficulty: number
  difficultyName: string
  steps: string[]
  tagList: string[]
  servings: number
  likeCount: number
  favorited: boolean
  ingredients?: IngredientVO[]
}

/** 食谱分类 */
export interface RecipeCategory {
  key: string
  label: string
  icon: string
}

/** 食谱分类列表 */
export const RECIPE_CATEGORIES: RecipeCategory[] = [
  { key: '', label: '全部', icon: '🍽️' },
  { key: '早餐', label: '早餐', icon: '🌅' },
  { key: '午餐', label: '午餐', icon: '☀️' },
  { key: '晚餐', label: '晚餐', icon: '🌙' },
  { key: '加餐', label: '加餐', icon: '🍎' },
  { key: '低卡', label: '低卡', icon: '🥗' },
  { key: '高蛋白', label: '高蛋白', icon: '🥩' }
]
