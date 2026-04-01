<template>
  <view class="register-container">
    <view class="header gradient-bg">
      <view class="back-btn" @click="goBack">
        <text class="back-icon">←</text>
      </view>
      <view class="header-content">
        <text class="title">创建账号</text>
        <text class="subtitle">开启你的健康轻瘦之旅</text>
      </view>
    </view>

    <view class="form-card card">
      <view class="form-item">
        <text class="label">手机号</text>
        <input
          class="input"
          type="number"
          placeholder="请输入手机号"
          v-model="form.phone"
          maxlength="11"
        />
      </view>
      <view class="form-item">
        <text class="label">验证码</text>
        <view class="code-row">
          <input
            class="input code-input"
            type="number"
            placeholder="请输入验证码"
            v-model="form.code"
            maxlength="6"
          />
          <button
            class="code-btn"
            :disabled="countdown > 0"
            @click="sendCode"
          >{{ countdown > 0 ? `${countdown}s后重发` : '发送验证码' }}</button>
        </view>
      </view>
      <view class="form-item">
        <text class="label">昵称</text>
        <input
          class="input"
          type="text"
          placeholder="给自己起个好听的名字"
          v-model="form.nickname"
          maxlength="12"
        />
      </view>
      <view class="form-item">
        <text class="label">密码</text>
        <input
          class="input"
          type="password"
          placeholder="请设置密码（6-20位）"
          v-model="form.password"
          maxlength="20"
        />
      </view>

      <!-- 健康信息 -->
      <view class="divider"><text>健康基础信息（可跳过）</text></view>
      <view class="health-row">
        <view class="form-item half">
          <text class="label">身高(cm)</text>
          <input class="input" type="digit" placeholder="165" v-model="form.height" />
        </view>
        <view class="form-item half">
          <text class="label">体重(kg)</text>
          <input class="input" type="digit" placeholder="60" v-model="form.weight" />
        </view>
      </view>
      <view class="health-row">
        <view class="form-item half">
          <text class="label">目标体重(kg)</text>
          <input class="input" type="digit" placeholder="52" v-model="form.targetWeight" />
        </view>
      </view>

      <button class="submit-btn gradient-bg" @click="handleRegister" :loading="loading">
        立即注册
      </button>

      <view class="login-link">
        <text>已有账号？</text>
        <text class="link" @click="goToLogin">去登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '../../store/user'
import { userApi } from '../../api/user.api'

const userStore = useUserStore()
const loading = ref(false)
const countdown = ref(0)

const form = reactive({
  phone: '',
  code: '',
  nickname: '',
  password: '',
  height: '',
  weight: '',
  targetWeight: ''
})

let timer: ReturnType<typeof setInterval> | null = null

async function sendCode() {
  if (!form.phone || form.phone.length !== 11) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  try {
    await userApi.sendSmsCode(form.phone, 'register')
    countdown.value = 60
    timer = setInterval(() => {
      if (countdown.value <= 0) {
        clearInterval(timer!)
        timer = null
      } else {
        countdown.value--
      }
    }, 1000)
    uni.showToast({ title: '验证码已发送', icon: 'success' })
  } catch (e: any) {
    uni.showToast({ title: e.message || '发送失败，请重试', icon: 'none' })
  }
}

async function handleRegister() {
  if (!form.phone || !form.code || !form.nickname || !form.password) {
    uni.showToast({ title: '请填写必填项', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await userStore.register({
      phone: form.phone,
      smsCode: form.code,
      nickname: form.nickname,
      password: form.password
    })
    // 注册成功后，如果有健康信息，补充更新
    if (form.height || form.weight || form.targetWeight) {
      const updateData: Record<string, any> = {}
      if (form.height) updateData.height = Number(form.height)
      if (form.targetWeight) updateData.targetWeight = Number(form.targetWeight)
      if (Object.keys(updateData).length > 0) {
        await userStore.updateProfile(updateData)
      }
      if (form.weight) {
        await userStore.updateWeight(Number(form.weight))
      }
    }
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' })
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e.message || '注册失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goBack() {
  uni.navigateBack()
}

function goToLogin() {
  uni.redirectTo({ url: '/pages/user/login' })
}
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.register-container {
  min-height: 100vh;
  background: $bg-color;

  .header {
    padding: 80rpx 40rpx 60rpx;
    color: #fff;
    position: relative;

    .back-btn {
      position: absolute;
      top: 80rpx;
      left: 30rpx;
      width: 60rpx;
      height: 60rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      .back-icon {
        font-size: 40rpx;
        font-weight: bold;
      }
    }

    .header-content {
      padding-top: 20rpx;
      padding-left: 60rpx;

      .title {
        display: block;
        font-size: $font-size-xl;
        font-weight: bold;
        margin-bottom: 12rpx;
      }

      .subtitle {
        font-size: $font-size-sm;
        opacity: 0.85;
      }
    }
  }

  .form-card {
    margin: -30rpx 30rpx 40rpx;
    padding: 50rpx 40rpx;

    .form-item {
      margin-bottom: 36rpx;

      &.half {
        flex: 1;
      }

      .label {
        display: block;
        font-size: $font-size-sm;
        color: $text-secondary;
        margin-bottom: 12rpx;
        font-weight: 500;
      }

      .input {
        width: 100%;
        height: 90rpx;
        border: 2rpx solid $border-color;
        border-radius: $border-radius;
        padding: 0 24rpx;
        font-size: $font-size-md;
        color: $text-primary;
        background: $bg-color;
        box-sizing: border-box;
      }
    }

    .code-row {
      display: flex;
      gap: 20rpx;
      align-items: center;

      .code-input {
        flex: 1;
      }

      .code-btn {
        width: 220rpx;
        height: 90rpx;
        background: $primary-color;
        color: #fff;
        font-size: $font-size-sm;
        border-radius: $border-radius;
        border: none;
        white-space: nowrap;
        flex-shrink: 0;
        line-height: 90rpx;
        padding: 0;

        &[disabled] {
          background: $text-placeholder;
        }
      }
    }

    .health-row {
      display: flex;
      gap: 24rpx;
    }

    .divider {
      text-align: center;
      margin: 20rpx 0 36rpx;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        top: 50%;
        left: 0;
        right: 0;
        height: 1px;
        background: $border-color;
      }

      text {
        position: relative;
        background: #fff;
        padding: 0 20rpx;
        font-size: $font-size-sm;
        color: $text-placeholder;
      }
    }

    .submit-btn {
      width: 100%;
      height: 100rpx;
      color: #fff;
      font-size: $font-size-md;
      font-weight: 600;
      border-radius: $border-radius;
      border: none;
      margin-top: 10rpx;
    }

    .login-link {
      text-align: center;
      margin-top: 30rpx;
      font-size: $font-size-sm;
      color: $text-secondary;

      .link {
        color: $primary-color;
        font-weight: 500;
      }
    }
  }
}
</style>
