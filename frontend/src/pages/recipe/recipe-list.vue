<template>
  <view class="recipe-container">
    <!-- 顶部搜索栏 -->
    <view class="search-bar">
      <view class="search-input-wrap">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索食谱、食材..."
          confirm-type="search"
          @confirm="handleSearch"
        />
        <text v-if="keyword" class="search-clear" @click="clearSearch">×</text>
      </view>
    </view>

    <!-- 分类Tab -->
    <scroll-view scroll-x class="category-scroll">
      <view class="category-list">
        <view
          class="category-item"
          :class="{ active: recipeStore.currentCategory === cat.key }"
          v-for="cat in categories"
          :key="cat.key"
          @click="switchCategory(cat.key)"
        >
          <text class="cat-icon">{{ cat.icon }}</text>
          <text class="cat-name">{{ cat.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 收藏Tab -->
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: !showFavorites }" @click="switchTab(false)">
        <text>推荐食谱</text>
      </view>
      <view class="tab-item" :class="{ active: showFavorites }" @click="switchTab(true)">
        <text>我的收藏</text>
      </view>
    </view>

    <!-- 推荐列表 -->
    <view v-if="!showFavorites" class="recipe-list">
      <!-- 空状态 -->
      <view v-if="!recipeStore.loading && displayList.length === 0" class="empty-state">
        <text class="empty-icon">🍲</text>
        <text class="empty-text">暂无食谱推荐</text>
        <text class="empty-hint">换个分类试试吧</text>
      </view>

      <!-- 列表 -->
      <view
        class="recipe-card card"
        v-for="item in displayList"
        :key="item.id"
        @click="goDetail(item.id)"
      >
        <view class="recipe-cover">
          <image
            v-if="item.coverImage"
            :src="item.coverImage"
            mode="aspectFill"
            class="cover-img"
          />
          <view v-else class="cover-placeholder">
            <text>{{ getCategoryIcon(item.category) }}</text>
          </view>
          <view class="cover-calorie">
            <text>{{ item.calorie }} kcal</text>
          </view>
          <view class="cover-tags" v-if="item.tagList && item.tagList.length > 0">
            <text class="tag-badge" v-for="(tag, idx) in item.tagList.slice(0, 2)" :key="idx">
              {{ tag }}
            </text>
          </view>
        </view>
        <view class="recipe-info">
          <view class="recipe-title-row">
            <text class="recipe-name">{{ item.name }}</text>
            <view
              class="fav-btn"
              :class="{ favorited: item.favorited }"
              @click.stop="handleFavorite(item)"
            >
              <text>{{ item.favorited ? '❤️' : '🤍' }}</text>
            </view>
          </view>
          <text class="recipe-desc">{{ item.description || '美味健康' }}</text>
          <view class="recipe-meta">
            <text class="meta-item">⏱ {{ item.cookingTime }}分钟</text>
            <text class="meta-item">📊 {{ item.difficultyName }}</text>
            <text class="meta-item">👥 {{ item.servings }}人份</text>
            <text class="meta-item">❤️ {{ item.likeCount }}</text>
          </view>
          <view class="nutrient-bar">
            <view class="nutrient-item">
              <text class="n-label">蛋白质</text>
              <text class="n-value">{{ item.protein }}g</text>
            </view>
            <view class="nutrient-item">
              <text class="n-label">脂肪</text>
              <text class="n-value">{{ item.fat }}g</text>
            </view>
            <view class="nutrient-item">
              <text class="n-label">碳水</text>
              <text class="n-value">{{ item.carbohydrate }}g</text>
            </view>
            <view class="nutrient-item">
              <text class="n-label">纤维</text>
              <text class="n-value">{{ item.fiber }}g</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view class="load-more" v-if="recipeStore.hasMore && displayList.length > 0">
        <text v-if="recipeStore.loadingMore" class="loading-text">加载中...</text>
        <text v-else class="more-text" @click="handleLoadMore">点击加载更多</text>
      </view>
    </view>

    <!-- 收藏列表 -->
    <view v-else class="recipe-list">
      <view v-if="!recipeStore.loading && recipeStore.favorites.length === 0" class="empty-state">
        <text class="empty-icon">💝</text>
        <text class="empty-text">暂无收藏</text>
        <text class="empty-hint">去推荐页收藏喜欢的食谱吧</text>
      </view>

      <view
        class="recipe-card card"
        v-for="item in recipeStore.favorites"
        :key="item.id"
        @click="goDetail(item.id)"
      >
        <view class="recipe-cover">
          <image
            v-if="item.coverImage"
            :src="item.coverImage"
            mode="aspectFill"
            class="cover-img"
          />
          <view v-else class="cover-placeholder">
            <text>{{ getCategoryIcon(item.category) }}</text>
          </view>
          <view class="cover-calorie">
            <text>{{ item.calorie }} kcal</text>
          </view>
        </view>
        <view class="recipe-info">
          <view class="recipe-title-row">
            <text class="recipe-name">{{ item.name }}</text>
            <view class="fav-btn favorited" @click.stop="handleFavorite(item)">
              <text>❤️</text>
            </view>
          </view>
          <text class="recipe-desc">{{ item.description }}</text>
          <view class="recipe-meta">
            <text class="meta-item">⏱ {{ item.cookingTime }}分钟</text>
            <text class="meta-item">📊 {{ item.difficultyName }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- Loading 骨架屏 -->
    <view v-if="recipeStore.loading" class="skeleton-wrap">
      <view class="skeleton-card card" v-for="i in 3" :key="i">
        <view class="skeleton-cover shimmer"></view>
        <view class="skeleton-info">
          <view class="skeleton-title shimmer"></view>
          <view class="skeleton-text shimmer"></view>
          <view class="skeleton-meta shimmer"></view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRecipeStore } from '../../store/recipe'
import { useUserStore } from '../../store/user'
import { RECIPE_CATEGORIES } from '../../types/recipe'
import type { RecipeVO } from '../../types/recipe'

const recipeStore = useRecipeStore()
const userStore = useUserStore()

const keyword = ref('')
const showFavorites = ref(false)
const categories = RECIPE_CATEGORIES

const displayList = computed(() => recipeStore.recipes)

function getCategoryIcon(category: string): string {
  const cat = categories.find(c => c.key === category)
  return cat ? cat.icon : '🍽️'
}

function switchCategory(key: string) {
  recipeStore.resetState()
  recipeStore.loadRecommend({
    category: key,
    maxCalorie: userStore.userInfo?.calorieGoal,
    reset: true
  })
}

function handleSearch() {
  if (!keyword.value.trim()) return
  recipeStore.resetState()
  recipeStore.searchRecipes(keyword.value, true)
}

function clearSearch() {
  keyword.value = ''
  recipeStore.resetState()
  recipeStore.loadRecommend({
    maxCalorie: userStore.userInfo?.calorieGoal,
    reset: true
  })
}

function switchTab(isFav: boolean) {
  showFavorites.value = isFav
  if (isFav) {
    recipeStore.loadFavorites(true)
  }
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/recipe/recipe-detail?id=${id}` })
}

async function handleFavorite(item: RecipeVO) {
  try {
    const favorited = await recipeStore.toggleFavorite(item.id)
    uni.showToast({
      title: favorited ? '已收藏' : '已取消收藏',
      icon: 'success'
    })
  } catch (e: any) {
    uni.showToast({ title: e.message || '操作失败', icon: 'none' })
  }
}

function handleLoadMore() {
  if (keyword.value.trim()) {
    recipeStore.searchRecipes(keyword.value, false)
  } else {
    recipeStore.loadMore(userStore.userInfo?.calorieGoal)
  }
}

onMounted(() => {
  recipeStore.loadRecommend({
    maxCalorie: userStore.userInfo?.calorieGoal,
    reset: true
  })
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.recipe-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 120rpx;

  .search-bar {
    background: #fff;
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid $border-color;

    .search-input-wrap {
      display: flex;
      align-items: center;
      background: $bg-color;
      border-radius: 40rpx;
      padding: 16rpx 28rpx;
      gap: 16rpx;

      .search-icon { font-size: 32rpx; }

      .search-input {
        flex: 1;
        font-size: $font-size-md;
        color: $text-primary;
      }

      .search-clear {
        font-size: 36rpx;
        color: $text-placeholder;
        padding: 0 8rpx;
      }
    }
  }

  .category-scroll {
    white-space: nowrap;
    background: #fff;
    padding: 16rpx 0;
    border-bottom: 1rpx solid $border-color;

    .category-list {
      display: inline-flex;
      gap: 20rpx;
      padding: 0 30rpx;
    }

    .category-item {
      display: inline-flex;
      align-items: center;
      gap: 8rpx;
      padding: 14rpx 28rpx;
      background: $bg-color;
      border-radius: 30rpx;
      transition: all 0.2s;

      &.active {
        background: rgba(102, 126, 234, 0.15);

        .cat-name { color: $primary-color; font-weight: 600; }
      }

      .cat-icon { font-size: 28rpx; }
      .cat-name { font-size: $font-size-sm; color: $text-secondary; }
    }
  }

  .tab-bar {
    display: flex;
    background: #fff;
    border-bottom: 1rpx solid $border-color;

    .tab-item {
      flex: 1;
      text-align: center;
      padding: 24rpx 0;
      font-size: $font-size-md;
      color: $text-secondary;
      position: relative;

      &.active {
        color: $primary-color;
        font-weight: 600;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 60rpx;
          height: 4rpx;
          background: $primary-color;
          border-radius: 2rpx;
        }
      }
    }
  }

  .recipe-list {
    padding: 20rpx 30rpx;

    .recipe-card {
      display: flex;
      gap: 24rpx;
      padding: 24rpx;
      margin-bottom: 20rpx;

      .recipe-cover {
        width: 220rpx;
        height: 220rpx;
        border-radius: $border-radius;
        overflow: hidden;
        flex-shrink: 0;
        position: relative;

        .cover-img { width: 100%; height: 100%; }

        .cover-placeholder {
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, #667eea20, #764ba220);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 64rpx;
        }

        .cover-calorie {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          background: rgba(0, 0, 0, 0.6);
          color: #fff;
          font-size: 22rpx;
          text-align: center;
          padding: 6rpx 0;
        }

        .cover-tags {
          position: absolute;
          top: 8rpx;
          left: 8rpx;
          display: flex;
          flex-direction: column;
          gap: 6rpx;

          .tag-badge {
            background: rgba(102, 126, 234, 0.9);
            color: #fff;
            font-size: 20rpx;
            padding: 4rpx 12rpx;
            border-radius: 6rpx;
          }
        }
      }

      .recipe-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        min-width: 0;

        .recipe-title-row {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;

          .recipe-name {
            font-size: $font-size-md;
            font-weight: 600;
            color: $text-primary;
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .fav-btn {
            flex-shrink: 0;
            padding: 8rpx;
            font-size: 36rpx;
            transition: transform 0.2s;

            &:active { transform: scale(1.2); }
          }
        }

        .recipe-desc {
          font-size: $font-size-sm;
          color: $text-secondary;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          margin: 8rpx 0;
        }

        .recipe-meta {
          display: flex;
          gap: 16rpx;
          flex-wrap: wrap;

          .meta-item {
            font-size: 22rpx;
            color: $text-placeholder;
          }
        }

        .nutrient-bar {
          display: flex;
          gap: 16rpx;
          margin-top: 8rpx;

          .nutrient-item {
            display: flex;
            flex-direction: column;
            align-items: center;

            .n-label {
              font-size: 20rpx;
              color: $text-placeholder;
            }

            .n-value {
              font-size: 22rpx;
              color: $text-primary;
              font-weight: 500;
            }
          }
        }
      }
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 120rpx 0;

    .empty-icon { font-size: 80rpx; margin-bottom: 20rpx; }
    .empty-text {
      font-size: $font-size-md;
      color: $text-secondary;
      margin-bottom: 8rpx;
    }
    .empty-hint { font-size: $font-size-sm; color: $text-placeholder; }
  }

  .load-more {
    text-align: center;
    padding: 30rpx;

    .loading-text { color: $text-placeholder; font-size: $font-size-sm; }
    .more-text {
      color: $primary-color;
      font-size: $font-size-sm;
    }
  }

  .skeleton-wrap {
    padding: 20rpx 30rpx;

    .skeleton-card {
      display: flex;
      gap: 24rpx;
      padding: 24rpx;
      margin-bottom: 20rpx;

      .skeleton-cover {
        width: 220rpx;
        height: 220rpx;
        border-radius: $border-radius;
        flex-shrink: 0;
      }

      .skeleton-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 16rpx;

        .skeleton-title {
          height: 36rpx;
          width: 70%;
          border-radius: 6rpx;
        }

        .skeleton-text {
          height: 28rpx;
          width: 100%;
          border-radius: 6rpx;
        }

        .skeleton-meta {
          height: 24rpx;
          width: 50%;
          border-radius: 6rpx;
        }
      }
    }
  }

  @keyframes shimmer {
    0% { background: #f0f0f0; }
    50% { background: #e0e0e0; }
    100% { background: #f0f0f0; }
  }

  .shimmer {
    animation: shimmer 1.5s infinite;
  }
}
</style>
