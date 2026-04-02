<template>
  <view class="goal-container">

    <!-- 当前目标卡片 -->
    <view v-if="goalStore.activeGoal" class="active-goal-card gradient-bg">
      <view class="goal-header">
        <view class="goal-type-badge">
          <text>{{ goalStore.activeGoal.goalTypeName }}</text>
        </view>
        <view class="goal-actions">
          <text class="action-btn" @click="showAbandonConfirm">放弃</text>
        </view>
      </view>

      <!-- 体重进度 -->
      <view class="weight-progress">
        <view class="weight-nums">
          <view class="wn-item">
            <text class="wn-val">{{ goalStore.activeGoal.startWeight }}</text>
            <text class="wn-label">起始</text>
          </view>
          <view class="wn-arrow">
            <text class="wn-current">{{ goalStore.activeGoal.currentWeight }}</text>
            <text class="wn-label">当前 kg</text>
          </view>
          <view class="wn-item">
            <text class="wn-val">{{ goalStore.activeGoal.targetWeight }}</text>
            <text class="wn-label">目标</text>
          </view>
        </view>

        <!-- 进度条 -->
        <view class="progress-wrap">
          <view class="progress-bar">
            <view
              class="progress-fill"
              :style="{ width: progressWidth }"
            ></view>
          </view>
          <text class="progress-text">{{ goalStore.activeGoal.progressPercent }}%</text>
        </view>

        <view class="weight-lost" v-if="(goalStore.activeGoal.weightLost || 0) > 0">
          <text>已减掉 {{ goalStore.activeGoal.weightLost?.toFixed(1) }} kg 🎉</text>
        </view>
      </view>

      <!-- 统计行 -->
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-val">{{ goalStore.activeGoal.persistDays }}</text>
          <text class="stat-label">已坚持(天)</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-val">{{ goalStore.activeGoal.remainDays ?? '--' }}</text>
          <text class="stat-label">剩余(天)</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-val">{{ goalStore.activeGoal.calorieGoal }}</text>
          <text class="stat-label">热量目标</text>
        </view>
      </view>
    </view>

    <!-- 无目标时 -->
    <view v-else-if="!goalStore.loading" class="no-goal-card card">
      <text class="no-goal-icon">🎯</text>
      <text class="no-goal-text">还没有设定目标</text>
      <text class="no-goal-hint">设定一个减脂目标，让每天的努力更有方向感</text>
      <button class="set-goal-btn" @click="goSetGoal">立即设定目标</button>
    </view>

    <!-- 目标计划详情 -->
    <view v-if="goalStore.activeGoal" class="section plan-section">
      <view class="section-title">目标计划</view>
      <view class="plan-list">
        <view class="plan-item">
          <text class="plan-icon">🔥</text>
          <text class="plan-key">每日热量</text>
          <text class="plan-val">{{ goalStore.activeGoal.calorieGoal }} kcal</text>
        </view>
        <view class="plan-item">
          <text class="plan-icon">🏃</text>
          <text class="plan-key">每周运动</text>
          <text class="plan-val">{{ goalStore.activeGoal.exerciseDaysPerWeek }} 天</text>
        </view>
        <view class="plan-item">
          <text class="plan-icon">⏱️</text>
          <text class="plan-key">每日运动时长</text>
          <text class="plan-val">{{ goalStore.activeGoal.exerciseMinutesPerDay }} 分钟</text>
        </view>
        <view class="plan-item" v-if="goalStore.activeGoal.targetDate">
          <text class="plan-icon">📅</text>
          <text class="plan-key">目标截止日</text>
          <text class="plan-val">{{ goalStore.activeGoal.targetDate }}</text>
        </view>
        <view class="plan-item" v-if="goalStore.activeGoal.remark">
          <text class="plan-icon">💬</text>
          <text class="plan-key">动力宣言</text>
          <text class="plan-val remark-text">{{ goalStore.activeGoal.remark }}</text>
        </view>
      </view>

      <button class="modify-btn" @click="goSetGoal">修改目标</button>
    </view>

    <!-- 历史目标 -->
    <view class="section" v-if="goalStore.goalHistory.length > 0">
      <view class="section-title">历史目标</view>
      <view
        class="history-item card"
        v-for="item in historyList"
        :key="item.id"
      >
        <view class="history-header">
          <view class="history-type">{{ item.goalTypeName }}</view>
          <view class="history-status" :class="'status-' + item.status">{{ item.statusName }}</view>
        </view>
        <view class="history-detail">
          <text>{{ item.startWeight }} kg → {{ item.targetWeight }} kg</text>
          <text class="history-date">{{ item.startDate }}</text>
        </view>
      </view>
    </view>

    <!-- Loading -->
    <view v-if="goalStore.loading" class="loading-wrap">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useGoalStore } from '../../store/goal'

const goalStore = useGoalStore()

const progressWidth = computed(() => {
  const p = goalStore.activeGoal?.progressPercent || 0
  return `${Math.min(p, 100)}%`
})

const historyList = computed(() =>
  goalStore.goalHistory.filter(g => g.status !== 1)
)

function goSetGoal() {
  uni.navigateTo({ url: '/pages/goal/goal-set' })
}

function showAbandonConfirm() {
  const id = goalStore.activeGoal?.id
  if (!id) return
  uni.showModal({
    title: '放弃目标',
    content: '确定要放弃当前目标吗？放弃后可以重新设定。',
    success: async (res) => {
      if (res.confirm) {
        try {
          await goalStore.abandonGoal(id)
          uni.showToast({ title: '已放弃目标', icon: 'success' })
        } catch (e: any) {
          uni.showToast({ title: e.message || '操作失败', icon: 'none' })
        }
      }
    }
  })
}

