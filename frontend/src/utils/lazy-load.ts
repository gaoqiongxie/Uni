/**
 * 图片懒加载工具
 */

interface LazyLoadOptions {
  rootMargin?: string
  threshold?: number
}

class LazyLoader {
  private observer: IntersectionObserver | null = null
  private imageMap: Map<Element, string> = new Map()

  constructor(options: LazyLoadOptions = {}) {
    if (typeof IntersectionObserver !== 'undefined') {
      this.observer = new IntersectionObserver(
        this.handleIntersection.bind(this),
        {
          rootMargin: options.rootMargin || '50px',
          threshold: options.threshold || 0
        }
      )
    }
  }

  private handleIntersection(entries: IntersectionObserverEntry[]) {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        this.loadImage(entry.target)
      }
    })
  }

  private loadImage(element: Element) {
    const src = this.imageMap.get(element)
    if (!src) return

    if (element.tagName === 'IMG') {
      const img = element as HTMLImageElement
      img.src = src
      img.classList.add('loaded')
    } else {
      // 处理背景图
      const el = element as HTMLElement
      el.style.backgroundImage = `url(${src})`
      el.classList.add('loaded')
    }

    this.imageMap.delete(element)
    this.observer?.unobserve(element)
  }

  /**
   * 添加图片到懒加载队列
   */
  observe(element: Element, src: string) {
    if (!this.observer) {
      // 不支持 IntersectionObserver，直接加载
      if (element.tagName === 'IMG') {
        (element as HTMLImageElement).src = src
      }
      return
    }

    this.imageMap.set(element, src)
    this.observer.observe(element)
  }

  /**
   * 停止观察元素
   */
  unobserve(element: Element) {
    this.imageMap.delete(element)
    this.observer?.unobserve(element)
  }

  /**
   * 销毁
   */
  destroy() {
    this.imageMap.clear()
    this.observer?.disconnect()
    this.observer = null
  }
}

// 全局懒加载实例
let globalLazyLoader: LazyLoader | null = null

/**
 * 获取全局懒加载实例
 */
export function getLazyLoader(): LazyLoader {
  if (!globalLazyLoader) {
    globalLazyLoader = new LazyLoader()
  }
  return globalLazyLoader
}

/**
 * 销毁全局懒加载实例
 */
export function destroyLazyLoader() {
  globalLazyLoader?.destroy()
  globalLazyLoader = null
}

export default LazyLoader
