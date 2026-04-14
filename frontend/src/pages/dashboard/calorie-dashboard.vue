<template>
  <view class="calorie-dashboard">
    <!-- 顶部标题 -->
    <view class="header">
      <view class="back-btn" @click="goBack">
        <text class="iconfont icon-back"></text>
      </view>
      <text class="title">热量收支看板</text>
      <view class="date-switch" @click="showDatePicker = true">
        <text class="date-text">{{ displayDate }}</text>
        <text class="arrow">▾</text>
      </view>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-wrap">
      <uni-load-more status="loading" />
    </view>

    <template v-else-if="dashboard">
      <!-- 今日热量环形卡片 -->
      <view class="ring-card">
        <view class="ring-left">
          <view class="ring-wrap">
            <canvas canvas-id="ringChart" style="width: 200rpx; height: 200rpx;" />
            <view class="ring-center">
              <text class="ring-value">{{ dashboard.today.netCalories }}</text>
              <text class="ring-label">净摄入</text>
            </view>
          </view>
        </view>
        <view class="ring-right">
          <view class="stat-row">
            <view class="stat-dot" style="background:#07c160"></view>
            <text class="stat-name">摄入</text>
            <text class="stat-val green">{{ dashboard.today.intakeCalories }} kcal</text>
          </view>
          <view class="stat-row">
            <view class="stat-dot" style="background:#fa8c16"></view>
            <text class="stat-name">消耗</text>
            <text class="stat-val orange">{{ dashboard.today.burnedCalories }} kcal</text>
          </view>
          <view class="stat-row">
            <view class="stat-dot" style="background:#1677ff"></view>
            <text class="stat-name">目标</text>
            <text class="stat-val blue">{{ dashboard.today.targetCalories }} kcal</text>
          </view>
          <view class="gap-badge" :class="gapClass">
            <text>{{ gapText }}</text>
          </view>
        </view>
      </view>

      <!-- 状态描述 -->
      <view class="status-banner" :class="gapClass">
        <text class="status-icon">{{ statusIcon }}</text>
        <text class="status-text">{{ dashboard.today.statusDesc }}</text>
        <text class="streak-text" v-if="dashboard.streakDays > 0">🔥 连续达标 {{ dashboard.streakDays }} 天</text>
      </view>

      <!-- 营养素分布 -->
      <view class="nutrient-card">
        <text class="card-title">今日营养素分布</text>
        <view class="nutrient-bars">
          <view class="nutrient-item">
            <view class="nutrient-header">
              <text class="n-name">蛋白质</text>
              <text class="n-val">{{ dashboard.today.proteinIntake }}g</text>
            </view>
            <view class="bar-bg">
              <view class="bar-fill protein" :style="{ width: proteinPercent + '%' }"></view>
            </view>
          </view>
          <view class="nutrient-item">
            <view class="nutrient-header">
              <text class="n-name">脂肪</text>
              <text class="n-val">{{ dashboard.today.fatIntake }}g</text>
            </view>
            <view class="bar-bg">
              <view class="bar-fill fat" :style="{ width: fatPercent + '%' }"></view>
            </view>
          </view>
          <view class="nutrient-item">
            <view class="nutrient-header">
              <text class="n-name">碳水</text>
              <text class="n-val">{{ dashboard.today.carbsIntake }}g</text>
            </view>
            <view class="bar-bg">
              <view class="bar-fill carbs" :style="{ width: carbsPercent + '%' }"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- 切换周/月趋势 -->
      <view class="trend-card">
        <view class="trend-header">
          <text class="card-title">热量趋势</text>
          <view class="trend-tabs">
            <text
              :class="['trend-tab', { active: trendMode === 'week' }]"
              @click="switchTrend('week')"
            >7天</text>
            <text
              :class="['trend-tab', { active: trendMode === 'month' }]"
              @click="switchTrend('month')"
            >30天</text>
          </view>
        </view>

        <!-- 趋势柱状图（用 view 模拟） -->
        <view class="bar-chart">
          <view
            v-for="(point, i) in trendData"
            :key="i"
            class="chart-col"
          >
            <view class="chart-bars">
              <view
                class="bar-intake"
                :style="{ height: barHeight(point.intake) + 'rpx' }"
              ></view>
              <view
                class="bar-burned"
                :style="{ height: barHeight(point.burned) + 'rpx' }"
              ></view>
            </view>
            <text class="chart-label">{{ point.label }}</text>
          </view>
        </view>

        <view class="chart-legend">
          <view class="legend-item"><view class="legend-dot green"></view><text>摄入</text></view>
          <view class="legend-item"><view class="legend-dot orange"></view><text>消耗</text></view>
        </view>
      </view>

      <!-- 本周汇总 -->
      <view class="week-summary">
        <view class="summary-item">
          <text class="summary-val">{{ dashboard.weekAvgIntake }}</text>
          <text class="summary-label">周均摄入(kcal)</text>
        </view>
        <view class="summary-divider"></view>
        <view class="summary-item">
          <text class="summary-val">{{ dashboard.weekAvgBurned }}</text>
          <text class="summary-label">周均消耗(kcal)</text>
        </view>
        <view class="summary-divider"></view>
        <view class="summary-item">
          <text class="summary-val" :class="dashboard.weekCumulativeGap < 0 ? 'green' : 'red'">
            {{ dashboard.weekCumulativeGap > 0 ? '+' : '' }}{{ dashboard.weekCumulativeGap }}
          </text>
          <text class="summary-label">本周累计缺口</text>
        </view>
      </view>

      <!-- 快速入口 -->
      <view class="quick-actions">
        <view class="action-btn" @click="goFoodRecord">
          <text class="action-icon">🍽️</text>
          <text class="action-label">记录饮食</text>
        </view>
        <view class="action-btn" @click="goExerciseCalc">
          <text class="action-icon">🏃</text>
          <text class="action-label">运动消耗</text>
        </view>
        <view class="action-btn" @click="goExerciseRecord">
          <text class="action-icon">💪</text>
          <text class="action-label">运动记录</text>
        </view>
      </view>
    </template>

    <view v-else class="empty-tip">
      <text>暂无数据，先去记录饮食和运动吧</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getTodayCalorieDashboard, getWeeklyTrend, getMonthlyTrend } from '@/api/calorie-dashboard.api'
