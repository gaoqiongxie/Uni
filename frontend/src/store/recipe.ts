import { defineStore } from 'pinia'
import { ref } from 'vue'
import { recipeApi } from '../api/recipe.api'
import type { RecipeVO } from '../types/recipe'

export const useRecipeStore = defineStore('recipe', () => {
  const recipes = ref<RecipeVO[]>([])
  const favorites = ref<RecipeVO[]>([])
  const loading = ref(false)
  const loadingMore = ref(false)
  const hasMore = ref(true)
  const currentPage = ref(1)
  const currentCategory = ref('')
  const searchKeyword = ref('')

  /**
   * 加载推荐食谱
   */
  async function loadRecommend(params: {
    category?: string
    maxCalorie?: number
    reset?: boolean
  }) {
    if (params.reset) {
      currentPage.value = 1
      hasMore.value = true
      recipes.value = []
    }

    if (params.category !== undefined) {
      currentCategory.value = params.category
    }

    if (recipes.value.length > 0) {
      loadingMore.value = true
    } else {
      loading.value = true
    }

    try {
      const result = await recipeApi.recommend({
        category: params.category ?? currentCategory.value,
        maxCalorie: params.maxCalorie,
        page: currentPage.value,
        size: 10
      })

      if (params.reset) {
        recipes.value = result
      } else {
        recipes.value.push(...result)
      }

      hasMore.value = result.length >= 10
      if (result.length > 0) {
        currentPage.value++
      }
    } finally {
      loading.value = false
      loadingMore.value = false
    }
  }

  /**
   * 加载更多
   */
  async function loadMore(maxCalorie?: number) {
    if (!hasMore.value || loadingMore.value) return
    await loadRecommend({ maxCalorie })
  }

  /**
   * 搜索食谱
   */
  async function searchRecipes(keyword: string, reset = true) {
    if (!keyword.trim()) return
    searchKeyword.value = keyword

    if (reset) {
      currentPage.value = 1
      recipes.value = []
    }

    loading.value = true
    try {
      const result = await recipeApi.search({
        keyword: keyword.trim(),
        page: currentPage.value,
        size: 10
      })

      if (reset) {
        recipes.value = result
      } else {
        recipes.value.push(...result)
      }

      hasMore.value = result.length >= 10
      if (result.length > 0) {
        currentPage.value++
      }
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取食谱详情
   */
  async function getRecipeDetail(id: number): Promise<RecipeVO> {
    return await recipeApi.getDetail(id)
  }

  /**
   * 收藏/取消收藏
   */
  async function toggleFavorite(recipeId: number): Promise<boolean> {
    const favorited = await recipeApi.toggleFavorite(recipeId)
    // 更新列表中的收藏状态
    recipes.value.forEach(r => {
      if (r.id === recipeId) {
        r.favorited = favorited
        r.likeCount += favorited ? 1 : -1
      }
    })
    // 更新收藏列表
    if (!favorited) {
      favorites.value = favorites.value.filter(r => r.id !== recipeId)
    }
    return favorited
  }

  /**
   * 加载收藏列表
   */
  async function loadFavorites(reset = true) {
    if (reset) {
      favorites.value = []
    }
    loading.value = true
    try {
      const result = await recipeApi.getFavorites({ page: 1, size: 50 })
      favorites.value = result
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置状态
   */
  function resetState() {
    recipes.value = []
    currentPage.value = 1
    hasMore.value = true
    currentCategory.value = ''
    searchKeyword.value = ''
  }

  return {
    recipes,
    favorites,
    loading,
    loadingMore,
    hasMore,
    currentPage,
    currentCategory,
    searchKeyword,
    loadRecommend,
    loadMore,
    searchRecipes,
    getRecipeDetail,
    toggleFavorite,
    loadFavorites,
    resetState
  }
})
