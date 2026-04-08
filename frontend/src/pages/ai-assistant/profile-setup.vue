<template>
  <view class="profile-setup-page">
    <!-- 进度指示器 -->
    <view class="progress-bar">
      <view class="progress-fill" :style="{ width: progress + '%' }"></view>
      <text class="progress-text">步骤 {{ currentStep }}/{{ totalSteps }}</text>
    </view>

    <!-- 步骤1: 基本信息 -->
    <view v-if="currentStep === 1" class="step-content">
      <view class="step-title">让我们先了解您的基本情况</view>
      <view class="step-desc">这些信息将帮助AI为您制定个性化方案</view>

      <view class="form-group">
        <text class="label">性别</text>
        <view class="gender-options">
          <view
            class="gender-option"
            :class="{ active: form.gender === 1 }"
            @click="form.gender = 1"
          >
            <text class="gender-icon">👨</text>
            <text>男</text>
          </view>
          <view
            class="gender-option"
            :class="{ active: form.gender === 0 }"
            @click="form.gender = 0"
          >
            <text class="gender-icon">👩</text>
            <text>女</text>
          </view>
        </view>
      </view>

      <view class="form-group">
        <text class="label">年龄</text>
        <input
          v-model="form.age"
          type="number"
          class="input"
          placeholder="请输入年龄"
        />
      </view>

      <view class="form-group">
        <text class="label">身高 (cm)</text>
        <input
          v-model="form.height"
          type="digit"
          class="input"
          placeholder="例如: 165"
        />
      </view>

      <view class="form-group">
        <text class="label">当前体重 (kg)</text>
        <input
          v-model="form.currentWeight"
          type="digit"
          class="input"
          placeholder="例如: 65"
        />
      </view>

      <view class="form-group">
        <text class="label">目标体重 (kg)</text>
        <input
          v-model="form.targetWeight"
          type="digit"
          class="input"
          placeholder="例如: 55"
        />
        <text v-if="bmi > 0" class="bmi-hint">
          当前BMI: {{ bmi }} ({{ bmiCategory }})
        </text>
      </view>
    </view>

    <!-- 步骤2: 生活习惯 -->
    <view v-if="currentStep === 2" class="step-content">
      <view class="step-title">您的生活习惯</view>
      <view class="step-desc">了解您的日常活动量</view>

      <view class="form-group">
        <text class="label">您觉得自己属于哪种体型?</text>
        <view class="option-list">
          <view
            v-for="opt in BODY_TYPE_OPTIONS"
            :key="opt.value"
            class="option-item"
            :class="{ active: form.bodyType === opt.value }"
            @click="form.bodyType = opt.value"
          >
            <view class="option-main">
              <text class="option-label">{{ opt.label }}</text>
              <text class="option-desc">{{ opt.desc }}</text>
            </view>
            <text v-if="form.bodyType === opt.value" class="check">✓</text>
          </view>
        </view>
      </view>

      <view class="form-group">
        <text class="label">日常活动量</text>
        <view class="option-list">
          <view
            v-for="opt in ACTIVITY_LEVEL_OPTIONS"
            :key="opt.value"
            class="option-item"
            :class="{ active: form.activityLevel === opt.value }"
            @click="form.activityLevel = opt.value"
          >
            <view class="option-main">
              <text class="option-label">{{ opt.label }}</text>
              <text class="option-desc">{{ opt.desc }}</text>
            </view>
            <text v-if="form.activityLevel === opt.value" class="check">✓</text>
          </view>
        </view>
      </view>

      <view class="form-group">
        <text class="label">平均睡眠时长 (小时)</text>
        <input
          v-model="form.sleepHours"
          type="digit"
          class="input"
          placeholder="例如: 7.5"
        />
      </view>
    </view>

    <!-- 步骤3: 运动情况 -->
    <view v-if="currentStep === 3" class="step-content">
      <view class="step-title">运动经验</view>
      <view class="step-desc">帮助我们制定适合您的运动计划</view>

      <view class="form-group">
        <text class="label">您的运动经验</text>
        <view class="option-list">
          <view
            v-for="opt in EXERCISE_EXPERIENCE_OPTIONS"
            :key="opt.value"
            class="option-item"
            :class="{ active: form.exerciseExperience === opt.value }"
            @click="form.exerciseExperience = opt.value"
          >
            <view class="option-main">
              <text class="option-label">{{ opt.label }}</text>
              <text class="option-desc">{{ opt.desc }}</text>
            </view>
            <text v-if="form.exerciseExperience === opt.value" class="check">✓</text>
          </view>
        </view>
      </view>

      <view class="form-group">
        <text class="label">是否有健身房条件?</text>
        <view class="gender-options">
          <view
            class="gender-option"
            :class="{ active: form.hasGymAccess === 1 }"
            @click="form.hasGymAccess = 1"
          >
            <text>有</text>
          </view>
          <view
            class="gender-option"
            :class="{ active: form.hasGymAccess === 0 }"
            @click="form.hasGymAccess = 0"
          >
            <text>没有</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 步骤4: 目标设定 -->
    <view v-if="currentStep === 4" class="step-content">
      <view class="step-title">您的减脂目标</view>
      <view class="step-desc">明确目标，更容易成功</view>

      <view class="form-group">
        <text class="label">主要目标</text>
        <view class="option-grid">
          <view
            v-for="opt in PRIMARY_GOAL_OPTIONS"
            :key="opt.value"
            class="grid-option"
            :class="{ active: form.primaryGoal === opt.value }"
            @click="form.primaryGoal = opt.value"
          >
            <text class="grid-label">{{ opt.label }}</text>
            <text class="grid-desc">{{ opt.desc }}</text>
          </view>
        </view>
      </view>

      <view class="form-group">
        <text class="label">目标时间</text>
        <view class="option-grid">
          <view
            v-for="opt in TIMELINE_OPTIONS"
            :key="opt.value"
            class="grid-option"
            :class="{ active: form.targetTimeline === opt.value }"
            @click="form.targetTimeline = opt.value"
          >
            <text class="grid-label">{{ opt.label }}</text>
            <text class="grid-desc">{{ opt.desc }}</text>
          </view>
        </view>
      </view>

      <view class="form-group">
        <text class="label">减脂过程中，您最大的挑战是?</text>
        <view class="option-list">
          <view
            v-for="opt in CHALLENGE_OPTIONS"
            :key="opt.value"
            class="option-item"
            :class="{ active: form.biggestChallenge === opt.value }"
            @click="form.biggestChallenge = opt.value"
          >
            <view class="option-main">
              <text class="option-label">{{ opt.label }}</text>
              <text class="option-desc">{{ opt.desc }}</text>
            </view>
            <text v-if="form.biggestChallenge === opt.value" class="check">✓</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="footer">
      <button
        v-if="currentStep > 1"
        class="btn btn-secondary"
        @click="prevStep"
      >
        上一步
      </button>
      <button
        class="btn btn-primary"
        :disabled="!canProceed"
        @click="nextStep"
      >
        {{ currentStep === totalSteps ? '完成' : '下一步' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import {
  BODY_TYPE_OPTIONS,
  ACTIVITY_LEVEL_OPTIONS,
  EXERCISE_EXPERIENCE_OPTIONS,
  PRIMARY_GOAL_OPTIONS,
  TIMELINE_OPTIONS,
  CHALLENGE_OPTIONS,
  calculateBMI,
  getBMICategory,
} from '@/types/ai-assistant'
import { saveUserProfile } from '@/api/ai-assistant.api'

const currentStep = ref(1)
const totalSteps = 4
const loading = ref(false)

const form = reactive({
  gender: 0,
  age: '',
  height: '',
  currentWeight: '',
  targetWeight: '',
  bodyType: '',
  activityLevel: '',
  sleepHours: '',
  exerciseExperience: '',
  hasGymAccess: 0,
  primaryGoal: '',
  targetTimeline: '',
  biggestChallenge: '',
})

const progress = computed(() => (currentStep.value / totalSteps) * 100)

const bmi = computed(() => {
  if (form.currentWeight && form.height) {
    return calculateBMI(Number(form.currentWeight), Number(form.height))
  }
  return 0
})

const bmiCategory = computed(() => getBMICategory(bmi.value))

const canProceed = computed(() => {
  switch (currentStep.value) {
    case 1:
      return form.gender !== undefined && form.age && form.height && form.currentWeight && form.targetWeight
    case 2:
      return form.bodyType && form.activityLevel && form.sleepHours
    case 3:
      return form.exerciseExperience && form.hasGymAccess !== undefined
    case 4:
      return form.primaryGoal && form.targetTimeline && form.biggestChallenge
    default:
      return false
  }
})

function prevStep() {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

async function nextStep() {
  if (currentStep.value < totalSteps) {
    currentStep.value++
  } else {
    await submitProfile()
  }
}

async function submitProfile() {
  loading.value = true
  try {
    const profileData = {
      gender: form.gender,
      age: Number(form.age),
      height: Number(form.height),
      currentWeight: Number(form.currentWeight),
      targetWeight: Number(form.targetWeight),
      bodyType: form.bodyType,
      activityLevel: form.activityLevel,
      sleepHours: Number(form.sleepHours),
      exerciseExperience: form.exerciseExperience,
      hasGymAccess: form.hasGymAccess,
      primaryGoal: form.primaryGoal,
      targetTimeline: form.targetTimeline,
      biggestChallenge: form.biggestChallenge,
    }

    await saveUserProfile(profileData)

    uni.showToast({ title: '保存成功', icon: 'success' })

    // 跳转到AI助手主页
    setTimeout(() => {
      uni.redirectTo({ url: '/pages/ai-assistant/ai-home' })
    }, 1500)
  } catch (error: any) {
    uni.showToast({ title: error.message || '保存失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.profile-setup-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
}

.progress-bar {
  background: rgba(255, 255, 255, 0.2);
  height: 8rpx;
  border-radius: 4rpx;
  margin-bottom: 40rpx;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: #fff;
  border-radius: 4rpx;
  transition: width 0.3s ease;
}

.progress-text {
  position: absolute;
  right: 0;
  top: -40rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
}

.step-content {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 40rpx;
}

.step-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12rpx;
}

.step-desc {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 40rpx;
}

.form-group {
  margin-bottom: 32rpx;
}

.label {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 16rpx;
}

.input {
  width: 100%;
  height: 90rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  color: #1a1a2e;
  box-sizing: border-box;
}

.bmi-hint {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: #667eea;
}

// 性别选择
.gender-options {
  display: flex;
  gap: 24rpx;
}

.gender-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;

  &.active {
    background: rgba(102, 126, 234, 0.1);
    border-color: #667eea;
  }
}

.gender-icon {
  font-size: 60rpx;
  margin-bottom: 12rpx;
}

// 选项列表
.option-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  border: 2rpx solid transparent;

  &.active {
    background: rgba(102, 126, 234, 0.1);
    border-color: #667eea;
  }
}

.option-main {
  flex: 1;
}

.option-label {
  display: block;
  font-size: 30rpx;
  font-weight: 500;
  color: #1a1a2e;
  margin-bottom: 4rpx;
}

.option-desc {
  font-size: 24rpx;
  color: #999;
}

.check {
  width: 40rpx;
  height: 40rpx;
  background: #667eea;
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
}

// 网格选项
.option-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
}

.grid-option {
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
  border: 2rpx solid transparent;
  text-align: center;

  &.active {
    background: rgba(102, 126, 234, 0.1);
    border-color: #667eea;
  }
}

.grid-label {
  display: block;
  font-size: 30rpx;
  font-weight: 500;
  color: #1a1a2e;
  margin-bottom: 4rpx;
}

.grid-desc {
  font-size: 22rpx;
  color: #999;
}

// 底部按钮
.footer {
  display: flex;
  gap: 24rpx;
}

.btn {
  flex: 1;
  height: 90rpx;
  border-radius: 45rpx;
  font-size: 32rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;

  &-primary {
    background: #fff;
    color: #667eea;

    &:disabled {
      opacity: 0.5;
    }
  }

  &-secondary {
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
  }
}
</style>
