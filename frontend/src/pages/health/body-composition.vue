<template>
  <view class="container">
    <!-- 页面标题 -->
    <view class="header">
      <text class="title">身体成分分析</text>
      <text class="subtitle">科学评估体脂率与身体组成</text>
    </view>

    <!-- 今日数据卡片 -->
    <view v-if="todayRecord" class="today-card">
      <view class="card-header">
        <text class="card-title">今日数据</text>
        <text class="record-date">{{ formatDate(todayRecord.recordDate) }}</text>
      </view>
      <view class="data-grid">
        <view class="data-item">
          <text class="data-value">{{ todayRecord.weight }}</text>
          <text class="data-label">体重(kg)</text>
        </view>
        <view class="data-item highlight">
          <text class="data-value">{{ todayRecord.bodyFatRate }}%</text>
          <text class="data-label">体脂率</text>
        </view>
        <view class="data-item">
          <text class="data-value">{{ todayRecord.bmi }}</text>
          <text class="data-label">BMI</text>
        </view>
        <view class="data-item">
          <text class="data-value" :class="getFatLevelClass(todayRecord.fatLevel)">
            {{ todayRecord.fatLevelText }}
          </text>
          <text class="data-label">体脂等级</text>
        </view>
      </view>
    </view>

    <!-- 录入表单 -->
    <view class="form-card">
      <view class="form-title">录入身体数据</view>

      <!-- 基本信息 -->
      <view class="form-section">
        <view class="section-title">基本信息</view>
        <view class="form-item">
          <text class="form-label">性别</text>
          <view class="gender-select">
            <view
              class="gender-btn"
              :class="{ active: form.gender === 'male' }"
              @click="form.gender = 'male'"
            >
              男
            </view>
            <view
              class="gender-btn"
              :class="{ active: form.gender === 'female' }"
              @click="form.gender = 'female'"
            >
              女
            </view>
          </view>
        </view>
        <view class="form-item">
          <text class="form-label">身高 (cm)</text>
          <input
            v-model="form.height"
            type="digit"
            placeholder="请输入身高"
            class="form-input"
          />
        </view>
        <view class="form-item">
          <text class="form-label">体重 (kg)</text>
          <input
            v-model="form.weight"
            type="digit"
            placeholder="请输入体重"
            class="form-input"
          />
        </view>
      </view>

      <!-- 围度测量 -->
      <view class="form-section">
        <view class="section-title">围度测量（用于计算体脂率）</view>
        <view class="form-item">
          <text class="form-label">腰围 (cm)</text>
          <input
            v-model="form.waist"
            type="digit"
            placeholder="请输入腰围"
            class="form-input"
          />
        </view>
        <view class="form-item">
          <text class="form-label">颈围 (cm)</text>
          <input
            v-model="form.neck"
            type="digit"
            placeholder="请输入颈围"
            class="form-input"
          />
        </view>
        <view v-if="form.gender === 'female'" class="form-item">
          <text class="form-label">臀围 (cm)</text>
          <input
            v-model="form.hip"
            type="digit"
            placeholder="请输入臀围"
            class="form-input"
          />
        </view>
        <view class="form-hint">
          <text>💡 测量方法：用软尺在腰部最细处测量腰围，喉结下方测颈围</text>
        </view>
      </view>

      <!-- 提交按钮 -->
      <button class="submit-btn" @click="handleSubmit" :loading="loading">
        {{ loading ? '计算中...' : '计算体脂率' }}
      </button>
    </view>

    <!-- 计算结果 -->
    <view v-if="result" class="result-card">
      <view class="result-header">
        <text class="result-title">分析结果</text>
      </view>

      <!-- 体脂率展示 -->
      <view class="body-fat-display">
        <view class="body-fat-circle">
          <text class="body-fat-value">{{ result.bodyFatRate }}%</text>
          <text class="body-fat-label">体脂率</text>
        </view>
        <view class="fat-level-badge" :class="result.fatLevel">
          {{ result.fatLevelText }}
        </view>
      </view>

      <!-- 身体组成雷达图（模拟） -->
      <view class="composition-chart">
        <view class="chart-title">身体组成分析</view>
        <view class="chart-bars">
          <view class="chart-item">
            <text class="chart-label">肌肉量</text>
            <view class="chart-bar-bg">
              <view class="chart-bar" :style="{ width: getBarWidth(result.muscleMass / 40 * 100) + '%', background: '#3498db' }"></view>
            </view>
            <text class="chart-value">{{ result.muscleMass }}kg</text>
          </view>
          <view class="chart-item">
            <text class="chart-label">骨量</text>
            <view class="chart-bar-bg">
              <view class="chart-bar" :style="{ width: getBarWidth(result.boneMass / 5 * 100) + '%', background: '#f39c12' }"></view>
            </view>
            <text class="chart-value">{{ result.boneMass }}kg</text>
          </view>
          <view class="chart-item">
            <text class="chart-label">水分率</text>
            <view class="chart-bar-bg">
              <view class="chart-bar" :style="{ width: getBarWidth(result.waterRate) + '%', background: '#27ae60' }"></view>
            </view>
            <text class="chart-value">{{ result.waterRate }}%</text>
          </view>
        </view>
      </view>

      <!-- 建议 -->
      <view class="suggestion">
        <text class="suggestion-title">健康建议</text>
        <text class="suggestion-text">{{ getSuggestionText(result.fatLevel, result.bodyFatRate) }}</text>
      </view>
    </view>

    <!-- 历史趋势 -->
    <view v-if="historyList.length > 0" class="history-card">
      <view class="history-header">
        <text class="history-title">历史趋势</text>
        <text class="history-hint">最近30天</text>
      </view>
      <view class="trend-chart">
        <view class="trend-labels">
          <text v-for="(item, index) in historyList.slice(0, 7).reverse()" :key="index" class="trend-label">
            {{ formatDateShort(item.recordDate) }}
          </text>
        </view>
        <view class="trend-lines">
          <!-- 体重趋势 -->
          <view class="trend-line">
            <text class="trend-line-label">体重</text>
            <view class="trend-dots">
              <view
                v-for="(item, index) in historyList.slice(0, 7).reverse()"
                :key="index"
                class="trend-dot weight"
                :style="{ top: getWeightDotTop(item.weight) + 'px' }"
              ></view>
            </view>
          </view>
          <!-- 体脂率趋势 -->
          <view class="trend-line">
            <text class="trend-line-label">体脂</text>
            <view class="trend-dots">
              <view
                v-for="(item, index) in historyList.slice(0, 7).reverse()"
                :key="index"
                class="trend-dot fat"
                :style="{ top: getFatDotTop(item.bodyFatRate) + 'px' }"
              ></view>
            </view>
          </view>
        </view>
        <view class="trend-legend">
          <view class="legend-item">
            <view class="legend-dot weight"></view>
            <text>体重(kg)</text>
          </view>
          <view class="legend-item">
            <view class="legend-dot fat"></view>
            <text>体脂率(%)</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getLatestBodyComposition, getBodyCompositionHistory, recordBodyComposition } from '@/api/body-composition.api';
