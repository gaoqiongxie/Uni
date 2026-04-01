<template>
  <view class="detail-container">
    <!-- 封面区域 -->
    <view class="cover-section">
      <image
        v-if="recipe && recipe.coverImage"
        :src="recipe.coverImage"
        mode="aspectFill"
        class="cover-img"
      />
      <view v-else class="cover-placeholder">
        <text>{{ coverIcon }}</text>
      </view>
      <view class="cover-overlay">
        <view class="cover-info">
          <text class="cover-title">{{ recipe?.name }}</text>
          <view class="cover-tags">
            <text class="c-tag" v-for="tag in recipe?.tagList" :key="tag">{{ tag }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 基本信息卡片 -->
    <view class="info-card card" v-if="recipe">
      <view class="info-row">
        <view class="info-item">
          <text class="info-value highlight">{{ recipe.calorie }}</text>
          <text class="info-label">总热量(kcal)</text>
        </view>
        <view class="info-divider"></view>
        <view class="info-item">
          <text class="info-value">{{ recipe.cookingTime }}</text>
          <text class="info-label">烹饪(分钟)</text>
        </view>
        <view class="info-divider"></view>
        <view class="info-item">
          <text class="info-value">{{ recipe.difficultyName }}</text>
          <text class="info-label">难度</text>
        </view>
        <view class="info-divider"></view>
        <view class="info-item">
          <text class="info-value">{{ recipe.servings }}</text>
          <text class="info-label">人份</text>
        </view>
      </view>
    </view>

    <!-- 营养详情 -->
    <view class="nutrition-card card" v-if="recipe">
      <text class="section-title">营养构成</text>
      <view class="nutrition-grid">
        <view class="nutrition-item">
          <view class="nutrition-circle protein">
            <text class="nc-value">{{ recipe.protein }}</text>
            <text class="nc-unit">g</text>
          </view>
          <text class="nc-label">蛋白质</text>
        </view>
        <view class="nutrition-item">
          <view class="nutrition-circle fat">
            <text class="nc-value">{{ recipe.fat }}</text>
            <text class="nc-unit">g</text>
          </view>
          <text class="nc-label">脂肪</text>
        </view>
        <view class="nutrition-item">
          <view class="nutrition-circle carb">
            <text class="nc-value">{{ recipe.carbohydrate }}</text>
            <text class="nc-unit">g</text>
          </view>
          <text class="nc-label">碳水化合物</text>
        </view>
        <view class="nutrition-item">
          <view class="nutrition-circle fiber">
            <text class="nc-value">{{ recipe.fiber }}</text>
            <text class="nc-unit">g</text>
          </view>
          <text class="nc-label">膳食纤维</text>
        </view>
      </view>
    </view>

    <!-- 食材清单 -->
    <view class="ingredients-card card" v-if="recipe && recipe.ingredients && recipe.ingredients.length > 0">
      <text class="section-title">食材清单</text>
      <view class="ingredient-list">
        <view class="ingredient-row" v-for="ing in recipe.ingredients" :key="ing.id">
          <view class="ing-left">
            <text class="ing-name">{{ ing.name }}</text>
          </view>
          <view class="ing-right">
            <text class="ing-amount">{{ ing.amount }}</text>
            <text class="ing-cal">{{ ing.calorie }} kcal</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 做法步骤 -->
    <view class="steps-card card" v-if="recipe && recipe.steps && recipe.steps.length > 0">
      <text class="section-title">做法步骤</text>
      <view class="step-list">
        <view class="step-item" v-for="(step, idx) in recipe.steps" :key="idx">
          <view class="step-number">{{ idx + 1 }}</view>
          <text class="step-text">{{ step }}</text>
        </view>
      </view>
    </view>

    <!-- 简介 -->
    <view class="desc-card card" v-if="recipe && recipe.description">
      <text class="section-title">食谱简介</text>
      <text class="desc-text">{{ recipe.description }}</text>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar" v-if="recipe">
      <view class="bar-fav" :class="{ favorited: recipe.favorited }" @click="handleFavorite">
        <text class="bar-fav-icon">{{ recipe.favorited ? '❤️' : '🤍' }}</text>
        <text class="bar-fav-text">{{ recipe.favorited ? '已收藏' : '收藏' }}</text>
      </view>
      <view class="bar-share" @click="handleShare">
        <text class="bar-share-icon">🔗</text>
        <text class="bar-share-text">分享</text>
      </view>
    </view>

    <!-- Loading -->
    <view v-if="loading" class="loading-state">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRecipeStore } from '../../store/recipe'
import { RECIPE_CATEGORIES } from '../../types/recipe'
import type { RecipeVO } from '../../types/recipe'

const recipeStore = useRecipeStore()

const recipe = ref<RecipeVO | null>(null)
const loading = ref(true)

const props = defineProps<{
  id: string
}>()

const coverIcon = computed(() => {
  if (!recipe.value) return '🍽️'
  const cat = RECIPE_CATEGORIES.find(c => c.key === recipe.value!.category)
  return cat ? cat.icon : '🍽️'
})