onMounted(async () => {
  await goalStore.loadActiveGoal()
  await goalStore.loadGoalHistory()
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.goal-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 60rpx;

  .active-goal-card {
    padding: 50rpx 40rpx;
    color: #fff;

    .goal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 40rpx;

      .goal-type-badge {
        background: rgba(255,255,255,0.25);
        border-radius: 30rpx;
        padding: 8rpx 24rpx;
        font-size: $font-size-sm;
        font-weight: 600;
      }

      .action-btn {
        font-size: $font-size-sm;
        opacity: 0.85;
        padding: 8rpx 20rpx;
        background: rgba(255,255,255,0.15);
        border-radius: 20rpx;
      }
    }

    .weight-progress {
      margin-bottom: 40rpx;

      .weight-nums {
        display: flex;
        align-items: flex-end;
        justify-content: space-between;
        margin-bottom: 20rpx;

        .wn-item {
          text-align: center;
          .wn-val { display: block; font-size: 40rpx; font-weight: bold; }
          .wn-label { font-size: $font-size-sm; opacity: 0.8; }
        }

        .wn-arrow {
          text-align: center;
          .wn-current {
            display: block;
            font-size: 80rpx;
            font-weight: bold;
            line-height: 1;
          }
          .wn-label { font-size: $font-size-sm; opacity: 0.8; }
        }
      }

      .progress-wrap {
        display: flex;
        align-items: center;
        gap: 20rpx;
        margin-bottom: 16rpx;

        .progress-bar {
          flex: 1;
          height: 16rpx;
          background: rgba(255,255,255,0.3);
          border-radius: 8rpx;
          overflow: hidden;

          .progress-fill {
            height: 100%;
            background: #fff;
            border-radius: 8rpx;
            transition: width 0.5s ease;
          }
        }

        .progress-text {
          font-size: $font-size-sm;
          font-weight: bold;
          min-width: 70rpx;
          text-align: right;
        }
      }

      .weight-lost {
        font-size: $font-size-sm;
        opacity: 0.9;
        text-align: center;
      }
    }

    .stats-row {
      display: flex;
      align-items: center;
      justify-content: space-around;
      background: rgba(255,255,255,0.15);
      border-radius: $border-radius;
      padding: 24rpx 0;

      .stat-item {
        text-align: center;
        flex: 1;
        .stat-val { display: block; font-size: 40rpx; font-weight: bold; }
        .stat-label { font-size: $font-size-sm; opacity: 0.85; }
      }

      .stat-divider {
        width: 1rpx;
        height: 50rpx;
        background: rgba(255,255,255,0.3);
      }
    }
  }

  .no-goal-card {
    margin: 40rpx 30rpx;
    text-align: center;
    padding: 60rpx 40rpx;

    .no-goal-icon { display: block; font-size: 80rpx; margin-bottom: 20rpx; }
    .no-goal-text { display: block; font-size: $font-size-lg; font-weight: 600; color: $text-primary; margin-bottom: 16rpx; }
    .no-goal-hint { display: block; font-size: $font-size-sm; color: $text-secondary; line-height: 1.6; margin-bottom: 40rpx; }

    .set-goal-btn {
      background: linear-gradient(135deg, $primary-color, $primary-light);
      color: #fff;
      border-radius: $border-radius;
      border: none;
      padding: 0 60rpx;
      height: 88rpx;
      line-height: 88rpx;
      font-size: $font-size-md;
      &::after { border: none; }
    }
  }

  .section {
    margin: 30rpx 30rpx 0;

    .section-title {
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: 16rpx;
    }
  }

  .plan-section {
    .plan-list {
      background: #fff;
      border-radius: $border-radius;
      overflow: hidden;

      .plan-item {
        display: flex;
        align-items: center;
        padding: 26rpx 30rpx;
        border-bottom: 1rpx solid $border-color;

        &:last-child { border-bottom: none; }

        .plan-icon { font-size: 36rpx; margin-right: 20rpx; }
        .plan-key { flex: 1; font-size: $font-size-md; color: $text-primary; }
        .plan-val { font-size: $font-size-md; color: $primary-color; font-weight: 500; }
        .remark-text { color: $text-secondary; font-weight: normal; max-width: 320rpx; text-align: right; }
      }
    }

    .modify-btn {
      margin-top: 24rpx;
      width: 100%;
      height: 88rpx;
      line-height: 88rpx;
      background: #fff;
      color: $primary-color;
      font-size: $font-size-md;
      border-radius: $border-radius;
      border: 2rpx solid $primary-color;
      &::after { border: none; }
    }
  }

  .history-item {
    margin-bottom: 16rpx;

    .history-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12rpx;

      .history-type { font-size: $font-size-md; font-weight: 600; color: $text-primary; }

      .history-status {
        font-size: $font-size-sm;
        padding: 4rpx 16rpx;
        border-radius: 20rpx;

        &.status-0 { background: #fff3e0; color: #e67e22; }
        &.status-2 { background: #e8f5e9; color: #27ae60; }
      }
    }

    .history-detail {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }

  .loading-wrap {
    text-align: center;
    padding: 80rpx 0;
    color: $text-hint;
    font-size: $font-size-sm;
  }
}
</style>
