import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi } from '../api/user.api'
import {
  setToken, setRefreshToken, setUserId, setUserInfo,
  getToken, getUserId, getUserInfo, clearToken, isLoggedIn
} from '../utils/storage'
import type { UserInfoVO, UserLoginDTO, UserRegisterDTO } from '../types/user'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfoVO | null>(null)
  const isLogin = ref(false)

  /**
   * 应用启动时，从本地存储恢复登录状态
   */
  function initUserState() {
    if (isLoggedIn()) {
      const cached = getUserInfo()
      if (cached) {
        userInfo.value = cached
        isLogin.value = true
      }
    }
  }

  /**
   * 登录
   */
  async function login(data: UserLoginDTO) {
    const result = await userApi.login(data)
    _saveLoginInfo(result)
    return result
  }

  /**
   * 注册
   */
  async function register(data: UserRegisterDTO) {
    const result = await userApi.register(data)
    _saveLoginInfo(result)
    return result
  }

  /**
   * 刷新用户信息
   */
  async function refreshUserInfo() {
    const result = await userApi.getUserInfo()
    userInfo.value = result
    setUserInfo(result)
    return result
  }

  /**
   * 更新个人资料
   */
  async function updateProfile(data: Partial<UserInfoVO>) {
    const result = await userApi.updateUserInfo(data)
    userInfo.value = { ...userInfo.value, ...result } as UserInfoVO
    setUserInfo(userInfo.value)
    return result
  }

  /**
   * 退出登录
   */
  async function logout() {
    try {
      await userApi.logout()
    } catch (e) {
      // 忽略退出接口报错
    } finally {
      clearToken()
      userInfo.value = null
      isLogin.value = false
      uni.reLaunch({ url: '/pages/user/login' })
    }
  }

  function _saveLoginInfo(info: UserInfoVO) {
    if (info.token) setToken(info.token)
    if (info.refreshToken) setRefreshToken(info.refreshToken)
    if (info.id) setUserId(info.id)
    setUserInfo(info)
    userInfo.value = info
    isLogin.value = true
  }

  return {
    userInfo,
    isLogin,
    initUserState,
    login,
    register,
    refreshUserInfo,
    updateProfile,
    logout
  }
})
