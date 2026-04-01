<template>
  <view class="list-page">
    <!-- 日期选择 -->
    <view class="date-picker card">
      <view class="date-nav">
        <view class="nav-btn" @click="changeWeek(-1)">
          <text>‹</text>
        </view>
        <text class="date-range">{{ weekLabel }}</text>
        <view class="nav-btn" @click="changeWeek(1)">
          <text>›</text>
        </view>
      </view>

      <!-- 周汇总 -->
      <view class="week-summary" v-if="weekSummary">
        <view class="ws-item">
          <text class="ws-value">{{ weekSummary.totalCount }}</text>
          <text class="ws-label">次数</text>
        </view>
        <view class="ws-item">
          <text class="ws-value">{{ weekSummary.totalDuration }}′</text>
          <text class="ws-label">时长</text>
        </view>
        <view class="ws-item">
          <text class="ws-value">{{ weekSummary.totalCalorieBurn }}</text>
          <text class="ws-label">kcal</text>
        </view>
      </view>
    </view>

    <!-- 按日期分组的运动记录 -->
    <view v-for="(records, date) in groupedRecords" :key="date" class="date-section">
      <view class="date-header">
        <text class="date-text">{{ formatDateLabel(date) }}</text>
        <text class="date-count">{{ records.length }}条记录</text>
      </view>

      <view class="record-item card" v-for="item in records" :key="item.id">
        <view class="record-header">
          <text class="exercise-icon">{{ getExerciseIcon(item.exerciseType) }}</text>
          <view class="record-info">
            <text class="exercise-name">{{ item.exerciseType }}</text>
            <text class="exercise-meta">
              {{ item.duration }}分钟 · {{ item.intensityName }} · {{ item.feelingName }}
            </text>
          </view>
          <view class="record-right">
            <text class="calorie">{{ item.calorieBurn || 0 }}kcal</text>
            <text class="distance" v-if="item.distance">{{ item.distance }}km</text>
          </view>
        </view>

        <!-- 删除按钮 -->
        <view class="record-actions" v-if="!item._deleting">
          <text class="delete-btn" @click="handleDelete(item)">删除</text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="Object.keys(groupedRecords).length === 0 && !loading">
      <text class="empty-icon">📊</text>
      <text class="empty-text">该时段暂无运动记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { exerciseApi } from '../../api/exercise.api'
import type { ExerciseRecordVO, ExerciseSummaryVO } from '../../types/exercise'

const loading = ref(false)
const weekOffset = ref(0)
const groupedRecords = reactive<Record<string, ExerciseRecordVO[]>>({})
const weekSummary = ref<ExerciseSummaryVO | null>(null)

const weekLabel = computed(() => {
  const today = new Date()
  const dayOfWeek = today.getDay() || 7 // 周一=1
  const monday = new Date(today)
  monday.setDate(today.getDate() - dayOfWeek + 1 + weekOffset.value * 7)
  const sunday = new Date(monday)
  sunday.setDate(monday.getDate() + 6)

  const fmt = (d: Date) => `${d.getMonth() + 1}/${d.getDate()}`
  return `${fmt(monday)} - ${fmt(sunday)}`
})

const weekStart = computed(() => {
  const today = new Date()
  const dayOfWeek = today.getDay() || 7
  const monday = new Date(today)
  monday.setDate(today.getDate() - dayOfWeek + 1 + weekOffset.value * 7)
  return monday.toISOString().split('T')[0]
})

const weekEnd = computed(() => {
  const today = new Date()
  const dayOfWeek = today.getDay() || 7
  const monday = new Date(today)
  monday.setDate(today.getDate() - dayOfWeek + 1 + weekOffset.value * 7)
  const sunday = new Date(monday)
  sunday.setDate(monday.getDate() + 6)
  return sunday.toISOString().split('T')[0]
})

function getExerciseIcon(type: string): string {
  const icons: Record<string, string> = {
    '跑步': '🏃', '游泳': '🏊', '瑜伽': '🧘', '力量训练': '💪',
    '骑行': '🚴', '跳绳': '⏭', '散步': '🚶'
  }
  return icons[type] || '⛹'
}

