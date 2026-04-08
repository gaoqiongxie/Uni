<template>
  <view class="ai-chat-page">
    <!-- 头部 -->
    <view class="chat-header">
      <view class="back-btn" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <view class="ai-info">
        <text class="ai-name">轻瘦AI助手</text>
        <text class="ai-status">在线</text>
      </view>
      <view class="header-actions">
        <text class="action-icon" @click="clearChat">🗑️</text>
      </view>
    </view>

    <!-- 消息列表 -->
    <scroll-view
      class="message-list"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-with-animation="true"
    >
      <!-- 欢迎消息 -->
      <view class="welcome-message" v-if="messages.length === 0">
        <view class="ai-avatar-large">
          <text>🤖</text>
        </view>
        <text class="welcome-title">你好！我是你的减脂助手</text>
        <text class="welcome-desc">
          我可以帮你：\n• 分析身体数据\n• 制定个性化计划\n• 解答减脂疑问\n• 每日打卡提醒
        </text>
        <view class="quick-questions">
          <text class="quick-q" @click="sendQuick('帮我分析数据')">📊 分析我的数据</text>
          <text class="quick-q" @click="sendQuick('生成饮食计划')">🥗 生成饮食计划</text>
          <text class="quick-q" @click="sendQuick('生成运动计划')">🏃 生成运动计划</text>
          <text class="quick-q" @click="sendQuick('如何突破平台期')">📈 如何突破平台期</text>
        </view>
      </view>

      <!-- 消息气泡 -->
      <view
        v-for="(msg, index) in messages"
        :key="index"
        class="message-item"
        :class="msg.role"
      >
        <view class="avatar" v-if="msg.role === 'assistant'">
          <text>🤖</text>
        </view>
        <view class="message-content">
          <text class="message-text">{{ msg.content }}</text>
          <text class="message-time">{{ formatTime(msg.createdAt) }}</text>
        </view>
        <view class="avatar user-avatar" v-if="msg.role === 'user'">
          <text>我</text>
        </view>
      </view>

      <!-- 加载中 -->
      <view class="typing-indicator" v-if="isTyping">
        <view class="avatar">
          <text>🤖</text>
        </view>
        <view class="typing-dots">
          <view class="dot"></view>
          <view class="dot"></view>
          <view class="dot"></view>
        </view>
      </view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="input-area">
      <view class="input-wrapper">
        <input
          v-model="inputMessage"
          class="message-input"
          placeholder="输入消息..."
          confirm-type="send"
          @confirm="sendMessage"
        />
        <view class="send-btn" :class="{ active: inputMessage.trim() }" @click="sendMessage">
          <text class="send-icon">➤</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { chatWithAi, getChatHistory, newSession } from '@/api/ai-assistant.api'
import type { AiChatMessage } from '@/types/ai-assistant'

const messages = ref<AiChatMessage[]>([])
const inputMessage = ref('')
const sessionId = ref('')
const isTyping = ref(false)
const scrollTop = ref(0)

onMounted(() => {
  initSession()
})

async function initSession() {
  try {
    const res = await newSession()
    if (res.code === 200) {
      sessionId.value = res.data
      loadHistory()
    }
  } catch (error) {
    console.error('创建会话失败:', error)
  }
}

async function loadHistory() {
  try {
    const res = await getChatHistory(sessionId.value)
    if (res.code === 200) {
      messages.value = res.data || []
      scrollToBottom()
    }
  } catch (error) {
    console.error('加载历史失败:', error)
  }
}

async function sendMessage() {
  const message = inputMessage.value.trim()
  if (!message || isTyping.value) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: message,
    messageType: 'text',
    createdAt: new Date().toISOString(),
  })

  inputMessage.value = ''
  isTyping.value = true
  scrollToBottom()

  try {
    const res = await chatWithAi(sessionId.value, message)
    if (res.code === 200) {
      messages.value.push(res.data)
    }
  } catch (error) {
    messages.value.push({
      role: 'assistant',
      content: '抱歉，我暂时无法回复，请稍后再试。',
      messageType: 'text',
      createdAt: new Date().toISOString(),
    })
  } finally {
    isTyping.value = false
    scrollToBottom()
  }
}