import type { CalorieDashboardVO, CalorieTrendPoint } from '@/types/calorie-dashboard'

const loading = ref(false)
const dashboard = ref<CalorieDashboardVO | null>(null)
const trendMode = ref<'week' | 'month'>('week')
const trendData = ref<CalorieTrendPoint[]>([])
const showDatePicker = ref(false)

const displayDate = computed(() => {
  const now = new Date()
  return `${now.getMonth() + 1}月${now.getDate()}日`
})

const gapClass = computed(() => {
  if (!dashboard.value) return ''
  const gap = Number(dashboard.value.today.calorieGap)
  if (gap < -200) return 'status-good'
  if (gap < 200)  return 'status-ok'
  return 'status-bad'
})

const gapText = computed(() => {
  if (!dashboard.value) return ''
  const gap = Number(dashboard.value.today.calorieGap)
  return gap <= 0 ? `赤字 ${Math.abs(gap)} kcal` : `超标 ${gap} kcal`
})

const statusIcon = computed(() => {
  const cls = gapClass.value
  if (cls === 'status-good') return '🎉'
  if (cls === 'status-ok')   return '👍'
  return '⚠️'
})

const totalNutrient = computed(() => {
  if (!dashboard.value) return 1
  const { proteinIntake, fatIntake, carbsIntake } = dashboard.value.today
  const total = Number(proteinIntake) * 4 + Number(fatIntake) * 9 + Number(carbsIntake) * 4
  return total > 0 ? total : 1
})

