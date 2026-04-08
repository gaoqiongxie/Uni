<template>
  <view class="sleep-record-page">
    <!-- 睡眠评分卡片 -->
    <view class="score-card" v-if="todayRecord">
      <view class="score-circle" :style="{ background: scoreGradient }">
        <view class="score-inner">
          <text class="score-value">{{ todayRecord.sleepScore }}</text>
          <text class="score-label">睡眠评分</text>
        </view>
      </view>
      <view class="sleep-info">
        <view class="info-item">
          <text class="info-icon">🌙</text>
          <view class="info-content">
            <text class="info-label">入睡时间</text>
            <text class="info-value">{{ todayRecord.bedTimeStr }}</text>
          </view>
        </view>
        <view class="info-item">
          <text class="info-icon">☀️</text>
          <view class="info-content">
            <text class="info-label">起床时间</text>
            <text class="info-value">{{ todayRecord.wakeTimeStr }}</text>
          </view>
        </view>
        <view class="info-item">
          <text class="info-icon">⏱️</text>
          <view class="info-content">
            <text class="info-label">睡眠时长</text>
            <text class="info-value">{{ todayRecord.durationStr }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 今日未记录 -->
    <view class="no-record-card" v-else>
      <text class="no-record-icon">🌙</text>
      <text class="no-record-title">昨晚睡眠怎么样？</text>
      <text class="no-record-desc">记录睡眠，了解你的睡眠质量</text>
      <button class="record-btn" @click="showRecordPopup">记录睡眠</button>
    </view>

    <!-- 睡眠质量 -->
    <view class="quality-section" v-if="todayRecord">
      <view class="section-title">睡眠质量</view>
      <view class="quality-display">
        <text class="quality-emoji">{{ getQualityEmoji(todayRecord.sleepQuality) }}</text>
        <text class="quality-text">{{ todayRecord.sleepQualityText }}</text>
      </view>
    </view>

    <!-- 睡眠结构 -->
    <view class="structure-section" v-if="todayRecord && (todayRecord.deepSleep || todayRecord.lightSleep)">
      <view class="section-title">睡眠结构</view>
      <view class="structure-chart">
        <view class="structure-bar">
          <view
            class="bar-segment deep"
            :style="{ width: getSleepPercent(todayRecord.deepSleep) + '%' }"
          ></view>
          <view
            class="bar-segment light"
            :style="{ width: getSleepPercent(todayRecord.lightSleep) + '%' }"
          ></view>
          <view
            class="bar-segment awake"
            :style="{ width: getSleepPercent(todayRecord.awakeDuration) + '%' }"
          ></view>
        </view>
        <view class="structure-legend">
          <view class="legend-item">
            <view class="legend-color deep"></view>
            <text class="legend-label">深睡 {{ formatDuration(todayRecord.deepSleep || 0) }}</text>
          </view>
          <view class="legend-item">
            <view class="legend-color light"></view>
            <text class="legend-label">浅睡 {{ formatDuration(todayRecord.lightSleep || 0) }}</text>
          </view>
          <view class="legend-item" v-if="todayRecord.awakeDuration">
            <view class="legend-color awake"></view>
            <text class="legend-label">清醒 {{ formatDuration(todayRecord.awakeDuration) }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 影响因素 -->
    <view class="factors-section" v-if="todayRecord?.factors">
      <view class="section-title">影响因素</view>
      <view class="factors-list">
        <text
          v-for="factor in todayRecord.factors.split(',')"
          :key="factor"
          class="factor-tag"
        >{{ getFactorLabel(factor) }}</text>
      </view>
    </view>

    <!-- 备注 -->
    <view class="notes-section" v-if="todayRecord?.notes">
      <view class="section-title">备注</view>
      <text class="notes-text">{{ todayRecord.notes }}</text>
    </view>

    <!-- 操作按钮 -->
    <view class="action-section" v-if="todayRecord">
      <button class="action-btn edit" @click="showEditPopup">编辑</button>
      <button class="action-btn delete" @click="deleteTodayRecord">删除</button>
    </view>

    <!-- 记录/编辑弹窗 -->
    <uni-popup ref="recordPopup" type="center">
      <view class="record-popup">
        <view class="popup-title">{{ isEdit ? '编辑睡眠记录' : '记录睡眠' }}</view>

        <!-- 日期 -->
        <view class="form-item">
          <text class="form-label">日期</text>
          <picker mode="date" :value="form.sleepDate" @change="onDateChange">
            <view class="picker-value">{{ form.sleepDate }}</view>
          </picker>
        </view>

        <!-- 入睡时间 -->
        <view class="form-item">
          <text class="form-label">入睡时间</text>
          <picker mode="time" :value="form.bedTime" @change="onBedTimeChange">
            <view class="picker-value">{{ form.bedTime }}</view>
          </picker>
        </view>

        <!-- 起床时间 -->
        <view class="form-item">
          <text class="form-label">起床时间</text>
          <picker mode="time" :value="form.wakeTime" @change="onWakeTimeChange">
            <view class="picker-value">{{ form.wakeTime }}</view>
          </picker>
        </view>

        <!-- 睡眠质量 -->
        <view class="form-item">
          <text class="form-label">睡眠质量</text>
          <view class="quality-options">
            <view
              v-for="option in qualityOptions"
              :key="option.value"
              class="quality-option"
              :class="{ active: form.sleepQuality === option.value }"
              @click="form.sleepQuality = option.value"
            >
              <text class="quality-emoji-small">{{ option.emoji }}</text>
              <text class="quality-label">{{ option.label }}</text>
            </view>
          </view>
        </view>

        <!-- 深睡时长 -->
        <view class="form-item">
          <text class="form-label">深睡时长(分钟)</text>
          <input
            v-model="form.deepSleep"
            type="number"
            class="form-input"
            placeholder="可选"
          />
        </view>

        <!-- 浅睡时长 -->
        <view class="form-item">
          <text class="form-label">浅睡时长(分钟)</text>
          <input
            v-model="form.lightSleep"
            type="number"
            class="form-input"
            placeholder="可选"
          />
        </view>

        <!-- 影响因素 -->
        <view class="form-item">
          <text class="form-label">影响因素</text>
          <view class="factor-options">
            <view
              v-for="factor in factorOptions"
              :key="factor.key"
              class="factor-option"
              :class="{ active: selectedFactors.includes(factor.key) }"
              @click="toggleFactor(factor.key)"
            >
              <text class="factor-icon">{{ factor.icon }}</text>
              <text class="factor-label">{{ factor.label }}</text>
            </view>
          </view>
        </view>

        <!-- 备注 -->
        <view class="form-item">
          <text class="form-label">备注</text>
          <textarea
            v-model="form.notes"
            class="form-textarea"
            placeholder="记录今天的睡眠感受..."
          />
        </view>

        <view class="popup-actions">
          <button class="popup-btn cancel" @click="closePopup">取消</button>
          <button class="popup-btn confirm" @click="saveRecord">保存</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { sleepApi } from '@/api/sleep.api'
import type { SleepRecord, SleepQuality } from '@/types/sleep'
import { SLEEP_QUALITY_OPTIONS, SLEEP_FACTORS, formatDuration } from '@/types/sleep'

const todayRecord = ref<SleepRecord | null>(null)
const recordPopup = ref()
const isEdit = ref(false)
const editingId = ref<number | null>(null)

const qualityOptions = SLEEP_QUALITY_OPTIONS
const factorOptions = SLEEP_FACTORS
const selectedFactors = ref<string[]>([])

const form = ref({
  sleepDate: getTodayDate(),
  bedTime: '23:00',
  wakeTime: '07:00',
  sleepQuality: 3 as SleepQuality,
  deepSleep: '',
  lightSleep: '',
  awakeDuration: '',
  notes: ''
})

const scoreGradient = computed(() => {
  const score = todayRecord.value?.sleepScore || 0
  if (score >= 80) return 'conic-gradient(#43e97b 0deg, #38f9d7 360deg)'
  if (score >= 60) return 'conic-gradient(#667eea 0deg, #764ba2 360deg)'
  return 'conic-gradient(#ff6b6b 0deg, #ffa500 360deg)'
})

function getTodayDate() {
  return new Date().toISOString().split('T')[0]
}

function getQualityEmoji(quality: number) {
  const option = qualityOptions.find(o => o.value === quality)
  return option?.emoji || '😐'
}

function getFactorLabel(key: string) {
  const factor = factorOptions.find(f => f.key === key)
  return factor ? `${factor.icon} ${factor.label}` : key
}

function getSleepPercent(minutes: number) {
  if (!todayRecord.value) return 0
  const total = todayRecord.value.sleepDuration
  return total > 0 ? (minutes / total) * 100 : 0
}

async function loadTodayRecord() {
  try {
    const res = await sleepApi.getSleepByDate(getTodayDate())
    todayRecord.value = res.data
  } catch (error) {
    console.error('加载睡眠记录失败', error)
  }
}

function showRecordPopup() {
  isEdit.value = false
  editingId.value = null
  resetForm()
  recordPopup.value?.open()
}

function showEditPopup() {
  if (!todayRecord.value) return
  isEdit.value = true
  editingId.value = todayRecord.value.id || null

  form.value = {
    sleepDate: todayRecord.value.sleepDate,
    bedTime: todayRecord.value.bedTimeStr || '23:00',
    wakeTime: todayRecord.value.wakeTimeStr || '07:00',
    sleepQuality: todayRecord.value.sleepQuality,
    deepSleep: todayRecord.value.deepSleep?.toString() || '',
    lightSleep: todayRecord.value.lightSleep?.toString() || '',
    awakeDuration: todayRecord.value.awakeDuration?.toString() || '',
    notes: todayRecord.value.notes || ''
  }

  selectedFactors.value = todayRecord.value.factors
    ? todayRecord.value.factors.split(',')
    : []

  recordPopup.value?.open()
}

function closePopup() {
  recordPopup.value?.close()
}

function resetForm() {
  form.value = {
    sleepDate: getTodayDate(),
    bedTime: '23:00',
    wakeTime: '07:00',
    sleepQuality: 3,
    deepSleep: '',
    lightSleep: '',
    awakeDuration: '',
    notes: ''
  }
  selectedFactors.value = []
}

function onDateChange(e: any) {
  form.value.sleepDate = e.detail.value
}

function onBedTimeChange(e: any) {
  form.value.bedTime = e.detail.value
}

function onWakeTimeChange(e: any) {
  form.value.wakeTime = e.detail.value
}

function toggleFactor(key: string) {
  const index = selectedFactors.value.indexOf(key)
  if (index > -1) {
    selectedFactors.value.splice(index, 1)
  } else {
    selectedFactors.value.push(key)
  }
}

async function saveRecord() {
  const sleepDate = new Date(form.value.sleepDate)
  const [bedHour, bedMin] = form.value.bedTime.split(':').map(Number)
  const [wakeHour, wakeMin] = form.value.wakeTime.split(':').map(Number)

  const bedTime = new Date(sleepDate)
  bedTime.setHours(bedHour, bedMin, 0, 0)

  const wakeTime = new Date(sleepDate)
  wakeTime.setDate(wakeTime.getDate() + 1)
  wakeTime.setHours(wakeHour, wakeMin, 0, 0)

  const recordData: SleepRecord = {
    sleepDate: form.value.sleepDate,
    bedTime: bedTime.toISOString(),
    wakeTime: wakeTime.toISOString(),
    sleepQuality: form.value.sleepQuality,
    sleepDuration: 0,
    deepSleep: form.value.deepSleep ? parseInt(form.value.deepSleep) : undefined,
    lightSleep: form.value.lightSleep ? parseInt(form.value.lightSleep) : undefined,
    awakeDuration: form.value.awakeDuration ? parseInt(form.value.awakeDuration) : undefined,
    factors: selectedFactors.value.join(','),
    notes: form.value.notes
  }

  try {
    if (isEdit.value && editingId.value) {
      await sleepApi.updateSleep(editingId.value, recordData)
    } else {
      await sleepApi.recordSleep(recordData)
    }

    uni.showToast({ title: '保存成功', icon: 'success' })
    closePopup()
    loadTodayRecord()
  } catch (error) {
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
}

function deleteTodayRecord() {
  if (!todayRecord.value?.id) return

  uni.showModal({
    title: '确认删除',
    content: '确定删除今天的睡眠记录吗？',
    confirmColor: '#ff4444',
    success: async (res) => {
      if (res.confirm) {
        try {
          await sleepApi.deleteSleep(todayRecord.value!.id!)
          uni.showToast({ title: '删除成功', icon: 'success' })
          todayRecord.value = null
        } catch (error) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

onMounted(() => {
  loadTodayRecord()
})
</script>

<style scoped>
.sleep-record-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  padding: 30rpx;
  padding-bottom: 60rpx;
}

/* 评分卡片 */
.score-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 30rpx;
  padding: 40rpx;
  margin-bottom: 30rpx;
}

.score-circle {
  width: 240rpx;
  height: 240rpx;
  border-radius: 50%;
  margin: 0 auto 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.score-inner {
  width: 200rpx;
  height: 200rpx;
  background: #1a1a2e;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.score-value {
  font-size: 72rpx;
  font-weight: 700;
  color: #fff;
  line-height: 1;
}

.score-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 10rpx;
}

.sleep-info {
  display: flex;
  justify-content: space-around;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-icon {
  font-size: 40rpx;
  margin-right: 16rpx;
}

.info-content {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.5);
}

.info-value {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
  margin-top: 4rpx;
}

/* 未记录卡片 */
.no-record-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 30rpx;
  padding: 80rpx 40rpx;
  text-align: center;
  margin-bottom: 30rpx;
}

.no-record-icon {
  font-size: 100rpx;
  margin-bottom: 30rpx;
}

.no-record-title {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #fff;
  margin-bottom: 16rpx;
}

.no-record-desc {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 40rpx;
}

.record-btn {
  width: 300rpx;
  height: 80rpx;
  line-height: 80rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 30rpx;
  border-radius: 40rpx;
  border: none;
}

/* 质量部分 */
.quality-section,
.structure-section,
.factors-section,
.notes-section {
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

.quality-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20rpx;
}

.quality-emoji {
  font-size: 60rpx;
}

.quality-text {
  font-size: 32rpx;
  color: #fff;
}

/* 睡眠结构 */
.structure-chart {
  margin-top: 20rpx;
}

.structure-bar {
  height: 40rpx;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20rpx;
  overflow: hidden;
  display: flex;
}

.bar-segment {
  height: 100%;
}

.bar-segment.deep {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.bar-segment.light {
  background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
}

.bar-segment.awake {
  background: linear-gradient(90deg, #ff6b6b 0%, #ffa500 100%);
}

.structure-legend {
  display: flex;
  justify-content: center;
  gap: 30rpx;
  margin-top: 20rpx;
}

.legend-item {
  display: flex;
  align-items: center;
}

.legend-color {
  width: 20rpx;
  height: 20rpx;
  border-radius: 10rpx;
  margin-right: 10rpx;
}

.legend-color.deep {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.legend-color.light {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.legend-color.awake {
  background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%);
}

.legend-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}

/* 影响因素 */
.factors-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.factor-tag {
  background: rgba(102, 126, 234, 0.3);
  color: #fff;
  font-size: 24rpx;
  padding: 12rpx 24rpx;
  border-radius: 30rpx;
}

/* 备注 */
.notes-text {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
}

/* 操作按钮 */
.action-section {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 30rpx;
  border-radius: 40rpx;
  border: none;
}

.action-btn.edit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.action-btn.delete {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

/* 弹窗 */
.record-popup {
  background: #1a1a2e;
  border-radius: 30rpx;
  padding: 40rpx;
  width: 600rpx;
  max-height: 800rpx;
  overflow-y: auto;
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

.picker-value {
  background: rgba(255, 255, 255, 0.1);
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
  color: #fff;
  font-size: 28rpx;
}

.form-input {
  background: rgba(255, 255, 255, 0.1);
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
  color: #fff;
  font-size: 28rpx;
}

.form-textarea {
  background: rgba(255, 255, 255, 0.1);
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
  color: #fff;
  font-size: 28rpx;
  height: 120rpx;
  width: 100%;
}

.quality-options {
  display: flex;
  justify-content: space-between;
}

.quality-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 12rpx;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
  min-width: 90rpx;
}

.quality-option.active {
  background: rgba(102, 126, 234, 0.3);
}

.quality-emoji-small {
  font-size: 40rpx;
  margin-bottom: 6rpx;
}

.quality-label {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.6);
}

.quality-option.active .quality-label {
  color: #fff;
}

.factor-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.factor-option {
  display: flex;
  align-items: center;
  padding: 12rpx 20rpx;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 30rpx;
}

.factor-option.active {
  background: rgba(102, 126, 234, 0.3);
}

.factor-icon {
  font-size: 28rpx;
  margin-right: 8rpx;
}

.factor-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}

.factor-option.active .factor-label {
  color: #fff;
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
