<template>
  <view class="monthly-report-page">
    <!-- 周期选择 -->
    <view class="period-selector">
      <text class="arrow" @click="changeMonth(-1)">‹</text>
      <text class="period">{{ report?.month || '加载中...' }}</text>
      <text class="arrow" @click="changeMonth(1)">›</text>
    </view>

    <!-- 总体概览 -->
    <view class="overview-card" v-if="report?.overview">
      <view class="overview-header">
        <text class="overview-title">本月概览</text>
        <view class="rating-badge" :class="getRatingClass(report.achievementRate.rating)">
          {{ report.achievementRate.rating }}
        </view>
      </view>
      <view class="overview-grid">
        <view class="overview-item">
          <text class="overview-value" :class="{ 'positive': report.overview.totalWeightLoss > 0 }">
            {{ report.overview.totalWeightLoss > 0 ? '-' : '+' }}{{ Math.abs(report.overview.totalWeightLoss).toFixed(1) }}
          </text>
          <text class="overview-label">减重(kg)</text>
        </view>
        <view class="overview-item">
          <text class="overview-value">{{ report.overview.avgDailyCalorie }}</text>
          <text class="overview-label">日均热量</text>
        </view>
        <view class="overview-item">
          <text class="overview-value">{{ report.overview.totalExerciseHours }}h</text>
          <text class="overview-label">运动时长</text>
        </view>
        <view class="overview-item">
          <text class="overview-value">{{ report.overview.checkinDays }}</text>
          <text class="overview-label">打卡天数</text>
        </view>
      </view>
    </view>

    <!-- 体重趋势 -->
    <view class="card" v-if="report?.weightTrend">
      <view class="card-title">
        <text>体重趋势</text>
        <text class="trend-tag" :class="getTrendClass(report.weightTrend.trend)">{{ report.weightTrend.trend }}</text>
      </view>
      <view class="weight-stats">
        <view class="weight-stat">
          <text class="stat-label">月初</text>
          <text class="stat-num">{{ report.weightTrend.startWeight.toFixed(1) }}kg</text>
        </view>
        <view class="weight-stat">
          <text class="stat-label">月末</text>
          <text class="stat-num">{{ report.weightTrend.endWeight.toFixed(1) }}kg</text>
        </view>
        <view class="weight-stat">
          <text class="stat-label">最低</text>
          <text class="stat-num highlight">{{ report.weightTrend.minWeight.toFixed(1) }}kg</text>
        </view>
        <view class="weight-stat">
          <text class="stat-label">平均</text>
          <text class="stat-num">{{ report.weightTrend.avgWeight.toFixed(1) }}kg</text>
        </view>
      </view>
      <!-- 趋势图 -->
      <view class="trend-chart" v-if="report.weightTrend.trendData.length > 1">
        <view class="chart-container">
          <view class="chart-line">
            <view
              v-for="(point, index) in report.weightTrend.trendData"
              :key="index"
              class="chart-point"
              :style="getPointStyle(point, index)"
            >
              <view class="point-dot"></view>
              <text class="point-label" v-if="index % 3 === 0">{{ point.date.slice(5) }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 热量分析 -->
    <view class="card" v-if="report?.calorieAnalysis">
      <view class="card-title">热量分析</view>
      <view class="calorie-overview">
        <view class="calorie-circle">
          <text class="circle-value">{{ report.calorieAnalysis.avgIntake }}</text>
          <text class="circle-label">日均摄入</text>
        </view>
        <view class="calorie-goal">
          <text class="goal-label">目标</text>
          <text class="goal-value">{{ report.calorieAnalysis.goal }}kcal</text>
        </view>
      </view>
      <view class="calorie-detail">
        <view class="detail-row">
          <text class="detail-label">达标天数</text>
          <text class="detail-value success">{{ report.calorieAnalysis.达标Days }}天</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">超标天数</text>
          <text class="detail-value warning">{{ report.calorieAnalysis.overDays }}天</text>
        </view>
      </view>
      <view class="meal-calories">
        <view class="meal-item">
          <text class="meal-name">早餐</text>
          <text class="meal-value">{{ report.calorieAnalysis.avgBreakfast }}kcal</text>
        </view>
        <view class="meal-item">
          <text class="meal-name">午餐</text>
          <text class="meal-value">{{ report.calorieAnalysis.avgLunch }}kcal</text>
        </view>
        <view class="meal-item">
          <text class="meal-name">晚餐</text>
          <text class="meal-value">{{ report.calorieAnalysis.avgDinner }}kcal</text>
        </view>
      </view>
    </view>

    <!-- 运动分析 -->
    <view class="card" v-if="report?.exerciseAnalysis">
      <view class="card-title">运动分析</view>
      <view class="exercise-stats">
        <view class="exercise-item">
          <text class="exercise-value">{{ report.exerciseAnalysis.exerciseDays }}</text>
          <text class="exercise-label">运动天数</text>
        </view>
        <view class="exercise-item">
          <text class="exercise-value">{{ report.exerciseAnalysis.exerciseFrequency }}%</text>
          <text class="exercise-label">运动频率</text>
        </view>
        <view class="exercise-item">
          <text class="exercise-value">{{ report.exerciseAnalysis.totalBurn }}</text>
          <text class="exercise-label">总消耗(kcal)</text>
        </view>
      </view>
      <!-- 运动类型分布 -->
      <view class="type-distribution" v-if="report.exerciseAnalysis.typeDistribution.length">
        <text class="distribution-title">运动类型分布</text>
        <view class="type-list">
          <view class="type-item" v-for="(type, index) in report.exerciseAnalysis.typeDistribution" :key="index">
            <view class="type-info">
              <text class="type-name">{{ type.type }}</text>
              <text class="type-count">{{ type.count }}次 {{ type.minutes }}分钟</text>
            </view>
            <view class="type-bar">
              <view class="type-progress" :style="{ width: type.percentage + '%' }"></view>
            </view>
            <text class="type-percent">{{ type.percentage }}%</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 达成率 -->
    <view class="card achievement-card" v-if="report?.achievementRate">
      <view class="card-title">目标达成率</view>
      <view class="achievement-grid">
        <view class="achievement-item">
          <text class="achievement-label">体重目标</text>
          <view class="achievement-bar">
            <view class="progress-fill" :style="{ width: report.achievementRate.weightGoalRate + '%' }"></view>
          </view>
          <text class="achievement-percent">{{ report.achievementRate.weightGoalRate }}%</text>
        </view>
        <view class="achievement-item">
          <text class="achievement-label">热量目标</text>
          <view class="achievement-bar">
            <view class="progress-fill" :style="{ width: report.achievementRate.calorieGoalRate + '%' }"></view>
          </view>
          <text class="achievement-percent">{{ report.achievementRate.calorieGoalRate }}%</text>
        </view>
        <view class="achievement-item">
          <text class="achievement-label">运动目标</text>
          <view class="achievement-bar">
            <view class="progress-fill" :style="{ width: report.achievementRate.exerciseGoalRate + '%' }"></view>
          </view>
          <text class="achievement-percent">{{ report.achievementRate.exerciseGoalRate }}%</text>
        </view>
      </view>
      <view class="overall-rate">
        <text class="overall-label">综合达成率</text>
        <text class="overall-value">{{ report.achievementRate.overallRate }}%</text>
      </view>
    </view>

    <!-- 每周汇总 -->
    <view class="card" v-if="report?.weeklySummaries?.length">
      <view class="card-title">每周汇总</view>
      <view class="weekly-list">
        <view class="weekly-header">
          <text class="col">周期</text>
          <text class="col">体重</text>
          <text class="col">热量</text>
          <text class="col">运动</text>
        </view>
        <view class="weekly-item" v-for="(week, index) in report.weeklySummaries" :key="index">
          <text class="col">{{ week.weekRange }}</text>
          <text class="col" :class="{ 'positive': week.weightChange > 0 }">
            {{ week.weightChange > 0 ? '-' : '+' }}{{ Math.abs(week.weightChange).toFixed(1) }}
          </text>
          <text class="col">{{ week.avgCalorie }}</text>
          <text class="col">{{ week.exerciseMinutes }}'</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { reportApi } from '../../api/report.api'
import type { MonthlyReport, TrendPoint } from '../../types/report'

const report = ref<MonthlyReport | null>(null)
const monthOffset = ref(0)

onMounted(() => {
  loadReport()
})

async function loadReport() {
  try {
    report.value = await reportApi.getMonthlyReport(monthOffset.value)
  } catch (error) {
    console.error('加载月报告失败:', error)
  }
}

function changeMonth(delta: number) {
  monthOffset.value += delta
  loadReport()
}

function getRatingClass(rating: string): string {
  const map: Record<string, string> = {
    '优秀': 'excellent',
    '良好': 'good',
    '一般': 'average',
    '需努力': 'need-work'
  }
  return map[rating] || ''
}

function getTrendClass(trend: string): string {
  const map: Record<string, string> = {
    '下降': 'down',
    '上升': 'up',
    '持平': 'flat'
  }
  return map[trend] || ''
}

function getPointStyle(point: TrendPoint, index: number): any {
  if (!report.value?.weightTrend) return {}
  const trend = report.value.weightTrend
  const min = trend.minWeight
  const max = trend.maxWeight
  const range = max - min || 1
  const percent = ((point.weight - min) / range) * 100
  return {
    left: (index / (trend.trendData.length - 1) * 100) + '%',
    bottom: percent + '%'
  }
}
</script>

<style lang="scss" scoped>
.monthly-report-page {
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
    font-size: 36rpx;
    font-weight: 600;
    color: #333;
  }
}

