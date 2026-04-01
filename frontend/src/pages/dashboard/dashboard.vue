<template>
  <view class="dashboard-container">
    <!-- 概览卡片 -->
    <view class="overview-row">
      <view class="overview-card card" v-for="item in overviewItems" :key="item.label">
        <text class="o-icon">{{ item.icon }}</text>
        <text class="o-val">{{ item.value }}</text>
        <text class="o-label">{{ item.label }}</text>
      </view>
    </view>

    <!-- 减重进度 -->
    <view class="progress-card card">
      <view class="progress-header">
        <text class="p-title">减重进度</text>
        <text class="p-sub">目标 {{ targetWeight }} kg</text>
      </view>
      <view class="progress-bar-wrap">
        <view class="progress-bg">
          <view class="progress-fill" :style="{ width: lossPercent + '%' }"></view>
        </view>
        <text class="progress-txt">{{ lossPercent.toFixed(0) }}%</text>
      </view>
      <view class="progress-detail">
        <text class="p-start">起始 {{ startWeight || '--' }} kg</text>
        <text class="p-current">当前 {{ currentWeight || '--' }} kg</text>
        <text class="p-target">目标 {{ targetWeight || '--' }} kg</text>
      </view>
    </view>

    <!-- 本周热量分析 -->
    <view class="week-calorie-card card">
      <text class="section-title">近7日热量</text>
      <view class="bar-chart">
        <view
          class="bar-item"
          v-for="item in weekCalorieData"
          :key="item.date"
        >
          <view class="bar-col">
            <view
              class="bar-fill"
              :style="{ height: getBarHeight(item.calorie) + 'rpx' }"
              :class="{ over: item.calorie > calorieGoal }"
            ></view>
          </view>
          <text class="bar-label">{{ item.shortDate }}</text>
          <text class="bar-cal">{{ item.calorie }}</text>
        </view>
      </view>
      <view class="goal-line-hint">
        <view class="goal-dot"></view>
        <text class="goal-hint-text">目标线 {{ calorieGoal }} kcal</text>
      </view>
    </view>

    <!-- 打卡率统计 -->
    <view class="checkin-card card">
      <text class="section-title">本月打卡率</text>
      <view class="checkin-ring-wrap">
        <view class="ring-outer">
          <view
            class="ring-inner"
            :style="{ background: `conic-gradient(#667eea ${checkinPercent}%, #f0f0f0 0)` }"
          ></view>
          <view class="ring-center">
            <text class="ring-val">{{ checkinPercent }}%</text>
            <text class="ring-label">打卡率</text>
          </view>
        </view>
      </view>
      <view class="checkin-detail">
        <view class="ci-item">
          <text class="ci-val">{{ monthStats.checkDays }}</text>
          <text class="ci-label">已打卡</text>
        </view>
        <view class="ci-item">
          <text class="ci-val">{{ monthStats.remainDays }}</text>
          <text class="ci-label">剩余天数</text>
        </view>
        <view class="ci-item">
          <text class="ci-val">{{ monthStats.fullDays }}</text>
          <text class="ci-label">全餐天数</text>
        </view>
      </view>
    </view>

    <!-- 健康建议 -->
    <view class="advice-card card">
      <text class="section-title">🌟 健康建议</text>
      <view class="advice-list">
        <view class="advice-item" v-for="(a, i) in advices" :key="i">
          <text class="advice-dot">•</text>
          <text class="advice-text">{{ a }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useHealthStore } from '../../store/health'
import { useMealStore } from '../../store/meal'
import { useUserStore } from '../../store/user'
import { today } from '../../utils/date'

const healthStore = useHealthStore()
const mealStore = useMealStore()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const currentWeight = computed(() => userInfo.value?.currentWeight)
const targetWeight = computed(() => userInfo.value?.targetWeight)
const startWeight = computed(() => healthStore.weightList[healthStore.weightList.length - 1]?.weight)
const calorieGoal = computed(() => userInfo.value?.calorieGoal || 1400)

const lossPercent = computed(() => {
  const s = startWeight.value
  const t = targetWeight.value
  const c = currentWeight.value
  if (!s || !t || !c) return 0
  const totalNeed = s - t
  if (totalNeed <= 0) return 100
  return Math.min(Math.max(((s - c) / totalNeed) * 100, 0), 100)
})

