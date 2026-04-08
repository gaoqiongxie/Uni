<template>
  <view class="sleep-stats-page">
    <!-- 统计概览 -->
    <view class="stats-overview">
      <view class="overview-card">
        <text class="overview-value">{{ stats?.avgDurationStr || '0小时' }}</text>
        <text class="overview-label">平均睡眠</text>
      </view>
      <view class="overview-card">
        <text class="overview-value">{{ stats?.avgQuality?.toFixed(1) || '0' }}</text>
        <text class="overview-label">平均质量</text>
      </view>
      <view class="overview-card">
        <text class="overview-value">{{ stats?.goalAchievementRate || 0 }}%</text>
        <text class="overview-label">目标达成</text>
      </view>
    </view>

    <!-- 本周趋势 -->
    <view class="trend-section">
      <view class="section-title">本周睡眠趋势</view>
      <view class="trend-chart">
        <view
          v-for="(day, index) in stats?.weeklyStats"
          :key="index"
          class="trend-day"
        >
          <view class="trend-bars">
            <view
              class="trend-bar duration"
              :style="{ height: getDurationHeight(day.duration) + '%' }"
            ></view>
            <view
              class="trend-bar quality"
              :style="{ height: (day.quality * 20) + '%' }"
            ></view>
          </view>
          <text class="trend-date">{{ formatDay(day.date) }}</text>
          <text class="trend-score" v-if="day.score > 0">{{ day.score }}</text>
        </view>
      </view>
      <view class="trend-legend">
        <view class="legend-item">
          <view class="legend-dot duration"></view>
          <text class="legend-text">时长</text>
        </view>
        <view class="legend-item">
          <view class="legend-dot quality"></view>
          <text class="legend-text">质量</text>
        </view>
      </view>
    </view>

    <!-- 睡眠目标 -->
    <view class="goal-section">
      <view class="section-header">
        <text class="section-title">睡眠目标</text>
        <text class="edit-link" @click="showGoalPopup">设置</text>
      </view>
      <view class="goal-content" v-if="goal">
        <view class="goal-item">
          <text class="goal-icon">🎯</text>
          <view class="goal-info">
            <text class="goal-label">目标时长</text>
            <text class="goal-value">{{ formatDuration(goal.targetDuration) }}</text>
          </view>
        </view>
        <view class="goal-item">
          <text class="goal-icon">🌙</text>
          <view class="goal-info">
            <text class="goal-label">就寝时间</text>
            <text class="goal-value">{{ goal.targetBedTime }}</text>
          </view>
        </view>
        <view class="goal-item">
          <text class="goal-icon">☀️</text>
          <view class="goal-info">
            <text class="goal-label">起床时间</text>
            <text class="goal-value">{{ goal.targetWakeTime }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 睡眠建议 -->
    <view class="tips-section">
      <view class="section-title">💡 睡眠建议</view>
      <view class="tips-list">
        <view class="tip-item">
          <text class="tip-icon">🌙</text>
          <text class="tip-text">保持规律作息，每天同一时间上床睡觉</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">📱</text>
          <text class="tip-text">睡前1小时避免使用电子屏幕</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">☕</text>
          <text class="tip-text">下午2点后避免摄入咖啡因</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">🛁</text>
          <text class="tip-text">睡前洗个热水澡，帮助身体放松</text>
        </view>
      </view>
    </view>

    <!-- 目标设置弹窗 -->
    <uni-popup ref="goalPopup" type="center">
      <view class="goal-popup">
        <view class="popup-title">睡眠目标设置</view>

        <view class="form-item">
          <text class="form-label">目标时长</text>
          <view class="duration-options">
            <view
              v-for="option in durationOptions"
              :key="option.value"
              class="duration-option"
              :class="{ active: goalForm.targetDuration === option.value }"
              @click="goalForm.targetDuration = option.value"
            >
              {{ option.label }}
            </view>
          </view>
        </view>

        <view class="form-item">
          <text class="form-label">目标就寝时间</text>
          <picker mode="time" :value="goalForm.targetBedTime" @change="onBedTimeChange">
            <view class="picker-value">{{ goalForm.targetBedTime }}</view>
          </picker>
        </view>

        <view class="form-item">
          <text class="form-label">目标起床时间</text>
          <picker mode="time" :value="goalForm.targetWakeTime" @change="onWakeTimeChange">
            <view class="picker-value">{{ goalForm.targetWakeTime }}</view>
          </picker>
        </view>

        <view class="form-item">
          <view class="switch-row">
            <text class="switch-label">开启提醒</text>
            <switch :checked="goalForm.reminderEnabled" @change="onReminderChange" color="#667eea" />
          </view>
        </view>

        <view class="form-item" v-if="goalForm.reminderEnabled">
          <view class="switch-row">
            <text class="switch-label">就寝提醒</text>
            <switch :checked="goalForm.bedReminder" @change="onBedReminderChange" color="#667eea" />
          </view>
        </view>

        <view class="form-item" v-if="goalForm.reminderEnabled">
          <view class="switch-row">
            <text class="switch-label">起床提醒</text>
            <switch :checked="goalForm.wakeReminder" @change="onWakeReminderChange" color="#667eea" />
          </view>
        </view>

        <view class="popup-actions">
          <button class="popup-btn cancel" @click="closeGoalPopup">取消</button>
          <button class="popup-btn confirm" @click="saveGoal">保存</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { sleepApi } from '@/api/sleep.api'
import type { SleepStats, SleepGoal } from '@/types/sleep'
import { TARGET_DURATION_OPTIONS, formatDuration } from '@/types/sleep'

const stats = ref<SleepStats | null>(null)
const goal = ref<SleepGoal | null>(null)
const goalPopup = ref()

const durationOptions = TARGET_DURATION_OPTIONS

const goalForm = ref<SleepGoal>({
  targetDuration: 480,
  targetBedTime: '23:00',
  targetWakeTime: '07:00',
  reminderEnabled: true,
  bedReminder: true,
  wakeReminder: true
})

async function loadStats() {
  try {
    const res = await sleepApi.getSleepStats()
    stats.value = res.data
  } catch (error) {
    console.error('加载睡眠统计失败', error)
  }
}

async function loadGoal() {
  try {
    const res = await sleepApi.getSleepGoal()
    goal.value = res.data
    goalForm.value = { ...res.data }
  } catch (error) {
    console.error('加载睡眠目标失败', error)
  }
}

function getDurationHeight(duration: number) {
  const maxDuration = 600 // 10小时
  return Math.min(100, (duration / maxDuration) * 100)
}

function formatDay(dateStr: string) {
  const date = new Date(dateStr)
  const days = ['日', '一', '二', '三', '四', '五', '六']
  return days[date.getDay()]
}

function showGoalPopup() {
  goalPopup.value?.open()
}

function closeGoalPopup() {
  goalPopup.value?.close()
}

function onBedTimeChange(e: any) {
  goalForm.value.targetBedTime = e.detail.value
}

function onWakeTimeChange(e: any) {
  goalForm.value.targetWakeTime = e.detail.value
}

function onReminderChange(e: any) {
  goalForm.value.reminderEnabled = e.detail.value
}

function onBedReminderChange(e: any) {
  goalForm.value.bedReminder = e.detail.value
}

function onWakeReminderChange(e: any) {
  goalForm.value.wakeReminder = e.detail.value
}

async function saveGoal() {
  try {
    await sleepApi.updateSleepGoal(goalForm.value)
    uni.showToast({ title: '保存成功', icon: 'success' })
    closeGoalPopup()
    loadGoal()
  } catch (error) {
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

onMounted(() => {
  loadStats()
  loadGoal()
})
</script>

<style scoped>
.sleep-stats-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  padding: 30rpx;
  padding-bottom: 60rpx;
}

/* 统计概览 */
.stats-overview {
  display: flex;
  gap: 20rpx;
  margin-bottom: 30rpx;
}

.overview-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20rpx;
  padding: 30rpx 20rpx;
  text-align: center;
}

.overview-value {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8rpx;
}

.overview-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.5);
}

