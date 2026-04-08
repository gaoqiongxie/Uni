import { request } from '@/utils/request'
import type { UserTheme } from '@/types/theme'

/**
 * 获取用户主题设置
 */
export function getUserTheme() {
  return request<UserTheme>({
    url: '/api/theme/get',
    method: 'GET',
  })
}

/**
 * 保存主题设置
 */
export function saveUserTheme(data: UserTheme) {
  return request<void>({
    url: '/api/theme/save',
    method: 'POST',
    data,
  })
}

/**
 * 切换主题模式
 */
export function switchThemeMode(themeMode: number) {
  return request<void>({
    url: `/api/theme/switch/${themeMode}`,
    method: 'POST',
  })
}