function formatDateLabel(dateStr: string): string {
  const today = new Date().toISOString().split('T')[0]
  const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0]
  const d = new Date(dateStr)
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  let label = `${d.getMonth() + 1}月${d.getDate()}日 ${weekDays[d.getDay()]}`
  if (dateStr === today) label = '今天 · ' + label
  else if (dateStr === yesterday) label = '昨天 · ' + label
  return label
}

async function loadWeekData() {
  loading.value = true
  try {
    // 并行加载记录和汇总
    const [recordsData, summaryData] = await Promise.all([
      exerciseApi.getByRange({ startDate: weekStart.value, endDate: weekEnd.value }),
      exerciseApi.getSummary({ startDate: weekStart.value, endDate: weekEnd.value })
    ])
    // 清空并重新填充
    Object.keys(groupedRecords).forEach(k => delete groupedRecords[k])
    Object.assign(groupedRecords, recordsData)
    weekSummary.value = summaryData
  } finally {
    loading.value = false
  }
}

function changeWeek(delta: number) {
  weekOffset.value += delta
  loadWeekData()
}

async function handleDelete(item: ExerciseRecordVO) {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除这条"${item.exerciseType}"记录吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await exerciseApi.delete(item.id)
          // 从列表移除
          for (const date of Object.keys(groupedRecords)) {
            const idx = groupedRecords[date].findIndex(r => r.id === item.id)
            if (idx >= 0) {
              groupedRecords[date].splice(idx, 1)
              if (groupedRecords[date].length === 0) {
                delete groupedRecords[date]
              }
            }
          }
          // 刷新汇总
          loadWeekData()
          uni.showToast({ title: '已删除', icon: 'success' })
        } catch (e) {
          // 错误已在 request 中 toast
        }
      }
    }
  })
}

onMounted(() => {
  loadWeekData()
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.list-page {
  min-height: 100vh;
  padding: 24rpx;
  padding-bottom: 60rpx;
  background: $bg-color;

  .date-picker {
    margin-bottom: 24rpx;

    .date-nav {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 24rpx;

      .nav-btn {
        width: 64rpx;
        height: 64rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        background: $bg-gray;
        border-radius: 50%;
        font-size: 40rpx;
        color: $text-secondary;

        &:active {
          background: $primary-light;
          color: $primary-color;
        }
      }

      .date-range {
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
      }
    }

    .week-summary {
      display: flex;
      justify-content: space-around;
      padding-top: 20rpx;
      border-top: 2rpx solid $border-color;

      .ws-item {
        text-align: center;

        .ws-value {
          display: block;
          font-size: $font-size-lg;
          font-weight: 700;
          color: $primary-color;
        }

        .ws-label {
          display: block;
          font-size: $font-size-xs;
          color: $text-tertiary;
          margin-top: 4rpx;
        }
      }
    }
  }

  .date-section {
    margin-bottom: 24rpx;

    .date-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16rpx 8rpx;

      .date-text {
        font-size: $font-size;
        font-weight: 600;
        color: $text-primary;
      }

      .date-count {
        font-size: $font-size-xs;
        color: $text-tertiary;
      }
    }

    .record-item {
      margin-bottom: 16rpx;

      .record-header {
        display: flex;
        align-items: center;

        .exercise-icon {
          font-size: 48rpx;
          margin-right: 20rpx;
        }

        .record-info {
          flex: 1;

          .exercise-name {
            display: block;
            font-size: $font-size;
            font-weight: 600;
            color: $text-primary;
          }

          .exercise-meta {
            display: block;
            font-size: $font-size-xs;
            color: $text-tertiary;
            margin-top: 4rpx;
          }
        }

        .record-right {
          text-align: right;

          .calorie {
            display: block;
            font-size: $font-size-md;
            font-weight: 600;
            color: $error-color;
          }

          .distance {
            display: block;
            font-size: $font-size-xs;
            color: $text-tertiary;
          }
        }
      }

      .record-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 16rpx;
        padding-top: 16rpx;
        border-top: 2rpx solid $border-color;

        .delete-btn {
          font-size: $font-size-sm;
          color: $error-color;
          padding: 4rpx 16rpx;
        }
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 120rpx 40rpx;

    .empty-icon {
      display: block;
      font-size: 100rpx;
      margin-bottom: 20rpx;
    }

    .empty-text {
      font-size: $font-size;
      color: $text-tertiary;
    }
  }
}
</style>
