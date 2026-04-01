<template>
  <view class="login-container">
    <view class="login-header">
      <text class="app-icon">🥗</text>
      <text class="app-name">轻瘦</text>
      <text class="app-slogan">科学减脂，轻松生活</text>
    </view>

    <view class="login-form card">
      <view class="form-item">
        <text class="label">手机号</text>
        <input
          class="input"
          v-model="formData.phone"
          placeholder="请输入手机号"
          maxlength="11"
          type="number"
        />
      </view>

      <view class="form-item">
        <text class="label">密码</text>
        <input
          class="input"
          v-model="formData.password"
          placeholder="请输入密码"
          :password="true"
        />
      </view>

      <button class="submit-btn" :disabled="loading" @click="handleLogin">
        {{ loading ? '登录中...' : '登录' }}
      </button>

      <view class="form-footer">
        <text class="link" @click="navigateToRegister">没有账号？立即注册</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const loading = ref(false)

const formData = reactive({
  phone: '',
  password: ''
})

async function handleLogin() {
  if (!formData.phone || !formData.password) {
    uni.showToast({ title: '请输入手机号和密码', icon: 'none' })
    return
  }
  if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' })
    return
  }
  try {
    loading.value = true
    await userStore.login({ phone: formData.phone, password: formData.password })
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1000)
  } catch (e) {
    // 错误已在 request 中 toast
  } finally {
    loading.value = false
  }
}

function navigateToRegister() {
  uni.navigateTo({ url: '/pages/user/register' })
}
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.login-container {
  min-height: 100vh;
  background: linear-gradient(160deg, $primary-color 0%, $primary-dark 100%);
  padding: 120rpx 40rpx 40rpx;

  .login-header {
    text-align: center;
    margin-bottom: 80rpx;
    color: #fff;

    .app-icon {
      display: block;
      font-size: 100rpx;
      margin-bottom: 20rpx;
    }

    .app-name {
      display: block;
      font-size: 60rpx;
      font-weight: bold;
      margin-bottom: 16rpx;
    }

    .app-slogan {
      font-size: 28rpx;
      opacity: 0.85;
    }
  }

  .login-form {
    background: #fff;
    border-radius: 24rpx;
    padding: 60rpx 40rpx;

    .form-item {
      margin-bottom: 40rpx;

      .label {
        display: block;
        font-size: $font-size-sm;
        color: $text-secondary;
        margin-bottom: 12rpx;
      }

      .input {
        width: 100%;
        height: 88rpx;
        border: 2rpx solid $border-color;
        border-radius: $border-radius;
        padding: 0 24rpx;
        font-size: $font-size;
        background: $bg-gray;
        box-sizing: border-box;
      }
    }

    .submit-btn {
      width: 100%;
      height: 96rpx;
      background: linear-gradient(135deg, $primary-color 0%, $primary-dark 100%);
      color: #fff;
      border-radius: 48rpx;
      font-size: $font-size-md;
      font-weight: 500;
      border: none;
      margin-top: 20rpx;

      &[disabled] {
        opacity: 0.6;
      }
    }

    .form-footer {
      text-align: center;
      margin-top: 40rpx;

      .link {
        color: $primary-color;
        font-size: $font-size-sm;
      }
    }
  }
}
</style>
