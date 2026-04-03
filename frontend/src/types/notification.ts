/**
 * 消息通知类型定义
 */

// 消息实体
export interface UserMessage {
  id: number
  userId: number
  type: MessageType
  title: string
  content: string
  relatedId?: number
  relatedType?: string
  isRead: number
  createTime: string
  readTime?: string
}

// 消息类型
export type MessageType = 'MEAL_REMIND' | 'EXERCISE_REMIND' | 'WEIGHT_REMIND' | 'GOAL_ACHIEVE' | 'SYSTEM'

// 消息类型显示名称
export const MessageTypeNames: Record<MessageType, string> = {
  MEAL_REMIND: '餐食提醒',
  EXERCISE_REMIND: '运动提醒',
  WEIGHT_REMIND: '体重提醒',
  GOAL_ACHIEVE: '目标达成',
  SYSTEM: '系统通知'
}

// 推送设置
export interface UserPushSetting {
  id?: number
  userId: number
  mealRemind: number
  mealRemindTime: string
  exerciseRemind: number
  exerciseRemindTime: string
  weightRemind: number
  weightRemindTime: string
  goalAchieveRemind: number
  systemNotify: number
  createTime?: string
  updateTime?: string
}

// 默认推送设置
export const defaultPushSetting: UserPushSetting = {
  userId: 0,
  mealRemind: 1,
  mealRemindTime: '08:00,12:00,18:00',
  exerciseRemind: 1,
  exerciseRemindTime: '20:00',
  weightRemind: 1,
  weightRemindTime: '09:00',
  goalAchieveRemind: 1,
  systemNotify: 1
}
