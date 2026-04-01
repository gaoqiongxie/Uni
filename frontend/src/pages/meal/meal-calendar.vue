<template>
  <view class="calendar-container">
    <!-- 月份切换 -->
    <view class="month-bar">
      <text class="arrow" @click="prevMonth">‹</text>
      <text class="month-title">{{ currentYear }}年{{ currentMonth }}月</text>
      <text
        class="arrow"
        :class="{ disabled: isCurrentMonth }"
        @click="nextMonth"
      >›</text>
    </view>

    <!-- 星期头 -->
    <view class="week-row">
      <text class="week-label" v-for="w in weekDays" :key="w">{{ w }}</text>
    </view>

    <!-- 日历网格 -->
    <view class="calendar-grid">
      <view
        class="day-cell"
        v-for="(day, i) in calendarDays"
        :key="i"
        :class="{
          'other-month': !day.currentMonth,
          'today': day.isToday,
          'selected': day.dateStr === selectedDate,
          'has-record': day.hasRecord
        }"
        @click="selectDay(day)"
      >
        <text class="day-num">{{ day.date }}</text>
        <view class="dot-row" v-if="day.hasRecord && day.currentMonth">
          <view class="dot" v-for="t in day.mealTypes" :key="t" :class="`type-${t}`"></view>
        </view>
      </view>
    </view>

    <!-- 选中日的打卡详情 -->
    <view class="detail-card card">
      <view class="detail-header">
        <text class="detail-date">{{ selectedDate }} 打卡详情</text>
        <text
          class="detail-total-cal"
          v-if="selectedDayMeals.length > 0"
        >共 {{ selectedDayTotalCalorie }} kcal</text>
      </view>

      <view v-if="selectedDayMeals.length === 0" class="empty-tip">
        <text>这天还没有打卡记录</text>
      </view>

      <view v-else>
        <view
          class="detail-meal"
          v-for="mealType in mealTypes"
          :key="mealType.type"
          v-if="getSelectedMeals(mealType.type).length > 0"
        >
          <view class="detail-meal-header">
            <text class="dm-icon">{{ mealType.icon }}</text>
            <text class="dm-name">{{ mealType.name }}</text>
            <text class="dm-cal">{{ getSelectedMealCalorie(mealType.type) }} kcal</text>
          </view>
          <view
            class="detail-food-item"
            v-for="item in getSelectedMeals(mealType.type)"
            :key="item.id"
          >
            <text class="df-name">{{ item.foodName }}</text>
            <text class="df-amount">{{ item.amount }}</text>
            <text class="df-cal">{{ item.calorieEstimate }} kcal</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 打卡统计 -->
    <view class="stats-card card">
      <text class="stats-title">本月统计</text>
      <view class="stats-row">
        <view class="stats-item">
          <text class="s-val">{{ monthStats.checkDays }}</text>
          <text class="s-label">打卡天数</text>
        </view>
        <view class="stats-item">
          <text class="s-val">{{ monthStats.fullDays }}</text>
          <text class="s-label">全餐天数</text>
        </view>
        <view class="stats-item">
          <text class="s-val">{{ monthStats.avgCalorie }}</text>
          <text class="s-label">日均热量(kcal)</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useMealStore } from '../../store/meal'
import { today } from '../../utils/date'

const mealStore = useMealStore()
const todayStr = today()
const todayDate = new Date(todayStr)

const currentYear = ref(todayDate.getFullYear())
const currentMonth = ref(todayDate.getMonth() + 1)
const selectedDate = ref(todayStr)

const weekDays = ['日', '一', '二', '三', '四', '五', '六']
const mealTypes = [
  { type: 1, name: '早餐', icon: '🌅' },
  { type: 2, name: '午餐', icon: '☀️' },
  { type: 3, name: '晚餐', icon: '🌙' },
  { type: 4, name: '加餐', icon: '🍎' }
]

const isCurrentMonth = computed(() => {
  const now = new Date()
  return currentYear.value === now.getFullYear() && currentMonth.value === now.getMonth() + 1
})

const calendarDays = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  const firstDay = new Date(year, month - 1, 1)
  const lastDay = new Date(year, month, 0)
  const days = []

  // 填补月初空白
  for (let i = 0; i < firstDay.getDay(); i++) {
    const d = new Date(year, month - 1, -firstDay.getDay() + 1 + i)
    days.push({
      date: d.getDate(),
      dateStr: formatDate(d),
      currentMonth: false,
      isToday: false,
      hasRecord: false,
      mealTypes: []
    })
  }

  // 当月天数
  for (let d = 1; d <= lastDay.getDate(); d++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const records = mealStore.calendarData[dateStr] || []
    const mealTypeSet = [...new Set(records.map((r: any) => r.mealType))]
    days.push({
      date: d,
      dateStr,
      currentMonth: true,
      isToday: dateStr === todayStr,
      hasRecord: records.length > 0,
      mealTypes: mealTypeSet
    })
  }

  // 填补月末空白到整行
  const remaining = 42 - days.length
  for (let i = 1; i <= remaining; i++) {
    const d = new Date(year, month, i)
    days.push({
      date: d.getDate(),
      dateStr: formatDate(d),
      currentMonth: false,
      isToday: false,
      hasRecord: false,
      mealTypes: []
    })
  }

  return days
})

const selectedDayMeals = computed(() => mealStore.calendarData[selectedDate.value] || [])

const selectedDayTotalCalorie = computed(() =>
  selectedDayMeals.value.reduce((s: number, m: any) => s + (m.calorieEstimate || 0), 0)
)

