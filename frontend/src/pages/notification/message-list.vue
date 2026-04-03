<template>
  <view class="message-page">
    <!-- 顶部统计 -->
    <view class="header-stats">
      <view class="stat-item">
        <text class="stat-num">{{ unreadCount }}</text>
        <text class="stat-label">未读消息</text>
      </view>
      <view class="stat-item" @click="markAllRead">
        <text class="stat-icon">✓</text>
        <text class="stat-label">全部已读</text>
      </view>
    </view>

    <!-- 消息类型筛选 -->
    <view class="type-filter">
      <scroll-view scroll-x class="filter-scroll">
        <view
          v-for="type in messageTypes"
          :key="type.value"
          class="filter-item"
          :class="{ active: currentType === type.value }"
          @click="selectType(type.value)"
        >
          {{ type.label }}
        </view>
      </scroll-view>
    </view>

    <!-- 消息列表 -->
    <scroll-view
      scroll-y
      class="message-list"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <view
        v-for="msg in messageList"
        :key="msg.id"
        class="message-item"
        :class="{ unread: msg.isRead === 0 }"
        @click="readMessage(msg)"
      >
        <!-- 图标 -->
        <view class="msg-icon" :class="getIconClass(msg.type)">
          {{ getIcon(msg.type) }}
        </view>
        
        <!-- 内容 -->
        <view class="msg-content">
          <view class="msg-header">
            <text class="msg-title">{{ msg.title }}</text>
            <text class="msg-time">{{ formatTime(msg.createTime) }}</text>
          </view>
          <text class="msg-text">{{ msg.content }}</text>
        </view>
        
        <!-- 操作 -->
        <view class="msg-actions">
          <text v-if="msg.isRead === 0" class="unread-dot"></text>
          <text class="delete-btn" @click.stop="deleteMsg(msg.id)">删除</text>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="messageList.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无消息</text>
      </view>

      <!-- 加载更多 -->
      <view v-if="loadingMore" class="loading-more">加载中...</view>
      <view v-if="noMore && messageList.length > 0" class="no-more">没有更多了</view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMessageList, getUnreadCount, markAsRead, markAllAsRead, deleteMessage } from '@/api/notification.api'
import { MessageTypeNames, type MessageType, type UserMessage } from '@/types/notification'
import { getUserId } from '@/utils/storage'

const userId = getUserId()

// 消息类型
const messageTypes = [
  { value: '', label: '全部' },
  { value: 'MEAL_REMIND', label: '餐食' },
  { value: 'EXERCISE_REMIND', label: '运动' },
  { value: 'WEIGHT_REMIND', label: '体重' },
  { value: 'GOAL_ACHIEVE', label: '目标' },
  { value: 'SYSTEM', label: '系统' }
]

const currentType = ref('')
const messageList = ref<UserMessage[]>([])
const unreadCount = ref(0)
const page = ref(1)
const size = 20
const loading = ref(false)
const loadingMore = ref(false)
const noMore = ref(false)
const refreshing = ref(false)

// 获取图标
function getIcon(type: MessageType) {
  const icons: Record<MessageType, string> = {
    MEAL_REMIND: '🍽️',
    EXERCISE_REMIND: '🏃',
    WEIGHT_REMIND: '⚖️',
    GOAL_ACHIEVE: '🏆',
    SYSTEM: '📢'
  }
  return icons[type] || '📢'
}

// 获取图标样式类
function getIconClass(type: MessageType) {
  const classes: Record<MessageType, string> = {
    MEAL_REMIND: 'meal',
    EXERCISE_REMIND: 'exercise',
    WEIGHT_REMIND: 'weight',
    GOAL_ACHIEVE: 'goal',
    SYSTEM: 'system'
  }
  return classes[type] || 'system'
}

