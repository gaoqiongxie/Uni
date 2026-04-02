<template>
  <view class="set-goal-container">

    <!-- 步骤条 -->
    <view class="steps-bar">
      <view v-for="i in 3" :key="i" class="step-dot" :class="{ active: step >= i, done: step > i }">
        <text>{{ i }}</text>
      </view>
      <view class="step-line"></view>
    </view>

    <!-- Step 1：选择目标类型 -->
    <view v-if="step === 1" class="step-content">
      <text class="step-title">你的目标是？</text>
      <text class="step-hint">选择最符合你当前需求的目标</text>

      <view class="type-list">
        <view
          class="type-card"
          v-for="type in GOAL_TYPES"
          :key="type.value"
          :class="{ selected: form.goalType === type.value }"
          @click="form.goalType = type.value"
        >
          <text class="type-icon">{{ type.icon }}</text>
          <view class="type-info">
            <text class="type-label">{{ type.label }}</text>
            <text class="type-desc">{{ type.desc }}</text>
          </view>
          <view class="type-check" v-if="form.goalType === type.value">✓</view>
        </view>
      </view>
    </view>

    <!-- Step 2：设定体重和热量 -->
    <view v-if="step === 2" class="step-content">
      <text class="step-title">设定你的目标体重</text>
      <text class="step-hint">当前体重 {{ userInfo?.currentWeight || '--' }} kg</text>

      <view class="form-card card">
        <view class="form-item">
          <text class="form-label">目标体重 (kg)</text>
          <input
            class="form-input"
            type="digit"
            v-model="targetWeightStr"
            placeholder="请输入目标体重"
          />
        </view>

        <view class="form-item">
          <text class="form-label">每日热量目标 (kcal)</text>
          <view class="calorie-row">
            <input
              class="form-input"
              type="number"
              v-model="calorieStr"
              placeholder="留空自动推荐"
            />
            <text class="calorie-hint" v-if="recommendCalorie">推荐 {{ recommendCalorie }}</text>
          </view>
        </view>

        <view class="form-item">
          <text class="form-label">目标截止日期（可选）</text>
          <picker mode="date" :value="form.targetDate || ''" @change="onDateChange">
            <view class="form-input picker-input">
              <text :class="{ placeholder: !form.targetDate }">
                {{ form.targetDate || '请选择截止日期' }}
              </text>
            </view>
          </picker>
        </view>
      </view>
    </view>

    <!-- Step 3：运动计划 + 动力宣言 -->
    <view v-if="step === 3" class="step-content">
      <text class="step-title">制定运动计划</text>
      <text class="step-hint">合理的运动安排，让减脂事半功倍</text>

      <view class="form-card card">
        <view class="form-item">
          <text class="form-label">每周运动天数</text>
          <view class="days-grid">
            <view
              class="day-btn"
              v-for="d in [2,3,4,5,6,7]"
              :key="d"
              :class="{ selected: form.exerciseDaysPerWeek === d }"
              @click="form.exerciseDaysPerWeek = d"
            >{{ d }}天</view>
          </view>
        </view>

        <view class="form-item">
          <text class="form-label">每次运动时长：{{ form.exerciseMinutesPerDay }} 分钟</text>
          <slider
            :value="form.exerciseMinutesPerDay"
            :min="15"
            :max="120"
            :step="5"
            show-value
            activeColor="#667eea"
            @change="(e: any) => form.exerciseMinutesPerDay = e.detail.value"
          />
        </view>

        <view class="form-item">
          <text class="form-label">动力宣言（可选）</text>
          <textarea
            class="remark-input"
            v-model="form.remark"
            placeholder="给自己一句加油的话..."
            maxlength="100"
          />
          <text class="char-count">{{ (form.remark || '').length }}/100</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <button v-if="step > 1" class="back-btn" @click="step--">上一步</button>
      <button
        v-if="step < 3"
        class="next-btn"
        :disabled="!canNext"
        @click="nextStep"
      >下一步</button>
      <button
        v-if="step === 3"
        class="submit-btn"
        :loading="submitting"
        :disabled="submitting"
        @click="handleSubmit"
      >{{ activeGoalId ? '更新目标' : '开始挑战 🚀' }}</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useGoalStore } from '../../store/goal'
import { useUserStore } from '../../store/user'
import { GOAL_TYPES } from '../../types/goal'

const goalStore = useGoalStore()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const step = ref(1)
const submitting = ref(false)
const activeGoalId = computed(() => goalStore.activeGoal?.id)

const form = ref({
  goalType: 1,
  targetDate: '',
  exerciseDaysPerWeek: 3,
  exerciseMinutesPerDay: 30,
  remark: ''
})
const targetWeightStr = ref('')
const calorieStr = ref('')

// 自动推荐热量（简单估算）
const recommendCalorie = computed(() => {
  const w = userInfo.value?.currentWeight || 60
  const target = parseFloat(targetWeightStr.value)
  if (!target || target >= w) return null
  // BMR 粗略估算 - 女性轻度活动 TDEE - 400
  const bmr = 10 * w + 6.25 * 160 - 5 * 30 - 161
  return Math.max(1200, Math.round(bmr * 1.375 - 400))
})

const canNext = computed(() => {
  if (step.value === 1) return !!form.value.goalType
  if (step.value === 2) return !!targetWeightStr.value
  return true
})

function nextStep() {
  step.value++
}

function onDateChange(e: any) {
  form.value.targetDate = e.detail.value
}

