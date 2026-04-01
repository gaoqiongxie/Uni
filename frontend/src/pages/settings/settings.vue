<template>
  <view class="settings-container">
    <!-- 头像区域 -->
    <view class="profile-section gradient-bg" @click="goToProfile">
      <view class="avatar">
        <text class="avatar-text">{{ userInfo?.nickname?.charAt(0) || '我' }}</text>
      </view>
      <view class="profile-info">
        <text class="nickname">{{ userInfo?.nickname || '未设置昵称' }}</text>
        <text class="phone">{{ maskPhone(userInfo?.phone) }}</text>
      </view>
      <text class="arrow-right">›</text>
    </view>

    <!-- 目标设置 -->
    <view class="group-card card">
      <text class="group-title">目标设置</text>
      <view class="setting-item" @click="openGoalModal('calorie')">
        <text class="s-label">每日热量目标</text>
        <view class="s-right">
          <text class="s-val">{{ userInfo?.calorieGoal || 1400 }} kcal</text>
          <text class="s-arrow">›</text>
        </view>
      </view>
      <view class="setting-item" @click="openGoalModal('weight')">
        <text class="s-label">目标体重</text>
        <view class="s-right">
          <text class="s-val">{{ userInfo?.targetWeight || '--' }} kg</text>
          <text class="s-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 提醒设置 -->
    <view class="group-card card">
      <text class="group-title">提醒设置</text>
      <view class="setting-item">
        <text class="s-label">早餐提醒</text>
        <switch
          :checked="settings.breakfastRemind"
          color="#667eea"
          @change="onSwitchChange('breakfastRemind', $event)"
        />
      </view>
      <view class="setting-item">
        <text class="s-label">午餐提醒</text>
        <switch
          :checked="settings.lunchRemind"
          color="#667eea"
          @change="onSwitchChange('lunchRemind', $event)"
        />
      </view>
      <view class="setting-item">
        <text class="s-label">晚餐提醒</text>
        <switch
          :checked="settings.dinnerRemind"
          color="#667eea"
          @change="onSwitchChange('dinnerRemind', $event)"
        />
      </view>
      <view class="setting-item">
        <text class="s-label">体重提醒（每天早上）</text>
        <switch
          :checked="settings.weightRemind"
          color="#667eea"
          @change="onSwitchChange('weightRemind', $event)"
        />
      </view>
    </view>

    <!-- 关于 -->
    <view class="group-card card">
      <text class="group-title">关于</text>
      <view class="setting-item" @click="showAbout">
        <text class="s-label">关于轻瘦</text>
        <text class="s-arrow">›</text>
      </view>
      <view class="setting-item" @click="checkUpdate">
        <text class="s-label">检查更新</text>
        <view class="s-right">
          <text class="s-val version">v1.0.0</text>
          <text class="s-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" @click="handleLogout">
      <text>退出登录</text>
    </view>

    <!-- 目标设置弹窗 -->
    <view class="modal-mask" v-if="showGoalModal" @click.self="closeGoalModal">
      <view class="modal-panel">
        <text class="modal-title">{{ currentGoal === 'calorie' ? '修改每日热量目标' : '修改目标体重' }}</text>
        <view class="modal-input-wrap">
          <input
            class="modal-input"
            type="digit"
            v-model="goalValue"
            :placeholder="currentGoal === 'calorie' ? '建议 1200-1600' : '输入目标体重(kg)'"
          />
          <text class="modal-unit">{{ currentGoal === 'calorie' ? 'kcal' : 'kg' }}</text>
        </view>
        <view class="modal-actions">
          <button class="modal-cancel" @click="closeGoalModal">取消</button>
          <button class="modal-confirm gradient-bg" @click="saveGoal">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useUserStore } from '../../store/user'
import { getStorage, setStorage } from '../../utils/storage'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const showGoalModal = ref(false)
const currentGoal = ref<'calorie' | 'weight'>('calorie')
const goalValue = ref('')

const settings = reactive({
  breakfastRemind: false,
  lunchRemind: false,
  dinnerRemind: false,
  weightRemind: false
})