import type { BodyComposition } from '@/types/body-composition';
import { FAT_LEVEL_CONFIG } from '@/types/body-composition';

const loading = ref(false);
const todayRecord = ref<BodyComposition | null>(null);
const historyList = ref<BodyComposition[]>([]);
const result = ref<BodyComposition | null>(null);

// 表单数据（均用string便于input双向绑定，提交时转number）
const form = ref({
  gender: 'male' as 'male' | 'female',
  height: '',
  weight: '',
  waist: '',
  neck: '',
  hip: ''
});

onMounted(() => {
  loadData();
});

async function loadData() {
  try {
    // 获取今日数据
    const todayRes = await getLatestBodyComposition();
    if (todayRes.data) {
      todayRecord.value = todayRes.data;
      result.value = todayRes.data;
      // 回填表单
      form.value.gender = todayRes.data.gender || 'male';
      form.value.height = String(todayRes.data.height);
      form.value.weight = String(todayRes.data.weight);
      form.value.waist = String(todayRes.data.waist || '');
      form.value.neck = String(todayRes.data.neck || '');
      form.value.hip = String(todayRes.data.hip || '');
    }

    // 获取历史数据
    const historyRes = await getBodyCompositionHistory(30);
    if (historyRes.data) {
      historyList.value = historyRes.data;
    }
  } catch (e) {
    console.error('加载数据失败', e);
  }
}

