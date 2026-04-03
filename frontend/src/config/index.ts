// 环境配置
const env = import.meta.env.VITE_APP_ENV || 'development'

// 不同环境的API地址
const API_BASE_URL = {
  development: 'http://localhost:8080',
  test: 'http://test-api.qingshou.com',
  production: 'https://api.qingshou.com'
}

// 当前环境的API地址
export const BASE_URL = API_BASE_URL[env as keyof typeof API_BASE_URL] || API_BASE_URL.development

// 应用配置
export const APP_CONFIG = {
  name: '轻瘦',
  version: '1.0.0',
  env,
  // 分页默认大小
  pageSize: 10,
  // 图片压缩配置
  imageCompress: {
    quality: 80,
    maxWidth: 1080,
    maxHeight: 1080
  },
  // 缓存过期时间（毫秒）
  cacheExpire: 30 * 60 * 1000 // 30分钟
}

export default APP_CONFIG