function maskPhone(phone?: string): string {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

function goToProfile() {
  uni.navigateTo({ url: '/pages/user/profile' })
}

function onSwitchChange(key: keyof typeof settings, e: any) {
  settings[key] = e.detail.value
  setStorage(`remind_${key}`, settings[key])
}

function openGoalModal(type: 'calorie' | 'weight') {
  currentGoal.value = type
  goalValue.value = type === 'calorie'
    ? String(userInfo.value?.calorieGoal || 1400)
    : String(userInfo.value?.targetWeight || '')
  showGoalModal.value = true
}

function closeGoalModal() {
  showGoalModal.value = false
}

async function saveGoal() {
  const val = Number(goalValue.value)
  if (isNaN(val) || val <= 0) {
    uni.showToast({ title: '请输入有效数值', icon: 'none' })
    return
  }
  try {
    if (currentGoal.value === 'calorie') {
      await userStore.updateProfile({ calorieGoal: val })
    } else {
      await userStore.updateProfile({ targetWeight: val })
    }
    uni.showToast({ title: '保存成功', icon: 'success' })
    closeGoalModal()
  } catch (e: any) {
    uni.showToast({ title: e.message || '保存失败', icon: 'none' })
  }
}

function showAbout() {
  uni.showModal({
    title: '关于轻瘦',
    content: '轻瘦 v1.0.0\n科学健康的减脂陪伴应用\n不节食、不过度运动\n用科学的方式享受轻松的健康生活 🌿',
    showCancel: false,
    confirmText: '好的'
  })
}

function checkUpdate() {
  uni.showToast({ title: '已是最新版本', icon: 'success' })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定退出登录？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/user/login' })
      }
    }
  })
}

onMounted(() => {
  settings.breakfastRemind = getStorage('remind_breakfastRemind') || false
  settings.lunchRemind = getStorage('remind_lunchRemind') || false
  settings.dinnerRemind = getStorage('remind_dinnerRemind') || false
  settings.weightRemind = getStorage('remind_weightRemind') || false
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.settings-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 60rpx;

  .profile-section {
    display: flex;
    align-items: center;
    padding: 60rpx 40rpx;
    color: #fff;
    gap: 30rpx;

    .avatar {
      width: 110rpx;
      height: 110rpx;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      .avatar-text {
        font-size: 50rpx;
        font-weight: bold;
      }
    }

    .profile-info {
      flex: 1;

      .nickname {
        display: block;
        font-size: $font-size-md;
        font-weight: 600;
        margin-bottom: 8rpx;
      }

      .phone {
        font-size: $font-size-sm;
        opacity: 0.85;
      }
    }

    .arrow-right {
      font-size: 40rpx;
      opacity: 0.7;
    }
  }

  .group-card {
    margin: 24rpx 30rpx 0;
    padding: 0 30rpx;

    .group-title {
      display: block;
      font-size: $font-size-sm;
      color: $text-secondary;
      padding: 24rpx 0 12rpx;
      font-weight: 500;
    }

    .setting-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 100rpx;
      border-bottom: 1rpx solid $border-color;

      &:last-child { border-bottom: none; }

      .s-label {
        font-size: $font-size-md;
        color: $text-primary;
      }

      .s-right {
        display: flex;
        align-items: center;
        gap: 10rpx;
      }

      .s-val {
        font-size: $font-size-sm;
        color: $text-secondary;

        &.version {
          background: rgba(102, 126, 234, 0.1);
          color: $primary-color;
          padding: 6rpx 16rpx;
          border-radius: 20rpx;
          font-size: $font-size-sm;
        }
      }

      .s-arrow {
        font-size: $font-size-lg;
        color: $text-placeholder;
      }
    }
  }

  .logout-btn {
    margin: 48rpx 30rpx 0;
    background: #fff;
    border-radius: $border-radius;
    height: 100rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: $font-size-md;
    color: #ff4d4f;
    font-weight: 500;
    box-shadow: $shadow-sm;
  }

  /* 弹窗 */
  .modal-mask {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 999;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 60rpx;

    .modal-panel {
      width: 100%;
      background: #fff;
      border-radius: $border-radius-lg;
      padding: 50rpx 40rpx;

      .modal-title {
        display: block;
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
        text-align: center;
        margin-bottom: 36rpx;
      }

      .modal-input-wrap {
        display: flex;
        align-items: center;
        border: 2rpx solid $border-color;
        border-radius: $border-radius;
        padding: 0 24rpx;
        margin-bottom: 36rpx;

        .modal-input {
          flex: 1;
          height: 90rpx;
          font-size: $font-size-lg;
          color: $text-primary;
        }

        .modal-unit {
          font-size: $font-size-sm;
          color: $text-secondary;
        }
      }

      .modal-actions {
        display: flex;
        gap: 20rpx;

        .modal-cancel, .modal-confirm {
          flex: 1;
          height: 90rpx;
          border-radius: $border-radius;
          border: none;
          font-size: $font-size-md;
          font-weight: 500;
        }

        .modal-cancel {
          background: $bg-color;
          color: $text-secondary;
        }

        .modal-confirm { color: #fff; }
      }
    }
  }
}
</style>
