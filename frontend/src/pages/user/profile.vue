<template>
  <view class="profile-container">
    <!-- 头部信息卡 -->
    <view class="hero gradient-bg">
      <view class="avatar-wrap">
        <text class="avatar-text">{{ userInfo?.nickname?.charAt(0) || '我' }}</text>
      </view>
      <text class="nickname">{{ userInfo?.nickname }}</text>
      <text class="phone">{{ maskPhone(userInfo?.phone) }}</text>
    </view>

    <!-- 健康数据卡 -->
    <view class="health-card card">
      <view class="health-item">
        <text class="h-value">{{ userInfo?.currentWeight || '--' }}</text>
        <text class="h-label">当前体重(kg)</text>
      </view>
      <view class="divider-v"></view>
      <view class="health-item">
        <text class="h-value">{{ userInfo?.targetWeight || '--' }}</text>
        <text class="h-label">目标体重(kg)</text>
      </view>
      <view class="divider-v"></view>
      <view class="health-item">
        <text class="h-value">{{ userInfo?.bmi || '--' }}</text>
        <text class="h-label">BMI</text>
      </view>
    </view>

    <!-- 编辑表单 -->
    <view class="form-card card">
      <view class="form-title">个人信息</view>
      <view class="form-item" v-for="field in fields" :key="field.key">
        <text class="label">{{ field.label }}</text>
        <input
          class="input"
          :type="field.inputType || 'text'"
          :placeholder="field.placeholder"
          v-model="(form as any)[field.key]"
        />
      </view>
      <view class="form-item">
        <text class="label">性别</text>
        <view class="gender-row">
          <view
            class="gender-btn"
            :class="{ active: form.gender === 1 }"
            @click="form.gender = 1"
          ><text>女</text></view>
          <view
            class="gender-btn"
            :class="{ active: form.gender === 0 }"
            @click="form.gender = 0"
          ><text>男</text></view>
        </view>
      </view>
      <button class="save-btn gradient-bg" @click="handleSave" :loading="loading">
        保存修改
      </button>
    </view>

    <!-- 操作区 -->
    <view class="action-list card">
      <view class="action-item" @click="goToSettings">
        <text class="a-icon">⚙️</text>
        <text class="a-label">设置</text>
        <text class="a-arrow">›</text>
      </view>
      <view class="action-item danger" @click="handleLogout">
        <text class="a-icon">🚪</text>
        <text class="a-label">退出登录</text>
        <text class="a-arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const loading = ref(false)

const form = reactive({
  nickname: '',
  height: undefined as number | undefined,
  currentWeight: undefined as number | undefined,
  targetWeight: undefined as number | undefined,
  age: undefined as number | undefined,
  gender: undefined as number | undefined
})

const fields = [
  { key: 'nickname', label: '昵称', placeholder: '请输入昵称', inputType: 'text' },
  { key: 'height', label: '身高(cm)', placeholder: '如: 165', inputType: 'digit' },
  { key: 'currentWeight', label: '当前体重(kg)', placeholder: '如: 60', inputType: 'digit' },
  { key: 'targetWeight', label: '目标体重(kg)', placeholder: '如: 52', inputType: 'digit' },
  { key: 'age', label: '年龄', placeholder: '如: 28', inputType: 'number' }
]

onMounted(async () => {
  if (!userInfo.value) {
    await userStore.refreshUserInfo()
  }
  if (userInfo.value) {
    form.nickname = userInfo.value.nickname || ''
    form.height = userInfo.value.height
    form.currentWeight = userInfo.value.currentWeight
    form.targetWeight = userInfo.value.targetWeight
    form.age = userInfo.value.age
    form.gender = userInfo.value.gender
  }
})

function maskPhone(phone?: string): string {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

async function handleSave() {
  loading.value = true
  try {
    await userStore.updateProfile(form)
    uni.showToast({ title: '保存成功', icon: 'success' })
  } catch (e: any) {
    uni.showToast({ title: e.message || '保存失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goToSettings() {
  uni.navigateTo({ url: '/pages/settings/settings' })
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
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.profile-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 60rpx;

  .hero {
    padding: 80rpx 40rpx 60rpx;
    color: #fff;
    text-align: center;

    .avatar-wrap {
      width: 140rpx;
      height: 140rpx;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 24rpx;

      .avatar-text {
        font-size: 60rpx;
        font-weight: bold;
      }
    }

    .nickname {
      display: block;
      font-size: $font-size-lg;
      font-weight: bold;
      margin-bottom: 10rpx;
    }

    .phone {
      font-size: $font-size-sm;
      opacity: 0.8;
    }
  }

  .health-card {
    display: flex;
    align-items: center;
    margin: -30rpx 30rpx 24rpx;
    padding: 36rpx 20rpx;

    .health-item {
      flex: 1;
      text-align: center;

      .h-value {
        display: block;
        font-size: $font-size-xl;
        font-weight: bold;
        color: $primary-color;
        margin-bottom: 8rpx;
      }

      .h-label {
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }

    .divider-v {
      width: 2rpx;
      height: 60rpx;
      background: $border-color;
    }
  }

  .form-card {
    margin: 0 30rpx 24rpx;
    padding: 40rpx;

    .form-title {
      font-size: $font-size-md;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 30rpx;
    }

    .form-item {
      margin-bottom: 30rpx;

      .label {
        display: block;
        font-size: $font-size-sm;
        color: $text-secondary;
        margin-bottom: 10rpx;
      }

      .input {
        width: 100%;
        height: 88rpx;
        border: 2rpx solid $border-color;
        border-radius: $border-radius;
        padding: 0 24rpx;
        font-size: $font-size-md;
        color: $text-primary;
        background: $bg-color;
        box-sizing: border-box;
      }

      .gender-row {
        display: flex;
        gap: 20rpx;

        .gender-btn {
          flex: 1;
          height: 88rpx;
          border: 2rpx solid $border-color;
          border-radius: $border-radius;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: $font-size-md;
          color: $text-secondary;

          &.active {
            border-color: $primary-color;
            color: $primary-color;
            background: rgba(102, 126, 234, 0.05);
          }
        }
      }
    }

    .save-btn {
      width: 100%;
      height: 96rpx;
      color: #fff;
      font-size: $font-size-md;
      font-weight: 600;
      border-radius: $border-radius;
      border: none;
    }
  }

  .action-list {
    margin: 0 30rpx;
    padding: 0 24rpx;

    .action-item {
      display: flex;
      align-items: center;
      height: 100rpx;
      border-bottom: 1rpx solid $border-color;

      &:last-child {
        border-bottom: none;
      }

      &.danger .a-label {
        color: #ff4d4f;
      }

      .a-icon {
        font-size: 36rpx;
        margin-right: 20rpx;
      }

      .a-label {
        flex: 1;
        font-size: $font-size-md;
        color: $text-primary;
      }

      .a-arrow {
        font-size: $font-size-lg;
        color: $text-placeholder;
      }
    }
  }
}
</style>