const overviewItems = computed(() => [
  {
    icon: '⚖️',
    value: currentWeight.value ? `${currentWeight.value}kg` : '--',
    label: '当前体重'
  },
  {
    icon: '🎯',
    value: currentWeight.value && targetWeight.value
      ? `${(currentWeight.value - targetWeight.value).toFixed(1)}kg`
      : '--',
    label: '距目标'
  },
  {
    icon: '🔥',
    value: healthStore.weightList.length > 0
      ? `${((startWeight.value || 0) - (currentWeight.value || 0)).toFixed(1)}kg`
      : '--',
    label: '已减重'
  },
  {
    icon: '📅',
    value: `${monthStats.value.checkDays}天`,
    label: '本月打卡'
  }
])

// 近7日热量
const weekCalorieData = computed(() => {
  const result = []
  for (let i = 6; i >= 0; i--) {
    const d = new Date()
    d.setDate(d.getDate() - i)
    const dateStr = d.toISOString().substring(0, 10)
    const records = mealStore.calendarData[dateStr] || []
    const calorie = records.reduce((s: number, m: any) => s + (m.calorieEstimate || 0), 0)
    result.push({
      date: dateStr,
      shortDate: `${d.getMonth() + 1}/${d.getDate()}`,
      calorie
    })
  }
  return result
})

const maxCalorie = computed(() =>
  Math.max(...weekCalorieData.value.map(d => d.calorie), calorieGoal.value, 100)
)

function getBarHeight(calorie: number): number {
  return Math.max((calorie / maxCalorie.value) * 200, 8)
}

// 本月统计
const monthStats = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const daysInMonth = new Date(year, month, 0).getDate()
  const todayDay = now.getDate()
  let checkDays = 0
  let fullDays = 0
  for (let d = 1; d <= todayDay; d++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const records = mealStore.calendarData[dateStr] || []
    if (records.length > 0) {
      checkDays++
      const types = new Set(records.map((r: any) => r.mealType))
      if (types.has(1) && types.has(2) && types.has(3)) fullDays++
    }
  }
  return { checkDays, fullDays, remainDays: daysInMonth - todayDay }
})

const checkinPercent = computed(() => {
  const now = new Date()
  const passedDays = now.getDate()
  return passedDays > 0 ? Math.round((monthStats.value.checkDays / passedDays) * 100) : 0
})

const advices = computed(() => {
  const list = []
  const w = currentWeight.value
  const t = targetWeight.value
  if (w && t && w > t) {
    const diff = w - t
    if (diff > 10) {
      list.push('距目标体重还有一段路，保持每天300-500大卡热量缺口，循序渐进')
    } else {
      list.push('离目标越来越近了！继续保持，不要放弃 💪')
    }
  }
  if (checkinPercent.value < 60) {
    list.push('本月打卡率偏低，坚持每天记录饮食是减脂的第一步哦')
  } else {
    list.push('打卡很稳定，好习惯正在养成！')
  }
  const todayStr = today()
  const todayCalorie = (mealStore.calendarData[todayStr] || [])
    .reduce((s: number, m: any) => s + (m.calorieEstimate || 0), 0)
  if (todayCalorie > calorieGoal.value) {
    list.push(`今日热量超出目标，建议晚餐选择轻食，多喝水`)
  } else if (todayCalorie === 0) {
    list.push('今天还没有记录餐食哦，快去打卡！')
  }
  list.push('充足的睡眠可以帮助减脂，每天7-8小时让身体更好恢复')
  return list.slice(0, 4)
})

