<template>
  <view class="weight-history-container">
    <!-- 体脂率入口 -->
    <view class="body-fat-entry card" @click="goToBodyComposition">
      <view class="entry-left">
        <text class="entry-title">身体成分分析</text>
        <text class="entry-subtitle">体脂率 / 肌肉量 / 水分率</text>
      </view>
      <view class="entry-right">
        <text v-if="latestBodyFat" class="body-fat-val">{{ latestBodyFat }}%</text>
        <text class="entry-arrow">›</text>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-row">
      <view class="stat-card card">
        <text class="stat-val">{{ stats.totalLoss || '--' }}</text>
        <text class="stat-label">累计减重(kg)</text>
      </view>
      <view class="stat-card card">
        <text class="stat-val">{{ stats.dayCount || 0 }}</text>
        <text class="stat-label">记录天数</text>
      </view>
      <view class="stat-card card">
        <text class="stat-val">{{ stats.avgWeekLoss || '--' }}</text>
        <text class="stat-label">周均减重(kg)</text>
      </view>
    </view>

    <!-- 体重趋势图（简易折线展示） -->
    <view class="chart-card card">
      <view class="chart-header">
        <text class="chart-title">体重趋势</text>
        <view class="range-tabs">
          <text
            v-for="tab in rangeTabs"
            :key="tab.value"
            class="range-tab"
            :class="{ active: activeRange === tab.value }"
            @click="activeRange = tab.value"
          >{{ tab.label }}</text>
        </view>
      </view>

      <!-- 简易折线图：用 canvas 或占位 -->
      <view class="chart-placeholder">
        <view class="chart-line">
          <view
            v-for="(item, i) in chartData"
            :key="i"
            class="chart-point-wrap"
          >
            <view
              class="chart-point"
              :style="{ bottom: getPointBottom(item.weight) + 'rpx' }"
            ></view>
            <text class="chart-date">{{ item.shortDate }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 历史记录列表 -->
    <view class="list-card card">
      <text class="list-title">全部记录</text>
      <view v-if="list.length === 0" class="empty-tip">
        <text>暂无体重记录，快去记录吧 💪</text>
      </view>
      <view v-else>
        <view class="record-item" v-for="(item, i) in listWithDiff" :key="item.id">
          <view class="record-main">
            <view class="record-left">
              <text class="record-date">{{ item.recordDate }}</text>
              <text class="record-note" v-if="item.note">{{ item.note }}</text>
            </view>
            <view class="record-right">
              <text class="record-weight">{{ item.weight }} kg</text>
              <text
                class="record-diff"
                :class="item.diff > 0 ? 'up' : item.diff < 0 ? 'down' : 'same'"
                v-if="i < listWithDiff.length - 1"
              >
                {{ item.diff > 0 ? '▲' : item.diff < 0 ? '▼' : '—' }}
                {{ Math.abs(item.diff).toFixed(1) }}
              </text>
            </view>
          </view>
        </view>

        <!-- 加载更多 -->
        <view class="load-more" v-if="hasMore" @click="loadMore">
          <text v-if="!loadingMore">加载更多</text>
          <text v-else>加载中...</text>
        </view>
        <view class="no-more" v-else>
          <text>已显示全部记录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useHealthStore } from '../../store/health'
import { getLatestBodyComposition } from '@/api/body-composition.api'

const healthStore = useHealthStore()
const activeRange = ref('month')
const page = ref(1)
const pageSize = 20
const loadingMore = ref(false)
const hasMore = ref(true)
const latestBodyFat = ref<number | null>(null)

async function loadBodyFat() {
  try {
    const res = await getLatestBodyComposition()
    if (res.data) {
      latestBodyFat.value = res.data.bodyFatRate
    }
  } catch (e) {
    // 忽略错误
  }
}

function goToBodyComposition() {
  uni.navigateTo({ url: '/pages/health/body-composition' })
}

const rangeTabs = [
  { label: '近7天', value: 'week' },
  { label: '近30天', value: 'month' },
  { label: '全部', value: 'all' }
]

const list = computed(() => healthStore.weightList)

const listWithDiff = computed(() => {
  return list.value.map((item, i, arr) => ({
    ...item,
    diff: i < arr.length - 1 ? item.weight - arr[i + 1].weight : 0
  }))
})

const stats = computed(() => {
  if (list.value.length < 2) return { totalLoss: '--', dayCount: list.value.length, avgWeekLoss: '--' }
  const first = list.value[list.value.length - 1].weight
  const last = list.value[0].weight
  const totalLoss = (first - last).toFixed(1)
  const dayCount = list.value.length
  const weeks = Math.max(1, dayCount / 7)
  const avgWeekLoss = ((first - last) / weeks).toFixed(2)
  return { totalLoss, dayCount, avgWeekLoss }
})

