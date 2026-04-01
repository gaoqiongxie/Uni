/**
 * 本地存储工具
 */

const TOKEN_KEY = 'uni_token'
const REFRESH_TOKEN_KEY = 'uni_refresh_token'
const USER_ID_KEY = 'uni_user_id'
const USER_INFO_KEY = 'uni_user_info'

export function setToken(token: string): void {
  uni.setStorageSync(TOKEN_KEY, token)
}

export function getToken(): string {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

export function clearToken(): void {
  uni.removeStorageSync(TOKEN_KEY)
  uni.removeStorageSync(REFRESH_TOKEN_KEY)
  uni.removeStorageSync(USER_ID_KEY)
  uni.removeStorageSync(USER_INFO_KEY)
}

export function setRefreshToken(token: string): void {
  uni.setStorageSync(REFRESH_TOKEN_KEY, token)
}

export function getRefreshToken(): string {
  return uni.getStorageSync(REFRESH_TOKEN_KEY) || ''
}

export function setUserId(userId: number): void {
  uni.setStorageSync(USER_ID_KEY, userId)
}

export function getUserId(): number | null {
  return uni.getStorageSync(USER_ID_KEY) || null
}

export function setUserInfo(userInfo: any): void {
  uni.setStorageSync(USER_INFO_KEY, JSON.stringify(userInfo))
}

export function getUserInfo(): any | null {
  const str = uni.getStorageSync(USER_INFO_KEY)
  return str ? JSON.parse(str) : null
}

export function isLoggedIn(): boolean {
  return !!getToken()
}
