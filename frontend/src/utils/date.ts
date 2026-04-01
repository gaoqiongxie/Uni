/**
 * 日期工具函数
 */

/**
 * 格式化日期
 */
export function formatDate(date: Date | string, fmt = 'yyyy-MM-dd'): string {
  if (typeof date === 'string') {
    date = new Date(date.replace(/-/g, '/'))
  }
  const o: Record<string, number> = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds()
  }
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  for (const k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? String(o[k]) : ('00' + o[k]).substr(('' + o[k]).length)
      )
    }
  }
  return fmt
}

/**
 * 获取今天日期字符串
 */
export function today(): string {
  return formatDate(new Date(), 'yyyy-MM-dd')
}

/**
 * 获取N天前的日期
 */
export function daysAgo(n: number): string {
  const d = new Date()
  d.setDate(d.getDate() - n)
  return formatDate(d, 'yyyy-MM-dd')
}

/**
 * 获取某月的天数
 */
export function getDaysInMonth(year: number, month: number): number {
  return new Date(year, month, 0).getDate()
}

/**
 * 格式化时间戳为 "xx分钟前" / "xx小时前" 等
 */
export function timeAgo(timestamp: number): string {
  const now = Date.now()
  const diff = Math.floor((now - timestamp) / 1000)

  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  if (diff < 2592000) return `${Math.floor(diff / 86400)}天前`
  return formatDate(new Date(timestamp), 'yyyy-MM-dd')
}