async function loadDetail() {
  loading.value = true
  try {
    recipe.value = await recipeStore.getRecipeDetail(Number(props.id))
  } catch (e: any) {
    uni.showToast({ title: e.message || '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function handleFavorite() {
  if (!recipe.value) return
  try {
    const favorited = await recipeStore.toggleFavorite(recipe.value.id)
    recipe.value.favorited = favorited
    uni.showToast({
      title: favorited ? '已收藏' : '已取消收藏',
      icon: 'success'
    })
  } catch (e: any) {
    uni.showToast({ title: e.message || '操作失败', icon: 'none' })
  }
}

function handleShare() {
  uni.showShareMenu({ withShareTicket: true })
}

onMounted(() => {
  loadDetail()
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.detail-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 140rpx;

  .cover-section {
    position: relative;
    height: 480rpx;

    .cover-img {
      width: 100%;
      height: 100%;
    }

    .cover-placeholder {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #667eea, #764ba2);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 120rpx;
    }

    .cover-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
      padding: 40rpx 30rpx 30rpx;

      .cover-title {
        font-size: 40rpx;
        font-weight: 700;
        color: #fff;
        margin-bottom: 12rpx;
      }

      .cover-tags {
        display: flex;
        gap: 12rpx;

        .c-tag {
          background: rgba(255, 255, 255, 0.25);
          color: #fff;
          font-size: 22rpx;
          padding: 6rpx 16rpx;
          border-radius: 20rpx;
          backdrop-filter: blur(4px);
        }
      }
    }
  }

  .info-card {
    margin: -40rpx 30rpx 20rpx;
    position: relative;
    z-index: 1;
    padding: 36rpx;

    .info-row {
      display: flex;
      align-items: center;
      justify-content: space-around;
    }

    .info-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;

      .info-value {
        font-size: 40rpx;
        font-weight: 700;
        color: $text-primary;
        margin-bottom: 6rpx;

        &.highlight {
          color: $primary-color;
        }
      }

      .info-label {
        font-size: 22rpx;
        color: $text-secondary;
      }
    }

    .info-divider {
      width: 1rpx;
      height: 60rpx;
      background: $border-color;
    }
  }

  .section-title {
    font-size: $font-size-md;
    font-weight: 600;
    color: $text-primary;
    display: block;
    margin-bottom: 24rpx;
  }

  .nutrition-card {
    margin: 0 30rpx 20rpx;
    padding: 36rpx;

    .nutrition-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20rpx;

      .nutrition-item {
        display: flex;
        flex-direction: column;
        align-items: center;

        .nutrition-circle {
          width: 100rpx;
          height: 100rpx;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 12rpx;

          &.protein { background: rgba(76, 175, 80, 0.12); }
          &.fat { background: rgba(255, 152, 0, 0.12); }
          &.carb { background: rgba(102, 126, 234, 0.12); }
          &.fiber { background: rgba(156, 39, 176, 0.12); }

          .nc-value {
            font-size: 32rpx;
            font-weight: 700;
            color: $text-primary;
          }

          .nc-unit {
            font-size: 20rpx;
            color: $text-secondary;
            margin-left: 2rpx;
          }
        }

        .nc-label {
          font-size: 22rpx;
          color: $text-secondary;
        }
      }
    }
  }

  .ingredients-card {
    margin: 0 30rpx 20rpx;
    padding: 36rpx;

    .ingredient-list {
      .ingredient-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16rpx 0;
        border-bottom: 1rpx solid $border-color;

        &:last-child { border-bottom: none; }

        .ing-left {
          .ing-name {
            font-size: $font-size-md;
            color: $text-primary;
          }
        }

        .ing-right {
          display: flex;
          gap: 20rpx;

          .ing-amount {
            font-size: $font-size-sm;
            color: $text-secondary;
          }

          .ing-cal {
            font-size: $font-size-sm;
            color: $primary-color;
            font-weight: 500;
          }
        }
      }
    }
  }

  .steps-card {
    margin: 0 30rpx 20rpx;
    padding: 36rpx;

    .step-list {
      .step-item {
        display: flex;
        gap: 20rpx;
        margin-bottom: 24rpx;

        &:last-child { margin-bottom: 0; }

        .step-number {
          width: 48rpx;
          height: 48rpx;
          background: linear-gradient(135deg, #667eea, #764ba2);
          color: #fff;
          font-size: 24rpx;
          font-weight: 600;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          margin-top: 4rpx;
        }

        .step-text {
          flex: 1;
          font-size: $font-size-md;
          color: $text-primary;
          line-height: 1.8;
        }
      }
    }
  }

  .desc-card {
    margin: 0 30rpx 20rpx;
    padding: 36rpx;

    .desc-text {
      font-size: $font-size-md;
      color: $text-secondary;
      line-height: 1.8;
    }
  }

  .bottom-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 120rpx;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 40rpx;
    box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
    z-index: 100;
    padding-bottom: env(safe-area-inset-bottom);

    .bar-fav, .bar-share {
      display: flex;
      align-items: center;
      gap: 12rpx;
      padding: 20rpx 60rpx;
      border-radius: 40rpx;

      .bar-fav-icon, .bar-share-icon { font-size: 36rpx; }
      .bar-fav-text, .bar-share-text {
        font-size: $font-size-md;
        font-weight: 500;
      }
    }

    .bar-fav {
      background: rgba(255, 77, 79, 0.1);
      color: #ff4d4f;

      &.favorited {
        background: rgba(255, 77, 79, 0.15);
      }
    }

    .bar-share {
      background: rgba(102, 126, 234, 0.1);
      color: $primary-color;
    }
  }

  .loading-state {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 100rpx;
    color: $text-placeholder;
    font-size: $font-size-md;
  }
}
</style>
