<template>
  <view class="badge-page">
    <!-- 头部统计 -->
    <view class="stats-header">
      <view class="stats-main">
        <view class="stats-item">
          <text class="stats-number">{{ stats.acquiredCount }}</text>
          <text class="stats-label">已获得</text>
        </view>
        <view class="stats-divider">/</view>
        <view class="stats-item">
          <text class="stats-number total">{{ stats.totalBadges }}</text>
          <text class="stats-label">总徽章</text>
        </view>
      </view>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
      </view>
      <text class="progress-text">已完成 {{ progressPercent }}%</text>
    </view>

    <!-- 分类标签 -->
    <scroll-view scroll-x class="category-tabs">
      <view
        v-for="cat in categories"
        :key="cat.code"
        class="category-tab"
        :class="{ active: currentCategory === cat.code }"
        @click="switchCategory(cat.code)"
      >
        <text class="tab-icon">{{ cat.icon }}</text>
        <text class="tab-name">{{ cat.name }}</text>
        <text v-if="cat.code !== 'all' && getCategoryCount(cat.code)" class="tab-count">
          {{ getCategoryCount(cat.code) }}
        </text>
      </view>
    </scroll-view>

    <!-- 徽章列表 -->
    <scroll-view scroll-y class="badge-list">
      <view class="badge-grid">
        <view
          v-for="badge in filteredBadges"
          :key="badge.id"
          class="badge-card"
          :class="[badge.rarity, { acquired: badge.acquired, new: badge.isNew }]"
          @click="showBadgeDetail(badge)"
        >
          <!-- 新徽章标记 -->
          <view v-if="badge.isNew" class="new-badge">NEW</view>
          
          <!-- 徽章图标 -->
          <view class="badge-icon-wrapper">
            <text class="badge-icon">{{ badge.icon || '🏆' }}</text>
            <view v-if="!badge.acquired" class="locked-overlay">
              <text class="lock-icon">🔒</text>
            </view>
          </view>
          
          <!-- 徽章信息 -->
          <text class="badge-name">{{ badge.name }}</text>
          <text class="badge-rarity">{{ badge.rarityName }}</text>
          
          <!-- 获得时间 -->
          <text v-if="badge.acquired && badge.acquireTime" class="acquire-time">
            {{ formatDate(badge.acquireTime) }}
          </text>
        </view>
      </view>
    </scroll-view>

    <!-- 徽章详情弹窗 -->
    <view v-if="showDetail" class="detail-modal" @click="closeDetail">
      <view class="detail-content" @click.stop>
        <view class="detail-header" :class="selectedBadge?.rarity">
          <text class="detail-icon">{{ selectedBadge?.icon || '🏆' }}</text>
          <text v-if="!selectedBadge?.acquired" class="detail-locked">🔒 未解锁</text>
          <text v-if="selectedBadge?.isNew" class="detail-new">NEW</text>
        </view>
        <view class="detail-body">
          <text class="detail-name">{{ selectedBadge?.name }}</text>
          <text class="detail-rarity">{{ selectedBadge?.rarityName }}</text>
          <text class="detail-desc">{{ selectedBadge?.description }}</text>
          <view class="detail-condition">
            <text class="condition-label">解锁条件：</text>
            <text class="condition-value">
              {{ getConditionText(selectedBadge) }}
            </text>
          </view>
          <text v-if="selectedBadge?.acquired && selectedBadge?.acquireTime" class="detail-time">
            获得时间：{{ formatDateTime(selectedBadge.acquireTime) }}
          </text>
        </view>
        <view class="detail-footer">
          <button class="close-btn" @click="closeDetail">知道了</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getAllBadges, getBadgeStats, markBadgesAsRead } from '@/api/badge.api'
import type { Badge, UserBadgeStats } from '@/types/badge'
import { BADGE_CATEGORIES, BADGE_RARITIES } from '@/types/badge'

const badges = ref<Badge[]>([])
const stats = ref<UserBadgeStats>({
  totalBadges: 0,
  acquiredCount: 0,
  newCount: 0,
  categoryStats: {},
  recentBadges: []
})
const currentCategory = ref('all')
const showDetail = ref(false)
const selectedBadge = ref<Badge | null>(null)

const categories = BADGE_CATEGORIES

const progressPercent = computed(() => {
  if (stats.value.totalBadges === 0) return 0
  return Math.round((stats.value.acquiredCount / stats.value.totalBadges) * 100)
})

const filteredBadges = computed(() => {
  if (currentCategory.value === 'all') {
    return badges.value
  }
  return badges.value.filter(b => b.category === currentCategory.value)
})

onMounted(() => {
  loadData()
})

onShow(() => {
  loadData()
})

async function loadData() {
  try {
    const [badgesRes, statsRes] = await Promise.all([
      getAllBadges(),
      getBadgeStats()
    ])
    
    if (badgesRes.code === 200 && badgesRes.data) {
      badges.value = badgesRes.data
    }
    
    if (statsRes.code === 200 && statsRes.data) {
      stats.value = statsRes.data
    }
    
    // 如果有新徽章，标记为已读
    if (stats.value.newCount > 0) {
      markBadgesAsRead()
    }
  } catch (error) {
    console.error('加载徽章数据失败:', error)
  }
}

function switchCategory(code: string) {
  currentCategory.value = code
}

function getCategoryCount(code: string): number {
  const stat = stats.value.categoryStats[code]
  return stat ? stat.acquired : 0
}

function showBadgeDetail(badge: Badge) {
  selectedBadge.value = badge
  showDetail.value = true
}

function closeDetail() {
  showDetail.value = false
  selectedBadge.value = null
}