const proteinPercent = computed(() =>
  Math.min(Math.round(Number(dashboard.value?.today.proteinIntake ?? 0) * 4 / totalNutrient.value * 100), 100)
)
const fatPercent = computed(() =>
  Math.min(Math.round(Number(dashboard.value?.today.fatIntake ?? 0) * 9 / totalNutrient.value * 100), 100)
)
const carbsPercent = computed(() =>
  Math.min(Math.round(Number(dashboard.value?.today.carbsIntake ?? 0) * 4 / totalNutrient.value * 100), 100)
)

const maxIntake = computed(() =>
  Math.max(...trendData.value.map(t => Number(t.intake)), 1)
)

function barHeight(val: number): number {
  return Math.round((Number(val) / maxIntake.value) * 160)
}

onMounted(async () => {
  await loadData()
})

async function loadData() {
  loading.value = true
  try {
    const res = await getTodayCalorieDashboard()
    if (res.data) {
      dashboard.value = res.data
      trendData.value = res.data.weekTrend
    }
  } catch (e) {
    console.error('加载看板失败', e)
  } finally {
    loading.value = false
  }
}

async function switchTrend(mode: 'week' | 'month') {
  trendMode.value = mode
  try {
    const res = mode === 'week' ? await getWeeklyTrend() : await getMonthlyTrend()
    if (res.data) trendData.value = res.data
  } catch (e) {
    console.error('加载趋势失败', e)
  }
}

function goFoodRecord() {
  uni.navigateTo({ url: '/pages/meal/food-record' })
}
function goExerciseCalc() {
  uni.navigateTo({ url: '/pages/exercise/exercise-calc' })
}
function goExerciseRecord() {
  uni.navigateTo({ url: '/pages/exercise/exercise-record' })
}
function goBack() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.calorie-dashboard {
  min-height: 100vh;
  background: #f5f6fa;
  padding-bottom: 60rpx;
}

