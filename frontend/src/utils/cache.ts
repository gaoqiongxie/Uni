/**
 * 缓存工具类
 */

interface CacheItem<T> {
  data: T
  expireAt: number
}

class CacheManager {
  private memoryCache: Map<string, CacheItem<any>> = new Map()
  private readonly prefix = 'uni_cache_'

  /**
   * 设置缓存
   */
  set<T>(key: string, data: T, expireMinutes: number = 5): void {
    const item: CacheItem<T> = {
      data,
      expireAt: Date.now() + expireMinutes * 60 * 1000
    }
    
    // 内存缓存
    this.memoryCache.set(key, item)
    
    // 本地存储
    try {
      uni.setStorageSync(this.prefix + key, item)
    } catch (e) {
      console.error('Cache set error:', e)
    }
  }

  /**
   * 获取缓存
   */
  get<T>(key: string): T | null {
    // 先查内存
    let item = this.memoryCache.get(key)
    
    // 内存未命中，查本地
    if (!item) {
      try {
        item = uni.getStorageSync(this.prefix + key)
        if (item) {
          this.memoryCache.set(key, item)
        }
      } catch (e) {
        console.error('Cache get error:', e)
      }
    }

    if (!item) return null

    // 检查过期
    if (Date.now() > item.expireAt) {
      this.remove(key)
      return null
    }

    return item.data as T
  }

  /**
   * 删除缓存
   */
  remove(key: string): void {
    this.memoryCache.delete(key)
    try {
      uni.removeStorageSync(this.prefix + key)
    } catch (e) {
      console.error('Cache remove error:', e)
    }
  }

  /**
   * 清空缓存
   */
  clear(): void {
    this.memoryCache.clear()
    try {
      const keys = uni.getStorageInfoSync().keys
      keys.forEach(key => {
        if (key.startsWith(this.prefix)) {
          uni.removeStorageSync(key)
        }
      })
    } catch (e) {
      console.error('Cache clear error:', e)
    }
  }

  /**
   * 检查是否存在
   */
  has(key: string): boolean {
    return this.get(key) !== null
  }
}

// 全局缓存实例
const cacheManager = new CacheManager()

export default cacheManager

/**
 * API 请求缓存装饰器
 */
export function withCache<T>(
  fn: (...args: any[]) => Promise<T>,
  keyGenerator: (...args: any[]) => string,
  expireMinutes: number = 5
) {
  return async function (...args: any[]): Promise<T> {
    const cacheKey = keyGenerator(...args)
    const cached = cacheManager.get<T>(cacheKey)
    
    if (cached !== null) {
      return cached
    }

    const result = await fn(...args)
    cacheManager.set(cacheKey, result, expireMinutes)
    return result
  }
}
