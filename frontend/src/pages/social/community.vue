<template>
  <view class="community-page">
    <!-- 标签切换 -->
    <view class="tab-bar">
      <view
        class="tab-item"
        :class="{ active: currentTab === 'all' }"
        @click="switchTab('all')"
      >
        社区动态
      </view>
      <view
        class="tab-item"
        :class="{ active: currentTab === 'following' }"
        @click="switchTab('following')"
      >
        关注
      </view>
    </view>

    <!-- 动态列表 -->
    <scroll-view
      scroll-y
      class="moment-list"
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <!-- 发布按钮 -->
      <view class="publish-btn" @click="goToPublish">
        <text class="publish-icon">+</text>
        <text class="publish-text">分享你的减脂时刻</text>
      </view>

      <!-- 动态卡片 -->
      <view
        v-for="moment in momentList"
        :key="moment.id"
        class="moment-card"
        @click="goToDetail(moment.id)"
      >
        <!-- 用户信息 -->
        <view class="user-info">
          <image class="avatar" :src="moment.userAvatar || '/static/default-avatar.png'" />
          <view class="user-meta">
            <text class="nickname">{{ moment.userNickname }}</text>
            <text class="time">{{ formatTime(moment.createTime) }}</text>
          </view>
        </view>

        <!-- 内容 -->
        <view class="content">{{ moment.content }}</view>

        <!-- 图片 -->
        <view v-if="moment.images && moment.images.length" class="image-grid">
          <image
            v-for="(img, index) in moment.images.slice(0, 9)"
            :key="index"
            class="moment-image"
            :src="img"
            mode="aspectFill"
            @click.stop="previewImage(moment.images, index)"
          />
        </view>

        <!-- 分享数据 -->
        <view v-if="moment.shareWeight || moment.shareExercise" class="share-data">
          <view v-if="moment.shareWeight && moment.weightValue" class="share-item">
            <text class="share-icon">⚖️</text>
            <text>今日体重 {{ moment.weightValue }}kg</text>
          </view>
          <view v-if="moment.shareExercise && moment.exerciseMinutes" class="share-item">
            <text class="share-icon">🏃</text>
            <text>运动 {{ moment.exerciseMinutes }}分钟，消耗 {{ moment.exerciseCalories }}千卡</text>
          </view>
        </view>

        <!-- 操作栏 -->
        <view class="action-bar">
          <view class="action-item" @click.stop="toggleLike(moment)">
            <text class="action-icon" :class="{ liked: moment.isLiked }">
              {{ moment.isLiked ? '❤️' : '🤍' }}
            </text>
            <text class="action-count" :class="{ liked: moment.isLiked }">
              {{ moment.likeCount || '点赞' }}
            </text>
          </view>
          <view class="action-item" @click.stop="goToDetail(moment.id)">
            <text class="action-icon">💬</text>
            <text class="action-count">{{ moment.commentCount || '评论' }}</text>
          </view>
        </view>
      </view>

      <!-- 加载状态 -->
      <view class="load-more">
        <text v-if="isLoading">加载中...</text>
        <text v-else-if="!hasMore">没有更多了</text>
        <text v-else>上拉加载更多</text>
      </view>

      <!-- 空状态 -->
      <view v-if="momentList.length === 0 && !isLoading" class="empty-state">
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无动态，来分享第一条吧~</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMomentList, getFollowingMoments, toggleLike } from '@/api/social.api'
import type { Moment } from '@/types/social'

const currentTab = ref<'all' | 'following'>('all')
const momentList = ref<Moment[]>([])
const isLoading = ref(false)
const isRefreshing = ref(false)
const hasMore = ref(true)
const page = ref(1)
const size = 20

onMounted(() => {
  loadMoments()
})

onShow(() => {
  // 页面显示时刷新
  onRefresh()
})

function switchTab(tab: 'all' | 'following') {
  if (currentTab.value === tab) return
  currentTab.value = tab
  page.value = 1
  hasMore.value = true
  momentList.value = []
  loadMoments()
}

async function loadMoments() {
  if (isLoading.value || !hasMore.value) return
  isLoading.value = true

  try {
    const api = currentTab.value === 'all' ? getMomentList : getFollowingMoments
    const res = await api(page.value, size)

    if (res.code === 200 && res.data) {
      const list = res.data.records || []
      if (page.value === 1) {
        momentList.value = list
      } else {
        momentList.value.push(...list)
      }
      hasMore.value = list.length === size
    }
  } catch (error) {
    console.error('加载动态失败:', error)
  } finally {
    isLoading.value = false
  }
}

async function onRefresh() {
  isRefreshing.value = true
  page.value = 1
  hasMore.value = true
  await loadMoments()
  isRefreshing.value = false
}

function loadMore() {
  if (!hasMore.value || isLoading.value) return
  page.value++
  loadMoments()
}

async function toggleLikeMoment(moment: Moment) {
  try {
    const res = await toggleLike(moment.id)
    if (res.code === 200) {
      moment.isLiked = res.data
      moment.likeCount += moment.isLiked ? 1 : -1
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

function goToPublish() {
  uni.navigateTo({ url: '/pages/social/publish' })
}

function goToDetail(momentId: number) {
  uni.navigateTo({ url: `/pages/social/moment-detail?id=${momentId}` })
}

function previewImage(images: string[], current: number) {
  uni.previewImage({
    urls: images,
    current: images[current]
  })
}

function formatTime(time: string): string {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return `${date.getMonth() + 1}月${date.getDate()}日`
}
</script>

<style scoped>
.community-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.tab-bar {
  display: flex;
  background: #fff;
  border-bottom: 1rpx solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 30rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #667eea;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background: #667eea;
  border-radius: 2rpx;
}

.moment-list {
  height: calc(100vh - 88rpx);
}

.publish-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20rpx;
  padding: 30rpx;
  background: #fff;
  border-radius: 16rpx;
  border: 2rpx dashed #667eea;
}

.publish-icon {
  font-size: 40rpx;
  color: #667eea;
  margin-right: 10rpx;
}

.publish-text {
  font-size: 28rpx;
  color: #667eea;
}

.moment-card {
  background: #fff;
  margin: 20rpx;
  padding: 30rpx;
  border-radius: 16rpx;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.user-meta {
  flex: 1;
}

.nickname {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
}

.time {
  font-size: 24rpx;
  color: #999;
}

.content {
  font-size: 30rpx;
  color: #333;
  line-height: 1.6;
  margin-bottom: 20rpx;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-bottom: 20rpx;
}

.moment-image {
  width: 220rpx;
  height: 220rpx;
  border-radius: 8rpx;
}

.share-data {
  background: #f8f9fa;
  padding: 20rpx;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.share-item {
  display: flex;
  align-items: center;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.share-item:last-child {
  margin-bottom: 0;
}

.share-icon {
  margin-right: 10rpx;
}

.action-bar {
  display: flex;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 20rpx;
}

.action-item {
  display: flex;
  align-items: center;
  margin-right: 40rpx;
}

.action-icon {
  font-size: 32rpx;
  margin-right: 8rpx;
}

.action-icon.liked {
  color: #ff4757;
}

.action-count {
  font-size: 26rpx;
  color: #666;
}

.action-count.liked {
  color: #ff4757;
}

.load-more {
  text-align: center;
  padding: 30rpx;
  font-size: 26rpx;
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 40rpx;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
