<template>
  <view class="challenge-page">
    <view class="page-header">
      <text class="title">打卡挑战</text>
      <text class="subtitle">坚持打卡，养成健康习惯</text>
    </view>

    <!-- 我的挑战 -->
    <view class="section my-challenges" v-if="myChallenges.length > 0">
      <view class="section-header">
        <text class="section-title">我的挑战</text>
      </view>
      <view class="challenge-list">
        <view class="challenge-card active" v-for="item in myChallenges" :key="item.id" @click="goToDetail(item)">
          <view class="card-header">
            <text class="challenge-name">{{ item.title }}</text>
            <view class="status-tag" :class="item.todayChecked ? 'checked' : 'unchecked'">
              {{ item.todayChecked ? '已打卡' : '未打卡' }}
            </view>
          </view>
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: item.completeRate + '%' }"></view>
          </view>
          <view class="progress-info">
            <text class="progress-text">进度 {{ item.completeRate }}%</text>
            <text class="days-text">{{ item.checkinCount }}/{{ item.durationDays }}天</text>
          </view>
          <view class="streak-info" v-if="item.streakDays > 0">
            <text class="fire-icon">🔥</text>
            <text class="streak-text">连续打卡 {{ item.streakDays }} 天</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 推荐挑战 -->
    <view class="section recommend-challenges">
      <view class="section-header">
        <text class="section-title">推荐挑战</text>
      </view>
      <view class="challenge-grid">
        <view class="challenge-item" v-for="item in challenges" :key="item.id">
          <view class="item-content">
            <text class="item-title">{{ item.title }}</text>
            <text class="item-desc">{{ item.description }}</text>
            <view class="item-meta">
              <text class="meta-tag">{{ item.typeName }}</text>
              <text class="meta-days">{{ item.durationDays }}天</text>
            </view>
            <view class="item-stats">
              <text class="joined-count">{{ item.joinedCount }}人参与</text>
            </view>
          </view>
          <button class="join-btn" @click="joinChallenge(item.id)">立即加入</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getChallenges, getMyChallenges, joinChallenge as apiJoinChallenge } from '@/api/challenge.api';

const challenges = ref([]);
const myChallenges = ref([]);

onMounted(() => {
  loadData();
});

const loadData = async () => {
  const [listRes, myRes] = await Promise.all([
    getChallenges(),
    getMyChallenges()
  ]);
  if (listRes.code === 200) challenges.value = listRes.data;
  if (myRes.code === 200) myChallenges.value = myRes.data;
};

const joinChallenge = async (id: number) => {
  const res = await apiJoinChallenge(id);
  if (res.code === 200) {
    uni.showToast({ title: '加入成功', icon: 'success' });
    loadData();
  }
};

const goToDetail = (item: any) => {
  uni.navigateTo({
    url: `/pages/challenge/challenge-detail?id=${item.challengeId}`
  });
};
</script>

<style lang="scss" scoped>
.challenge-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20rpx;
}

.page-header {
  padding: 40rpx 20rpx;
  text-align: center;
  .title {
    font-size: 48rpx;
    font-weight: bold;
    color: #fff;
  }
  .subtitle {
    font-size: 28rpx;
    color: rgba(255,255,255,0.8);
    margin-top: 10rpx;
  }
}

.section {
  margin-bottom: 30rpx;
}

.section-header {
  margin-bottom: 20rpx;
  .section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #fff;
  }
}

.challenge-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    .challenge-name {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    .status-tag {
      padding: 6rpx 16rpx;
      border-radius: 20rpx;
      font-size: 24rpx;
      &.checked {
        background: #52c41a;
        color: #fff;
      }
      &.unchecked {
        background: #f5f5f5;
        color: #999;
      }
    }
  }
}

.progress-bar {
  height: 12rpx;
  background: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 15rpx;
  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #667eea, #764ba2);
    border-radius: 6rpx;
    transition: width 0.3s;
  }
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 15rpx;
}

.streak-info {
  display: flex;
  align-items: center;
  .fire-icon {
    font-size: 32rpx;
    margin-right: 10rpx;
  }
  .streak-text {
    font-size: 26rpx;
    color: #ff6b6b;
    font-weight: bold;
  }
}

.challenge-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.challenge-item {
  background: #fff;
  border-radius: 20rpx;
  padding: 25rpx;
  .item-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 10rpx;
  }
  .item-desc {
    font-size: 24rpx;
    color: #666;
    margin-bottom: 15rpx;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .item-meta {
    display: flex;
    gap: 15rpx;
    margin-bottom: 15rpx;
    .meta-tag {
      background: #e6f7ff;
      color: #1890ff;
      padding: 4rpx 12rpx;
      border-radius: 8rpx;
      font-size: 22rpx;
    }
    .meta-days {
      background: #f6ffed;
      color: #52c41a;
      padding: 4rpx 12rpx;
      border-radius: 8rpx;
      font-size: 22rpx;
    }
  }
  .item-stats {
    margin-bottom: 15rpx;
    .joined-count {
      font-size: 24rpx;
      color: #999;
    }
  }
}

.join-btn {
  background: linear-gradient(90deg, #667eea, #764ba2);
  color: #fff;
  border: none;
  border-radius: 30rpx;
  font-size: 26rpx;
  padding: 15rpx 0;
}
</style>
