import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { getUserTheme, saveUserTheme, switchThemeMode } from '@/api/theme.api'
import {
  ThemeMode,
  FontSize,
  LIGHT_THEME,
  DARK_THEME,
  getActualThemeMode,
  applyThemeVariables,
  THEME_COLORS,
  FONT_SIZE_OPTIONS,
} from '@/types/theme'
import type { UserTheme } from '@/types/theme'

/**
 * 主题Store
 */
export const useThemeStore = defineStore('theme', () => {
  // ========== State ==========
  
  /** 主题设置 */
  const themeSetting = ref<UserTheme>({
    themeMode: ThemeMode.SYSTEM,
    primaryColor: '#667eea',
    fontSize: FontSize.MEDIUM,
  })
  
  /** 是否已加载 */
  const loaded = ref(false)
  
  // ========== Getters ==========
  
  /** 当前实际主题模式 (light/dark) */
  const currentTheme = computed(() => getActualThemeMode(themeSetting.value.themeMode))
  
  /** 是否为深色模式 */
  const isDark = computed(() => currentTheme.value === 'dark')
  
  /** 当前主题变量 */
  const themeVariables = computed(() => (isDark.value ? DARK_THEME : LIGHT_THEME))
  
  /** 主题色 */
  const primaryColor = computed(() => themeSetting.value.primaryColor)
  
  /** 主题渐变 */
  const primaryGradient = computed(() => {
    const color = THEME_COLORS.find(c => c.value === themeSetting.value.primaryColor)
    return color?.gradient || `linear-gradient(135deg, ${themeSetting.value.primaryColor} 0%, ${themeSetting.value.primaryColor} 100%)`
  })
  
  /** 字体缩放比例 */
  const fontScale = computed(() => {
    const option = FONT_SIZE_OPTIONS.find(o => o.value === themeSetting.value.fontSize)
    return option?.scale || 1.0
  })
  
  // ========== Actions ==========
  
  /**
   * 加载用户主题设置
   */
  async function loadTheme() {
    try {
      const res = await getUserTheme()
      if (res.code === 200 && res.data) {
        themeSetting.value = {
          ...themeSetting.value,
          ...res.data,
        }
      }
      applyCurrentTheme()
      loaded.value = true
    } catch (error) {
      console.error('加载主题设置失败:', error)
      applyCurrentTheme()
    }
  }
  
  /**
   * 保存主题设置
   */
  async function saveTheme(setting: Partial<UserTheme>) {
    themeSetting.value = { ...themeSetting.value, ...setting }
    applyCurrentTheme()
    
    try {
      await saveUserTheme(themeSetting.value)
    } catch (error) {
      console.error('保存主题设置失败:', error)
    }
  }
  
  /**
   * 切换主题模式
   */
  async function setThemeMode(mode: ThemeMode) {
    themeSetting.value.themeMode = mode
    applyCurrentTheme()
    
    try {
      await switchThemeMode(mode)
    } catch (error) {
      console.error('切换主题模式失败:', error)
    }
  }
  
  /**
   * 设置主题色
   */
  async function setPrimaryColor(color: string) {
    themeSetting.value.primaryColor = color
    applyCurrentTheme()
    
    try {
      await saveUserTheme(themeSetting.value)
    } catch (error) {
      console.error('保存主题色失败:', error)
    }
  }
  
  /**
   * 设置字体大小
   */
  async function setFontSize(size: FontSize) {
    themeSetting.value.fontSize = size
    applyCurrentTheme()
    
    try {
      await saveUserTheme(themeSetting.value)
    } catch (error) {
      console.error('保存字体大小失败:', error)
    }
  }
  
  /**
   * 应用当前主题
   */
  function applyCurrentTheme() {
    // 应用主题变量
    applyThemeVariables(themeVariables.value)
    
    // 设置主题色
    const style = document.documentElement.style
    style.setProperty('--primary-color', themeSetting.value.primaryColor)
    style.setProperty('--font-scale', String(fontScale.value))
    
    // 设置深色模式类名
    // #ifdef H5
    if (isDark.value) {
      document.documentElement.classList.add('dark')
      document.body.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
      document.body.classList.remove('dark')
    }
    // #endif
  }
  
  /**
   * 切换深色/浅色模式（快捷方法）
   */
  function toggleTheme() {
    const newMode = isDark.value ? ThemeMode.LIGHT : ThemeMode.DARK
    setThemeMode(newMode)
  }
  
  // 监听系统主题变化
  // #ifdef H5
  if (typeof window !== 'undefined' && window.matchMedia) {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    mediaQuery.addEventListener('change', () => {
      if (themeSetting.value.themeMode === ThemeMode.SYSTEM) {
        applyCurrentTheme()
      }
    })
  }
  // #endif
  
  return {
    // State
    themeSetting,
    loaded,
    // Getters
    currentTheme,
    isDark,
    themeVariables,
    primaryColor,
    primaryGradient,
    fontScale,
    // Actions
    loadTheme,
    saveTheme,
    setThemeMode,
    setPrimaryColor,
    setFontSize,
    applyCurrentTheme,
    toggleTheme,
  }
})
