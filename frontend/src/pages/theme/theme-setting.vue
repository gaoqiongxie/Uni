<template>
  <view class="theme-setting-page">
    <!-- 主题模式选择 -->
    <view class="section">
      <view class="section-title">主题模式</view>
      <view class="mode-options">
        <view
          v-for="option in THEME_MODE_OPTIONS"
          :key="option.value"
          class="mode-option"
          :class="{ active: themeStore.themeSetting.themeMode === option.value }"
          @click="selectMode(option.value)"
        >
          <text class="mode-icon">{{ option.icon }}</text>
          <text class="mode-label">{{ option.label }}</text>
          <view v-if="themeStore.themeSetting.themeMode === option.value" class="check-mark">
            <text class="check-icon">✓</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 主题色选择 -->
    <view class="section">
      <view class="section-title">主题色</view>
      <view class="color-options">
        <view
          v-for="color in THEME_COLORS"
          :key="color.value"
          class="color-option"
          :class="{ active: themeStore.themeSetting.primaryColor === color.value }"
          :style="{ background: color.gradient }"
          @click="selectColor(color.value)"
        >
          <text v-if="themeStore.themeSetting.primaryColor === color.value" class="color-check">✓</text>
        </view>
      </view>
    </view>

    <!-- 字体大小 -->
    <view class="section">
      <view class="section-title">字体大小</view>
      <view class="font-size-options">
        <view
          v-for="option in FONT_SIZE_OPTIONS"
          :key="option.value"
          class="font-option"
          :class="{ active: themeStore.themeSetting.fontSize === option.value }"
          :style="{ fontSize: `${option.scale * 28}rpx` }"
          @click="selectFontSize(option.value)"
        >
          {{ option.label }}
        </view>
      </view>
    </view>

    <!-- 快速切换 -->
    <view class="section">
      <view class="quick-toggle" @click="themeStore.toggleTheme">
        <text class="toggle-icon">{{ themeStore.isDark ? '🌙' : '☀️' }}</text>
        <text class="toggle-text">切换到{{ themeStore.isDark ? '浅色' : '深色' }}模式</text>
        <text class="toggle-arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useThemeStore } from '@/stores/theme'
import { ThemeMode, THEME_MODE_OPTIONS, THEME_COLORS, FONT_SIZE_OPTIONS, FontSize } from '@/types/theme'

const themeStore = useThemeStore()

function selectMode(mode: ThemeMode) {
  themeStore.setThemeMode(mode)
}

function selectColor(color: string) {
  themeStore.setPrimaryColor(color)
}

function selectFontSize(size: FontSize) {
  themeStore.setFontSize(size)
}
</script>

<style lang="scss" scoped>
.theme-setting-page {
  min-height: 100vh;
  background-color: var(--bg-primary);
  padding: 24rpx;
}
.section {
  margin-bottom: 32rpx;
  background-color: var(--bg-card);
  border-radius: 16rpx;
  padding: 24rpx;
}
.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20rpx;
}
.mode-options {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}
.mode-option {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background-color: var(--bg-tertiary);
  border-radius: 12rpx;
  border: 2rpx solid transparent;
}
.mode-option.active {
  border-color: var(--primary-color);
  background-color: rgba(102, 126, 234, 0.1);
}
.mode-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}
.mode-label {
  flex: 1;
  font-size: 30rpx;
  color: var(--text-primary);
}
.check-mark {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
}
.check-icon {
  color: #fff;
  font-size: 24rpx;
}
.color-options {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
}
.color-option {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid transparent;
}
.color-option.active {
  border-color: var(--text-primary);
  transform: scale(1.1);
}
.color-check {
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
}
.font-size-options {
  display: flex;
  gap: 16rpx;
}
.font-option {
  flex: 1;
  padding: 24rpx;
  text-align: center;
  background-color: var(--bg-tertiary);
  border-radius: 12rpx;
  border: 2rpx solid transparent;
  color: var(--text-primary);
}
.font-option.active {
  border-color: var(--primary-color);
  background-color: rgba(102, 126, 234, 0.1);
  color: var(--primary-color);
}
.quick-toggle {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background-color: var(--bg-tertiary);
  border-radius: 12rpx;
}
.toggle-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}
.toggle-text {
  flex: 1;
  font-size: 30rpx;
  color: var(--text-primary);
}
.toggle-arrow {
  font-size: 36rpx;
  color: var(--text-tertiary);
}
</style>