onMounted(async () => {
  await healthStore.loadWeightRecords({ page: 1, pageSize: 50 })
  // 加载最近30天日历数据
  const end = today()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 30)
  const start = startDate.toISOString().substring(0, 10)
  await mealStore.loadCalendarData({ startDate: start, endDate: end })
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.dashboard-container {
  min-height: 100vh;
  background: $bg-color;
  padding: 24rpx 30rpx 60rpx;

  .section-title {
    display: block;
    font-size: $font-size-md;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 24rpx;
  }

  .overview-row {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20rpx;
    margin-bottom: 24rpx;

    .overview-card {
      text-align: center;
      padding: 36rpx 20rpx;

      .o-icon {
        display: block;
        font-size: 44rpx;
        margin-bottom: 12rpx;
      }

      .o-val {
        display: block;
        font-size: $font-size-xl;
        font-weight: bold;
        color: $primary-color;
        margin-bottom: 8rpx;
      }

      .o-label {
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }
  }

  .progress-card {
    margin-bottom: 24rpx;
    padding: 36rpx;

    .progress-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24rpx;

      .p-title {
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
      }

      .p-sub {
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }

    .progress-bar-wrap {
      display: flex;
      align-items: center;
      gap: 20rpx;
      margin-bottom: 20rpx;

      .progress-bg {
        flex: 1;
        height: 20rpx;
        background: $border-color;
        border-radius: 10rpx;
        overflow: hidden;

        .progress-fill {
          height: 100%;
          background: linear-gradient(90deg, #667eea, #764ba2);
          border-radius: 10rpx;
          transition: width 0.5s ease;
        }
      }

      .progress-txt {
        font-size: $font-size-md;
        font-weight: bold;
        color: $primary-color;
        width: 70rpx;
        text-align: right;
      }
    }

    .progress-detail {
      display: flex;
      justify-content: space-between;
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }

  .week-calorie-card {
    margin-bottom: 24rpx;
    padding: 36rpx 30rpx 24rpx;

    .bar-chart {
      display: flex;
      justify-content: space-around;
      align-items: flex-end;
      height: 220rpx;
      margin-bottom: 16rpx;

      .bar-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8rpx;
        flex: 1;

        .bar-col {
          width: 40rpx;
          height: 200rpx;
          background: $border-color;
          border-radius: 4rpx 4rpx 0 0;
          display: flex;
          align-items: flex-end;
          overflow: hidden;

          .bar-fill {
            width: 100%;
            background: $primary-color;
            border-radius: 4rpx 4rpx 0 0;
            transition: height 0.3s;

            &.over { background: #ff7875; }
          }
        }

        .bar-label {
          font-size: 20rpx;
          color: $text-secondary;
        }

        .bar-cal {
          font-size: 20rpx;
          color: $text-placeholder;
        }
      }
    }

    .goal-line-hint {
      display: flex;
      align-items: center;
      gap: 10rpx;

      .goal-dot {
        width: 16rpx;
        height: 16rpx;
        border-radius: 50%;
        background: $primary-color;
      }

      .goal-hint-text {
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }
  }

  .checkin-card {
    margin-bottom: 24rpx;
    padding: 36rpx;

    .checkin-ring-wrap {
      display: flex;
      justify-content: center;
      margin-bottom: 30rpx;

      .ring-outer {
        width: 180rpx;
        height: 180rpx;
        border-radius: 50%;
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;

        .ring-inner {
          width: 180rpx;
          height: 180rpx;
          border-radius: 50%;
          position: absolute;
        }

        .ring-center {
          width: 120rpx;
          height: 120rpx;
          background: #fff;
          border-radius: 50%;
          z-index: 1;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;

          .ring-val {
            font-size: $font-size-lg;
            font-weight: bold;
            color: $primary-color;
          }

          .ring-label {
            font-size: $font-size-sm;
            color: $text-secondary;
          }
        }
      }
    }

    .checkin-detail {
      display: flex;
      justify-content: space-around;

      .ci-item {
        text-align: center;

        .ci-val {
          display: block;
          font-size: $font-size-lg;
          font-weight: bold;
          color: $primary-color;
          margin-bottom: 6rpx;
        }

        .ci-label {
          font-size: $font-size-sm;
          color: $text-secondary;
        }
      }
    }
  }

  .advice-card {
    padding: 36rpx;

    .advice-list {
      .advice-item {
        display: flex;
        align-items: flex-start;
        gap: 12rpx;
        margin-bottom: 16rpx;

        .advice-dot {
          color: $primary-color;
          font-size: $font-size-md;
          margin-top: -2rpx;
          flex-shrink: 0;
        }

        .advice-text {
          font-size: $font-size-sm;
          color: $text-secondary;
          line-height: 1.6;
        }
      }
    }
  }
}
</style>
