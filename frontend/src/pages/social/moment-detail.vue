<template>
  <view class="detail-page">
    <!-- 动态内容 -->
    <view v-if="moment" class="moment-section">
      <view class="user-info">
        <image class="avatar" :src="moment.userAvatar || '/static/default-avatar.png'" />
        <view class="user-meta">
          <text class="nickname">{{ moment.userNickname }}</text>
          <text class="time">{{ formatTime(moment.createTime) }}</text>
        </view>
        <button
          v-if="moment.userId !== currentUserId"
          class="follow-btn"
          :class="{ following: isFollowing }"
          @click="toggleFollowUser"
        >
          {{ isFollowing ? '已关注' : '+ 关注' }}
        </button>
      </view>

      <view class="content">{{ moment.content }}</view>

      <view v-if="moment.images && moment.images.length" class="image-grid">
        <image
          v-for="(img, index) in moment.images"
          :key="index"
          class="moment-image"
          :src="img"
          mode="aspectFill"
          @click="previewImage(moment.images, index)"
        />
      </view>

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

      <view class="action-bar">
        <view class="action-item" @click="toggleLikeMoment">
          <text class="action-icon" :class="{ liked: moment.isLiked }">
            {{ moment.isLiked ? '❤️' : '🤍' }}
          </text>
          <text class="action-count" :class="{ liked: moment.isLiked }">
            {{ moment.likeCount }}
          </text>
        </view>
        <view class="action-item">
          <text class="action-icon">💬</text>
          <text class="action-count">{{ moment.commentCount }}</text>
        </view>
      </view>
    </view>

    <!-- 评论列表 -->
    <view class="comment-section">
      <view class="section-title">
        评论 ({{ comments.length }})
      </view>

      <view
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <image class="comment-avatar" :src="comment.userAvatar || '/static/default-avatar.png'" />
        <view class="comment-content">
          <view class="comment-header">
            <text class="comment-user">{{ comment.userNickname }}</text>
            <text class="comment-time">{{ formatTime(comment.createTime) }}</text>
          </view>
          <view class="comment-text">{{ comment.content }}</view>
          <view
            v-if="comment.children && comment.children.length"
            class="reply-list"
          >
            <view
              v-for="reply in comment.children"
              :key="reply.id"
              class="reply-item"
            >
              <text class="reply-user">{{ reply.userNickname }}</text>
              <text class="reply-text">回复</text>
              <text class="reply-user">{{ reply.parentUserNickname }}</text>
              <text class="reply-text">: {{ reply.content }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="comments.length === 0" class="empty-comment">
        暂无评论，来抢沙发吧~
      </view>
    </view>

    <!-- 评论输入框 -->
    <view class="comment-input-bar">
      <input
        v-model="commentContent"
        class="comment-input"
        placeholder="写评论..."
        confirm-type="send"
        @confirm="submitComment"
      />
      <button class="send-btn" :disabled="!commentContent.trim()" @click="submitComment">
        发送
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMomentDetail, toggleLike, getMomentComments, addComment, toggleFollow, isFollowing as checkFollowing } from '@/api/social.api'
import type { Moment, MomentComment } from '@/types/social'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const currentUserId = userStore.userInfo?.id

const momentId = ref<number>(0)
const moment = ref<Moment | null>(null)
const comments = ref<MomentComment[]>([])
const commentContent = ref('')
const isFollowing = ref(false)

onLoad((options) => {
  if (options?.id) {
    momentId.value = parseInt(options.id)
    loadDetail()
    loadComments()
  }
})

async function loadDetail() {
  try {
    const res = await getMomentDetail(momentId.value)
    if (res.code === 200 && res.data) {
      moment.value = res.data
      // 检查是否已关注
      if (moment.value.userId !== currentUserId) {
        checkFollowStatus()
      }
    }
  } catch (error) {
    console.error('加载动态详情失败:', error)
  }
}

async function checkFollowStatus() {
  if (!moment.value) return
  try {
    const res = await checkFollowing(moment.value.userId)
    if (res.code === 200) {
      isFollowing.value = res.data
    }
  } catch (error) {
    console.error('检查关注状态失败:', error)
  }
}

async function loadComments() {
  try {
    const res = await getMomentComments(momentId.value)
    if (res.code === 200 && res.data) {
      comments.value = res.data
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

async function toggleLikeMoment() {
  if (!moment.value) return
  try {
    const res = await toggleLike(moment.value.id)
    if (res.code === 200) {
      moment.value.isLiked = res.data
      moment.value.likeCount += moment.value.isLiked ? 1 : -1
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

async function toggleFollowUser() {
  if (!moment.value) return
  try {
    const res = await toggleFollow(moment.value.userId)
    if (res.code === 200) {
      isFollowing.value = res.data
      uni.showToast({
        title: isFollowing.value ? '关注成功' : '已取消关注',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('关注失败:', error)
  }
}

async function submitComment() {
  if (!commentContent.value.trim()) return

  try {
    const res = await addComment(momentId.value, {
      content: commentContent.value.trim()
    })
    if (res.code === 200) {
      uni.showToast({ title: '评论成功', icon: 'success' })
      commentContent.value = ''
      loadComments()
      if (moment.value) {
        moment.value.commentCount++
      }
    }
  } catch (error) {
    uni.showToast({ title: '评论失败', icon: 'none' })
  }
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
.detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.moment-section {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
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

.follow-btn {
  padding: 10rpx 30rpx;
  background: #667eea;
  color: #fff;
  font-size: 26rpx;
  border-radius: 30rpx;
}

.follow-btn.following {
  background: #f0f0f0;
  color: #666;
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

.comment-section {
  background: #fff;
  padding: 30rpx;
  min-height: 400rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 30rpx;
}

.comment-item {
  display: flex;
  margin-bottom: 30rpx;
}

.comment-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  margin-right: 20rpx;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10rpx;
}

.comment-user {
  font-size: 28rpx;
  color: #667eea;
  font-weight: 500;
}

.comment-time {
  font-size: 24rpx;
  color: #999;
}

.comment-text {
  font-size: 28rpx;
  color: #333;
  line-height: 1.5;
}

.reply-list {
  margin-top: 15rpx;
  padding: 15rpx;
  background: #f8f9fa;
  border-radius: 8rpx;
}

.reply-item {
  font-size: 26rpx;
  line-height: 1.5;
  margin-bottom: 10rpx;
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-user {
  color: #667eea;
}

.reply-text {
  color: #666;
}

.empty-comment {
  text-align: center;
  padding: 60rpx 0;
  font-size: 28rpx;
  color: #999;
}

.comment-input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  background: #fff;
  border-top: 1rpx solid #eee;
}

.comment-input {
  flex: 1;
  height: 70rpx;
  background: #f5f5f5;
  border-radius: 35rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.send-btn {
  margin-left: 20rpx;
  padding: 15rpx 40rpx;
  background: #667eea;
  color: #fff;
  font-size: 28rpx;
  border-radius: 35rpx;
}

.send-btn[disabled] {
  background: #ccc;
}
</style>
