<template>
  <view class="weekly-report-page">
    <!-- 周期选择 -->
    <view class="period-selector">
      <text class="arrow" @click="changeWeek(-1)">‹</text>
      <text class="period">{{ report?.weekRange || '加载中...' }}</text>
      <text class="arrow" @click="changeWeek(1)">›</text>
    </view>

    <!-- 体重变化卡片 -->
    <view class="card" v-if="report?.weightChange">
      <view class="card-title">体重变化</view>
      <view class="weight-change">
        <view class="weight-item">
          <text class="weight-label">周初</text>
          <text class="weight-value">{{ report.weightChange.startWeight.toFixed(1) }}</text>
          <text class="weight-unit">kg</text>
        </view>
        <view class="change-arrow" :class="getDirectionClass(report.weightChange.direction)">
          <text class="change-icon">{{ getDirectionIcon(report.weightChange.direction) }}</text>
          <text class="change-value">{{ report.weightChange.change.toFixed(1) }}kg</text>
        </view>
        <view class="weight-item">
          <text class="weight-label">周末</text>
          <text class="weight-value">{{ report.weightChange.endWeight.toFixed(1) }}</text>
          <text class="weight-unit">kg</text>
        </view>
      </view>
    </view>

    <!-- 热量统计 -->
    <view class="card" v-if="report?.calorieStats">
      <view class="card-title">热量统计</view>
      <view class="stats-grid">
        <view class="stat-item">
          <text class="stat-value">{{ report.calorieStats.avgIntake }}</text>
          <text class="stat-label">日均摄入(kcal)</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ report.calorieStats.goal }}</text>
          <text class="stat-label">目标(kcal)</text>
        </view>
        <view class="stat-item">
          <text class="stat-value highlight">{{ report.calorieStats.达标Days }}</text>
          <text class="stat-label">达标天数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ report.calorieStats.totalIntake }}</text>
          <text class="stat-label">总摄入(kcal)</text>
        </view>
      </view>
    </view>

    <!-- 运动汇总 -->
    <view class="card" v-if="report?.exerciseSummary">
      <view class="card-title">运动汇总</view>
      <view class="stats-grid">
        <view class="stat-item">
          <text class="stat-value">{{ report.exerciseSummary.exerciseDays }}</text>
          <text class="stat-label">运动天数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ report.exerciseSummary.totalDuration }}</text>
          <text class="stat-label">总时长(分钟)</text>
        </view>
        <view class="stat-item">
          <text class="stat-value highlight">{{ report.exerciseSummary.totalBurn }}</text>
          <text class="stat-label">总消耗(kcal)</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ report.exerciseSummary.topExerciseType || '-' }}</text>
          <text class="stat-label">最常运动</text>
        </view>
      </view>
    </view>

    <!-- 打卡统计 -->
    <view class="card" v-if="report?.checkinStats">
      <view class="card-title">打卡统计</view>
      <view class="checkin-stats">
        <view class="checkin-item">
          <text class="checkin-value">{{ report.checkinStats.checkinDays }}</text>
          <text class="checkin-label">打卡天数</text>
        </view>
        <view class="checkin-item">
          <text class="checkin-value">{{ report.checkinStats.streakDays }}</text>
          <text class="checkin-label">连续打卡</text>
        </view>
      </view>
    </view>

    <!-- 每日数据 -->
    <view class="card daily-card" v-if="report?.dailyDataList?.length">
      <view class="card-title">每日详情</view>
      <view class="daily-list">
        <view class="daily-header">
          <text class="col">日期</text>
          <text class="col">体重</text>
          <text class="col">摄入</text>
          <text class="col">运动</text>
        </view>
        <view class="daily-item" v-for="(day, index) in report.dailyDataList" :key="index">
          <view class="col date-col">
            <text class="day-date">{{ day.date.slice(5) }}</text>
            <text class="day-weekday">{{ day.weekday }}</text>
          </view>
          <text class="col">{{ day.weight ? day.weight.toFixed(1) : '-' }}</text>
          <text class="col" :class="{ 'over-goal': day.calorieIntake > report.calorieStats.goal }">
            {{ day.calorieIntake || '-' }}
          </text>
          <text class="col">{{ day.exerciseMinutes ? day.exerciseMinutes + '\'' : '-' }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { reportApi } from '../../api/report.api'
import type { WeeklyReport } from '../../types/report'

const report = ref<WeeklyReport | null>(null)
const weekOffset = ref(0)

onMounted(() => {
  loadReport()
})

async function loadReport() {
  try {
    report.value = await reportApi.getWeeklyReport(weekOffset.value)
  } catch (error) {
    console.error('加载周报告失败:', error)
  }
}

function changeWeek(delta: number) {
  weekOffset.value += delta
  loadReport()
}

function getDirectionClass(direction: number): string {
  if (direction < 0) return 'down'
  if (direction > 0) return 'up'
  return 'flat'
}

function getDirectionIcon(direction: number): string {
  if (direction < 0) return '↓'
  if (direction > 0) return '↑'
  return '→'
}
</script>

<style lang="scss" scoped>
.weekly-report-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.period-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx;
  background: #fff;
  margin-bottom: 20rpx;

  .arrow {
    font-size: 48rpx;
    color: #667eea;
    padding: 0 40rpx;
  }

  .period {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
  }
}