.overview-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16rpx;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  color: #fff;

  .overview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;

    .overview-title {
      font-size: 32rpx;
      font-weight: 600;
    }

    .rating-badge {
      padding: 8rpx 24rpx;
      border-radius: 24rpx;
      font-size: 24rpx;
      font-weight: 600;
      background: rgba(255,255,255,0.3);

      &.excellent { background: #4caf50; }
      &.good { background: #8bc34a; }
      &.average { background: #ffc107; }
      &.need-work { background: #ff9800; }
    }
  }

  .overview-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20rpx;

    .overview-item {
      text-align: center;

      .overview-value {
        display: block;
        font-size: 40rpx;
        font-weight: 700;
        margin-bottom: 8rpx;

        &.positive {
          color: #a5d6a7;
        }
      }

      .overview-label {
        font-size: 22rpx;
        opacity: 0.9;
      }
    }
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
    display: flex;
    justify-content: space-between;
    align-items: center;

    .trend-tag {
      font-size: 24rpx;
      padding: 4rpx 16rpx;
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
    }
  }
}

.weight-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  margin-bottom: 30rpx;

  .weight-stat {
    text-align: center;

    .stat-label {
      display: block;
      font-size: 22rpx;
      color: #999;
      margin-bottom: 8rpx;
    }

    .stat-num {
      font-size: 28rpx;
      font-weight: 600;
      color: #333;

      &.highlight {
        color: #667eea;
      }
    }
  }
}

