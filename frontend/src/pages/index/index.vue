<template>
  <view class="index-container">
    <!-- 顶部用户信息 -->
    <view class="header gradient-bg">
      <view class="header-top">
        <view class="user-info">
          <text class="greeting">{{ greeting }}，{{ userInfo?.nickname || '健康达人' }}！</text>
          <text class="date">{{ todayStr }}</text>
        </view>
        <view class="avatar" @click="goToProfile">
          <text class="avatar-text">{{ userInfo?.nickname?.charAt(0) || '我' }}</text>
        </view>
      </view>

      <!-- 体重卡片 -->
      <view class="weight-card">
        <view class="weight-main">
          <text class="weight-value">{{ userInfo?.currentWeight || '--' }}</text>
          <text class="weight-unit">kg</text>
        </view>
        <view class="weight-meta">
          <text class="meta-item">目标 {{ userInfo?.targetWeight || '--' }}kg</text>
          <text class="meta-item">BMI {{ userInfo?.bmi || '--' }}</text>
        </view>
        <view class="calorie-bar">
          <text class="calorie-label">今日热量</text>
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: caloriePercent + '%' }"></view>
          </view>
          <text class="calorie-text">{{ todayCalorie }} / {{ userInfo?.calorieGoal || 1400 }} kcal</text>
        </view>
      </view>
    </view>

    <!-- 今日打卡情况 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">今日打卡</text>
        <text class="section-link" @click="goToMealRecord">去打卡 →</text>
      </view>
      <view class="meal-grid">
        <view
          v-for="meal in mealList"
          :key="meal.type"
          class="meal-item"
          :class="{ done: meal.done }"
          @click="goToMealRecord"
        >
          <text class="meal-icon">{{ meal.icon }}</text>
          <text class="meal-name">{{ meal.name }}</text>
          <text class="meal-status">{{ meal.done ? '✓' : '+' }}</text>
        </view>
      </view>
    </view>

    <!-- 快速入口 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">快速记录</text>
      </view>
      <view class="quick-actions">
        <view class="action-item" @click="goToRecordWeight">
          <text class="action-icon">⚖️</text>
          <text class="action-name">记录体重</text>
        </view>
        <view class="action-item" @click="goToWeightHistory">
          <text class="action-icon">📈</text>
          <text class="action-name">体重趋势</text>
        </view>
        <view class="action-item" @click="goToCalendar">
          <text class="action-icon">📅</text>
          <text class="action-name">打卡日历</text>
        </view>
        <view class="action-item" @click="goToExercise">
          <text class="action-icon">🏃</text>
          <text class="action-name">记录运动</text>
        </view>
        <view class="action-item" @click="goToWaterRecord">
          <text class="action-icon">💧</text>
          <text class="action-name">饮水记录</text>
        </view>
        <view class="action-item" @click="goToWaterSetting">
          <text class="action-icon">⚙️</text>
          <text class="action-name">饮水设置</text>
        </view>
        <view class="action-item" @click="goToSleepRecord">
          <text class="action-icon">🌙</text>
          <text class="action-name">睡眠记录</text>
        </view>
        <view class="action-item" @click="goToSleepStats">
          <text class="action-icon">📊</text>
          <text class="action-name">睡眠统计</text>
        </view>
      </view>
    </view>

    <!-- 推荐食谱入口 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">健康食谱</text>
        <text class="section-link" @click="goToRecipes">查看更多 →</text>
      </view>
      <view class="recipe-card" @click="goToRecipes">
        <view class="recipe-info">
          <text class="recipe-title">🥗 减脂食谱推荐</text>
          <text class="recipe-desc">科学搭配 · 低卡美味 · 轻松减脂</text>
        </view>
        <text class="recipe-arrow">›</text>
      </view>
    </view>

    <!-- 健康小贴士 -->
    <view class="section tips-card card">
      <text class="tips-icon">💡</text>
      <text class="tips-text">{{ tip }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../store/user'
import { useMealStore } from '../../store/meal'
import { today } from '../../utils/date'

const userStore = useUserStore()
const mealStore = useMealStore()

const userInfo = computed(() => userStore.userInfo)
const todayStr = today()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 10) return '早上好'
  if (hour < 14) return '上午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const todayCalorie = computed(() => {
  return mealStore.todayMeals.reduce((sum, m) => sum + (m.calorieEstimate || 0), 0)
})

const caloriePercent = computed(() => {
  const goal = userInfo.value?.calorieGoal || 1400
  return Math.min((todayCalorie.value / goal) * 100, 100)
})

const mealList = computed(() => {
  const done = new Set(mealStore.todayMeals.map(m => m.mealType))
  return [
    { type: 1, name: '早餐', icon: '🌅', done: done.has(1) },
    { type: 2, name: '午餐', icon: '☀️', done: done.has(2) },
    { type: 3, name: '晚餐', icon: '🌙', done: done.has(3) },
    { type: 4, name: '加餐', icon: '🍎', done: done.has(4) }
  ]
})

const tips = [
  '今天喝够8杯水了吗？水是最好的减脂饮料～',
  '饭前喝一杯温水，有助于控制食量哦！',
  '细嚼慢咽，让大脑及时感知饱腹感！',
  '饭后30分钟，散步15分钟，帮助消化！',
  '选择蒸煮而非油炸，减少额外热量摄入！'
]
const tip = tips[new Date().getDate() % tips.length]