.card {
  background: #fff;
  border-radius: 16rpx;
  margin: 20rpx 30rpx;
  padding: 30rpx;

  .card-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 24rpx;
    padding-bottom: 16rpx;
    border-bottom: 2rpx solid #f0f0f0;
  }
}

.weight-change {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .weight-item {
    text-align: center;

    .weight-label {
      display: block;
      font-size: 24rpx;
      color: #999;
      margin-bottom: 8rpx;
    }

    .weight-value {
      font-size: 48rpx;
      font-weight: 700;
      color: #333;
    }

    .weight-unit {
      font-size: 24rpx;
      color: #999;
      margin-left: 4rpx;
    }
  }

  .change-arrow {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx 40rpx;
    border-radius: 12rpx;

    &.down {
      background: #e8f5e9;
      color: #4caf50;
    }

    &.up {
      background: #ffebee;
      color: #f44336;
    }

    &.flat {
      background: #f5f5f5;
      color: #999;
    }

    .change-icon {
      font-size: 40rpx;
      font-weight: 700;
    }

    .change-value {
      font-size: 24rpx;
      margin-top: 4rpx;
    }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;

  .stat-item {
    text-align: center;

    .stat-value {
      display: block;
      font-size: 36rpx;
      font-weight: 700;
      color: #333;
      margin-bottom: 8rpx;

      &.highlight {
        color: #667eea;
      }
    }

    .stat-label {
      font-size: 22rpx;
      color: #999;
    }
  }
}

.checkin-stats {
  display: flex;
  justify-content: space-around;

  .checkin-item {
    text-align: center;

    .checkin-value {
      display: block;
      font-size: 56rpx;
      font-weight: 700;
      color: #667eea;
      margin-bottom: 8rpx;
    }

    .checkin-label {
      font-size: 26rpx;
      color: #666;
    }
  }
}

.daily-card {
  .daily-list {
    .daily-header {
      display: flex;
      padding: 16rpx 0;
      border-bottom: 2rpx solid #f0f0f0;

      .col {
        flex: 1;
        text-align: center;
        font-size: 24rpx;
        color: #999;
        font-weight: 600;
      }
    }

    .daily-item {
      display: flex;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f5f5f5;

      &:last-child {
        border-bottom: none;
      }

      .col {
        flex: 1;
        text-align: center;
        font-size: 26rpx;
        color: #333;

        &.over-goal {
          color: #f44336;
        }
      }

      .date-col {
        display: flex;
        flex-direction: column;

        .day-date {
          font-size: 24rpx;
          color: #333;
        }

        .day-weekday {
          font-size: 20rpx;
          color: #999;
          margin-top: 4rpx;
        }
      }
    }
  }
}
</style>
