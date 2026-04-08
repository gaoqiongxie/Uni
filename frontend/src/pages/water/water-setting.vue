<template>
  <view class="water-setting-page">
    <!-- 每日目标设置 -->
    <view class="setting-section">
      <view class="section-title">每日饮水目标</view>
      <view class="goal-options">
        <view
          v-for="goal in dailyGoalOptions"
          :key="goal"
          class="goal-option"
          :class="{ active: setting.dailyGoal === goal }"
          @click="setting.dailyGoal = goal"
        >
          <text class="goal-amount">{{ goal }}ml</text>
          <text class="goal-desc">{{ getGoalDesc(goal) }}</text>
        </view>
      </view>
    </view>

    <!-- 提醒开关 -->
    <view class="setting-section">
      <view class="switch-item">
        <view class="switch-info">
          <text class="switch-title">饮水提醒</text>
          <text class="switch-desc">定时提醒您喝水</text>
        </view>
        <switch
          :checked="setting.reminderEnabled"
          @change="onReminderChange"
          color="#4facfe"
        />
      </view>
    </view>

    <!-- 提醒设置 -->
    <view class="setting-section" v-if="setting.reminderEnabled">
      <view class="section-title">提醒设置</view>

      <!-- 提醒间隔 -->
      <view class="setting-item">
        <text class="item-label">提醒间隔</text>
        <picker
          mode="selector"
          :range="intervalOptions"
          :value="intervalIndex"
          @change="onIntervalChange"
        >
          <view class="picker-value">
            <text>{{ intervalOptions[intervalIndex] }}</text>
            <text class="arrow">›</text>
          </view>
        </picker>
      </view>

      <!-- 开始时间 -->
      <view class="setting-item">
        <text class="item-label">开始时间</text>
        <picker
          mode="time"
          :value="setting.startTime"
          @change="onStartTimeChange"
        >
          <view class="picker-value">
            <text>{{ setting.startTime }}</text>
            <text class="arrow">›</text>
          </view>
        </picker>
      </view>

      <!-- 结束时间 -->
      <view class="setting-item">
        <text class="item-label">结束时间</text>
        <picker
          mode="time"
          :value="setting.endTime"
          @change="onEndTimeChange"
        >
          <view class="picker-value">
            <text>{{ setting.endTime }}</text>
            <text class="arrow">›</text>
          </view>
        </picker>
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="save-section">
      <button class="save-btn" @click="saveSetting">保存设置</button>
    </view>

    <!-- 饮水小知识 -->
    <view class="tips-section">
      <view class="tips-title">💡 饮水小知识</view>
      <view class="tips-list">
        <view class="tip-item">
          <text class="tip-icon">🌅</text>
          <text class="tip-text">早上起床后喝一杯温水，帮助身体排毒</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">🍽️</text>
          <text class="tip-text">饭前30分钟喝水，有助于控制食欲</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">🏃</text>
          <text class="tip-text">运动后及时补水，补充流失的水分</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">🌙</text>
          <text class="tip-text">睡前1小时减少饮水，避免夜间起夜</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { waterApi } from '@/api/water.api'
import type { WaterReminderSetting } from '@/types/water'
import { DAILY_GOAL_OPTIONS, REMINDER_INTERVAL_OPTIONS } from '@/types/water'

const setting = ref<WaterReminderSetting>({
  dailyGoal: 2000,
  reminderEnabled: true,
  reminderInterval: 60,
  startTime: '08:00',
  endTime: '22:00'
})

const dailyGoalOptions = DAILY_GOAL_OPTIONS
const intervalOptions = REMINDER_INTERVAL_OPTIONS.map(item => item.label)

const intervalIndex = computed(() => {
  const index = REMINDER_INTERVAL_OPTIONS.findIndex(
    item => item.value === setting.value.reminderInterval
  )
  return index >= 0 ? index : 1
})

function getGoalDesc(goal: number) {
  const descs: Record<number, string> = {
    1500: '轻度活动',
    2000: '一般推荐',
    2500: '中度活动',
    3000: '重度活动',
    3500: '运动达人'
  }
  return descs[goal] || ''
}

function onReminderChange(e: any) {
  setting.value.reminderEnabled = e.detail.value
}

function onIntervalChange(e: any) {
  const index = e.detail.value
  setting.value.reminderInterval = REMINDER_INTERVAL_OPTIONS[index].value
}

function onStartTimeChange(e: any) {
  setting.value.startTime = e.detail.value
}

function onEndTimeChange(e: any) {
  setting.value.endTime = e.detail.value
}

async function loadSetting() {
  try {
    const res = await waterApi.getReminderSetting()
    setting.value = res.data
  } catch (error) {
    uni.showToast({ title: '加载设置失败', icon: 'none' })
  }
}

async function saveSetting() {
  // 验证时间
  if (setting.value.startTime >= setting.value.endTime) {
    uni.showToast({ title: '结束时间必须晚于开始时间', icon: 'none' })
    return
  }

  try {
    await waterApi.updateReminderSetting(setting.value)
    uni.showToast({ title: '保存成功', icon: 'success' })

    // 如果开启了提醒，请求通知权限
    if (setting.value.reminderEnabled) {
      requestNotificationPermission()
    }
  } catch (error) {
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

function requestNotificationPermission() {
  // #ifdef APP-PLUS
  uni.requestSubscribeMessage({
    tmplIds: ['WATER_REMINDER'],
    success: (res) => {
      console.log('订阅成功', res)
    },
    fail: (err) => {
      console.error('订阅失败', err)
    }
  })
  // #endif
}

onMounted(() => {
  loadSetting()
})
</script>

<style scoped>
.water-setting-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.setting-section {
  margin: 20rpx 30rpx;
  padding: 30rpx;
  background: white;
  border-radius: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
}

/* 目标选项 */
.goal-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
}

.goal-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 20rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  border: 4rpx solid transparent;
  transition: all 0.3s;
}

.goal-option.active {
  background: rgba(79, 172, 254, 0.1);
  border-color: #4facfe;
}

.goal-amount {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.goal-option.active .goal-amount {
  color: #4facfe;
}

.goal-desc {
  font-size: 22rpx;
  color: #999;
  margin-top: 8rpx;
}

.goal-option.active .goal-desc {
  color: #4facfe;
}

/* 开关项 */
.switch-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.switch-info {
  display: flex;
  flex-direction: column;
}

.switch-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

.switch-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
}

/* 设置项 */
.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 2rpx solid #f5f5f5;
}

.setting-item:last-child {
  border-bottom: none;
}

.item-label {
  font-size: 28rpx;
  color: #333;
}

.picker-value {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 28rpx;
}

.arrow {
  margin-left: 10rpx;
  color: #ccc;
}

/* 保存按钮 */
.save-section {
  margin: 40rpx 30rpx;
}

.save-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 45rpx;
  border: none;
}

.save-btn:active {
  opacity: 0.9;
}

/* 小知识 */
.tips-section {
  margin: 30rpx;
  padding: 30rpx;
  background: linear-gradient(135deg, #e3f2fd 0%, #f0f8ff 100%);
  border-radius: 20rpx;
}

.tips-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.tip-item {
  display: flex;
  align-items: center;
  background: white;
  padding: 20rpx;
  border-radius: 12rpx;
}

.tip-icon {
  font-size: 40rpx;
  margin-right: 16rpx;
}

.tip-text {
  font-size: 26rpx;
  color: #666;
  flex: 1;
}
</style>