function goToProfile() {
  uni.navigateTo({ url: '/pages/user/profile' })
}
function goToMealRecord() {
  uni.switchTab({ url: '/pages/meal/meal-record' })
}
function goToRecordWeight() {
  uni.navigateTo({ url: '/pages/health/record-weight' })
}
function goToWeightHistory() {
  uni.navigateTo({ url: '/pages/health/weight-history' })
}
function goToCalendar() {
  uni.navigateTo({ url: '/pages/meal/meal-calendar' })
}
function goToExercise() {
  uni.switchTab({ url: '/pages/exercise/exercise-record' })
}
function goToRecipes() {
  uni.switchTab({ url: '/pages/recipe/recipe-list' })
}
function goToWaterRecord() {
  uni.navigateTo({ url: '/pages/water/water-record' })
}
function goToWaterSetting() {
  uni.navigateTo({ url: '/pages/water/water-setting' })
}
function goToSleepRecord() {
  uni.navigateTo({ url: '/pages/sleep/sleep-record' })
}
function goToSleepStats() {
  uni.navigateTo({ url: '/pages/sleep/sleep-stats' })
}

onMounted(async () => {
  await mealStore.loadTodayMeals(todayStr)
  if (!userInfo.value) {
    await userStore.refreshUserInfo()
  }
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.index-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 120rpx;

  .header {
    padding: 80rpx 40rpx 60rpx;
    color: #fff;

    .header-top {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 40rpx;

      .greeting {
        display: block;
        font-size: $font-size-md;
        font-weight: 500;
        margin-bottom: 8rpx;
      }

      .date {
        font-size: $font-size-sm;
        opacity: 0.8;
      }

      .avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.3);
        display: flex;
        align-items: center;
        justify-content: center;

        .avatar-text {
          font-size: $font-size-md;
          font-weight: bold;
        }
      }
    }

    .weight-card {
      background: rgba(255, 255, 255, 0.15);
      border-radius: $border-radius-lg;
      padding: 40rpx;

      .weight-main {
        display: flex;
        align-items: baseline;
        margin-bottom: 16rpx;

        .weight-value {
          font-size: 80rpx;
          font-weight: bold;
        }

        .weight-unit {
          font-size: $font-size-lg;
          margin-left: 10rpx;
          opacity: 0.8;
        }
      }

      .weight-meta {
        display: flex;
        gap: 30rpx;
        margin-bottom: 30rpx;

        .meta-item {
          font-size: $font-size-sm;
          opacity: 0.85;
        }
      }

      .calorie-bar {
        .calorie-label {
          font-size: $font-size-sm;
          opacity: 0.85;
          margin-bottom: 10rpx;
          display: block;
        }

        .progress-bar {
          height: 12rpx;
          background: rgba(255, 255, 255, 0.3);
          border-radius: 6rpx;
          overflow: hidden;
          margin-bottom: 10rpx;

          .progress-fill {
            height: 100%;
            background: #fff;
            border-radius: 6rpx;
            transition: width 0.3s ease;
          }
        }

        .calorie-text {
          font-size: $font-size-sm;
          opacity: 0.9;
        }
      }
    }
  }

  .section {
    margin: 30rpx 30rpx 0;

    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20rpx;

      .section-title {
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
      }

      .section-link {
        font-size: $font-size-sm;
        color: $primary-color;
      }
    }

    .meal-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16rpx;

      .meal-item {
        background: #fff;
        border-radius: $border-radius;
        padding: 30rpx 16rpx;
        text-align: center;
        box-shadow: $shadow-sm;
        border: 2rpx solid transparent;

        &.done {
          border-color: $primary-color;
          background: rgba(102, 126, 234, 0.05);
        }

        .meal-icon {
          display: block;
          font-size: 44rpx;
          margin-bottom: 10rpx;
        }

        .meal-name {
          display: block;
          font-size: $font-size-sm;
          color: $text-secondary;
          margin-bottom: 8rpx;
        }

        .meal-status {
          display: block;
          font-size: $font-size-sm;
          color: $primary-color;
          font-weight: bold;
        }
      }
    }

    .quick-actions {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16rpx;

      .action-item {
        background: #fff;
        border-radius: $border-radius;
        padding: 30rpx 16rpx;
        text-align: center;
        box-shadow: $shadow-sm;

        .action-icon {
          display: block;
          font-size: 44rpx;
          margin-bottom: 12rpx;
        }

        .action-name {
          display: block;
          font-size: $font-size-sm;
          color: $text-secondary;
        }
      }
    }
  }

  .tips-card {
    display: flex;
    align-items: flex-start;
    gap: 16rpx;
    margin: 30rpx 30rpx 0;

    .tips-icon {
      font-size: 36rpx;
      flex-shrink: 0;
    }

    .tips-text {
      font-size: $font-size-sm;
      color: $text-secondary;
      line-height: 1.6;
    }
  }

  .recipe-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: $border-radius;
    padding: 40rpx 30rpx;
    margin: 0 30rpx;

    .recipe-info {
      .recipe-title {
        display: block;
        font-size: $font-size-md;
        font-weight: 600;
        color: #fff;
        margin-bottom: 10rpx;
      }

      .recipe-desc {
        font-size: $font-size-sm;
        color: rgba(255, 255, 255, 0.85);
      }
    }

    .recipe-arrow {
      font-size: 48rpx;
      color: rgba(255, 255, 255, 0.8);
    }
  }
}
</style>
