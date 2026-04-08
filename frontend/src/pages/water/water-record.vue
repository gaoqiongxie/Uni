<template>
  <view class="water-record-page">
    <!-- 水波纹背景 -->
    <view class="wave-bg">
      <view class="wave"></view>
      <view class="wave wave2"></view>
    </view>

    <!-- 今日进度卡片 -->
    <view class="progress-card">
      <view class="progress-header">
        <text class="progress-title">今日饮水</text>
        <text class="progress-goal">目标 {{ todayStats?.dailyGoal || 2000 }}ml</text>
      </view>

      <view class="progress-circle-wrapper">
        <view class="progress-circle" :style="{ background: circleGradient }">
          <view class="progress-inner">
            <text class="progress-amount">{{ todayStats?.totalAmount || 0 }}</text>
            <text class="progress-unit">ml</text>
          </view>
        </view>
        <view class="progress-percent">{{ todayStats?.completionRate || 0 }}%</view>
      </view>

      <view class="progress-stats">
        <view class="stat-item">
          <text class="stat-value">{{ todayStats?.recordCount || 0 }}</text>
          <text class="stat-label">记录次数</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ remainingAmount }}</text>
          <text class="stat-label">还需(ml)</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ averageAmount }}</text>
          <text class="stat-label">平均(ml)</text>
        </view>
      </view>
    </view>

    <!-- 快捷记录 -->
    <view class="quick-record-section">
      <view class="section-title">快捷记录</view>
      <view class="quick-options">
        <view
          v-for="option in quickOptions"
          :key="option.label"
          class="quick-option"
          :class="{ custom: option.amount === 0 }"
          @click="onQuickRecord(option)"
        >
          <text class="option-icon">{{ option.icon }}</text>
          <text class="option-label">{{ option.label }}</text>
          <text v-if="option.amount > 0" class="option-amount">{{ option.amount }}ml</text>
        </view>
      </view>
    </view>

    <!-- 今日记录 -->
    <view class="records-section">
      <view class="section-header">
        <text class="section-title">今日记录</text>
        <text class="clear-btn" @click="clearAllRecords" v-if="todayStats?.records?.length">清空</text>
      </view>

      <view class="records-list" v-if="todayStats?.records?.length">
        <view
          v-for="record in todayStats.records"
          :key="record.id"
          class="record-item"
        >
          <view class="record-icon">💧</view>
          <view class="record-info">
            <text class="record-amount">+{{ record.amount }}ml</text>
            <text class="record-time">{{ formatTime(record.recordTime) }}</text>
          </view>
          <text class="record-delete" @click="deleteRecord(record.id)">删除</text>
        </view>
      </view>

      <view class="empty-state" v-else>
        <text class="empty-icon">💧</text>
        <text class="empty-text">今天还没有饮水记录</text>
        <text class="empty-subtext">点击上方按钮开始记录吧</text>
      </view>
    </view>

    <!-- 本周趋势 -->
    <view class="trend-section">
      <view class="section-title">本周趋势</view>
      <view class="trend-chart">
        <view
          v-for="(day, index) in weeklyStats"
          :key="index"
          class="trend-bar-wrapper"
        >
          <view class="trend-bar-bg">
            <view
              class="trend-bar"
              :style="{ height: getBarHeight(day.completionRate) + '%' }"
              :class="{ reached: day.completionRate >= 100 }"
            ></view>
          </view>
          <text class="trend-day">{{ formatDay(day.date) }}</text>
          <text class="trend-amount">{{ day.totalAmount }}</text>
        </view>
      </view>
    </view>

    <!-- 自定义饮水量弹窗 -->
    <uni-popup ref="customPopup" type="center">
      <view class="custom-popup">
        <view class="popup-title">自定义饮水量</view>
        <view class="popup-input-wrapper">
          <input
            v-model="customAmount"
            type="number"
            class="popup-input"
            placeholder="请输入饮水量"
          />
          <text class="popup-unit">ml</text>
        </view>
        <view class="popup-actions">
          <button class="popup-btn cancel" @click="closeCustomPopup">取消</button>
          <button class="popup-btn confirm" @click="confirmCustomRecord">确定</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { waterApi } from '@/api/water.api'
import type { WaterDailyStats, WaterRecord } from '@/types/water'
import { QUICK_AMOUNT_OPTIONS } from '@/types/water'

