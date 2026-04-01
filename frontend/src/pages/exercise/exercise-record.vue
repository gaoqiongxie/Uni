<template>
  <view class="exercise-page">
    <!-- 今日运动汇总卡片 -->
    <view class="summary-card card">
      <view class="summary-title">
        <text class="icon">🔥</text>
        <text>今日运动</text>
      </view>
      <view class="summary-grid">
        <view class="summary-item">
          <text class="summary-value">{{ exerciseStore.todayExercises.length }}</text>
          <text class="summary-label">运动次数</text>
        </view>
        <view class="summary-item">
          <text class="summary-value">{{ totalDuration }}</text>
          <text class="summary-label">总时长(分钟)</text>
        </view>
        <view class="summary-item">
          <text class="summary-value">{{ totalCalories }}</text>
          <text class="summary-label">消耗(kcal)</text>
        </view>
      </view>
    </view>

    <!-- 今日运动记录列表 -->
    <view class="section-title" v-if="exerciseStore.todayExercises.length > 0">
      <text>今日记录</text>
    </view>

    <view
      class="record-item card"
      v-for="item in exerciseStore.todayExercises"
      :key="item.id"
      @click="navigateToDetail(item)"
    >
      <view class="record-header">
        <text class="exercise-icon">{{ getExerciseIcon(item.exerciseType) }}</text>
        <view class="record-info">
          <text class="exercise-name">{{ item.exerciseType }}</text>
          <text class="exercise-time">{{ formatTime(item.createTime) }}</text>
        </view>
        <view class="record-stats">
          <text class="stat-duration">{{ item.duration }}分钟</text>
          <text class="stat-calorie">{{ item.calorieBurn || 0 }}kcal</text>
        </view>
      </view>
      <view class="record-tags">
        <text class="tag">{{ item.intensityName }}</text>
        <text class="tag feeling">{{ item.feelingName }}</text>
        <text class="tag" v-if="item.distance">{{ item.distance }}km</text>
      </view>
      <!-- 运动照片缩略图 -->
      <view class="record-photos" v-if="item.photos && item.photos.length">
        <image
          v-for="(photo, idx) in item.photos.slice(0, 3)"
          :key="idx"
          class="exercise-thumb"
          :src="photo"
          mode="aspectFill"
          @click.stop="previewPhotos(item.photos!, idx)"
        />
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="exerciseStore.todayExercises.length === 0 && !exerciseStore.loading">
      <text class="empty-icon">🏃‍♀️</text>
      <text class="empty-text">今天还没有运动记录</text>
      <text class="empty-hint">点击下方按钮记录你的运动</text>
    </view>

    <!-- 浮动添加按钮 -->
    <view class="fab-btn" @click="navigateToAdd">
      <text class="fab-icon">+</text>
    </view>

    <!-- 底部入口 -->
    <view class="bottom-actions" v-if="exerciseStore.todayExercises.length > 0">
      <view class="history-link" @click="navigateToHistory">
        <text>查看运动历史</text>
        <text class="arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useExerciseStore } from '../../store/exercise'
import type { ExerciseRecordVO } from '../../types/exercise'

const exerciseStore = useExerciseStore()

const totalDuration = computed(() =>
  exerciseStore.todayExercises.reduce((sum, r) => sum + (r.duration || 0), 0)
)

const totalCalories = computed(() =>
  exerciseStore.todayExercises.reduce((sum, r) => sum + (r.calorieBurn || 0), 0)
)

function getExerciseIcon(type: string): string {
  const icons: Record<string, string> = {
    '跑步': '🏃', '游泳': '🏊', '瑜伽': '🧘', '力量训练': '💪',
    '骑行': '🚴', '跳绳': '⏭', '散步': '🚶'
  }
  return icons[type] || '⛹'
}

function formatTime(time?: string): string {
  if (!time) return ''
  return time.substring(11, 16) // HH:mm
}

function previewPhotos(photos: string[], current: number) {
  uni.previewImage({ urls: photos, current })
}

