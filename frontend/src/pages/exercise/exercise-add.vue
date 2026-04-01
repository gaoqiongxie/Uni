<template>
  <view class="add-page">
    <view class="form card">
      <!-- 运动类型选择 -->
      <view class="form-item">
        <text class="label">运动类型</text>
        <view class="type-grid">
          <view
            class="type-item"
            :class="{ active: formData.exerciseType === item.value }"
            v-for="item in EXERCISE_TYPES"
            :key="item.value"
            @click="formData.exerciseType = item.value"
          >
            <text class="type-icon">{{ item.icon }}</text>
            <text class="type-name">{{ item.label }}</text>
          </view>
        </view>
      </view>

      <!-- 运动时长 -->
      <view class="form-item">
        <text class="label">运动时长（分钟）</text>
        <view class="duration-picker">
          <view class="picker-btn" @click="adjustDuration(-5)">
            <text>-5</text>
          </view>
          <input
            class="duration-input"
            v-model="formData.duration"
            type="number"
            placeholder="0"
          />
          <view class="picker-btn" @click="adjustDuration(5)">
            <text>+5</text>
          </view>
          <view class="picker-btn" @click="adjustDuration(10)">
            <text>+10</text>
          </view>
        </view>
      </view>

      <!-- 消耗热量（可选，自动估算） -->
      <view class="form-item">
        <text class="label">消耗热量（kcal）</text>
        <view class="row">
          <input
            class="input flex-1"
            v-model="formData.calorieBurn"
            type="number"
            placeholder="自动估算或手动输入"
          />
          <button class="estimate-btn" @click="estimateCalorie">估算</button>
        </view>
      </view>

      <!-- 运动距离（可选） -->
      <view class="form-item">
        <text class="label">运动距离（km，选填）</text>
        <input
          class="input"
          v-model="formData.distance"
          type="digit"
          placeholder="如 3.5"
        />
      </view>

      <!-- 运动强度 -->
      <view class="form-item">
        <text class="label">运动强度</text>
        <view class="option-row">
          <view
            class="option-item"
            :class="{ active: formData.intensity === item.value }"
            v-for="item in INTENSITY_OPTIONS"
            :key="item.value"
            @click="formData.intensity = item.value"
          >
            <text>{{ item.label }}</text>
          </view>
        </view>
      </view>

      <!-- 运动感受 -->
      <view class="form-item">
        <text class="label">运动感受</text>
        <view class="option-row">
          <view
            class="option-item"
            :class="{ active: formData.feeling === item.value }"
            v-for="item in FEELING_OPTIONS"
            :key="item.value"
            @click="formData.feeling = item.value"
          >
            <text>{{ item.label }}</text>
          </view>
        </view>
      </view>

      <!-- 心率（可选） -->
      <view class="form-item">
        <text class="label">心率（选填）</text>
        <view class="heart-rate-row">
          <input
            class="input hr-input"
            v-model="formData.heartRateAvg"
            type="number"
            placeholder="平均心率"
          />
          <text class="hr-sep">~</text>
          <input
            class="input hr-input"
            v-model="formData.heartRateMax"
            type="number"
            placeholder="最大心率"
          />
        </view>
      </view>

      <!-- 备注 -->
      <view class="form-item">
        <text class="label">备注（选填）</text>
        <textarea
          class="textarea"
          v-model="formData.remark"
          placeholder="记录今天的运动感受..."
          :maxlength="200"
        />
      </view>
    </view>

    <!-- 提交按钮 -->
    <button class="submit-btn" :disabled="loading || !canSubmit" @click="handleSubmit">
      {{ loading ? '保存中...' : '保存运动记录' }}
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useExerciseStore } from '../../store/exercise'
import { EXERCISE_TYPES, INTENSITY_OPTIONS, FEELING_OPTIONS } from '../../types/exercise'
import type { ExerciseRecordDTO } from '../../types/exercise'

const exerciseStore = useExerciseStore()
const loading = ref(false)

const formData = reactive({
  exerciseType: '',
  duration: '',
  calorieBurn: '',
  distance: '',
  intensity: 2,
  feeling: 0,
  heartRateAvg: '',
  heartRateMax: '',
  remark: ''
})

const canSubmit = computed(() =>
  formData.exerciseType && Number(formData.duration) > 0
)

function adjustDuration(delta: number) {
  const current = Number(formData.duration) || 0
  const newVal = Math.max(0, current + delta)
  formData.duration = newVal > 0 ? String(newVal) : ''
}

/**
 * 简单热量估算（基于运动类型和时长）
 * 粗略值：跑步 10kcal/min，游泳 12kcal/min，瑜伽 5kcal/min 等
 */
