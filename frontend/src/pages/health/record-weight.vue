<template>
  <view class="record-weight-container">
    <!-- 当前体重展示 -->
    <view class="current-card card">
      <text class="card-label">当前记录体重</text>
      <view class="weight-display">
        <text class="weight-num">{{ currentWeight || '--' }}</text>
        <text class="weight-unit">kg</text>
      </view>
      <text class="sub-text">上次记录：{{ lastRecord?.recordDate || '暂无记录' }}</text>
    </view>

    <!-- 输入区 -->
    <view class="input-card card">
      <text class="input-label">今日体重 (kg)</text>
      <view class="input-row">
        <text class="minus" @click="adjustWeight(-0.1)">−</text>
        <input
          class="weight-input"
          type="digit"
          v-model="inputWeight"
          placeholder="请输入体重"
          @input="onWeightInput"
        />
        <text class="plus" @click="adjustWeight(0.1)">+</text>
      </view>

      <view class="date-row">
        <text class="date-label">记录日期</text>
        <picker mode="date" :value="recordDate" @change="onDateChange">
          <view class="date-picker">
            <text>{{ recordDate }}</text>
            <text class="date-icon">📅</text>
          </view>
        </picker>
      </view>

      <view class="note-row">
        <text class="note-label">备注（可选）</text>
        <textarea
          class="note-input"
          v-model="note"
          placeholder="记录一下今天的状态..."
          maxlength="100"
        />
      </view>

      <button class="save-btn gradient-bg" @click="handleSave" :loading="loading">
        保存记录
      </button>
    </view>

    <!-- 最近记录 -->
    <view class="recent-card card">
      <view class="section-header">
        <text class="section-title">最近记录</text>
        <text class="section-link" @click="goToHistory">查看全部 →</text>
      </view>
      <view v-if="recentList.length === 0" class="empty-tip">
        <text>还没有记录，快来记录第一次吧 💪</text>
      </view>
      <view v-else class="record-list">
        <view class="record-item" v-for="item in recentList" :key="item.id">
          <view class="record-left">
            <text class="record-date">{{ item.recordDate }}</text>
            <text class="record-note">{{ item.note || '无备注' }}</text>
          </view>
          <view class="record-right">
            <text class="record-weight">{{ item.weight }} kg</text>
            <text
              class="record-diff"
              :class="item.diff > 0 ? 'up' : item.diff < 0 ? 'down' : 'same'"
            >
              {{ item.diff > 0 ? '+' : '' }}{{ item.diff !== 0 ? item.diff.toFixed(1) : '—' }}
            </text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useHealthStore } from '../../store/health'
import { useUserStore } from '../../store/user'
import { today } from '../../utils/date'

const healthStore = useHealthStore()
const userStore = useUserStore()

const loading = ref(false)
const inputWeight = ref('')
const recordDate = ref(today())
const note = ref('')

const currentWeight = computed(() => userStore.userInfo?.currentWeight)
const lastRecord = computed(() => healthStore.weightList[0] || null)

const recentList = computed(() => {
  return healthStore.weightList.slice(0, 7).map((item, i, arr) => ({
    ...item,
    diff: i < arr.length - 1 ? item.weight - arr[i + 1].weight : 0
  }))
})

function onWeightInput(e: any) {
  inputWeight.value = e.detail.value
}

function adjustWeight(delta: number) {
  const val = parseFloat(inputWeight.value || '0')
  inputWeight.value = Math.max(20, Math.min(300, val + delta)).toFixed(1)
}

function onDateChange(e: any) {
  recordDate.value = e.detail.value
}

async function handleSave() {
  const w = parseFloat(inputWeight.value)
  if (isNaN(w) || w < 20 || w > 300) {
    uni.showToast({ title: '请输入合理的体重值', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await healthStore.addWeightRecord({
      weight: w,
      recordDate: recordDate.value,
      note: note.value
    })
    uni.showToast({ title: '记录成功', icon: 'success' })
    inputWeight.value = ''
    note.value = ''
  } catch (e: any) {
    uni.showToast({ title: e.message || '记录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goToHistory() {
  uni.navigateTo({ url: '/pages/health/weight-history' })
}

onMounted(async () => {
  await healthStore.loadWeightRecords({ page: 1, pageSize: 10 })
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.record-weight-container {
  min-height: 100vh;
  background: $bg-color;
  padding: 24rpx 30rpx 60rpx;

  .current-card {
    text-align: center;
    padding: 50rpx 40rpx;
    margin-bottom: 24rpx;

    .card-label {
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: 20rpx;
      display: block;
    }

    .weight-display {
      display: flex;
      align-items: baseline;
      justify-content: center;
      margin-bottom: 16rpx;

      .weight-num {
        font-size: 100rpx;
        font-weight: bold;
        color: $primary-color;
        line-height: 1;
      }

      .weight-unit {
        font-size: $font-size-lg;
        color: $text-secondary;
        margin-left: 10rpx;
      }
    }

    .sub-text {
      font-size: $font-size-sm;
      color: $text-placeholder;
    }
  }

  .input-card {
    padding: 40rpx;
    margin-bottom: 24rpx;

    .input-label {
      display: block;
      font-size: $font-size-md;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 24rpx;
    }

    .input-row {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 30rpx;
      margin-bottom: 36rpx;

      .minus, .plus {
        width: 80rpx;
        height: 80rpx;
        background: $bg-color;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 48rpx;
        color: $primary-color;
        font-weight: bold;
        box-shadow: $shadow-sm;
      }

      .weight-input {
        flex: 1;
        height: 100rpx;
        text-align: center;
        font-size: 48rpx;
        font-weight: bold;
        color: $text-primary;
        border: 2rpx solid $border-color;
        border-radius: $border-radius;
        background: $bg-color;
      }
    }

    .date-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 24rpx 0;
      border-top: 1rpx solid $border-color;
      margin-bottom: 24rpx;

      .date-label {
        font-size: $font-size-md;
        color: $text-secondary;
      }

      .date-picker {
        display: flex;
        align-items: center;
        gap: 10rpx;
        font-size: $font-size-md;
        color: $text-primary;

        .date-icon {
          font-size: 28rpx;
        }
      }
    }

    .note-row {
      margin-bottom: 36rpx;

      .note-label {
        display: block;
        font-size: $font-size-sm;
        color: $text-secondary;
        margin-bottom: 12rpx;
      }

      .note-input {
        width: 100%;
        height: 120rpx;
        border: 2rpx solid $border-color;
        border-radius: $border-radius;
        padding: 20rpx 24rpx;
        font-size: $font-size-sm;
        color: $text-primary;
        background: $bg-color;
        box-sizing: border-box;
      }
    }

    .save-btn {
      width: 100%;
      height: 96rpx;
      color: #fff;
      font-size: $font-size-md;
      font-weight: 600;
      border-radius: $border-radius;
      border: none;
    }
  }

  .recent-card {
    padding: 40rpx;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24rpx;

      .section-title {
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
      }

      .section-link {
        font-size: $font-size-sm;
        color: $primary-color;
      }
    }

    .empty-tip {
      text-align: center;
      padding: 40rpx;
      font-size: $font-size-sm;
      color: $text-placeholder;
    }

    .record-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid $border-color;

      &:last-child { border-bottom: none; }

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
}
</style>
