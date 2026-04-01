<template>
  <view class="login-container">
    <view class="login-header">
      <text class="app-icon">🥗</text>
      <text class="app-name">轻瘦</text>
      <text class="app-slogan">科学减脂，轻松生活</text>
    </view>

    <view class="login-form card">
      <!-- 登录方式 Tab -->
      <view class="login-tabs">
        <view
          class="tab-item"
          :class="{ active: loginType === 'password' }"
          @click="loginType = 'password'"
        >
          <text>密码登录</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: loginType === 'sms' }"
          @click="loginType = 'sms'"
        >
          <text>验证码登录</text>
        </view>
        <view class="tab-indicator" :class="{ right: loginType === 'sms' }"></view>
      </view>

      <!-- 手机号（共用） -->
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

      <!-- 密码登录 -->
      <template v-if="loginType === 'password'">
        <view class="form-item">
          <text class="label">密码</text>
          <input
            class="input"
            v-model="formData.password"
            placeholder="请输入密码"
            :password="true"
          />
        </view>
      </template>

      <!-- 验证码登录 -->
      <template v-else>
        <view class="form-item sms-item">
          <text class="label">验证码</text>
          <view class="sms-row">
            <input
              class="input sms-input"
              v-model="formData.smsCode"
              placeholder="请输入验证码"
              maxlength="6"
              type="number"
            />
            <button
              class="sms-btn"
              :disabled="smsCooldown > 0 || !isPhoneValid"
              @click="handleSendSms"
            >
              {{ smsCooldown > 0 ? `${smsCooldown}s` : '获取验证码' }}
            </button>
          </view>
        </view>
      </template>

      <!-- 登录按钮 -->
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
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '../../store/user'
import { userApi } from '../../api/user.api'

const userStore = useUserStore()
const loading = ref(false)
const loginType = ref<'password' | 'sms'>('password')
const smsCooldown = ref(0)
let smsTimer: ReturnType<typeof setInterval> | null = null

const formData = reactive({
  phone: '',
  password: '',
  smsCode: ''
})

const isPhoneValid = computed(() => /^1[3-9]\d{9}$/.test(formData.phone))

function validatePhone(): boolean {
  if (!formData.phone) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return false
  }
  if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' })
    return false
  }
  return true
}

async function handleSendSms() {
  if (!validatePhone()) return
  try {
    await userApi.sendSmsCode(formData.phone, 'login')
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    // 开始倒计时 60s
    smsCooldown.value = 60
    smsTimer = setInterval(() => {
      smsCooldown.value--
      if (smsCooldown.value <= 0) {
        clearInterval(smsTimer!)
        smsTimer = null
      }
    }, 1000)
  } catch (e) {
    // 错误已在 request 中 toast
  }
}

async function handleLogin() {
  if (!validatePhone()) return

  try {
    loading.value = true
    if (loginType.value === 'password') {
      if (!formData.password) {
        uni.showToast({ title: '请输入密码', icon: 'none' })
        return
      }
      await userStore.login({ phone: formData.phone, password: formData.password })
    } else {
      if (!formData.smsCode) {
        uni.showToast({ title: '请输入验证码', icon: 'none' })
        return
      }
      await userStore.loginByPhone({ phone: formData.phone, smsCode: formData.smsCode })
    }
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

    .login-tabs {
      display: flex;
      position: relative;
      margin-bottom: 40rpx;
      border-bottom: 2rpx solid $border-color;

      .tab-item {
        flex: 1;
        text-align: center;
        padding: 20rpx 0;
        font-size: $font-size-md;
        color: $text-secondary;
        transition: color 0.3s;

        &.active {
          color: $primary-color;
          font-weight: 600;
        }
      }

      .tab-indicator {
        position: absolute;
        bottom: -2rpx;
        left: 0;
        width: 50%;
        height: 4rpx;
        background: $primary-color;
        border-radius: 2rpx;
        transition: transform 0.3s ease;

        &.right {
          transform: translateX(100%);
        }
      }
    }

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

    .sms-item {
      .sms-row {
        display: flex;
        align-items: center;
        gap: 16rpx;

        .sms-input {
          flex: 1;
        }

        .sms-btn {
          flex-shrink: 0;
          width: 220rpx;
          height: 88rpx;
          line-height: 88rpx;
          padding: 0;
          background: $primary-light;
          color: $primary-color;
          font-size: $font-size-sm;
          border: 2rpx solid $primary-color;
          border-radius: $border-radius;
          white-space: nowrap;

          &[disabled] {
            opacity: 0.5;
            background: $bg-gray;
            color: $text-placeholder;
            border-color: $border-color;
          }
        }
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
