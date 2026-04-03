<template>
  <view class="mine-container">
    <!-- 头部用户信息 -->
    <view class="header gradient-bg">
      <view class="user-card">
        <view class="avatar-section" @click="goToProfile">
          <view class="avatar">
            <text class="avatar-text">{{ userInfo?.nickname?.charAt(0) || '我' }}</text>
          </view>
          <view class="user-info">
            <text class="nickname">{{ userInfo?.nickname || '健康达人' }}</text>
            <text class="user-id">ID: {{ userInfo?.id || '--' }}</text>
          </view>
          <text class="arrow">›</text>
        </view>

        <!-- 数据概览 -->
        <view class="stats-row">
          <view class="stat-item" @click="goToWeightHistory">
            <text class="stat-value">{{ userInfo?.currentWeight || '--' }}</text>
            <text class="stat-label">当前体重(kg)</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ userInfo?.targetWeight || '--' }}</text>
            <text class="stat-label">目标体重(kg)</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ bmiText }}</text>
            <text class="stat-label">BMI指数</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <!-- 健康数据 -->
      <view class="menu-group">
        <view class="menu-title">健康数据</view>
        <view class="menu-list">
          <view class="menu-item" @click="goToGoal">
            <text class="menu-icon">🎯</text>
            <text class="menu-text">我的目标</text>
            <view class="menu-right">
              <text v-if="hasActiveGoal" class="menu-badge">进行中</text>
              <text class="menu-arrow">›</text>
            </view>
          </view>
          <view class="menu-item" @click="goToWeightHistory">
            <text class="menu-icon">⚖️</text>
            <text class="menu-text">体重记录</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="goToMealCalendar">
            <text class="menu-icon">📅</text>
            <text class="menu-text">打卡日历</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="goToExerciseList">
            <text class="menu-icon">🏃</text>
            <text class="menu-text">运动历史</text>
            <text class="menu-arrow">›</text>
          </view>
        </view>
      </view>

      <!-- 我的收藏 -->
      <view class="menu-group">
        <view class="menu-title">我的收藏</view>
        <view class="menu-list">
          <view class="menu-item" @click="goToFavoriteRecipes">
            <text class="menu-icon">🍳</text>
            <text class="menu-text">收藏食谱</text>
            <text class="menu-arrow">›</text>
          </view>
        </view>
      </view>

      <!-- 系统设置 -->
      <view class="menu-group">
        <view class="menu-title">系统设置</view>
        <view class="menu-list">
          <view class="menu-item" @click="goToSettings">
            <text class="menu-icon">⚙️</text>
            <text class="menu-text">设置</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="showAbout">
            <text class="menu-icon">ℹ️</text>
            <text class="menu-text">关于轻瘦</text>
            <text class="menu-arrow">›</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>

    <!-- 版本信息 -->
    <view class="version-info">
      <text>轻瘦 v1.0.0</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../store/user'
import { useGoalStore } from '../../store/goal'

const userStore = useUserStore()
const goalStore = useGoalStore()
const userInfo = computed(() => userStore.userInfo)
const hasActiveGoal = computed(() => !!goalStore.activeGoal)

const bmiText = computed(() => {
  const bmi = userInfo.value?.bmi
  return bmi ? bmi.toFixed(1) : '--'
})

function goToGoal() {
  uni.navigateTo({ url: '/pages/goal/goal-detail' })
}

function goToWeeklyReport() {
  uni.navigateTo({ url: '/pages/report/weekly-report' })
}

function goToMonthlyReport() {
  uni.navigateTo({ url: '/pages/report/monthly-report' })
}

function goToProfile() {
  uni.navigateTo({ url: '/pages/user/profile' })
}

function goToWeightHistory() {
  uni.navigateTo({ url: '/pages/health/weight-history' })
}

function goToMealCalendar() {
  uni.navigateTo({ url: '/pages/meal/meal-calendar' })
}

function goToExerciseList() {
  uni.navigateTo({ url: '/pages/exercise/exercise-list' })
}

function goToFavoriteRecipes() {
  uni.navigateTo({
    url: '/pages/recipe/recipe-list?tab=favorites'
  })
}