function getConditionText(badge: Badge | null): string {
  if (!badge) return ''
  
  const typeMap: Record<string, string> = {
    'consecutive_checkin': `连续打卡${badge.conditionValue}天`,
    'weight_count': `记录体重${badge.conditionValue}次`,
    'weight_loss': `累计减重${badge.conditionValue}斤`,
    'weight_goal_achieved': '达成减重目标',
    'exercise_count': `记录运动${badge.conditionValue}次`,
    'calories_burned': `单日消耗${badge.conditionValue}千卡`,
    'meal_count': `记录饮食${badge.conditionValue}天`,
    'calories_control': `连续${badge.conditionValue}天热量达标`,
    'first_moment': '发布首条动态',
    'moment_count': `发布${badge.conditionValue}条动态`,
    'follow_count': `关注${badge.conditionValue}位好友`,
    'liked_count': `获得${badge.conditionValue}个赞`,
    'early_checkin': '早上6点前打卡',
    'late_checkin': '晚上10点后打卡',
    'perfect_week': '一周全部打卡',
    'comeback': '断签后连续打卡7天'
  }
  
  return typeMap[badge.conditionType] || `完成${badge.conditionType}`
}

function formatDate(time: string): string {
  const date = new Date(time)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

function formatDateTime(time: string): string {
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.badge-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 头部统计 */
.stats-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 30rpx;
  color: #fff;
}

.stats-main {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
}

.stats-item {
  text-align: center;
}

.stats-number {
  font-size: 72rpx;
  font-weight: 700;
  display: block;
}

.stats-number.total {
  opacity: 0.7;
}

.stats-divider {
  font-size: 48rpx;
  margin: 0 30rpx;
  opacity: 0.5;
}

.stats-label {
  font-size: 28rpx;
  opacity: 0.9;
}

.progress-bar {
  height: 12rpx;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 15rpx;
}

.progress-fill {
  height: 100%;
  background: #fff;
  border-radius: 6rpx;
  transition: width 0.5s ease;
}

.progress-text {
  font-size: 24rpx;
  text-align: center;
  display: block;
  opacity: 0.9;
}

/* 分类标签 */
.category-tabs {
  white-space: nowrap;
  background: #fff;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
}

.category-tab {
  display: inline-flex;
  align-items: center;
  padding: 15rpx 30rpx;
  margin-right: 15rpx;
  background: #f5f5f5;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: #666;
}

.category-tab.active {
  background: #667eea;
  color: #fff;
}

.tab-icon {
  margin-right: 8rpx;
}

.tab-count {
  margin-left: 8rpx;
  font-size: 22rpx;
  background: rgba(102, 126, 234, 0.2);
  padding: 2rpx 10rpx;
  border-radius: 10rpx;
}

.category-tab.active .tab-count {
  background: rgba(255, 255, 255, 0.3);
}

/* 徽章列表 */
.badge-list {
  padding: 20rpx;
}

.badge-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.badge-card {
  width: calc(33.33% - 14rpx);
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx 20rpx;
  text-align: center;
  position: relative;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.badge-card:not(.acquired) {
  opacity: 0.6;
}

/* 稀有度样式 */
.badge-card.common { border-top: 4rpx solid #95a5a6; }
.badge-card.rare { border-top: 4rpx solid #3498db; }
.badge-card.epic { border-top: 4rpx solid #9b59b6; }
.badge-card.legendary { border-top: 4rpx solid #f39c12; }

.new-badge {
  position: absolute;
  top: 10rpx;
  right: 10rpx;
  background: #ff4757;
  color: #fff;
  font-size: 18rpx;
  padding: 4rpx 10rpx;
  border-radius: 10rpx;
  font-weight: 600;
}

.badge-icon-wrapper {
  width: 100rpx;
  height: 100rpx;
  margin: 0 auto 15rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 50%;
  position: relative;
}

.badge-icon {
  font-size: 50rpx;
}

.locked-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lock-icon {
  font-size: 40rpx;
  opacity: 0.5;
}

.badge-name {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
  display: block;
  margin-bottom: 8rpx;
}

.badge-rarity {
  font-size: 22rpx;
  color: #999;
}

.acquire-time {
  font-size: 20rpx;
  color: #667eea;
  margin-top: 8rpx;
  display: block;
}

/* 详情弹窗 */
.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.detail-content {
  width: 80%;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
}

.detail-header {
  padding: 60rpx;
  text-align: center;
  position: relative;
}

.detail-header.common { background: linear-gradient(135deg, #ecf0f1 0%, #bdc3c7 100%); }
.detail-header.rare { background: linear-gradient(135deg, #ebf5fb 0%, #85c1e9 100%); }
.detail-header.epic { background: linear-gradient(135deg, #f5eef8 0%, #bb8fce 100%); }
.detail-header.legendary { background: linear-gradient(135deg, #fef5e7 0%, #f8c471 100%); }

.detail-icon {
  font-size: 120rpx;
  display: block;
}

.detail-locked {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
}

.detail-new {
  position: absolute;
  top: 20rpx;
  left: 20rpx;
  background: #ff4757;
  color: #fff;
  font-size: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-weight: 600;
}

.detail-body {
  padding: 40rpx;
  text-align: center;
}

.detail-name {
  font-size: 40rpx;
  font-weight: 700;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.detail-rarity {
  font-size: 28rpx;
  color: #667eea;
  display: block;
  margin-bottom: 30rpx;
}

.detail-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 30rpx;
}

.detail-condition {
  background: #f8f9fa;
  padding: 20rpx;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.condition-label {
  font-size: 26rpx;
  color: #999;
}

.condition-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.detail-time {
  font-size: 24rpx;
  color: #999;
}

.detail-footer {
  padding: 0 40rpx 40rpx;
}

.close-btn {
  width: 100%;
  height: 80rpx;
  background: #667eea;
  color: #fff;
  font-size: 30rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
