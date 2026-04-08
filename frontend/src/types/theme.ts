/**
 * 主题类型定义
 */

/** 主题模式 */
export enum ThemeMode {
  /** 跟随系统 */
  SYSTEM = 0,
  /** 浅色模式 */
  LIGHT = 1,
  /** 深色模式 */
  DARK = 2,
}

/** 字体大小 */
export enum FontSize {
  /** 小 */
  SMALL = 0,
  /** 中 */
  MEDIUM = 1,
  /** 大 */
  LARGE = 2,
}

/** 用户主题设置 */
export interface UserTheme {
  id?: number
  /** 主题模式 */
  themeMode: ThemeMode
  /** 主题色 */
  primaryColor: string
  /** 字体大小 */
  fontSize: FontSize
  createdAt?: string
  updatedAt?: string
}

/** 主题色选项 */
export const THEME_COLORS = [
  { name: '紫罗兰', value: '#667eea', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { name: '天空蓝', value: '#4facfe', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { name: '薄荷绿', value: '#43e97b', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { name: '珊瑚橙', value: '#fa709a', gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
  { name: '深海蓝', value: '#30cfd0', gradient: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)' },
  { name: '日落红', value: '#f093fb', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
]

/** 主题模式选项 */
export const THEME_MODE_OPTIONS = [
  { label: '跟随系统', value: ThemeMode.SYSTEM, icon: '🔆' },
  { label: '浅色模式', value: ThemeMode.LIGHT, icon: '☀️' },
  { label: '深色模式', value: ThemeMode.DARK, icon: '🌙' },
]

/** 字体大小选项 */
export const FONT_SIZE_OPTIONS = [
  { label: '小', value: FontSize.SMALL, scale: 0.9 },
  { label: '中', value: FontSize.MEDIUM, scale: 1.0 },
  { label: '大', value: FontSize.LARGE, scale: 1.1 },
]

/** 浅色主题变量 */
export const LIGHT_THEME = {
  // 背景色
  '--bg-primary': '#f5f7fa',
  '--bg-secondary': '#ffffff',
  '--bg-tertiary': '#f0f2f5',
  '--bg-card': '#ffffff',
  '--bg-input': '#f5f7fa',
  '--bg-hover': '#f0f2f5',
  '--bg-pressed': '#e8eaed',
  
  // 文字色
  '--text-primary': '#1a1a2e',
  '--text-secondary': '#666666',
  '--text-tertiary': '#999999',
  '--text-inverse': '#ffffff',
  '--text-disabled': '#cccccc',
  
  // 边框色
  '--border-primary': '#e8eaed',
  '--border-secondary': '#f0f2f5',
  '--border-focus': 'var(--primary-color)',
  
  // 阴影
  '--shadow-sm': '0 2rpx 8rpx rgba(0, 0, 0, 0.04)',
  '--shadow-md': '0 4rpx 16rpx rgba(0, 0, 0, 0.08)',
  '--shadow-lg': '0 8rpx 32rpx rgba(0, 0, 0, 0.12)',
  
  // 遮罩
  '--mask': 'rgba(0, 0, 0, 0.5)',
  '--mask-light': 'rgba(0, 0, 0, 0.2)',
}

/** 深色主题变量 */
export const DARK_THEME = {
  // 背景色
  '--bg-primary': '#0d0d0d',
  '--bg-secondary': '#1a1a1a',
  '--bg-tertiary': '#262626',
  '--bg-card': '#1a1a1a',
  '--bg-input': '#262626',
  '--bg-hover': '#333333',
  '--bg-pressed': '#404040',
  
  // 文字色
  '--text-primary': '#ffffff',
  '--text-secondary': '#b3b3b3',
  '--text-tertiary': '#808080',
  '--text-inverse': '#1a1a2e',
  '--text-disabled': '#666666',
  
  // 边框色
  '--border-primary': '#333333',
  '--border-secondary': '#262626',
  '--border-focus': 'var(--primary-color)',
  
  // 阴影
  '--shadow-sm': '0 2rpx 8rpx rgba(0, 0, 0, 0.3)',
  '--shadow-md': '0 4rpx 16rpx rgba(0, 0, 0, 0.4)',
  '--shadow-lg': '0 8rpx 32rpx rgba(0, 0, 0, 0.5)',
  
  // 遮罩
  '--mask': 'rgba(0, 0, 0, 0.7)',
  '--mask-light': 'rgba(0, 0, 0, 0.4)',
}

/**
 * 应用主题CSS变量
 */
export function applyThemeVariables(theme: Record<string, string>) {
  const style = document.documentElement.style
  Object.entries(theme).forEach(([key, value]) => {
    style.setProperty(key, value)
  })
}

/**
 * 检测系统是否为深色模式
 */
export function isSystemDarkMode(): boolean {
  // #ifdef H5
  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
  // #endif
  // #ifndef H5
  return false
  // #endif
}

/**
 * 获取实际主题模式
 */
export function getActualThemeMode(themeMode: ThemeMode): 'light' | 'dark' {
  if (themeMode === ThemeMode.SYSTEM) {
    return isSystemDarkMode() ? 'dark' : 'light'
  }
  return themeMode === ThemeMode.DARK ? 'dark' : 'light'
}