.trend-chart {
  height: 200rpx;
  background: #f9f9f9;
  border-radius: 12rpx;
  padding: 20rpx;
  position: relative;

  .chart-container {
    position: relative;
    height: 100%;

    .chart-line {
      position: absolute;
      left: 0;
      right: 0;
      top: 20rpx;
      bottom: 40rpx;
      border-bottom: 2rpx solid #e0e0e0;

      .chart-point {
        position: absolute;
        transform: translateX(-50%);

        .point-dot {
          width: 16rpx;
          height: 16rpx;
          background: #667eea;
          border-radius: 50%;
          border: 4rpx solid #fff;
          box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.4);
        }

        .point-label {
          position: absolute;
          bottom: -36rpx;
          left: 50%;
          transform: translateX(-50%);
          font-size: 18rpx;
          color: #999;
          white-space: nowrap;
        }
      }
    }
  }
}

.calorie-overview {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 60rpx;
  margin-bottom: 30rpx;

  .calorie-circle {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    border: 12rpx solid #667eea;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .circle-value {
      font-size: 40rpx;
      font-weight: 700;
      color: #667eea;
    }

    .circle-label {
      font-size: 22rpx;
      color: #999;
    }
  }

  .calorie-goal {
    text-align: center;

    .goal-label {
      display: block;
      font-size: 24rpx;
      color: #999;
      margin-bottom: 8rpx;
    }

    .goal-value {
      font-size: 36rpx;
      font-weight: 600;
      color: #333;
    }
  }
}