function sendQuick(text: string) {
  inputMessage.value = text
  sendMessage()
}

function scrollToBottom() {
  nextTick(() => {
    scrollTop.value = messages.value.length * 1000
  })
}

function goBack() {
  uni.navigateBack()
}

function clearChat() {
  uni.showModal({
    title: '提示',
    content: '确定清空对话？',
    success: (res) => {
      if (res.confirm) {
        messages.value = []
        initSession()
      }
    },
  })
}

function formatTime(time?: string): string {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}
</script>

<style lang="scss" scoped>
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
}

// 头部
.chat-header {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}

.back-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: #666;
}

.ai-info {
  flex: 1;
  text-align: center;
}

.ai-name {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #1a1a2e;
}

.ai-status {
  font-size: 22rpx;
  color: #43e97b;
}

.header-actions {
  width: 60rpx;
  text-align: right;
}

.action-icon {
  font-size: 36rpx;
}

// 消息列表
.message-list {
  flex: 1;
  padding: 24rpx;
  overflow-y: auto;
}

// 欢迎消息
.welcome-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 40rpx;
  text-align: center;
}

.ai-avatar-large {
  width: 140rpx;
  height: 140rpx;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;

  text {
    font-size: 80rpx;
  }
}

.welcome-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 16rpx;
}

.welcome-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
  margin-bottom: 40rpx;
  white-space: pre-line;
}

.quick-questions {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  width: 100%;
}

.quick-q {
  padding: 24rpx 32rpx;
  background: #fff;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #667eea;
  text-align: left;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

// 消息项
.message-item {
  display: flex;
  margin-bottom: 24rpx;

  &.user {
    justify-content: flex-end;

    .message-content {
      background: #667eea;
      color: #fff;
      border-radius: 24rpx 24rpx 4rpx 24rpx;
    }

    .message-time {
      color: rgba(255, 255, 255, 0.7);
    }
  }

  &.assistant {
    .message-content {
      background: #fff;
      color: #1a1a2e;
      border-radius: 24rpx 24rpx 24rpx 4rpx;
    }
  }
}

.avatar {
  width: 72rpx;
  height: 72rpx;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16rpx;
  flex-shrink: 0;

  text {
    font-size: 40rpx;
  }
}

.user-avatar {
  background: #e8eaf6;
  margin-right: 0;
  margin-left: 16rpx;

  text {
    font-size: 28rpx;
    color: #667eea;
    font-weight: 600;
  }
}

.message-content {
  max-width: 70%;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.message-text {
  font-size: 30rpx;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}

.message-time {
  display: block;
  font-size: 20rpx;
  color: #999;
  margin-top: 8rpx;
  text-align: right;
}

// 输入中指示器
.typing-indicator {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.typing-dots {
  display: flex;
  gap: 8rpx;
  padding: 24rpx 32rpx;
  background: #fff;
  border-radius: 24rpx;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  background: #999;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;

  &:nth-child(1) { animation-delay: 0s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.4s; }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); }
  40% { transform: scale(1); }
}

// 输入区域
.input-area {
  padding: 20rpx 24rpx;
  background: #fff;
  border-top: 1rpx solid #eee;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 40rpx;
  padding: 8rpx 8rpx 8rpx 24rpx;
}

.message-input {
  flex: 1;
  height: 72rpx;
  font-size: 30rpx;
  color: #1a1a2e;
}

.send-btn {
  width: 72rpx;
  height: 72rpx;
  background: #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &.active {
    background: #667eea;
  }
}

.send-icon {
  font-size: 32rpx;
  color: #fff;
  margin-left: 4rpx;
}
</style>
