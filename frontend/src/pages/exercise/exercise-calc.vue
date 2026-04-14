<template>
  <view class="exercise-calc-page">
    <!-- 顶部标题栏 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <text class="iconfont icon-back"></text>
      </view>
      <text class="title">运动消耗计算</text>
    </view>

    <!-- 搜索栏 -->
    <view class="search-bar">
      <uni-icons type="search" size="18" color="#999" />
      <input
        v-model="searchKeyword"
        class="search-input"
        placeholder="搜索运动项目"
        confirm-type="search"
        @input="onSearch"
      />
      <text v-if="searchKeyword" class="clear-btn" @click="clearSearch">✕</text>
    </view>

    <!-- 分类选项卡 -->
    <scroll-view scroll-x class="category-tabs" v-if="!searchKeyword">
      <view
        v-for="cat in categories"
        :key="cat.value"
        class="tab-item"
        :class="{ active: activeCategory === cat.value }"
        @click="switchCategory(cat.value)"
      >
        <text class="tab-icon">{{ cat.icon }}</text>
        <text class="tab-label">{{ cat.label }}</text>
      </view>
    </scroll-view>

    <!-- 运动列表 -->
    <scroll-view scroll-y class="exercise-list">
      <view
        v-for="item in displayList"
        :key="item.id"
        class="exercise-item"
        :class="{ selected: selectedExercise?.id === item.id }"
        @click="selectExercise(item)"
      >
        <view class="exercise-info">
          <text class="exercise-name">{{ item.exerciseName }}</text>
          <view class="exercise-meta">
            <text class="category-tag">{{ item.categoryName }}</text>
            <text
              class="intensity-tag"
              :style="{ color: intensityColor(item.intensity) }"
            >{{ item.intensityName }}</text>
            <text class="met-text">MET {{ item.metValue }}</text>
          </view>
          <text class="exercise-desc" v-if="item.description">{{ item.description }}</text>
        </view>
        <view class="select-indicator">
          <view v-if="selectedExercise?.id === item.id" class="check-icon">✓</view>
        </view>
      </view>

      <view v-if="displayList.length === 0" class="empty-tip">
        <text>暂无相关运动</text>
      </view>
    </scroll-view>

    <!-- 计算区域（选中运动后显示） -->
    <view class="calc-panel" v-if="selectedExercise">
      <view class="panel-title">
        <text class="selected-name">{{ selectedExercise.exerciseName }}</text>
        <text class="panel-sub">填写运动参数，自动计算热量消耗</text>
      </view>

      <view class="input-row">
        <text class="input-label">体重</text>
        <view class="input-box">
          <input
            v-model.number="weight"
            type="digit"
            class="param-input"
            placeholder="kg"
          />
          <text class="input-unit">kg</text>
        </view>
      </view>

      <view class="input-row">
        <text class="input-label">运动时长</text>
        <view class="input-box">
          <input
            v-model.number="duration"
            type="number"
            class="param-input"
            placeholder="分钟"
          />
          <text class="input-unit">分钟</text>
        </view>
      </view>

      <!-- 计算结果 -->
      <view class="result-card" v-if="caloriesBurned > 0">
        <view class="result-main">
          <text class="result-value">{{ caloriesBurned }}</text>
          <text class="result-unit">千卡</text>
        </view>
        <text class="result-desc">{{ resultDescription }}</text>
      </view>

      <button class="calc-btn" @click="calculate" :disabled="!canCalc">
        {{ caloriesBurned > 0 ? '重新计算' : '计算消耗' }}
      </button>

      <!-- 加入运动记录 -->
      <button class="save-btn" v-if="caloriesBurned > 0" @click="saveRecord">
        + 加入运动记录
      </button>
    </view>

    <!-- 科普卡片 -->
    <view class="science-card" v-if="!selectedExercise">
      <text class="card-title">📐 MET 消耗计算原理</text>
      <text class="card-content">
        热量消耗 = MET值 × 体重(kg) × 运动时长(小时)
      </text>
      <text class="card-tip">MET（代谢当量）是衡量运动强度的指标，坐着休息 MET=1，快跑 MET≈11</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getCommonExercises, getExercisesByCategory, searchExercises } from '@/api/exercise-library.api'
import type { ExerciseLibraryItem } from '@/types/exercise-library'
import { EXERCISE_CATEGORIES, INTENSITY_CONFIG } from '@/types/exercise-library'