.calorie-detail {
  margin-bottom: 30rpx;

  .detail-row {
    display: flex;
    justify-content: space-between;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f5f5f5;

    .detail-label {
      font-size: 28rpx;
      color: #666;
    }

    .detail-value {
      font-size: 28rpx;
      font-weight: 600;

      &.success {
        color: #4caf50;
      }

      &.warning {
        color: #ff9800;
      }
    }
  }
}

.meal-calories {
  display: flex;
  justify-content: space-around;

  .meal-item {
    text-align: center;

    .meal-name {
      display: block;
      font-size: 24rpx;
      color: #999;
      margin-bottom: 8rpx;
    }

    .meal-value {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }
  }
}

.exercise-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30rpx;

  .exercise-item {
    text-align: center;

    .exercise-value {
      display: block;
      font-size: 48rpx;
      font-weight: 700;
      color: #667eea;
      margin-bottom: 8rpx;
    }

    .exercise-label {
      font-size: 24rpx;
      color: #666;
    }
  }
}

.type-distribution {
  .distribution-title {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 20rpx;
    display: block;
  }

  .type-list {
    .type-item {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;

      .type-info {
        width: 200rpx;

        .type-name {
          display: block;
          font-size: 28rpx;
          color: #333;
        }

        .type-count {
          font-size: 22rpx;
          color: #999;
        }
      }

      .type-bar {
        flex: 1;
        height: 16rpx;
        background: #f0f0f0;
        border-radius: 8rpx;
        margin: 0 20rpx;
        overflow: hidden;

        .type-progress {
          height: 100%;
          background: linear-gradient(90deg, #667eea, #764ba2);
          border-radius: 8rpx;
          transition: width 0.3s;
        }
      }

      .type-percent {
        width: 80rpx;
        text-align: right;
        font-size: 26rpx;
        color: #667eea;
        font-weight: 600;
      }
    }
  }
}

.achievement-card {
  .achievement-grid {
    margin-bottom: 30rpx;

    .achievement-item {
      display: flex;
      align-items: center;
      margin-bottom: 24rpx;

      .achievement-label {
        width: 140rpx;
        font-size: 28rpx;
        color: #666;
      }

      .achievement-bar {
        flex: 1;
        height: 20rpx;
        background: #f0f0f0;
        border-radius: 10rpx;
        margin: 0 20rpx;
        overflow: hidden;

        .progress-fill {
          height: 100%;
          background: linear-gradient(90deg, #667eea, #764ba2);
          border-radius: 10rpx;
          transition: width 0.3s;
        }
      }

      .achievement-percent {
        width: 100rpx;
        text-align: right;
        font-size: 28rpx;
        color: #667eea;
        font-weight: 600;
      }
    }
  }

  .overall-rate {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 24rpx;
    border-top: 2rpx solid #f0f0f0;

    .overall-label {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }

    .overall-value {
      font-size: 48rpx;
      font-weight: 700;
      color: #667eea;
    }
  }
}

.weekly-list {
  .weekly-header {
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

  .weekly-item {
    display: flex;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    .col {
      flex: 1;
      text-align: center;
      font-size: 24rpx;
      color: #333;

      &.positive {
        color: #4caf50;
      }
    }
  }
}
</style>