function estimateCalorie() {
  if (!formData.exerciseType || !formData.duration) {
    uni.showToast({ title: '请先选择运动类型和时长', icon: 'none' })
    return
  }
  const rates: Record<string, number> = {
    '跑步': 10, '游泳': 12, '瑜伽': 5, '力量训练': 8,
    '骑行': 8, '跳绳': 11, '散步': 4, '其他': 6
  }
  const rate = rates[formData.exerciseType] || 6
  const intensityMultiplier = formData.intensity === 1 ? 0.7 : formData.intensity === 3 ? 1.3 : 1.0
  const calorie = Math.round(Number(formData.duration) * rate * intensityMultiplier)
  formData.calorieBurn = String(calorie)
  uni.showToast({ title: `估算消耗 ${calorie} kcal`, icon: 'none' })
}

async function handleSubmit() {
  if (!canSubmit.value) {
    uni.showToast({ title: '请选择运动类型和输入时长', icon: 'none' })
    return
  }

  try {
    loading.value = true
    const dto: ExerciseRecordDTO = {
      recordDate: new Date().toISOString().split('T')[0],
      exerciseType: formData.exerciseType,
      duration: Number(formData.duration),
      calorieBurn: Number(formData.calorieBurn) || 0,
      intensity: formData.intensity,
      feeling: formData.feeling,
      remark: formData.remark
    }
    if (formData.distance) dto.distance = Number(formData.distance)
    if (formData.heartRateAvg) dto.heartRateAvg = Number(formData.heartRateAvg)
    if (formData.heartRateMax) dto.heartRateMax = Number(formData.heartRateMax)

    await exerciseStore.createExerciseRecord(dto)
    uni.showToast({ title: '记录成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1000)
  } catch (e) {
    // 错误已在 request 中 toast
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.add-page {
  min-height: 100vh;
  padding: 24rpx;
  padding-bottom: 160rpx;
  background: $bg-color;

  .form {
    margin-bottom: 24rpx;

    .form-item {
      margin-bottom: 40rpx;

      &:last-child { margin-bottom: 0; }

      .label {
        display: block;
        font-size: $font-size;
        font-weight: 600;
        color: $text-primary;
        margin-bottom: 16rpx;
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

      .textarea {
        width: 100%;
        height: 160rpx;
        border: 2rpx solid $border-color;
        border-radius: $border-radius;
        padding: 20rpx 24rpx;
        font-size: $font-size;
        background: $bg-gray;
        box-sizing: border-box;
      }

      .row {
        display: flex;
        gap: 16rpx;
        align-items: center;

        .flex-1 { flex: 1; }

        .estimate-btn {
          flex-shrink: 0;
          height: 88rpx;
          padding: 0 28rpx;
          background: $primary-light;
          color: $primary-color;
          font-size: $font-size-sm;
          border: 2rpx solid $primary-color;
          border-radius: $border-radius;
          line-height: 84rpx;
        }
      }
    }
  }

  .type-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;

    .type-item {
      width: calc(25% - 12rpx);
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20rpx 0;
      border: 2rpx solid $border-color;
      border-radius: $border-radius;
      background: $bg-gray;
      transition: all 0.2s;

      &.active {
        border-color: $primary-color;
        background: rgba(102, 126, 234, 0.08);

        .type-name { color: $primary-color; }
      }

      .type-icon {
        font-size: 48rpx;
        margin-bottom: 8rpx;
      }

      .type-name {
        font-size: $font-size-xs;
        color: $text-secondary;
      }
    }
  }

  .duration-picker {
    display: flex;
    gap: 16rpx;
    align-items: center;

    .picker-btn {
      width: 100rpx;
      height: 88rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background: $bg-gray;
      border: 2rpx solid $border-color;
      border-radius: $border-radius;
      font-size: $font-size;
      color: $text-secondary;
      flex-shrink: 0;

      &:active {
        background: $primary-light;
        border-color: $primary-color;
        color: $primary-color;
      }
    }

    .duration-input {
      flex: 1;
      height: 88rpx;
      border: 2rpx solid $border-color;
      border-radius: $border-radius;
      padding: 0 24rpx;
      font-size: 48rpx;
      font-weight: 600;
      text-align: center;
      background: $bg-gray;
      box-sizing: border-box;
    }
  }

  .option-row {
    display: flex;
    gap: 12rpx;
    flex-wrap: wrap;

    .option-item {
      padding: 12rpx 28rpx;
      border: 2rpx solid $border-color;
      border-radius: 32rpx;
      font-size: $font-size-sm;
      color: $text-secondary;
      background: $bg-gray;
      transition: all 0.2s;

      &.active {
        border-color: $primary-color;
        background: $primary-color;
        color: #fff;
      }
    }
  }

  .heart-rate-row {
    display: flex;
    gap: 12rpx;
    align-items: center;

    .hr-input { flex: 1; }

    .hr-sep {
      font-size: $font-size-lg;
      color: $text-tertiary;
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
}

$primary-light: rgba(102, 126, 234, 0.1);
</style>