const categories = EXERCISE_CATEGORIES
const activeCategory = ref('all')
const searchKeyword = ref('')
const exerciseList = ref<ExerciseLibraryItem[]>([])
const searchResult = ref<ExerciseLibraryItem[]>([])
const selectedExercise = ref<ExerciseLibraryItem | null>(null)
const weight = ref<number | null>(null)
const duration = ref<number | null>(null)
const caloriesBurned = ref(0)

const displayList = computed(() =>
  searchKeyword.value ? searchResult.value : exerciseList.value
)

const canCalc = computed(() =>
  selectedExercise.value && weight.value && weight.value > 0 && duration.value && duration.value > 0
)

const resultDescription = computed(() => {
  if (!caloriesBurned.value) return ''
  const kcal = caloriesBurned.value
  if (kcal < 100) return '相当于一个苹果的热量'
  if (kcal < 200) return '相当于一碗米饭的热量'
  if (kcal < 300) return '相当于一顿简单早餐的热量'
  if (kcal < 500) return '相当于一顿普通午餐的热量'
  return '消耗量很可观，继续保持！'
})

onMounted(() => {
  loadCommonExercises()
})

async function loadCommonExercises() {
  try {
    const res = await getCommonExercises()
    if (res.data) exerciseList.value = res.data
  } catch (e) {
    console.error('加载运动列表失败', e)
  }
}

async function switchCategory(cat: string) {
  activeCategory.value = cat
  try {
    if (cat === 'all') {
      await loadCommonExercises()
    } else {
      const res = await getExercisesByCategory(cat)
      if (res.data) exerciseList.value = res.data
    }
  } catch (e) {
    console.error('切换分类失败', e)
  }
}

let searchTimer: ReturnType<typeof setTimeout> | null = null
function onSearch() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    if (!searchKeyword.value.trim()) return
    try {
      const res = await searchExercises(searchKeyword.value.trim())
      if (res.data) searchResult.value = res.data
    } catch (e) {
      console.error('搜索失败', e)
    }
  }, 400)
}

function clearSearch() {
  searchKeyword.value = ''
  searchResult.value = []
}

function selectExercise(item: ExerciseLibraryItem) {
  selectedExercise.value = selectedExercise.value?.id === item.id ? null : item
  caloriesBurned.value = 0
}

function calculate() {
  if (!canCalc.value || !selectedExercise.value || !weight.value || !duration.value) return
  // 热量消耗 = MET × 体重(kg) × 时长(h)
  const kcal = selectedExercise.value.metValue * weight.value * (duration.value / 60)
  caloriesBurned.value = Math.round(kcal * 10) / 10
}

async function saveRecord() {
  if (!selectedExercise.value || !duration.value || !caloriesBurned.value) return
  // 跳转到运动记录页并带上参数
  uni.navigateTo({
    url: `/pages/exercise/exercise-add?exerciseName=${encodeURIComponent(selectedExercise.value.exerciseName)}&duration=${duration.value}&calories=${caloriesBurned.value}`
  })
}

function intensityColor(intensity: string) {
  return (INTENSITY_CONFIG as any)[intensity]?.color || '#999'
}

function goBack() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.exercise-calc-page {
  min-height: 100vh;
  background: #f5f6fa;
  padding-bottom: 40rpx;
}

.header {
  display: flex;
  align-items: center;
  padding: 80rpx 32rpx 24rpx;
  background: #fff;

  .back-btn {
    margin-right: 24rpx;
    font-size: 36rpx;
    color: #333;
  }

  .title {
    font-size: 36rpx;
    font-weight: 700;
    color: #1a1a1a;
  }
}

.search-bar {
  margin: 20rpx 32rpx;
  background: #fff;
  border-radius: 50rpx;
  padding: 18rpx 28rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);

  .search-input {
    flex: 1;
    font-size: 28rpx;
    color: #333;
  }

  .clear-btn {
    color: #999;
    font-size: 28rpx;
    padding: 0 8rpx;
  }
}

