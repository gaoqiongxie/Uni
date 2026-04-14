<template>
  <view class="challenge-page">
    <!-- 顶部标题 -->
    <view class="header">
      <text class="title">打卡挑战</text>
      <text class="subtitle">坚持打卡，养成健康习惯</text>
    </view>

    <!-- 我的挑战 -->
    <view class="section" v-if="myChallenges.length > 0">
      <view class="section-title">
        <text class="title-text">我的挑战</text>
        <text class="count">{{ myChallenges.length }}个进行中</text>
      </view>
      <view class="my-challenge-list">
        <view class="my-challenge-card" v-for="item in myChallenges" :key="item.id">
          <view class="challenge-info">
            <text class="challenge-title">{{ item.title }}</text>
            <view class="progress-bar">
              <view class="progress-fill" :style="{ width: item.completeRate + '%' }"></view>
            </view>
            <view class="challenge-stats">
              <text class="stat">已打卡 {{ item.checkinCount }} 天</text>
              <text class="stat">连续 {{ item.streakDays }} 天</text>
            </view>
          </view>
          <view class="checkin-btn" :class="{ checked: isCheckedToday(item) }" @click="handleCheckin(item)">
            <text class="btn-text">{{ isCheckedToday(item) ? '已打卡' : '打卡' }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 推荐挑战 -->
    <view class="section">
      <view class="section-title">
        <text class="title-text">推荐挑战</text>
      </view>
      <view class="challenge-grid">
        <view class="challenge-item" v-for="item in challenges" :key="item.id" @click="showDetail(item)">
          <view class="challenge-icon" :class="item.type">
            <text class="iconfont">{{ getTypeIcon(item.type) }}</text>
          </view>
          <view class="challenge-content">
            <text class="item-title">{{ item.title }}</text>
            <text class="item-desc">{{ item.durationDays }}天 · {{ item.joinedCount }}人参与</text>
          </view>
          <view class="join-btn" @click.stop="handleJoin(item)">
            <text class="btn-text">参加</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { challengeApi } from '@/api/challenge.api';
import type { Challenge, UserChallenge } from '@/types/challenge';

const challenges = ref<Challenge[]>([]);
const myChallenges = ref<UserChallenge[]>([]);

onMounted(() => {
  loadData();
});

const loadData = async () => {
  const [listRes, myRes] = await Promise.all([
    challengeApi.getList(),
    challengeApi.getMyChallenges()
  ]);
  challenges.value = listRes.data || [];
  myChallenges.value = myRes.data || [];
};

const getTypeIcon = (type: string) => {
  const icons: Record<string, string> = {
    meal: '🍽️',
    exercise: '🏃',
    water: '💧',
    weight: '⚖️',
    custom: '🎯'
  };
  return icons[type] || '🎯';
};

const isCheckedToday = (item: UserChallenge) => {
  // 简化判断，实际应根据打卡记录
  return false;
};

const handleCheckin = async (item: UserChallenge) => {
  try {
    await challengeApi.checkin(item.challengeId);
    uni.showToast({ title: '打卡成功！', icon: 'success' });
    loadData();
  } catch (e) {
    uni.showToast({ title: '打卡失败', icon: 'none' });
  }
};

const handleJoin = async (item: Challenge) => {
  try {
    await challengeApi.join(item.id);
    uni.showToast({ title: '参加成功！', icon: 'success' });
    loadData();
  } catch (e) {
    uni.showToast({ title: '参加失败', icon: 'none' });
  }
};

const showDetail = (item: Challenge) => {
  uni.showModal({
    title: item.title,
    content: item.description || '暂无描述',
    showCancel: false
  });
};
</script>

<style lang="scss" scoped>
.challenge-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 40rpx;
  color: #fff;

  .title {
    font-size: 48rpx;
    font-weight: bold;
    display: block;
  }

  .subtitle {
    font-size: 28rpx;
    opacity: 0.9;
    margin-top: 10rpx;
    display: block;
  }
}

.section {
  margin-top: 30rpx;
  padding: 0 30rpx;

  .section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .title-text {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .count {
      font-size: 24rpx;
      color: #667eea;
    }
  }
}

.my-challenge-list {
  .my-challenge-card {
    background: #fff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);

    .challenge-info {
      flex: 1;

      .challenge-title {
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 15rpx;
        display: block;
      }

      .progress-bar {
        height: 12rpx;
        background: #eee;
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

      .challenge-stats {
        display: flex;
        gap: 30rpx;

        .stat {
          font-size: 24rpx;
          color: #666;
        }
      }
    }

    .checkin-btn {
      width: 120rpx;
      height: 60rpx;
      background: #667eea;
      border-radius: 30rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-left: 20rpx;

      &.checked {
        background: #ccc;
      }

      .btn-text {
        color: #fff;
        font-size: 26rpx;
        font-weight: bold;
      }
    }
  }
}

.challenge-grid {
  .challenge-item {
    background: #fff;
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.05);

    .challenge-icon {
      width: 80rpx;
      height: 80rpx;
      border-radius: 20rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 40rpx;
      margin-right: 20rpx;

      &.meal { background: #fff3e0; }
      &.exercise { background: #e8f5e9; }
      &.water { background: #e3f2fd; }
      &.weight { background: #fce4ec; }
      &.custom { background: #f3e5f5; }
    }

    .challenge-content {
      flex: 1;

      .item-title {
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 8rpx;
        display: block;
      }

      .item-desc {
        font-size: 24rpx;
        color: #999;
      }
    }

    .join-btn {
      width: 100rpx;
      height: 50rpx;
      background: #667eea;
      border-radius: 25rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      .btn-text {
        color: #fff;
        font-size: 24rpx;
      }
    }
  }
}
</style>
