import { getToken, getUserId, clearToken } from './storage'

// 接口基础地址
const BASE_URL = 'http://localhost:8080'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  params?: Record<string, any>
  showLoading?: boolean
  loadingTitle?: string
}

interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

/**
 * 统一请求封装
 */
function request<T = any>(options: RequestOptions): Promise<T> {
  const {
    url,
    method = 'GET',
    data,
    params,
    showLoading = true,
    loadingTitle = '加载中...'
  } = options

  // 拼接query参数
  let fullUrl = BASE_URL + url
  if (params && Object.keys(params).length > 0) {
    const query = Object.entries(params)
      .filter(([, v]) => v !== undefined && v !== null)
      .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
      .join('&')
    if (query) fullUrl += '?' + query
  }

  if (showLoading) {
    uni.showLoading({ title: loadingTitle, mask: true })
  }

  const token = getToken()
  const userId = getUserId()
  const headers: Record<string, string> = {
    'Content-Type': 'application/json'
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  if (userId) {
    headers['X-User-Id'] = String(userId)
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method,
      data: data ? JSON.stringify(data) : undefined,
      header: headers,
      success: (res) => {
        if (showLoading) uni.hideLoading()

        const response = res.data as ApiResponse<T>

        if (response.code === 200) {
          resolve(response.data)
        } else if (response.code === 401) {
          clearToken()
          uni.reLaunch({ url: '/pages/user/login' })
          reject(new Error('登录已过期，请重新登录'))
        } else {
          const msg = response.message || '请求失败'
          uni.showToast({ title: msg, icon: 'none' })
          reject(new Error(msg))
        }
      },
      fail: (err) => {
        if (showLoading) uni.hideLoading()
        uni.showToast({ title: '网络连接失败', icon: 'none' })
        reject(new Error('网络连接失败'))
      }
    })
  })
}

export const http = {
  get: <T = any>(url: string, params?: Record<string, any>, options?: Partial<RequestOptions>) =>
    request<T>({ url, method: 'GET', params, ...options }),

  post: <T = any>(url: string, data?: any, options?: Partial<RequestOptions>) =>
    request<T>({ url, method: 'POST', data, ...options }),

  put: <T = any>(url: string, data?: any, options?: Partial<RequestOptions>) =>
    request<T>({ url, method: 'PUT', data, ...options }),

  delete: <T = any>(url: string, options?: Partial<RequestOptions>) =>
    request<T>({ url, method: 'DELETE', ...options })
}

export default http