const todayStats = ref<WaterDailyStats | null>(null)
const weeklyStats = ref<WaterDailyStats[]>([])
const customAmount = ref('')
const customPopup = ref()

const quickOptions = QUICK_AMOUNT_OPTIONS

// 计算属性
const remainingAmount = computed(() => {
  if (!todayStats.value) return 2000
  const remaining = todayStats.value.dailyGoal - todayStats.value.totalAmount
  return remaining > 0 ? remaining : 0
})

const averageAmount = computed(() => {
  if (!todayStats.value || todayStats.value.recordCount === 0) return 0
  return Math.round(todayStats.value.totalAmount / todayStats.value.recordCount)
})

const circleGradient = computed(() => {
  const rate = todayStats.value?.completionRate || 0
  const degree = (rate / 100) * 360
  return `conic-gradient(#4facfe 0deg, #4facfe ${degree}deg, #e0e0e0 ${degree}deg)`
})

// 加载数据
async function loadTodayStats() {
  try {
    const res = await waterApi.getTodayStats()
    todayStats.value = res.data
  } catch (error) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

async function loadWeeklyStats() {
  try {
    const res = await waterApi.getWeeklyStats()
    weeklyStats.value = res.data
  } catch (error) {
    console.error('加载周统计失败', error)
  }
}

// 快捷记录
function onQuickRecord(option: { label: string; amount: number; icon: string }) {
  if (option.amount === 0) {
    customPopup.value?.open()
  } else {
    recordWater(option.amount)
  }
}

async function recordWater(amount: number) {
  try {
    await waterApi.recordWater(amount)
    uni.showToast({ title: '记录成功', icon: 'success' })
    loadTodayStats()
    loadWeeklyStats()
  } catch (error) {
    uni.showToast({ title: '记录失败', icon: 'none' })
  }
}

// 自定义记录
function closeCustomPopup() {
  customPopup.value?.close()
  customAmount.value = ''
}

function confirmCustomRecord() {
  const amount = parseInt(customAmount.value)
  if (!amount || amount <= 0) {
    uni.showToast({ title: '请输入有效的饮水量', icon: 'none' })
    return
  }
  closeCustomPopup()
  recordWater(amount)
}

// 删除记录
async function deleteRecord(recordId: number) {
  uni.showModal({
    title: '确认删除',
    content: '确定删除这条饮水记录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await waterApi.deleteRecord(recordId)
          uni.showToast({ title: '删除成功', icon: 'success' })
          loadTodayStats()
          loadWeeklyStats()
        } catch (error) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

// 清空记录
function clearAllRecords() {
  uni.showModal({
    title: '确认清空',
    content: '确定清空今日所有饮水记录吗？此操作不可恢复',
    confirmColor: '#ff4444',
    success: async (res) => {
      if (res.confirm && todayStats.value?.records) {
        try {
          for (const record of todayStats.value.records) {
            await waterApi.deleteRecord(record.id)
          }
          uni.showToast({ title: '清空成功', icon: 'success' })
          loadTodayStats()
          loadWeeklyStats()
        } catch (error) {
          uni.showToast({ title: '清空失败', icon: 'none' })
        }
      }
    }
  })
}

// 格式化
function formatTime(timeStr: string) {
  const date = new Date(timeStr)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function formatDay(dateStr: string) {
  const date = new Date(dateStr)
  const days = ['日', '一', '二', '三', '四', '五', '六']
  return days[date.getDay()]
}

function getBarHeight(rate: number) {
  return Math.min(100, Math.max(10, rate))
}

onMounted(() => {
  loadTodayStats()
  loadWeeklyStats()
})
</script>

<style scoped>
.water-record-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #e3f2fd 0%, #f5f5f5 100%);
  padding-bottom: 40rpx;
}

/* 水波纹背景 */
.wave-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 400rpx;
  overflow: hidden;
  z-index: 0;
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 200%;
  height: 200rpx;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 320'%3E%3Cpath fill='%234facfe' fill-opacity='0.2' d='M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,112C672,96,768,96,864,112C960,128,1056,160,1152,160C1248,160,1344,128,1392,112L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z'%3E%3C/path%3E%3C/svg%3E") repeat-x;
  animation: wave 10s linear infinite;
}

.wave2 {
  animation: wave 15s linear infinite reverse;
  opacity: 0.5;
}

@keyframes wave {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

/* 进度卡片 */
.progress-card {
  position: relative;
  z-index: 1;
  margin: 40rpx 30rpx;
  padding: 40rpx;
  background: white;
  border-radius: 30rpx;
  box-shadow: 0 10rpx 40rpx rgba(79, 172, 254, 0.15);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.progress-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.progress-goal {
  font-size: 26rpx;
  color: #4facfe;
  background: rgba(79, 172, 254, 0.1);
  padding: 8rpx 20rpx;
  border-radius: 30rpx;
}

.progress-circle-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30rpx;
}

.progress-circle {
  width: 280rpx;
  height: 280rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.progress-inner {
  width: 220rpx;
  height: 220rpx;
  background: white;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.progress-amount {
  font-size: 64rpx;
  font-weight: 700;
  color: #4facfe;
  line-height: 1;
}

.progress-unit {
  font-size: 28rpx;
  color: #999;
  margin-top: 10rpx;
}

.progress-percent {
  margin-top: 20rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: #4facfe;
}

.progress-stats {
  display: flex;
  justify-content: space-around;
  padding-top: 30rpx;
  border-top: 2rpx solid #f0f0f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

/* 快捷记录 */
.quick-record-section {
  margin: 30rpx;
  padding: 30rpx;
  background: white;
  border-radius: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
}

.quick-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
}

.quick-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 20rpx;
  background: linear-gradient(135deg, #e3f2fd 0%, #f0f8ff 100%);
  border-radius: 20rpx;
  transition: all 0.3s;
}

.quick-option:active {
  transform: scale(0.95);
}

.quick-option.custom {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
}

.option-icon {
  font-size: 48rpx;
  margin-bottom: 10rpx;
}

.option-label {
  font-size: 26rpx;
  color: #666;
}

.option-amount {
  font-size: 24rpx;
  color: #4facfe;
  margin-top: 6rpx;
}

/* 记录列表 */
.records-section {
  margin: 30rpx;
  padding: 30rpx;
  background: white;
  border-radius: 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.clear-btn {
  font-size: 26rpx;
  color: #ff6b6b;
}

.records-list {
  max-height: 400rpx;
  overflow-y: auto;
}

.record-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 2rpx solid #f5f5f5;
}

.record-item:last-child {
  border-bottom: none;
}

.record-icon {
  width: 60rpx;
  height: 60rpx;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  margin-right: 20rpx;
}

.record-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.record-amount {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}

.record-time {
  font-size: 24rpx;
  color: #999;
  margin-top: 4rpx;
}

.record-delete {
  font-size: 24rpx;
  color: #ff6b6b;
  padding: 10rpx 20rpx;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #666;
}

.empty-subtext {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

/* 趋势图表 */
.trend-section {
  margin: 30rpx;
  padding: 30rpx;
  background: white;
  border-radius: 24rpx;
}

.trend-chart {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 200rpx;
  padding-top: 20rpx;
}

.trend-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.trend-bar-bg {
  width: 40rpx;
  height: 140rpx;
  background: #f0f0f0;
  border-radius: 20rpx;
  overflow: hidden;
  position: relative;
}

.trend-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(180deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 20rpx;
  transition: height 0.5s;
}

.trend-bar.reached {
  background: linear-gradient(180deg, #43e97b 0%, #38f9d7 100%);
}

.trend-day {
  font-size: 22rpx;
  color: #999;
  margin-top: 10rpx;
}

.trend-amount {
  font-size: 20rpx;
  color: #666;
  margin-top: 4rpx;
}

/* 弹窗 */
.custom-popup {
  background: white;
  border-radius: 24rpx;
  padding: 40rpx;
  width: 560rpx;
}

.popup-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 30rpx;
}

.popup-input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 16rpx;
  padding: 20rpx 30rpx;
  margin-bottom: 30rpx;
}

.popup-input {
  flex: 1;
  font-size: 32rpx;
  color: #333;
}

.popup-unit {
  font-size: 28rpx;
  color: #999;
  margin-left: 10rpx;
}

.popup-actions {
  display: flex;
  gap: 20rpx;
}

.popup-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 30rpx;
}

.popup-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.popup-btn.confirm {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
</style>