.category-tabs {
  white-space: nowrap;
  padding: 0 32rpx 20rpx;

  .tab-item {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    margin-right: 24rpx;
    padding: 14rpx 24rpx;
    background: #fff;
    border-radius: 24rpx;
    min-width: 100rpx;
    box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.06);

    &.active {
      background: var(--primary-color, #07c160);
      .tab-icon, .tab-label { color: #fff; }
    }

    .tab-icon { font-size: 32rpx; }
    .tab-label { font-size: 22rpx; color: #555; margin-top: 6rpx; }
  }
}

.exercise-list {
  max-height: 550rpx;
  padding: 0 32rpx;

  .exercise-item {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 20rpx;
    padding: 24rpx 28rpx;
    margin-bottom: 16rpx;
    box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
    transition: all 0.2s;

    &.selected {
      border: 2rpx solid var(--primary-color, #07c160);
      background: #f0fff4;
    }

    .exercise-info {
      flex: 1;

      .exercise-name {
        font-size: 30rpx;
        font-weight: 600;
        color: #1a1a1a;
      }

      .exercise-meta {
        display: flex;
        align-items: center;
        gap: 16rpx;
        margin-top: 8rpx;

        .category-tag {
          font-size: 22rpx;
          color: #888;
          background: #f0f0f0;
          padding: 4rpx 12rpx;
          border-radius: 20rpx;
        }

        .intensity-tag {
          font-size: 22rpx;
          font-weight: 500;
        }

        .met-text {
          font-size: 22rpx;
          color: #aaa;
        }
      }

      .exercise-desc {
        font-size: 24rpx;
        color: #aaa;
        margin-top: 8rpx;
      }
    }

    .check-icon {
      width: 44rpx;
      height: 44rpx;
      background: var(--primary-color, #07c160);
      color: #fff;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 26rpx;
    }
  }

  .empty-tip {
    text-align: center;
    color: #ccc;
    padding: 60rpx 0;
    font-size: 28rpx;
  }
}

.calc-panel {
  margin: 24rpx 32rpx 0;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.08);

  .panel-title {
    margin-bottom: 28rpx;
    .selected-name {
      font-size: 32rpx;
      font-weight: 700;
      color: #1a1a1a;
      display: block;
    }
    .panel-sub {
      font-size: 24rpx;
      color: #999;
      margin-top: 6rpx;
    }
  }

  .input-row {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    .input-label {
      width: 140rpx;
      font-size: 28rpx;
      color: #555;
    }

    .input-box {
      flex: 1;
      display: flex;
      align-items: center;
      background: #f8f8f8;
      border-radius: 16rpx;
      padding: 16rpx 20rpx;

      .param-input {
        flex: 1;
        font-size: 30rpx;
        color: #333;
      }

      .input-unit {
        font-size: 26rpx;
        color: #999;
      }
    }
  }

  .result-card {
    background: linear-gradient(135deg, #07c160, #0dab52);
    border-radius: 20rpx;
    padding: 28rpx;
    text-align: center;
    margin: 24rpx 0;

    .result-main {
      display: flex;
      align-items: baseline;
      justify-content: center;
      gap: 8rpx;

      .result-value {
        font-size: 72rpx;
        font-weight: 800;
        color: #fff;
      }

      .result-unit {
        font-size: 28rpx;
        color: rgba(255,255,255,0.8);
      }
    }

    .result-desc {
      font-size: 24rpx;
      color: rgba(255,255,255,0.85);
      margin-top: 8rpx;
      display: block;
    }
  }

  .calc-btn {
    width: 100%;
    height: 88rpx;
    background: var(--primary-color, #07c160);
    color: #fff;
    border-radius: 44rpx;
    font-size: 30rpx;
    font-weight: 600;
    border: none;
    margin-top: 8rpx;

    &[disabled] {
      background: #ccc;
    }
  }

  .save-btn {
    width: 100%;
    height: 88rpx;
    background: #fff;
    color: var(--primary-color, #07c160);
    border: 2rpx solid var(--primary-color, #07c160);
    border-radius: 44rpx;
    font-size: 30rpx;
    font-weight: 600;
    margin-top: 20rpx;
  }
}

.science-card {
  margin: 24rpx 32rpx;
  background: linear-gradient(135deg, #fff7e6, #fffbe6);
  border-radius: 20rpx;
  padding: 28rpx;
  border-left: 6rpx solid #faad14;

  .card-title {
    font-size: 28rpx;
    font-weight: 700;
    color: #d46b08;
    display: block;
    margin-bottom: 12rpx;
  }

  .card-content {
    font-size: 28rpx;
    color: #5c3d00;
    display: block;
    margin-bottom: 12rpx;
    font-family: monospace;
  }

  .card-tip {
    font-size: 24rpx;
    color: #ad6800;
    display: block;
    line-height: 1.6;
  }
}
</style>