// 格式化时间
function formatTime(time: string) {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 加载消息列表
async function loadMessages(isRefresh = false) {
  if (loading.value) return
  loading.value = true
  
  if (isRefresh) {
    page.value = 1
    noMore.value = false
  }
  
  try {
    const res = await getMessageList({
      userId,
      type: currentType.value || undefined,
      page: page.value,
      size
    })
    
    if (isRefresh) {
      messageList.value = res.records
    } else {
      messageList.value.push(...res.records)
    }
    
    noMore.value = res.records.length < size
  } finally {
    loading.value = false
    refreshing.value = false
    loadingMore.value = false
  }
}

// 获取未读数量
async function loadUnreadCount() {
  const count = await getUnreadCount(userId)
  unreadCount.value = count
}

// 选择类型
function selectType(type: string) {
  currentType.value = type
  loadMessages(true)
}

// 下拉刷新
function onRefresh() {
  refreshing.value = true
  loadMessages(true)
  loadUnreadCount()
}

// 加载更多
function loadMore() {
  if (noMore.value || loadingMore.value) return
  loadingMore.value = true
  page.value++
  loadMessages()
}

// 阅读消息
async function readMessage(msg: UserMessage) {
  if (msg.isRead === 0) {
    await markAsRead(msg.id)
    msg.isRead = 1
    unreadCount.value--
  }
  
  // 根据消息类型跳转
  if (msg.relatedType === 'GOAL') {
    uni.navigateTo({ url: '/pages/goal/goal-detail' })
  } else if (msg.relatedType === 'RECIPE') {
    uni.navigateTo({ url: `/pages/recipe/recipe-detail?id=${msg.relatedId}` })
  }
}

// 标记全部已读
async function markAllRead() {
  if (unreadCount.value === 0) return
  
  uni.showModal({
    title: '提示',
    content: '确定将所有消息标记为已读？',
    success: async (res) => {
      if (res.confirm) {
        await markAllAsRead(userId)
        messageList.value.forEach(msg => msg.isRead = 1)
        unreadCount.value = 0
        uni.showToast({ title: '已标记', icon: 'success' })
      }
    }
  })
}

// 删除消息
async function deleteMsg(id: number) {
  uni.showModal({
    title: '提示',
    content: '确定删除这条消息？',
    success: async (res) => {
      if (res.confirm) {
        await deleteMessage(id)
        messageList.value = messageList.value.filter(msg => msg.id !== id)
        uni.showToast({ title: '已删除', icon: 'success' })
      }
    }
  })
}

onMounted(() => {
  loadMessages(true)
  loadUnreadCount()
})
</script>

<style scoped>
.message-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header-stats {
  display: flex;
  justify-content: space-around;
  padding: 30rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
}

.stat-num {
  font-size: 48rpx;
  font-weight: bold;
}

.stat-icon {
  font-size: 48rpx;
}

.stat-label {
  font-size: 24rpx;
  margin-top: 8rpx;
  opacity: 0.9;
}

.type-filter {
  background: #fff;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #eee;
}

.filter-scroll {
  white-space: nowrap;
  padding: 0 20rpx;
}

.filter-item {
  display: inline-block;
  padding: 12rpx 30rpx;
  margin-right: 16rpx;
  background: #f5f5f5;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
}

.filter-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.message-list {
  height: calc(100vh - 220rpx);
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 30rpx;
  background: #fff;
  margin-bottom: 2rpx;
}

.message-item.unread {
  background: #f8f9ff;
}

.msg-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.msg-icon.meal { background: #fff3e0; }
.msg-icon.exercise { background: #e8f5e9; }
.msg-icon.weight { background: #e3f2fd; }
.msg-icon.goal { background: #fce4ec; }
.msg-icon.system { background: #f3e5f5; }

.msg-content {
  flex: 1;
  min-width: 0;
}

.msg-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.msg-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

.msg-time {
  font-size: 24rpx;
  color: #999;
}

.msg-text {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.msg-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-left: 20rpx;
}

.unread-dot {
  width: 16rpx;
  height: 16rpx;
  background: #ff5252;
  border-radius: 50%;
  margin-bottom: 20rpx;
}

.delete-btn {
  font-size: 24rpx;
  color: #999;
  padding: 10rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 40rpx;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.loading-more, .no-more {
  text-align: center;
  padding: 30rpx;
  font-size: 24rpx;
  color: #999;
}
</style>
