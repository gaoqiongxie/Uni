<template>
  <view class="ai-home-page">
    <!-- AI助手头部 -->
    <view class="ai-header">
      <view class="ai-avatar">
        <text class="ai-icon">🤖</text>
        <view class="ai-status"></view>
      </view>
      <view class="ai-info">
        <text class="ai-name">轻瘦AI助手</text>
        <text class="ai-desc">您的专属减脂顾问</text>
      </view>
      <view class="quick-actions">
        <view class="action-btn" @click="startChat">
          <text class="action-icon">💬</text>
        </view>
      </view>
    </view>

    <!-- 今日建议卡片 -->
    <view class="tip-card" @click="startChat">
      <view class="tip-header">
        <text class="tip-icon">💡</text>
        <text class="tip-title">今日建议</text>
      </view>
      <text class="tip-content">{{ dailyTip || '正在获取今日建议...' }}</text>
      <view class="tip-footer">
        <text class="tip-more">点击与AI对话 ›</text>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-functions">
      <view class="func-title">快捷功能</view>
      <view class="func-grid">
        <view class="func-item" @click="showAnalysis">
          <view class="func-icon" style="background: linear-gradient(135deg, #667eea, #764ba2);">
            <text>📊</text>
          </view>
          <text class="func-label">数据分析</text>
        </view>
        <view class="func-item" @click="generatePlan('diet')">
          <view class="func-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7);">
            <text>🥗</text>
          </view>
          <text class="func-label">饮食计划</text>
        </view>
        <view class="func-item" @click="generatePlan('exercise')">
          <view class="func-icon" style="background: linear-gradient(135deg, #fa709a, #fee140);">
            <text>🏃</text>
          </view>
          <text class="func-label">运动计划</text>
        </view>
        <view class="func-item" @click="generatePlan('comprehensive')">
          <view class="func-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe);">
            <text>📋</text>
          </view>
          <text class="func-label">综合方案</text>
        </view>
      </view>
    </view>

    <!-- 我的计划 -->
    <view class="my-plans" v-if="plans.length > 0">
      <view class="plans-header">
        <text class="plans-title">我的计划</text>
        <text class="plans-more" @click="viewAllPlans">查看全部 ›</text>
      </view>
      <scroll-view scroll-x class="plans-scroll">
        <view class="plan-card" v-for="plan in plans.slice(0, 3)" :key="plan.id" @click="viewPlan(plan)">
          <view class="plan-icon">{{ getPlanIcon(plan.planType) }}</view>
          <view class="plan-info">
            <text class="plan-title">{{ plan.planTitle }}</text>
            <text class="plan-desc">{{ plan.aiAnalysis?.substring(0, 30) }}...</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 开始对话按钮 -->
    <view class="chat-entry" @click="startChat">
      <view class="chat-btn">
        <text class="chat-icon">💬</text>
        <text class="chat-text">开始对话</text>
      </view>
      <text class="chat-hint">有任何减脂问题，随时问我~</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDailyTip, getUserPlans, analyzeUserData } from '@/api/ai-assistant.api'
import type { PersonalizedPlan } from '@/types/ai-assistant'

const dailyTip = ref('')
const plans = ref<PersonalizedPlan[]>([])
const analysisResult = ref('')

onMounted(() => {
  loadData()
})

async function loadData() {
  try {
    const [tipRes, plansRes] = await Promise.all([
      getDailyTip(),
      getUserPlans(),
    ])

    if (tipRes.code === 200) {
      dailyTip.value = tipRes.data
    }

    if (plansRes.code === 200) {
      plans.value = plansRes.data || []
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

function startChat() {
  uni.navigateTo({ url: '/pages/ai-assistant/ai-chat' })
}

async function showAnalysis() {
  uni.showLoading({ title: '分析中...' })
  try {
    const res = await analyzeUserData()
    if (res.code === 200) {
      analysisResult.value = res.data
      uni.navigateTo({
        url: `/pages/ai-assistant/ai-chat?initialMessage=${encodeURIComponent('查看我的数据分析')}`,
      })
    }
  } catch (error) {
    uni.showToast({ title: '分析失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

function generatePlan(type: string) {
  uni.navigateTo({
    url: `/pages/ai-assistant/ai-chat?initialMessage=${encodeURIComponent(`生成${type === 'diet' ? '饮食' : type === 'exercise' ? '运动' : '综合'}计划`)}`,
  })
}

function viewAllPlans() {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function viewPlan(plan: PersonalizedPlan) {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}

function getPlanIcon(type: string): string {
  const icons: Record<string, string> = {
    diet: '🥗',
    exercise: '🏃',
    sleep: '💤',
    comprehensive: '📋',
  }
  return icons[type] || '📋'
}
</script>

<style lang="scss" scoped>
.ai-home-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24rpx;
}

// AI头部
.ai-header {
  display: flex;
  align-items: center;
  padding: 32rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24rpx;
  margin-bottom: 24rpx;
}

.ai-avatar {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.ai-icon {
  font-size: 60rpx;
}

.ai-status {
  position: absolute;
  bottom: 4rpx;
  right: 4rpx;
  width: 20rpx;
  height: 20rpx;
  background: #43e97b;
  border-radius: 50%;
  border: 4rpx solid #667eea;
}

.ai-info {
  flex: 1;
}

.ai-name {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8rpx;
}

.ai-desc {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.quick-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon {
  font-size: 40rpx;
}

// 建议卡片
.tip-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.tip-header {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.tip-icon {
  font-size: 36rpx;
  margin-right: 12rpx;
}

.tip-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1a1a2e;
}

.tip-content {
  display: block;
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20rpx;
}

.tip-footer {
  display: flex;
  justify-content: flex-end;
}

.tip-more {
  font-size: 26rpx;
  color: #667eea;
}

// 快捷功能
.quick-functions {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.func-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 24rpx;
}

.func-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
}

.func-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.func-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;

  text {
    font-size: 48rpx;
  }
}

.func-label {
  font-size: 24rpx;
  color: #666;
}

// 我的计划
.my-plans {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.plans-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.plans-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1a1a2e;
}

.plans-more {
  font-size: 26rpx;
  color: #667eea;
}

.plans-scroll {
  white-space: nowrap;
}

.plan-card {
  display: inline-flex;
  align-items: center;
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  margin-right: 16rpx;
  min-width: 400rpx;
}

.plan-icon {
  font-size: 48rpx;
  margin-right: 20rpx;
}

.plan-info {
  flex: 1;
}

.plan-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8rpx;
}

.plan-desc {
  font-size: 24rpx;
  color: #999;
}

// 对话入口
.chat-entry {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  text-align: center;
}

.chat-btn {
  display: inline-flex;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24rpx 48rpx;
  border-radius: 50rpx;
  margin-bottom: 16rpx;
}

.chat-icon {
  font-size: 36rpx;
  margin-right: 12rpx;
}

.chat-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}

.chat-hint {
  font-size: 26rpx;
  color: #999;
}
</style>