function navigateToAdd() {
  uni.navigateTo({ url: '/pages/exercise/exercise-add' })
}

function navigateToHistory() {
  uni.navigateTo({ url: '/pages/exercise/exercise-list' })
}

function navigateToDetail(item: ExerciseRecordVO) {
  // 暂不实现详情页，点击无效果
}

onMounted(() => {
  const today = new Date().toISOString().split('T')[0]
  exerciseStore.loadExercisesByDate(today)
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.exercise-page {
  min-height: 100vh;
  padding: 24rpx;
  padding-bottom: 140rpx;
  background: $bg-color;

  .summary-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    margin-bottom: 32rpx;

    .summary-title {
      display: flex;
      align-items: center;
      gap: 8rpx;
      font-size: $font-size-md;
      font-weight: 600;
      margin-bottom: 32rpx;

      .icon { font-size: 40rpx; }
    }

    .summary-grid {
      display: flex;
      justify-content: space-around;

      .summary-item {
        text-align: center;

        .summary-value {
          display: block;
          font-size: 48rpx;
          font-weight: 700;
          line-height: 1.2;
        }

        .summary-label {
          display: block;
          font-size: $font-size-xs;
          opacity: 0.8;
          margin-top: 8rpx;
        }
      }
    }
  }

  .section-title {
    font-size: $font-size-md;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 20rpx;
    padding-left: 8rpx;
  }

  .record-item {
    margin-bottom: 20rpx;

    .record-header {
      display: flex;
      align-items: center;

      .exercise-icon {
        font-size: 56rpx;
        margin-right: 20rpx;
      }

      .record-info {
        flex: 1;

        .exercise-name {
          display: block;
          font-size: $font-size-md;
          font-weight: 600;
          color: $text-primary;
        }

        .exercise-time {
          display: block;
          font-size: $font-size-xs;
          color: $text-tertiary;
          margin-top: 4rpx;
        }
      }

      .record-stats {
        text-align: right;

        .stat-duration {
          display: block;
          font-size: $font-size-md;
          font-weight: 600;
          color: $primary-color;
        }

        .stat-calorie {
          display: block;
          font-size: $font-size-xs;
          color: $text-tertiary;
        }
      }
    }

    .record-tags {
      display: flex;
      gap: 12rpx;
      margin-top: 16rpx;
      padding-top: 16rpx;
      border-top: 2rpx solid $border-color;

      .tag {
        font-size: $font-size-xs;
        padding: 4rpx 16rpx;
        background: $bg-gray;
        color: $text-secondary;
        border-radius: 20rpx;

        &.feeling {
          background: #fff3e0;
          color: #ff9800;
        }
      }
    }

    .record-photos {
      display: flex;
      gap: 12rpx;
      margin-top: 16rpx;

      .exercise-thumb {
        width: 120rpx;
        height: 120rpx;
        border-radius: $border-radius-sm;
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 120rpx 40rpx;

    .empty-icon {
      display: block;
      font-size: 120rpx;
      margin-bottom: 24rpx;
    }

    .empty-text {
      display: block;
      font-size: $font-size-md;
      color: $text-secondary;
    }

    .empty-hint {
      display: block;
      font-size: $font-size-sm;
      color: $text-tertiary;
      margin-top: 12rpx;
    }
  }

  .fab-btn {
    position: fixed;
    right: 48rpx;
    bottom: 180rpx;
    width: 112rpx;
    height: 112rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
    box-shadow: $shadow-md;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 100;

    .fab-icon {
      font-size: 56rpx;
      color: #fff;
      line-height: 1;
      margin-top: -4rpx;
    }

    &:active {
      opacity: 0.85;
      transform: scale(0.95);
    }
  }

  .bottom-actions {
    padding: 24rpx 0;

    .history-link {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8rpx;
      font-size: $font-size;
      color: $primary-color;

      .arrow {
        font-size: 36rpx;
      }
    }
  }
}
</style>