/* 趋势图表 */
.trend-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 24rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
  margin-bottom: 24rpx;
}

.trend-chart {
  display: flex;
  justify-content: space-between;
  height: 200rpx;
  padding: 20rpx 0;
}

.trend-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.trend-bars {
  display: flex;
  align-items: flex-end;
  gap: 4rpx;
  height: 140rpx;
}

.trend-bar {
  width: 20rpx;
  border-radius: 10rpx;
  transition: height 0.3s;
}

.trend-bar.duration {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.trend-bar.quality {
  background: linear-gradient(180deg, #43e97b 0%, #38f9d7 100%);
}

.trend-date {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 10rpx;
}

.trend-score {
  font-size: 20rpx;
  color: #667eea;
  margin-top: 4rpx;
}

.trend-legend {
  display: flex;
  justify-content: center;
  gap: 40rpx;
  margin-top: 20rpx;
}

.legend-item {
  display: flex;
  align-items: center;
}

.legend-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 8rpx;
  margin-right: 10rpx;
}

.legend-dot.duration {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.legend-dot.quality {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.legend-text {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.6);
}

/* 目标区域 */
.goal-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 24rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.edit-link {
  font-size: 26rpx;
  color: #667eea;
}

.goal-content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.goal-item {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.03);
  padding: 24rpx;
  border-radius: 16rpx;
}

.goal-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.goal-info {
  display: flex;
  flex-direction: column;
}

.goal-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.5);
}

.goal-value {
  font-size: 30rpx;
  color: #fff;
  font-weight: 500;
  margin-top: 4rpx;
}

/* 建议区域 */
.tips-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 24rpx;
  padding: 30rpx;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.tip-item {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.03);
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
}

.tip-icon {
  font-size: 36rpx;
  margin-right: 16rpx;
}

.tip-text {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  flex: 1;
}

/* 弹窗 */
.goal-popup {
  background: #1a1a2e;
  border-radius: 30rpx;
  padding: 40rpx;
  width: 600rpx;
}

.popup-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
  text-align: center;
  margin-bottom: 30rpx;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 12rpx;
}

.duration-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.duration-option {
  padding: 16rpx 24rpx;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12rpx;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.duration-option.active {
  background: rgba(102, 126, 234, 0.3);
  color: #fff;
}

.picker-value {
  background: rgba(255, 255, 255, 0.1);
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
  color: #fff;
  font-size: 28rpx;
}

.switch-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
}

.switch-label {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.popup-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 30rpx;
}

.popup-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 30rpx;
  border: none;
}

.popup-btn.cancel {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
}

.popup-btn.confirm {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}
</style>
