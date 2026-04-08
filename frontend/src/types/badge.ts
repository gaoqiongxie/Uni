/**
 * 成就徽章类型定义
 */

/** 徽章 */
export interface Badge {
  id: number
  code: string
  name: string
  description?: string
  icon?: string
  category: 'general' | 'weight' | 'meal' | 'exercise' | 'social'
  rarity: 'common' | 'rare' | 'epic' | 'legendary'
  conditionType: string
  conditionValue: number
  acquired: boolean
  acquireTime?: string
  isNew?: boolean
  categoryName: string
  rarityName: string
}

/** 分类统计 */
export interface CategoryStat {
  code: string
  name: string
  acquired: number
  total: number
}

/** 用户徽章统计 */
export interface UserBadgeStats {
  totalBadges: number
  acquiredCount: number
  newCount: number
  categoryStats: Record<string, CategoryStat>
  recentBadges: Badge[]
}

/** 徽章分类配置 */
export const BADGE_CATEGORIES = [
  { code: 'all', name: '全部', icon: '🏆' },
  { code: 'general', name: '通用', icon: '🌟' },
  { code: 'weight', name: '体重', icon: '⚖️' },
  { code: 'meal', name: '饮食', icon: '🥗' },
  { code: 'exercise', name: '运动', icon: '🏃' },
  { code: 'social', name: '社交', icon: '👥' }
]

/** 稀有度配置 */
export const BADGE_RARITIES = {
  common: { name: '普通', color: '#95a5a6', bgColor: '#ecf0f1' },
  rare: { name: '稀有', color: '#3498db', bgColor: '#ebf5fb' },
  epic: { name: '史诗', color: '#9b59b6', bgColor: '#f5eef8' },
  legendary: { name: '传说', color: '#f39c12', bgColor: '#fef5e7' }
}