const monthStats = computed(() => {
  const data = mealStore.calendarData
  let checkDays = 0
  let fullDays = 0
  let totalCalorie = 0
  const year = currentYear.value
  const month = currentMonth.value
  const daysInMonth = new Date(year, month, 0).getDate()

  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const records = data[dateStr] || []
    if (records.length > 0) {
      checkDays++
      totalCalorie += records.reduce((s: number, m: any) => s + (m.calorieEstimate || 0), 0)
      const types = new Set(records.map((r: any) => r.mealType))
      if (types.has(1) && types.has(2) && types.has(3)) fullDays++
    }
  }

  return {
    checkDays,
    fullDays,
    avgCalorie: checkDays > 0 ? Math.round(totalCalorie / checkDays) : 0
  }
})

function getSelectedMeals(type: number) {
  return selectedDayMeals.value.filter((m: any) => m.mealType === type)
}

function getSelectedMealCalorie(type: number) {
  return getSelectedMeals(type).reduce((s: number, m: any) => s + (m.calorieEstimate || 0), 0)
}

function formatDate(d: Date): string {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

function selectDay(day: any) {
  if (!day.currentMonth) return
  selectedDate.value = day.dateStr
}

function prevMonth() {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
  loadCalendar()
}

function nextMonth() {
  if (isCurrentMonth.value) return
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
  loadCalendar()
}

async function loadCalendar() {
  const year = currentYear.value
  const month = currentMonth.value
  const startDate = `${year}-${String(month).padStart(2, '0')}-01`
  const endDate = `${year}-${String(month).padStart(2, '0')}-${new Date(year, month, 0).getDate()}`
  await mealStore.loadCalendarData({ startDate, endDate })
}

onMounted(async () => {
  await loadCalendar()
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.calendar-container {
  min-height: 100vh;
  background: $bg-color;
  padding: 0 0 60rpx;

  .month-bar {
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fff;
    padding: 28rpx 40rpx;
    gap: 50rpx;
    border-bottom: 1rpx solid $border-color;

    .arrow {
      font-size: 48rpx;
      color: $primary-color;
      padding: 0 16rpx;

      &.disabled { color: $text-placeholder; }
    }

    .month-title {
      font-size: $font-size-md;
      font-weight: 600;
      color: $text-primary;
    }
  }

  .week-row {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    background: #fff;
    padding: 16rpx 0;

    .week-label {
      text-align: center;
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }

  .calendar-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    background: #fff;
    border-bottom: 8rpx solid $bg-color;

    .day-cell {
      aspect-ratio: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      position: relative;
      padding: 8rpx 0;

      &.other-month .day-num { color: $text-placeholder; }

      &.today .day-num {
        background: $primary-color;
        color: #fff;
        border-radius: 50%;
        width: 52rpx;
        height: 52rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      &.selected:not(.today) .day-num {
        background: rgba(102, 126, 234, 0.15);
        color: $primary-color;
        border-radius: 50%;
        width: 52rpx;
        height: 52rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .day-num {
        font-size: 26rpx;
        color: $text-primary;
      }

      .dot-row {
        display: flex;
        gap: 4rpx;
        margin-top: 4rpx;

        .dot {
          width: 8rpx;
          height: 8rpx;
          border-radius: 50%;
          background: $primary-color;

          &.type-1 { background: #ffa940; }
          &.type-2 { background: #52c41a; }
          &.type-3 { background: #667eea; }
          &.type-4 { background: #eb2f96; }
        }
      }
    }
  }

  .detail-card {
    margin: 24rpx 30rpx;
    padding: 36rpx;

    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24rpx;

      .detail-date {
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
      }

      .detail-total-cal {
        font-size: $font-size-sm;
        color: $primary-color;
        font-weight: 500;
      }
    }

    .empty-tip {
      text-align: center;
      padding: 40rpx;
      font-size: $font-size-sm;
      color: $text-placeholder;
    }

    .detail-meal {
      margin-bottom: 20rpx;

      .detail-meal-header {
        display: flex;
        align-items: center;
        gap: 12rpx;
        margin-bottom: 12rpx;

        .dm-icon { font-size: 28rpx; }
        .dm-name {
          font-size: $font-size-sm;
          font-weight: 600;
          color: $text-primary;
          flex: 1;
        }
        .dm-cal {
          font-size: $font-size-sm;
          color: $text-secondary;
        }
      }

      .detail-food-item {
        display: flex;
        align-items: center;
        padding: 10rpx 0 10rpx 40rpx;
        border-bottom: 1rpx solid $border-color;

        &:last-child { border-bottom: none; }

        .df-name {
          flex: 1;
          font-size: $font-size-sm;
          color: $text-primary;
        }

        .df-amount {
          font-size: $font-size-sm;
          color: $text-placeholder;
          margin-right: 20rpx;
        }

        .df-cal {
          font-size: $font-size-sm;
          color: $primary-color;
        }
      }
    }
  }

  .stats-card {
    margin: 0 30rpx;
    padding: 36rpx;

    .stats-title {
      display: block;
      font-size: $font-size-md;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 24rpx;
    }

    .stats-row {
      display: flex;
      justify-content: space-around;

      .stats-item {
        text-align: center;

        .s-val {
          display: block;
          font-size: $font-size-xl;
          font-weight: bold;
          color: $primary-color;
          margin-bottom: 8rpx;
        }

        .s-label {
          font-size: $font-size-sm;
          color: $text-secondary;
        }
      }
    }
  }
}
</style>
