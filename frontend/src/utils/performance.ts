/**
 * 性能优化工具
 */

/**
 * 防抖函数
 */
export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): (...args: Parameters<T>) => void {
  let timer: ReturnType<typeof setTimeout> | null = null
  
  return function (...args: Parameters<T>) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(null, args)
    }, delay)
  }
}

/**
 * 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  interval: number = 300
): (...args: Parameters<T>) => void {
  let lastTime = 0
  
  return function (...args: Parameters<T>) {
    const now = Date.now()
    if (now - lastTime >= interval) {
      lastTime = now
      fn.apply(null, args)
    }
  }
}

/**
 * 分页加载优化 - 虚拟列表
 */
export class VirtualList<T> {
  private allData: T[] = []
  private visibleData: T[] = []
  private pageSize: number = 20
  private currentPage: number = 1
  private loading: boolean = false
  private hasMore: boolean = true

  constructor(pageSize: number = 20) {
    this.pageSize = pageSize
  }

  /**
   * 设置数据
   */
  setData(data: T[]) {
    this.allData = data
    this.currentPage = 1
    this.hasMore = data.length > this.pageSize
    this.updateVisibleData()
  }

  /**
   * 追加数据
   */
  appendData(data: T[]) {
    this.allData.push(...data)
    this.hasMore = data.length === this.pageSize
  }

  /**
   * 加载更多
   */
  loadMore(): T[] {
    if (this.loading || !this.hasMore) return []
    
    this.loading = true
    this.currentPage++
    const start = (this.currentPage - 1) * this.pageSize
    const end = start + this.pageSize
    const newData = this.allData.slice(start, end)
    this.visibleData.push(...newData)
    this.hasMore = end < this.allData.length
    this.loading = false
    
    return newData
  }

  /**
   * 获取可见数据
   */
  getVisibleData(): T[] {
    return this.visibleData
  }

  /**
   * 是否有更多数据
   */
  hasMoreData(): boolean {
    return this.hasMore
  }

  /**
   * 是否正在加载
   */
  isLoading(): boolean {
    return this.loading
  }

  private updateVisibleData() {
    const end = this.currentPage * this.pageSize
    this.visibleData = this.allData.slice(0, end)
  }
}

/**
 * 预加载图片
 */
export function preloadImages(urls: string[]): Promise<void> {
  return new Promise((resolve) => {
    let loaded = 0
    const total = urls.length
    
    if (total === 0) {
      resolve()
      return
    }

    urls.forEach(url => {
      const img = new Image()
      img.onload = () => {
        loaded++
        if (loaded === total) resolve()
      }
      img.onerror = () => {
        loaded++
        if (loaded === total) resolve()
      }
      img.src = url
    })
  })
}

/**
 * 页面可见性优化
 * 页面不可见时暂停某些操作
 */
export function usePageVisibility(
  onVisible?: () => void,
  onHidden?: () => void
) {
  const handleVisibilityChange = () => {
    if (document.hidden) {
      onHidden?.()
    } else {
      onVisible?.()
    }
  }

  onMounted(() => {
    document.addEventListener('visibilitychange', handleVisibilityChange)
  })

  onUnmounted(() => {
    document.removeEventListener('visibilitychange', handleVisibilityChange)
  })
}

import { onMounted, onUnmounted } from 'vue'
