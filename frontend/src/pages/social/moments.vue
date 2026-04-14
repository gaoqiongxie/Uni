<template>
  <view class="moments-page">
    <view class="page-header">
      <text class="title">好友动态</text>
    </view>

    <!-- 发布框 -->
    <view class="publish-box" @click="goToPublish">
      <view class="publish-input">
        <text class="placeholder">分享你的健康时刻...</text>
      </view>
      <view class="publish-actions">
        <text class="action-icon">📷</text>
        <text class="action-icon">😊</text>
      </view>
    </view>

    <!-- 动态列表 -->
    <view class="moment-list">
      <view class="moment-item" v-for="item in moments" :key="item.id">
        <view class="user-info">
          <image class="avatar" :src="item.avatarUrl || '/static/default-avatar.png'" mode="aspectFill" />
          <view class="user-meta">
            <text class="nickname">{{ item.nickname || '用户' + item.userId }}</text>
            <text class="time">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>

        <view class="content">
          <text class="text">{{ item.content }}</text>
          <view class="images" v-if="item.images">
            <image v-for="(img, idx) in parseImages(item.images)" :key="idx" :src="img" mode="aspectFill" class="moment-img" @click="previewImage(img, parseImages(item.images))" />
          </view>
        </view>

        <view class="actions">
          <view class="action-btn" :class="{ active: item.liked }" @click="toggleLike(item)">
            <text class="icon">{{ item.liked ? '❤️' : '🤍' }}</text>
            <text class="count">{{ item.likeCount || '点赞' }}</text>
          </view>
          <view class="action-btn" @click="showComment(item)">
            <text class="icon">💬</text>
            <text class="count">{{ item.commentCount || '评论' }}</text>
          </view>
          <view class="action-btn">
            <text class="icon">↗️</text>
            <text class="count">分享</text>
          </view>
        </view>

        <!-- 评论区 -->
        <view class="comments" v-if="item.comments && item.comments.length > 0">
          <view class="comment-item" v-for="c in item.comments" :key="c.id">
            <text class="comment-user">{{ c.nickname }}：</text>
            <text class="comment-text">{{ c.content }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="loading">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getFriendMoments, like, unlike } from '@/api/social.api';

const moments = ref([]);
const loading = ref(false);

onMounted(() => {
  loadMoments();
});

const loadMoments = async () => {
  loading.value = true;
  const res = await getFriendMoments(20);
  if (res.code === 200) {
    moments.value = res.data.map(m => ({ ...m, liked: false, comments: [] }));
  }
  loading.value = false;
};

const toggleLike = async (item: any) => {
  if (item.liked) {
    await unlike('moment', item.id);
    item.likeCount--;
  } else {
    await like('moment', item.id);
    item.likeCount++;
  }
  item.liked = !item.liked;
};

const parseImages = (images: string) => {
  try {
    return JSON.parse(images);
  } catch {
    return images ? [images] : [];
  }
};

const previewImage = (url: string, urls: string[]) => {
  uni.previewImage({ current: url, urls });
};

const formatTime = (time: string) => {
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
  return date.toLocaleDateString();
};

const goToPublish = () => {
  uni.navigateTo({ url: '/pages/social/publish' });
};

const showComment = (item: any) => {
  uni.navigateTo({ url: `/pages/social/comment?targetId=${item.id}` });
};
</script>

<style lang="scss" scoped>
.moments-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  background: #fff;
  padding: 30rpx;
  text-align: center;
  border-bottom: 1rpx solid #eee;
  .title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
  }
}

.publish-box {
  background: #fff;
  margin: 20rpx;
  padding: 25rpx;
  border-radius: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .publish-input {
    flex: 1;
    .placeholder {
      color: #999;
      font-size: 28rpx;
    }
  }
  .publish-actions {
    display: flex;
    gap: 30rpx;
    .action-icon {
      font-size: 40rpx;
    }
  }
}

.moment-list {
  padding: 0 20rpx 20rpx;
}

.moment-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  .avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-right: 20rpx;
  }
  .user-meta {
    .nickname {
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
    }
    .time {
      font-size: 24rpx;
      color: #999;
      margin-top: 5rpx;
    }
  }
}

.content {
  margin-bottom: 20rpx;
  .text {
    font-size: 30rpx;
    color: #333;
    line-height: 1.6;
  }
  .images {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10rpx;
    margin-top: 15rpx;
    .moment-img {
      width: 100%;
      height: 200rpx;
      border-radius: 8rpx;
    }
  }
}

.actions {
  display: flex;
  border-top: 1rpx solid #f0f0f0;
  padding-top: 20rpx;
  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10rpx;
    &.active {
      .icon, .count {
        color: #ff6b6b;
      }
    }
    .icon {
      font-size: 32rpx;
    }
    .count {
      font-size: 26rpx;
      color: #666;
    }
  }
}

.comments {
  background: #f8f8f8;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-top: 20rpx;
  .comment-item {
    margin-bottom: 10rpx;
    &:last-child {
      margin-bottom: 0;
    }
    .comment-user {
      font-size: 28rpx;
      color: #576b95;
    }
    .comment-text {
      font-size: 28rpx;
      color: #333;
    }
  }
}

.load-more {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 26rpx;
}
</style>