async function handleSubmit() {
  const targetWeight = parseFloat(targetWeightStr.value)
  if (!targetWeight || targetWeight <= 0) {
    uni.showToast({ title: '请输入有效的目标体重', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await goalStore.createGoal({
      goalType: form.value.goalType,
      targetWeight,
      calorieGoal: calorieStr.value ? parseInt(calorieStr.value) : undefined,
      exerciseDaysPerWeek: form.value.exerciseDaysPerWeek,
      exerciseMinutesPerDay: form.value.exerciseMinutesPerDay,
      targetDate: form.value.targetDate || undefined,
      remark: form.value.remark || undefined
    })

    uni.showToast({ title: '目标已设定，加油！', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } catch (e: any) {
    uni.showToast({ title: e.message || '设定失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  // 如有活动目标，预填表单
  const active = goalStore.activeGoal
  if (active) {
    form.value.goalType = active.goalType
    form.value.exerciseDaysPerWeek = active.exerciseDaysPerWeek
    form.value.exerciseMinutesPerDay = active.exerciseMinutesPerDay
    form.value.remark = active.remark || ''
    form.value.targetDate = active.targetDate || ''
    targetWeightStr.value = String(active.targetWeight)
    calorieStr.value = String(active.calorieGoal)
  }
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.set-goal-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 140rpx;

  .steps-bar {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40rpx 60rpx 20rpx;
    position: relative;
    gap: 100rpx;

    .step-line {
      position: absolute;
      top: 60rpx;
      left: 130rpx;
      right: 130rpx;
      height: 4rpx;
      background: $border-color;
      z-index: 0;
    }

    .step-dot {
      width: 64rpx;
      height: 64rpx;
      border-radius: 50%;
      background: $border-color;
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1;
      font-size: $font-size-sm;
      color: $text-hint;

      &.active {
        background: $primary-color;
        color: #fff;
        font-weight: bold;
      }

      &.done {
        background: $primary-color;
        color: #fff;
      }
    }
  }

  .step-content {
    padding: 20rpx 30rpx;

    .step-title {
      display: block;
      font-size: $font-size-xl;
      font-weight: bold;
      color: $text-primary;
      margin-bottom: 12rpx;
    }

    .step-hint {
      display: block;
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: 40rpx;
    }

    .type-list {
      display: flex;
      flex-direction: column;
      gap: 20rpx;

      .type-card {
        display: flex;
        align-items: center;
        background: #fff;
        border-radius: $border-radius;
        padding: 30rpx;
        border: 2rpx solid $border-color;

        &.selected {
          border-color: $primary-color;
          background: rgba(102, 126, 234, 0.05);
        }

        .type-icon { font-size: 48rpx; margin-right: 24rpx; }

        .type-info {
          flex: 1;
          .type-label { display: block; font-size: $font-size-md; font-weight: 600; color: $text-primary; }
          .type-desc { font-size: $font-size-sm; color: $text-secondary; }
        }

        .type-check {
          font-size: $font-size-md;
          color: $primary-color;
          font-weight: bold;
        }
      }
    }

    .form-card {
      .form-item {
        padding: 24rpx 0;
        border-bottom: 1rpx solid $border-color;
        &:last-child { border-bottom: none; }

        .form-label {
          display: block;
          font-size: $font-size-sm;
          color: $text-secondary;
          margin-bottom: 16rpx;
        }

        .form-input {
          width: 100%;
          height: 72rpx;
          border: 1rpx solid $border-color;
          border-radius: $border-radius-sm;
          padding: 0 20rpx;
          font-size: $font-size-md;
          color: $text-primary;
          box-sizing: border-box;

          &.picker-input { display: flex; align-items: center; }
          .placeholder { color: $text-hint; }
        }

        .calorie-row {
          display: flex;
          align-items: center;
          gap: 16rpx;
          .calorie-hint { font-size: $font-size-sm; color: $primary-color; white-space: nowrap; }
        }

        .days-grid {
          display: flex;
          gap: 16rpx;
          flex-wrap: wrap;

          .day-btn {
            width: 100rpx;
            height: 64rpx;
            line-height: 64rpx;
            text-align: center;
            border: 2rpx solid $border-color;
            border-radius: $border-radius-sm;
            font-size: $font-size-sm;
            color: $text-secondary;

            &.selected {
              border-color: $primary-color;
              background: $primary-color;
              color: #fff;
            }
          }
        }

        .remark-input {
          width: 100%;
          height: 160rpx;
          border: 1rpx solid $border-color;
          border-radius: $border-radius-sm;
          padding: 20rpx;
          font-size: $font-size-sm;
          color: $text-primary;
          box-sizing: border-box;
        }

        .char-count {
          text-align: right;
          display: block;
          font-size: $font-size-sm;
          color: $text-hint;
          margin-top: 8rpx;
        }
      }
    }
  }

  .bottom-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 24rpx 30rpx;
    background: #fff;
    border-top: 1rpx solid $border-color;
    display: flex;
    gap: 20rpx;

    button {
      flex: 1;
      height: 88rpx;
      line-height: 88rpx;
      border-radius: $border-radius;
      font-size: $font-size-md;
      border: none;
      &::after { border: none; }
    }

    .back-btn {
      background: $bg-color;
      color: $text-secondary;
      border: 2rpx solid $border-color;
    }

    .next-btn {
      background: $primary-color;
      color: #fff;
      &[disabled] { background: $border-color; color: $text-hint; }
    }

    .submit-btn {
      background: linear-gradient(135deg, $primary-color, $primary-light);
      color: #fff;
    }
  }
}
</style>
