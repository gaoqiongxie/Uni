import http from '../utils/request'
import type { RecipeVO } from '../types/recipe'

export const recipeApi = {
  /** 推荐食谱列表 */
  recommend: (params: {
    category?: string
    maxCalorie?: number
    page?: number
    size?: number
  }) =>
    http.get<RecipeVO[]>('/api/recipe/recommend', params),

  /** 食谱详情 */
  getDetail: (id: number) =>
    http.get<RecipeVO>(`/api/recipe/${id}`),

  /** 搜索食谱 */
  search: (params: {
    keyword: string
    page?: number
    size?: number
  }) =>
    http.get<RecipeVO[]>('/api/recipe/search', params),

  /** 收藏/取消收藏 */
  toggleFavorite: (recipeId: number) =>
    http.post<boolean>(`/api/recipe/favorite/${recipeId}`),

  /** 我的收藏列表 */
  getFavorites: (params?: { page?: number; size?: number }) =>
    http.get<RecipeVO[]>('/api/recipe/favorites', params || {})
}