async function handleSubmit() {
  // 验证表单
  if (!form.value.height || !form.value.weight) {
    uni.showToast({ title: '请填写身高和体重', icon: 'none' });
    return;
  }
  if (!form.value.waist || !form.value.neck) {
    uni.showToast({ title: '请填写腰围和颈围', icon: 'none' });
    return;
  }
  if (form.value.gender === 'female' && !form.value.hip) {
    uni.showToast({ title: '请填写臀围', icon: 'none' });
    return;
  }

  loading.value = true;
  try {
    const res = await recordBodyComposition({
      height: Number(form.value.height),
      weight: Number(form.value.weight),
      waist: Number(form.value.waist),
      neck: Number(form.value.neck),
      hip: form.value.gender === 'female' ? Number(form.value.hip) : undefined,
      gender: form.value.gender
    });

    if (res.data) {
      result.value = res.data;
      todayRecord.value = res.data;
      uni.showToast({ title: '计算成功', icon: 'success' });
      // 刷新历史数据
      loadData();
    }
  } catch (e) {
    console.error('提交失败', e);
    uni.showToast({ title: '提交失败', icon: 'none' });
  } finally {
    loading.value = false;
  }
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
}

function formatDateShort(dateStr: string): string {
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}/${date.getDate()}`;
}

function getFatLevelClass(level: string): string {
  return `level-${level}`;
}

function getBarWidth(percent: number): number {
  return Math.min(100, Math.max(0, percent));
}

function getSuggestionText(level: string, fatRate: number): string {
  const config = FAT_LEVEL_CONFIG[level as keyof typeof FAT_LEVEL_CONFIG];
  switch (level) {
    case 'lean':
      return '体脂率偏低，建议适当增肌，可适当增加力量训练和蛋白质摄入。';
    case 'normal':
      return '体脂率处于健康范围，请保持良好的饮食和运动习惯！';
    case 'high':
      return '体脂率偏高，建议增加有氧运动（如跑步、游泳）和减少高热量食物摄入。';
    case 'obese':
      return '体脂率偏高，建议咨询专业健身教练，制定减脂计划，注意运动安全。';
    default:
      return '继续努力，保持健康的生活方式！';
  }
}

function getWeightDotTop(weight: number): number {
  // 假设体重范围 40-100kg，映射到 0-80px
  return Math.max(0, Math.min(80, (100 - weight) * 1.33));
}

function getFatDotTop(fatRate: number): number {
  // 假设体脂率范围 5-40%，映射到 0-80px
  return Math.max(0, Math.min(80, (40 - fatRate) * 2.29));
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.header {
  padding: 30rpx 20rpx;
  .title {
    font-size: 40rpx;
    font-weight: 600;
    color: #333;
    display: block;
  }
  .subtitle {
    font-size: 26rpx;
    color: #999;
    margin-top: 10rpx;
    display: block;
  }
}

.today-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    .card-title {
      font-size: 32rpx;
      color: #fff;
      font-weight: 600;
    }
    .record-date {
      font-size: 24rpx;
      color: rgba(255,255,255,0.8);
    }
  }
  .data-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20rpx;
    .data-item {
      text-align: center;
      .data-value {
        font-size: 36rpx;
        font-weight: 600;
        color: #fff;
        display: block;
      }
      .data-label {
        font-size: 22rpx;
        color: rgba(255,255,255,0.8);
        margin-top: 8rpx;
        display: block;
      }
      &.highlight .data-value {
        font-size: 42rpx;
      }
    }
  }
}

.form-card, .result-card, .history-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.form-title, .result-title, .history-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 30rpx;
}

.form-section {
  margin-bottom: 30rpx;
  .section-title {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 20rpx;
    font-weight: 500;
  }
}

.form-item {
  margin-bottom: 24rpx;
  .form-label {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 12rpx;
    display: block;
  }
  .form-input {
    width: 100%;
    height: 80rpx;
    background: #f8f8f8;
    border-radius: 12rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
  }
}

.gender-select {
  display: flex;
  gap: 20rpx;
  .gender-btn {
    flex: 1;
    height: 80rpx;
    line-height: 80rpx;
    text-align: center;
    background: #f8f8f8;
    border-radius: 12rpx;
    font-size: 28rpx;
    &.active {
      background: #667eea;
      color: #fff;
    }
  }
}

.form-hint {
  background: #f0f7ff;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-top: 20rpx;
  text {
    font-size: 24rpx;
    color: #666;
  }
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 45rpx;
  margin-top: 20rpx;
  &::after {
    border: none;
  }
}

.body-fat-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40rpx;
  margin-bottom: 40rpx;
  .body-fat-circle {
    width: 200rpx;
    height: 200rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    .body-fat-value {
      font-size: 56rpx;
      font-weight: 700;
      color: #fff;
    }
    .body-fat-label {
      font-size: 24rpx;
      color: rgba(255,255,255,0.8);
      margin-top: 8rpx;
    }
  }
  .fat-level-badge {
    padding: 16rpx 32rpx;
    border-radius: 30rpx;
    font-size: 28rpx;
    font-weight: 600;
    &.lean {
      background: #e8f4fc;
      color: #3498db;
    }
    &.normal {
      background: #e8f6ed;
      color: #27ae60;
    }
    &.high {
      background: #fef9e7;
      color: #f39c12;
    }
    &.obese {
      background: #fdeaea;
      color: #e74c3c;
    }
  }
}

.composition-chart {
  .chart-title {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 20rpx;
  }
  .chart-bars {
    .chart-item {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;
      .chart-label {
        width: 100rpx;
        font-size: 26rpx;
        color: #666;
      }
      .chart-bar-bg {
        flex: 1;
        height: 24rpx;
        background: #f0f0f0;
        border-radius: 12rpx;
        margin: 0 20rpx;
        overflow: hidden;
        .chart-bar {
          height: 100%;
          border-radius: 12rpx;
          transition: width 0.3s ease;
        }
      }
      .chart-value {
        width: 100rpx;
        font-size: 26rpx;
        color: #333;
        text-align: right;
      }
    }
  }
}

.suggestion {
  margin-top: 30rpx;
  padding: 24rpx;
  background: #f8f8f8;
  border-radius: 12rpx;
  .suggestion-title {
    font-size: 28rpx;
    font-weight: 600;
    color: #333;
    margin-bottom: 12rpx;
    display: block;
  }
  .suggestion-text {
    font-size: 26rpx;
    color: #666;
    line-height: 1.6;
  }
}

.history-card {
  .history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
  }
  .history-hint {
    font-size: 24rpx;
    color: #999;
  }
}

.trend-chart {
  position: relative;
  height: 200rpx;
  .trend-labels {
    display: flex;
    justify-content: space-around;
    padding: 0 80rpx;
    .trend-label {
      font-size: 20rpx;
      color: #999;
    }
  }
  .trend-lines {
    position: absolute;
    top: 40rpx;
    left: 80rpx;
    right: 0;
    bottom: 60rpx;
    .trend-line {
      position: absolute;
      left: 0;
      right: 0;
      height: 100rpx;
      .trend-line-label {
        position: absolute;
        left: -70rpx;
        top: 50%;
        transform: translateY(-50%);
        font-size: 22rpx;
        color: #666;
      }
      .trend-dots {
        position: relative;
        height: 100%;
        display: flex;
        justify-content: space-around;
        .trend-dot {
          position: absolute;
          width: 16rpx;
          height: 16rpx;
          border-radius: 50%;
          transform: translate(-50%, -50%);
          &.weight {
            background: #667eea;
          }
          &.fat {
            background: #e74c3c;
          }
        }
      }
    }
  }
  .trend-legend {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    justify-content: center;
    gap: 40rpx;
    .legend-item {
      display: flex;
      align-items: center;
      gap: 10rpx;
      .legend-dot {
        width: 16rpx;
        height: 16rpx;
        border-radius: 50%;
        &.weight {
          background: #667eea;
        }
        &.fat {
          background: #e74c3c;
        }
      }
      text {
        font-size: 24rpx;
        color: #666;
      }
    }
  }
}

.level-lean { color: #3498db; }
.level-normal { color: #27ae60; }
.level-high { color: #f39c12; }
.level-obese { color: #e74c3c; }
</style>