const chartData = computed(() => {
  const days = activeRange.value === 'week' ? 7 : activeRange.value === 'month' ? 30 : 999
  return list.value
    .slice(0, days)
    .reverse()
    .map(item => ({
      ...item,
      shortDate: item.recordDate.substring(5)
    }))
})

const minWeight = computed(() => Math.min(...chartData.value.map(d => d.weight), 40))
const maxWeight = computed(() => Math.max(...chartData.value.map(d => d.weight), 100))

function getPointBottom(weight: number) {
  const range = maxWeight.value - minWeight.value || 1
  return ((weight - minWeight.value) / range) * 200
}

async function loadMore() {
  if (loadingMore.value || !hasMore.value) return
  loadingMore.value = true
  page.value++
  const result = await healthStore.loadWeightRecords({ page: page.value, pageSize })
  if (!result || result.length < pageSize) hasMore.value = false
  loadingMore.value = false
}

onMounted(async () => {
  await healthStore.loadWeightRecords({ page: 1, pageSize })
  await loadBodyFat()
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.weight-history-container {
  min-height: 100vh;
  background: $bg-color;
  padding: 24rpx 30rpx 60rpx;

  .body-fat-entry {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    margin-bottom: 24rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16rpx;

    .entry-left {
      .entry-title {
        font-size: 32rpx;
        font-weight: 600;
        color: #fff;
        display: block;
        margin-bottom: 8rpx;
      }
      .entry-subtitle {
        font-size: 24rpx;
        color: rgba(255,255,255,0.8);
      }
    }

    .entry-right {
      display: flex;
      align-items: center;
      gap: 16rpx;

      .body-fat-val {
        font-size: 36rpx;
        font-weight: 700;
        color: #fff;
      }
      .entry-arrow {
        font-size: 40rpx;
        color: rgba(255,255,255,0.8);
      }
    }
  }

  .stats-row {
    display: flex;
    gap: 20rpx;
    margin-bottom: 24rpx;

    .stat-card {
      flex: 1;
      text-align: center;
      padding: 30rpx 10rpx;

      .stat-val {
        display: block;
        font-size: $font-size-xl;
        font-weight: bold;
        color: $primary-color;
        margin-bottom: 8rpx;
      }

      .stat-label {
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }
  }

  .chart-card {
    margin-bottom: 24rpx;
    padding: 36rpx 30rpx;

    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30rpx;

      .chart-title {
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
      }

      .range-tabs {
        display: flex;
        gap: 10rpx;

        .range-tab {
          padding: 8rpx 20rpx;
          border-radius: 30rpx;
          font-size: $font-size-sm;
          color: $text-secondary;
          background: $bg-color;

          &.active {
            background: $primary-color;
            color: #fff;
          }
        }
      }
    }

    .chart-placeholder {
      height: 280rpx;
      position: relative;
      overflow-x: auto;

      .chart-line {
        display: flex;
        align-items: flex-end;
        height: 240rpx;
        gap: 20rpx;
        padding-bottom: 40rpx;
        min-width: 100%;

        .chart-point-wrap {
          flex: 1;
          min-width: 60rpx;
          position: relative;
          height: 240rpx;
          display: flex;
          flex-direction: column;
          justify-content: flex-end;
          align-items: center;

          .chart-point {
            width: 16rpx;
            height: 16rpx;
            border-radius: 50%;
            background: $primary-color;
            position: absolute;
          }

          .chart-date {
            font-size: 20rpx;
            color: $text-placeholder;
            position: absolute;
            bottom: 0;
            white-space: nowrap;
          }
        }
      }
    }
  }

  .list-card {
    padding: 36rpx 30rpx;

    .list-title {
      display: block;
      font-size: $font-size-md;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 24rpx;
    }

    .empty-tip {
      text-align: center;
      padding: 60rpx;
      font-size: $font-size-sm;
      color: $text-placeholder;
    }

    .record-item {
      border-bottom: 1rpx solid $border-color;

      &:last-child { border-bottom: none; }

      .record-main {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 24rpx 0;

        .record-left {
          .record-date {
            display: block;
            font-size: $font-size-md;
            color: $text-primary;
            margin-bottom: 6rpx;
          }

          .record-note {
            font-size: $font-size-sm;
            color: $text-placeholder;
          }
        }

        .record-right {
          text-align: right;

          .record-weight {
            display: block;
            font-size: $font-size-md;
            font-weight: bold;
            color: $text-primary;
            margin-bottom: 6rpx;
          }

          .record-diff {
            font-size: $font-size-sm;

            &.up { color: #ff4d4f; }
            &.down { color: #52c41a; }
            &.same { color: $text-placeholder; }
          }
        }
      }
    }

    .load-more, .no-more {
      text-align: center;
      padding: 30rpx;
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}
</style>