function goToSettings() {
  uni.navigateTo({ url: '/pages/settings/settings' })
}

function showAbout() {
  uni.showModal({
    title: '关于轻瘦',
    content: '轻瘦 v1.0.0\n\n科学健康的减脂陪伴应用\n不节食、不过度运动\n用科学的方式享受轻松的健康生活',
    showCancel: false
  })
}

function handleLogout() {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/user/login' })
      }
    }
  })
}

onMounted(() => {
  if (!userInfo.value) {
    userStore.refreshUserInfo()
  }
  goalStore.loadActiveGoal()
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.mine-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 40rpx;

  .header {
    padding: 60rpx 30rpx 40rpx;

    .user-card {
      background: rgba(255, 255, 255, 0.95);
      border-radius: $border-radius-lg;
      padding: 40rpx;
      box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);

      .avatar-section {
        display: flex;
        align-items: center;
        margin-bottom: 40rpx;

        .avatar {
          width: 120rpx;
          height: 120rpx;
          border-radius: 50%;
          background: linear-gradient(135deg, $primary-color, $primary-light);
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 30rpx;

          .avatar-text {
            font-size: 48rpx;
            color: #fff;
            font-weight: bold;
          }
        }

        .user-info {
          flex: 1;

          .nickname {
            display: block;
            font-size: $font-size-lg;
            font-weight: 600;
            color: $text-primary;
            margin-bottom: 8rpx;
          }

          .user-id {
            font-size: $font-size-sm;
            color: $text-secondary;
          }
        }

        .arrow {
          font-size: 40rpx;
          color: $text-hint;
        }
      }

      .stats-row {
        display: flex;
        align-items: center;
        justify-content: space-around;
        padding-top: 30rpx;
        border-top: 1rpx solid $border-color;

        .stat-item {
          text-align: center;
          flex: 1;

          .stat-value {
            display: block;
            font-size: 40rpx;
            font-weight: bold;
            color: $primary-color;
            margin-bottom: 8rpx;
          }

          .stat-label {
            font-size: $font-size-sm;
            color: $text-secondary;
          }
        }

        .stat-divider {
          width: 1rpx;
          height: 60rpx;
          background: $border-color;
        }
      }
    }
  }

  .menu-section {
    padding: 0 30rpx;

    .menu-group {
      margin-bottom: 30rpx;

      .menu-title {
        font-size: $font-size-sm;
        color: $text-secondary;
        margin-bottom: 16rpx;
        padding-left: 10rpx;
      }

      .menu-list {
        background: #fff;
        border-radius: $border-radius;
        overflow: hidden;

        .menu-item {
          display: flex;
          align-items: center;
          padding: 30rpx;
          border-bottom: 1rpx solid $border-color;

          &:last-child {
            border-bottom: none;
          }

          &:active {
            background: $bg-color;
          }

          .menu-icon {
            font-size: 40rpx;
            margin-right: 20rpx;
          }

          .menu-text {
            flex: 1;
            font-size: $font-size-md;
            color: $text-primary;
          }

          .menu-right {
            display: flex;
            align-items: center;
            gap: 10rpx;
          }

          .menu-badge {
            font-size: $font-size-sm;
            color: $primary-color;
            background: rgba(102, 126, 234, 0.1);
            padding: 4rpx 14rpx;
            border-radius: 20rpx;
          }

          .menu-arrow {
            font-size: 32rpx;
            color: $text-hint;
          }
        }
      }
    }
  }

  .logout-section {
    padding: 0 30rpx;
    margin-top: 40rpx;

    .logout-btn {
      width: 100%;
      height: 90rpx;
      line-height: 90rpx;
      background: #fff;
      color: #ff4d4f;
      font-size: $font-size-md;
      border-radius: $border-radius;
      border: none;

      &:active {
        background: $bg-color;
      }

      &::after {
        border: none;
      }
    }
  }

  .version-info {
    text-align: center;
    margin-top: 40rpx;

    text {
      font-size: $font-size-sm;
      color: $text-hint;
    }
  }
}
</style>
