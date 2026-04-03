<template>
  <view class="setting-page">
    <!-- 餐食提醒 -->
    <view class="setting-group">
      <view class="group-title">餐食打卡提醒</view>
      <view class="setting-item">
        <view class="item-left">
          <text class="item-icon">🍽️</text>
          <view class="item-info">
            <text class="item-name">餐食提醒</text>
            <text class="item-desc">每日三餐打卡提醒</text>
          </view>
        </view>
        <switch
          :checked="setting.mealRemind === 1"
          @change="toggleSetting('mealRemind', $event)"
          color="#667eea"
        />
      </view>
      <view v-if="setting.mealRemind === 1" class="time-display">
        <text class="time-label">提醒时间：</text>
        <text class="time-value">08:00、12:00、18:00</text>
      </view>
    </view>

    <!-- 运动提醒 -->
    <view class="setting-group">
      <view class="group-title">运动打卡提醒</view>
      <view class="setting-item">
        <view class="item-left">
          <text class="item-icon">🏃</text>
          <view class="item-info">
            <text class="item-name">运动提醒</text>
            <text class="item-desc">每日运动打卡提醒</text>
          </view>
        </view>
        <switch
          :checked="setting.exerciseRemind === 1"
          @change="toggleSetting('exerciseRemind', $event)"
          color="#667eea"
        />
      </view>
      <view v-if="setting.exerciseRemind === 1" class="time-picker" @click="showTimePicker('exercise')">
        <text class="time-label">提醒时间：</text>
        <text class="time-value">{{ setting.exerciseRemindTime }}</text>
        <text class="time-arrow">›</text>
      </view>
    </view>

    <!-- 体重提醒 -->
    <view class="setting-group">
      <view class="group-title">体重记录提醒</view>
      <view class="setting-item">
        <view class="item-left">
          <text class="item-icon">⚖️</text>
          <view class="item-info">
            <text class="item-name">体重提醒</text>
            <text class="item-desc">每日体重记录提醒</text>
          </view>
        </view>
        <switch
          :checked="setting.weightRemind === 1"
          @change="toggleSetting('weightRemind', $event)"
          color="#667eea"
        />
      </view>
      <view v-if="setting.weightRemind === 1" class="time-picker" @click="showTimePicker('weight')">
        <text class="time-label">提醒时间：</text>
        <text class="time-value">{{ setting.weightRemindTime }}</text>
        <text class="time-arrow">›</text>
      </view>
    </view>

    <!-- 目标提醒 -->
    <view class="setting-group">
      <view class="group-title">目标相关</view>
      <view class="setting-item">
        <view class="item-left">
          <text class="item-icon">🏆</text>
          <view class="item-info">
            <text class="item-name">目标达成提醒</text>
            <text class="item-desc">目标达成时发送通知</text>
          </view>
        </view>
        <switch
          :checked="setting.goalAchieveRemind === 1"
          @change="toggleSetting('goalAchieveRemind', $event)"
          color="#667eea"
        />
      </view>
    </view>

    <!-- 系统通知 -->
    <view class="setting-group">
      <view class="group-title">系统通知</view>
      <view class="setting-item">
        <view class="item-left">
          <text class="item-icon">📢</text>
          <view class="item-info">
            <text class="item-name">系统公告</text>
            <text class="item-desc">接收系统更新和活动通知</text>
          </view>
        </view>
        <switch
          :checked="setting.systemNotify === 1"
          @change="toggleSetting('systemNotify', $event)"
          color="#667eea"
        />
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="save-section">
      <button class="save-btn" @click="saveSetting">保存设置</button>
    </view>

    <!-- 时间选择器 -->
    <picker
      mode="time"
      :value="pickerTime"
      @change="onTimeChange"
      @cancel="hidePicker"
    >
      <view v-if="showPicker" class="picker-mask">
        <view class="picker-content">
          <view class="picker-header">
            <text class="picker-cancel" @click="hidePicker">取消</text>
            <text class="picker-title">选择时间</text>
            <text class="picker-confirm" @click="confirmTime">确定</text>
          </view>
        </view>
      </view>
    </picker>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPushSetting, updatePushSetting } from '@/api/notification.api'
import { defaultPushSetting, type UserPushSetting } from '@/types/notification'
import { getUserId } from '@/utils/storage'

const userId = getUserId()
const setting = ref<UserPushSetting>({ ...defaultPushSetting, userId })
const showPicker = ref(false)
const pickerType = ref('')
const pickerTime = ref('20:00')

// 加载设置
async function loadSetting() {
  try {
    const res = await getPushSetting(userId)
    if (res) {
      setting.value = res
    }
  } catch {
    // 使用默认设置
  }
}

// 切换开关
function toggleSetting(key: keyof UserPushSetting, e: any) {
  const value = e.detail.value ? 1 : 0
  setting.value = { ...setting.value, [key]: value }
}

// 显示时间选择器
function showTimePicker(type: string) {
  pickerType.value = type
  pickerTime.value = type === 'exercise' 
    ? setting.value.exerciseRemindTime 
    : setting.value.weightRemindTime
  showPicker.value = true
}

// 隐藏选择器
function hidePicker() {
  showPicker.value = false
}

// 时间变化
function onTimeChange(e: any) {
  pickerTime.value = e.detail.value
}

// 确认时间
function confirmTime() {
  if (pickerType.value === 'exercise') {
    setting.value.exerciseRemindTime = pickerTime.value
  } else if (pickerType.value === 'weight') {
    setting.value.weightRemindTime = pickerTime.value
  }
  hidePicker()
}

// 保存设置
async function saveSetting() {
  uni.showLoading({ title: '保存中' })
  try {
    await updatePushSetting(userId, setting.value)
    uni.showToast({ title: '保存成功', icon: 'success' })
  } finally {
    uni.hideLoading()
  }
}

onMounted(() => {
  loadSetting()
})
</script>

<style scoped>
.setting-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.setting-group {
  background: #fff;
  margin-bottom: 20rpx;
}

.group-title {
  font-size: 26rpx;
  color: #999;
  padding: 20rpx 30rpx 10rpx;
  background: #f5f5f5;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.item-left {
  display: flex;
  align-items: center;
}

.item-icon {
  font-size: 44rpx;
  margin-right: 20rpx;
}

.item-info {
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 30rpx;
  color: #333;
  margin-bottom: 6rpx;
}

.item-desc {
  font-size: 24rpx;
  color: #999;
}

.time-display, .time-picker {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #fafafa;
}

.time-label {
  font-size: 26rpx;
  color: #666;
}

.time-value {
  font-size: 26rpx;
  color: #667eea;
  margin-left: 20rpx;
  flex: 1;
}

.time-arrow {
  font-size: 30rpx;
  color: #999;
}

.save-section {
  padding: 40rpx 30rpx;
}

.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 50rpx;
  font-size: 32rpx;
  height: 90rpx;
  line-height: 90rpx;
}

.picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.picker-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-radius: 30rpx 30rpx 0 0;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
}

.picker-cancel {
  font-size: 28rpx;
  color: #999;
}

.picker-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

.picker-confirm {
  font-size: 28rpx;
  color: #667eea;
}
</style>
