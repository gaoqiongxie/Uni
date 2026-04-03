import { request } from '@/utils/request'
import type { UserMessage, UserPushSetting } from '@/types/notification'

/**
 * 获取消息列表
 */
export function getMessageList(params: {
  userId: number
  type?: string
  isRead?: number
  page?: number
  size?: number
}) {
  return request<{ records: UserMessage[]; total: number }>({
    url: '/api/notification/list',
    method: 'GET',
    data: params
  })
}

/**
 * 获取未读消息数量
 */
export function getUnreadCount(userId: number) {
  return request<number>({
    url: '/api/notification/unread-count',
    method: 'GET',
    data: { userId }
  })
}

/**
 * 标记消息已读
 */
export function markAsRead(messageId: number) {
  return request<void>({
    url: `/api/notification/read/${messageId}`,
    method: 'POST'
  })
}

/**
 * 标记所有消息已读
 */
export function markAllAsRead(userId: number) {
  return request<void>({
    url: '/api/notification/read-all',
    method: 'POST',
    data: { userId }
  })
}

/**
 * 删除消息
 */
export function deleteMessage(messageId: number) {
  return request<void>({
    url: `/api/notification/${messageId}`,
    method: 'DELETE'
  })
}

/**
 * 获取推送设置
 */
export function getPushSetting(userId: number) {
  return request<UserPushSetting>({
    url: '/api/notification/push-setting',
    method: 'GET',
    data: { userId }
  })
}

/**
 * 更新推送设置
 */
export function updatePushSetting(userId: number, setting: UserPushSetting) {
  return request<void>({
    url: '/api/notification/push-setting',
    method: 'PUT',
    data: { userId, ...setting }
  })
}