.header {
  display: flex;
  align-items: center;
  padding: 80rpx 32rpx 24rpx;
  background: #fff;

  .back-btn { margin-right: 24rpx; font-size: 36rpx; color: #333; }
  .title { flex: 1; font-size: 36rpx; font-weight: 700; color: #1a1a1a; }
  .date-switch {
    display: flex;
    align-items: center;
    gap: 8rpx;
    .date-text { font-size: 26rpx; color: #555; }
    .arrow { font-size: 20rpx; color: #999; }
  }
}

.loading-wrap { padding: 80rpx; text-align: center; }

.ring-card {
  margin: 24rpx 32rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  display: flex;
  align-items: center;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.07);

  .ring-left {
    .ring-wrap {
      position: relative;
      width: 200rpx;
      height: 200rpx;
      .ring-center {
        position: absolute;
        inset: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        .ring-value { font-size: 40rpx; font-weight: 800; color: #1a1a1a; }
        .ring-label { font-size: 22rpx; color: #999; margin-top: 4rpx; }
      }
    }
  }

  .ring-right {
    flex: 1;
    padding-left: 32rpx;

    .stat-row {
      display: flex;
      align-items: center;
      gap: 12rpx;
      margin-bottom: 16rpx;
      .stat-dot { width: 16rpx; height: 16rpx; border-radius: 50%; }
      .stat-name { font-size: 26rpx; color: #555; flex: 1; }
      .stat-val { font-size: 28rpx; font-weight: 600; }
      &.green { color: #07c160; }
      &.orange { color: #fa8c16; }
      &.blue { color: #1677ff; }
    }

    .gap-badge {
      display: inline-block;
      padding: 8rpx 20rpx;
      border-radius: 30rpx;
      font-size: 24rpx;
      font-weight: 600;
      margin-top: 8rpx;

      &.status-good { background: #f0fff4; color: #07c160; }
      &.status-ok   { background: #fff7e6; color: #faad14; }
      &.status-bad  { background: #fff2f0; color: #ff4d4f; }
    }
  }
}

.status-banner {
  margin: 0 32rpx 24rpx;
  border-radius: 20rpx;
  padding: 20rpx 28rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;

  &.status-good { background: #f0fff4; }
  &.status-ok   { background: #fff7e6; }
  &.status-bad  { background: #fff2f0; }

  .status-icon { font-size: 36rpx; }
  .status-text { flex: 1; font-size: 28rpx; color: #333; }
  .streak-text { font-size: 24rpx; color: #fa8c16; }
}

.nutrient-card, .trend-card, .week-summary {
  margin: 0 32rpx 24rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx 32rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
}

.card-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #1a1a1a;
  display: block;
  margin-bottom: 24rpx;
}

.nutrient-item {
  margin-bottom: 20rpx;
  .nutrient-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10rpx;
    .n-name { font-size: 26rpx; color: #555; }
    .n-val  { font-size: 26rpx; font-weight: 600; color: #333; }
  }
  .bar-bg {
    height: 16rpx;
    background: #f0f0f0;
    border-radius: 8rpx;
    overflow: hidden;
    .bar-fill {
      height: 100%;
      border-radius: 8rpx;
      transition: width 0.4s;
      &.protein { background: linear-gradient(90deg, #07c160, #52c41a); }
      &.fat      { background: linear-gradient(90deg, #fa8c16, #ffc53d); }
      &.carbs    { background: linear-gradient(90deg, #1677ff, #69b1ff); }
    }
  }
}

.trend-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
  .trend-tabs {
    display: flex;
    gap: 0;
    background: #f5f5f5;
    border-radius: 30rpx;
    overflow: hidden;
    .trend-tab {
      padding: 10rpx 28rpx;
      font-size: 24rpx;
      color: #888;
      &.active { background: var(--primary-color, #07c160); color: #fff; border-radius: 30rpx; }
    }
  }
}

.bar-chart {
  display: flex;
  align-items: flex-end;
  gap: 8rpx;
  height: 200rpx;
  padding-bottom: 40rpx;

  .chart-col {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;

    .chart-bars {
      display: flex;
      gap: 4rpx;
      align-items: flex-end;
      .bar-intake { width: 16rpx; background: #07c160; border-radius: 4rpx 4rpx 0 0; min-height: 4rpx; }
      .bar-burned { width: 16rpx; background: #fa8c16; border-radius: 4rpx 4rpx 0 0; min-height: 4rpx; }
    }

    .chart-label { font-size: 20rpx; color: #aaa; margin-top: 8rpx; }
  }
}

.chart-legend {
  display: flex;
  gap: 24rpx;
  .legend-item {
    display: flex;
    align-items: center;
    gap: 8rpx;
    font-size: 24rpx;
    color: #666;
    .legend-dot {
      width: 16rpx;
      height: 16rpx;
      border-radius: 50%;
      &.green  { background: #07c160; }
      &.orange { background: #fa8c16; }
    }
  }
}

.week-summary {
  display: flex;
  align-items: center;

  .summary-item {
    flex: 1;
    text-align: center;
    .summary-val {
      font-size: 36rpx;
      font-weight: 800;
      color: #1a1a1a;
      display: block;
      &.green { color: #07c160; }
      &.red   { color: #ff4d4f; }
    }
    .summary-label { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }
  }

  .summary-divider {
    width: 2rpx;
    height: 60rpx;
    background: #f0f0f0;
  }
}

.quick-actions {
  margin: 0 32rpx;
  display: flex;
  gap: 20rpx;

  .action-btn {
    flex: 1;
    background: #fff;
    border-radius: 20rpx;
    padding: 24rpx 16rpx;
    text-align: center;
    box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);

    .action-icon { font-size: 40rpx; display: block; }
    .action-label { font-size: 24rpx; color: #555; margin-top: 12rpx; display: block; }
  }
}

.empty-tip {
  text-align: center;
  padding: 100rpx 0;
  color: #ccc;
  font-size: 28rpx;
}

.green { color: #07c160 !important; }
.orange { color: #fa8c16 !important; }
.blue { color: #1677ff !important; }
.red { color: #ff4d4f !important; }
</style>
