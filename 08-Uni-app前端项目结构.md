# 轻瘦 - Uni-app前端项目结构

## 🏗️ 项目脚手架

### 项目初始化

```bash
# 使用HBuilderX创建项目
# 1. 选择项目类型: Uni-app
# 2. 模板选择: 默认模板
# 3. Vue版本: 3
# 4. 启用TypeScript: 是
# 5. 启用Vuex/Pinia: Pinia
```

### 项目目录结构

```
qingshou-app/
├── src/                           # 源码目录
│   ├── pages/                     # 页面文件
│   │   ├── index/                 # 首页
│   │   │   ├── index.vue          # 首页
│   │   │   └── index.scss         # 首页样式
│   │   ├── user/                  # 用户相关
│   │   │   ├── login.vue          # 登录页面
│   │   │   ├── register.vue       # 注册页面
│   │   │   ├── profile.vue        # 个人资料
│   │   │   └── edit-profile.vue   # 编辑资料
│   │   ├── health/                # 健康档案
│   │   │   ├── record-weight.vue  # 记录体重
│   │   │   ├── weight-history.vue # 体重历史
│   │   │   ├── health-goal.vue    # 健康目标
│   │   │   └── bmi-calculator.vue # BMI计算
│   │   ├── meal/                  # 餐食打卡
│   │   │   ├── meal-record.vue    # 打卡页面
│   │   │   ├── meal-history.vue   # 历史记录
│   │   │   ├── meal-calendar.vue  # 打卡日历
│   │   │   └── upload-photo.vue   # 上传照片
│   │   ├── dashboard/             # 数据看板
│   │   │   ├── dashboard.vue      # 数据概览
│   │   │   ├── charts.vue         # 图表展示
│   │   │   └── stats.vue          # 统计页面
│   │   └── settings/              # 设置页面
│   │       ├── settings.vue       # 设置首页
│   │       ├── notification.vue   # 通知设置
│   │       └── about.vue          # 关于我们
│   ├── components/                # 公共组件
│   │   ├── common/                # 通用组件
│   │   │   ├── HeaderBar.vue      # 导航栏
│   │   │   ├── TabBar.vue         # 底部Tab
│   │   │   ├── Loading.vue        # 加载组件
│   │   │   ├── EmptyData.vue      # 空数据组件
│   │   │   └── ErrorTip.vue       # 错误提示
│   │   ├── health/                # 健康相关组件
│   │   │   ├── WeightCard.vue     # 体重卡片
│   │   │   ├── BMIProgress.vue    # BMI进度条
│   │   │   ├── GoalCard.vue       # 目标卡片
│   │   │   └── TrendChart.vue     # 趋势图表
│   │   ├── meal/                  # 餐食相关组件
│   │   │   ├── MealCard.vue       # 餐食卡片
│   │   │   ├── PhotoUploader.vue  # 照片上传器
│   │   │   ├── MealCalendar.vue   # 打卡日历组件
│   │   │   └── FoodEstimate.vue   # 食物热量估算
│   │   └── ui/                    # UI组件
│   │       ├── Button.vue         # 按钮组件
│   │       ├── Input.vue          # 输入框组件
│   │       ├── Card.vue           # 卡片组件
│   │       ├── Modal.vue          # 模态框
│   │       └── Tabs.vue           # 标签页
│   ├── store/                     # 状态管理
│   │   ├── index.ts               # Pinia主入口
│   │   ├── user.ts                # 用户状态
│   │   ├── health.ts              # 健康数据状态
│   │   ├── meal.ts                # 餐食记录状态
│   │   └── app.ts                 # 应用状态
│   ├── api/                       # API接口
│   │   ├── index.ts               # API主入口
│   │   ├── request.ts             # 请求拦截器
│   │   ├── user.api.ts            # 用户相关API
│   │   ├── health.api.ts          # 健康相关API
│   │   ├── meal.api.ts            # 餐食相关API
│   │   └── upload.api.ts          # 上传相关API
│   ├── utils/                     # 工具函数
│   │   ├── index.ts               # 工具函数入口
│   │   ├── storage.ts             # 本地存储封装
│   │   ├── validate.ts            # 表单验证
│   │   ├── date.ts                # 日期处理
│   │   ├── calculation.ts         # 计算工具
│   │   ├── image.ts               # 图片处理
│   │   └── common.ts              # 通用函数
│   ├── static/                    # 静态资源
│   │   ├── images/                # 图片资源
│   │   │   ├── icons/             # 图标
│   │   │   ├── avatars/           # 头像
│   │   │   └── illustrations/     # 插画
│   │   └── fonts/                 # 字体文件
│   ├── styles/                    # 样式文件
│   │   ├── index.scss             # 主样式文件
│   │   ├── variables.scss         # 样式变量
│   │   ├── mixins.scss            # 样式混合
│   │   ├── reset.scss             # 重置样式
│   │   └── common.scss            # 公共样式
│   └── types/                     # TypeScript类型定义
│       ├── api.ts                 # API相关类型
│       ├── user.ts                # 用户相关类型
│       ├── health.ts              # 健康相关类型
│       ├── meal.ts                # 餐食相关类型
│       └── common.ts              # 通用类型
├── uniapp.config.ts               # Uni-app配置文件
├── package.json                   # 依赖管理
├── tsconfig.json                  # TypeScript配置
├── manifest.json                  # 应用配置
└── pages.json                     # 页面配置
```

---

## 📱 核心页面实现

### 1. 登录页面 (login.vue)

```vue
<template>
  <view class="login-container">
    <view class="login-header">
      <image class="logo" src="/static/images/logo.png" mode="aspectFit" />
      <text class="app-name">轻瘦</text>
      <text class="app-slogan">科学减脂，轻松生活</text>
    </view>

    <view class="login-form">
      <u-form :model="formData" :rules="rules" ref="formRef">
        <u-form-item label="手机号" prop="phone">
          <u-input 
            v-model="formData.phone" 
            placeholder="请输入手机号"
            maxlength="11"
            type="number"
          />
        </u-form-item>

        <u-form-item label="密码" prop="password">
          <u-input 
            v-model="formData.password" 
            placeholder="请输入密码"
            type="password"
            password
          />
        </u-form-item>
      </u-form>

      <view class="form-actions">
        <u-button 
          type="primary" 
          shape="circle" 
          @click="handleLogin"
          :loading="loading"
        >
          登录
        </u-button>

        <view class="register-tip">
          <text>还没有账号？</text>
          <text class="register-link" @click="navigateToRegister">立即注册</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from '@/utils/router'

const formRef = ref()
const loading = ref(false)
const userStore = useUserStore()
const router = useRouter()

const formData = reactive({
  phone: '',
  password: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    loading.value = true
    await userStore.login({
      phone: formData.phone,
      password: formData.password
    })
    
    uni.showToast({
      title: '登录成功',
      icon: 'success'
    })
    
    // 跳转到首页
    router.switchTab('/pages/index/index')
  } catch (error: any) {
    uni.showToast({
      title: error.message || '登录失败',
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}

const navigateToRegister = () => {
  router.navigateTo('/pages/user/register')
}
</script>

<style lang="scss" scoped>
.login-container {
  padding: 60rpx 40rpx;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .login-header {
    text-align: center;
    margin-bottom: 100rpx;
    
    .logo {
      width: 200rpx;
      height: 200rpx;
      margin-bottom: 30rpx;
    }
    
    .app-name {
      display: block;
      font-size: 56rpx;
      font-weight: bold;
      color: #fff;
      margin-bottom: 20rpx;
    }
    
    .app-slogan {
      font-size: 28rpx;
      color: rgba(255, 255, 255, 0.8);
    }
  }
  
  .login-form {
    background: #fff;
    border-radius: 20rpx;
    padding: 60rpx 40rpx;
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
  }
  
  .form-actions {
    margin-top: 80rpx;
    
    .register-tip {
      text-align: center;
      margin-top: 40rpx;
      font-size: 28rpx;
      color: #999;
      
      .register-link {
        color: #007aff;
        margin-left: 10rpx;
      }
    }
  }
}
</style>
```

### 2. 体重记录页面 (record-weight.vue)

```vue
<template>
  <view class="record-weight-container">
    <u-navbar
      title="记录体重"
      :border="false"
      :autoBack="true"
    />
    
    <view class="form-container">
      <u-form :model="formData" :rules="rules" ref="formRef">
        <u-form-item label="体重(kg)" prop="weight">
          <u-input
            v-model="formData.weight"
            placeholder="请输入体重"
            type="digit"
            suffix="kg"
            @blur="calculateBMI"
          />
        </u-form-item>
        
        <u-form-item label="体脂率(%)" prop="bodyFatRate">
          <u-input
            v-model="formData.bodyFatRate"
            placeholder="请输入体脂率"
            type="digit"
            suffix="%"
          />
        </u-form-item>
        
        <u-form-item label="记录日期" prop="recordDate">
          <u-input
            v-model="formData.recordDate"
            placeholder="请选择日期"
            disabled
            @click="showDatePicker = true"
          />
          <u-icon slot="right" name="calendar" size="20"></u-icon>
        </u-form-item>
        
        <u-form-item label="备注" prop="remark">
          <u-textarea
            v-model="formData.remark"
            placeholder="可选：记录心情或特殊情况"
            maxlength="200"
            showCount
          />
        </u-form-item>
      </u-form>
      
      <view class="bmi-info" v-if="bmiValue > 0">
        <view class="bmi-label">BMI指数</view>
        <view class="bmi-value">{{ bmiValue.toFixed(1) }}</view>
        <view class="bmi-status" :class="getBMIClass(bmiValue)">
          {{ getBMIStatus(bmiValue) }}
        </view>
      </view>
      
      <u-button
        type="primary"
        shape="circle"
        :loading="loading"
        @click="handleSubmit"
        class="submit-btn"
      >
        保存记录
      </u-button>
    </view>
    
    <u-datetime-picker
      :show="showDatePicker"
      v-model="formData.recordDate"
      mode="date"
      @confirm="showDatePicker = false"
      @cancel="showDatePicker = false"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useHealthStore } from '@/store/health'
import { useUserStore } from '@/store/user'
import { formatDate } from '@/utils/date'

const formRef = ref()
const loading = ref(false)
const showDatePicker = ref(false)
const healthStore = useHealthStore()
const userStore = useUserStore()

const formData = reactive({
  weight: '',
  bodyFatRate: '',
  recordDate: formatDate(new Date(), 'yyyy-MM-dd'),
  remark: ''
})

const rules = {
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' },
    { validator: validateWeight, message: '体重范围应在20-200kg之间', trigger: 'blur' }
  ],
  bodyFatRate: [
    { validator: validateBodyFat, message: '体脂率范围应在5-60%之间', trigger: 'blur' }
  ]
}

// 计算BMI
const bmiValue = computed(() => {
  if (!formData.weight || !userStore.userInfo?.height) return 0
  const weight = parseFloat(formData.weight)
  const height = userStore.userInfo.height / 100 // 转为米
  return weight / (height * height)
})

function validateWeight(value: string) {
  const weight = parseFloat(value)
  return weight >= 20 && weight <= 200
}

function validateBodyFat(value: string) {
  if (!value) return true
  const rate = parseFloat(value)
  return rate >= 5 && rate <= 60
}

function calculateBMI() {
  // 触发计算
}

function getBMIClass(bmi: number) {
  if (bmi < 18.5) return 'underweight'
  if (bmi < 24) return 'normal'
  if (bmi < 28) return 'overweight'
  return 'obese'
}

function getBMIStatus(bmi: number) {
  if (bmi < 18.5) return '偏瘦'
  if (bmi < 24) return '正常'
  if (bmi < 28) return '超重'
  return '肥胖'
}

async function handleSubmit() {
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    loading.value = true
    
    await healthStore.createWeightRecord({
      weight: parseFloat(formData.weight),
      bodyFatRate: formData.bodyFatRate ? parseFloat(formData.bodyFatRate) : undefined,
      recordDate: formData.recordDate,
      remark: formData.remark
    })
    
    uni.showToast({
      title: '记录成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error: any) {
    uni.showToast({
      title: error.message || '记录失败',
      icon: 'error'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 设置默认日期为今天
  formData.recordDate = formatDate(new Date(), 'yyyy-MM-dd')
})
</script>

<style lang="scss" scoped>
.record-weight-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  
  .form-container {
    padding: 30rpx;
    
    .bmi-info {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 16rpx;
      padding: 40rpx 30rpx;
      margin: 40rpx 0;
      color: #fff;
      text-align: center;
      
      .bmi-label {
        font-size: 28rpx;
        opacity: 0.9;
        margin-bottom: 10rpx;
      }
      
      .bmi-value {
        font-size: 72rpx;
        font-weight: bold;
        margin-bottom: 10rpx;
      }
      
      .bmi-status {
        font-size: 32rpx;
        font-weight: 500;
        
        &.underweight {
          color: #ffc107;
        }
        
        &.normal {
          color: #4caf50;
        }
        
        &.overweight {
          color: #ff9800;
        }
        
        &.obese {
          color: #f44336;
        }
      }
    }
    
    .submit-btn {
      margin-top: 60rpx;
    }
  }
}
</style>
```

---

## 📦 核心组件实现

### 1. 请求拦截器 (request.ts)

```typescript
import { showToast, showLoading, hideLoading } from '@/utils/ui'
import { getToken, clearToken } from '@/utils/storage'

// 创建请求实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 显示加载中
    if (config.showLoading !== false) {
      showLoading()
    }
    
    // 添加token
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 添加用户ID
    const userId = uni.getStorageSync('userId')
    if (userId) {
      config.headers['X-User-Id'] = userId
    }
    
    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    hideLoading()
    
    const { code, message, data } = response.data
    
    // 请求成功
    if (code === 200) {
      return data
    }
    
    // 特殊错误码处理
    if (code === 401) {
      // token过期
      clearToken()
      uni.reLaunch({
        url: '/pages/user/login'
      })
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }
    
    // 其他错误
    const errorMessage = message || '请求失败'
    showToast(errorMessage)
    return Promise.reject(new Error(errorMessage))
  },
  (error) => {
    hideLoading()
    
    // 网络错误
    if (!error.response) {
      showToast('网络连接失败，请检查网络设置')
      return Promise.reject(new Error('网络连接失败'))
    }
    
    // HTTP错误
    const status = error.response.status
    switch (status) {
      case 400:
        showToast('请求参数错误')
        break
      case 403:
        showToast('没有权限访问')
        break
      case 404:
        showToast('请求资源不存在')
        break
      case 500:
        showToast('服务器内部错误')
        break
      default:
        showToast('请求失败')
    }
    
    return Promise.reject(error)
  }
)

export default request
```

### 2. 用户API封装 (user.api.ts)

```typescript
import request from './request'
import type { UserLoginDTO, UserRegisterDTO, UserUpdateDTO, UserInfoVO } from '@/types/user'

export const userApi = {
  // 用户注册
  register(data: UserRegisterDTO): Promise<UserInfoVO> {
    return request.post('/api/user/register', data)
  },
  
  // 用户登录
  login(data: UserLoginDTO): Promise<UserInfoVO> {
    return request.post('/api/user/login', data)
  },
  
  // 获取用户信息
  getUserInfo(): Promise<UserInfoVO> {
    return request.get('/api/user/info')
  },
  
  // 更新用户信息
  updateUserInfo(data: UserUpdateDTO): Promise<UserInfoVO> {
    return request.put('/api/user/update', data)
  },
  
  // 更新体重
  updateWeight(weight: number): Promise<UserInfoVO> {
    return request.put('/api/user/weight', { weight })
  },
  
  // 退出登录
  logout(): Promise<void> {
    return request.post('/api/user/logout')
  },
  
  // 发送验证码
  sendSmsCode(phone: string): Promise<void> {
    return request.post('/api/user/sms-code', { phone })
  },
  
  // 重置密码
  resetPassword(data: { phone: string; code: string; newPassword: string }): Promise<void> {
    return request.post('/api/user/reset-password', data)
  }
}
```

### 3. 健康API封装 (health.api.ts)

```typescript
import request from './request'
import type { 
  WeightRecordDTO, 
  WeightRecordVO,
  WeightStatsVO 
} from '@/types/health'

export const healthApi = {
  // 创建体重记录
  createWeightRecord(data: WeightRecordDTO): Promise<WeightRecordVO> {
    return request.post('/api/weight-record', data)
  },
  
  // 获取体重记录列表
  getWeightRecords(params: {
    page?: number
    size?: number
    startDate?: string
    endDate?: string
  }): Promise<{
    records: WeightRecordVO[]
    total: number
    page: number
    size: number
  }> {
    return request.get('/api/weight-record/list', { params })
  },
  
  // 获取体重统计
  getWeightStats(params: {
    startDate?: string
    endDate?: string
  }): Promise<WeightStatsVO> {
    return request.get('/api/weight-record/stats', { params })
  },
  
  // 删除体重记录
  deleteWeightRecord(id: number): Promise<void> {
    return request.delete(`/api/weight-record/${id}`)
  },
  
  // 获取BMI分析
  getBMIAnalysis(): Promise<{
    currentBMI: number
    status: string
    suggestion: string
    trend: Array<{ date: string; bmi: number }>
  }> {
    return request.get('/api/health/bmi-analysis')
  }
}
```

### 4. 餐食API封装 (meal.api.ts)

```typescript
import request from './request'
import type { 
  MealRecordDTO, 
  MealRecordVO,
  MealStatsVO,
  MealCalendarVO 
} from '@/types/meal'

export const mealApi = {
  // 创建餐食记录
  createMealRecord(data: MealRecordDTO): Promise<MealRecordVO> {
    return request.post('/api/meal-record', data)
  },
  
  // 获取某天的餐食记录
  getMealRecordsByDate(date: string): Promise<MealRecordVO[]> {
    return request.get(`/api/meal-record/date/${date}`)
  },
  
  // 获取最近7天统计
  getRecentMealStats(): Promise<MealStatsVO> {
    return request.get('/api/meal-record/stats/recent')
  },
  
  // 获取打卡日历
  getMealCalendar(params: {
    year: number
    month: number
  }): Promise<MealCalendarVO> {
    return request.get('/api/meal-record/calendar', { params })
  },
  
  // 删除餐食记录
  deleteMealRecord(id: number): Promise<void> {
    return request.delete(`/api/meal-record/${id}`)
  },
  
  // 估算食物热量
  estimateCalorie(data: {
    foodName: string
    quantity: number
    unit: string
  }): Promise<{ calorie: number }> {
    return request.post('/api/meal-record/estimate-calorie', data)
  }
}
```

---

## 🎨 样式配置

### 1. 样式变量 (variables.scss)

```scss
// 颜色系统
$primary-color: #667eea;
$primary-light: #764ba2;
$success-color: #4caf50;
$warning-color: #ff9800;
$error-color: #f44336;
$info-color: #2196f3;

// 中性色
$text-primary: #333333;
$text-secondary: #666666;
$text-tertiary: #999999;
$text-disabled: #cccccc;

$bg-color: #f5f5f5;
$bg-white: #ffffff;
$bg-gray: #f8f8f8;
$bg-dark: #333333;

// 边框
$border-color: #e0e0e0;
$border-radius-sm: 8rpx;
$border-radius: 16rpx;
$border-radius-lg: 32rpx;

// 字体
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
$font-size-xs: 20rpx;
$font-size-sm: 24rpx;
$font-size: 28rpx;
$font-size-md: 32rpx;
$font-size-lg: 36rpx;
$font-size-xl: 40rpx;

// 间距
$spacing-xs: 8rpx;
$spacing-sm: 16rpx;
$spacing: 24rpx;
$spacing-md: 32rpx;
$spacing-lg: 48rpx;
$spacing-xl: 64rpx;

// 阴影
$shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
$shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
$shadow-md: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
$shadow-lg: 0 16rpx 48rpx rgba(0, 0, 0, 0.1);

// 动画
$transition-duration: 0.3s;
$transition-timing: ease-in-out;
```

### 2. 工具函数 (utils/common.ts)

```typescript
/**
 * 防抖函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: NodeJS.Timeout | null = null
  
  return function(this: any, ...args: Parameters<T>) {
    if (timeout) clearTimeout(timeout)
    
    timeout = setTimeout(() => {
      func.apply(this, args)
    }, wait)
  }
}

/**
 * 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let lastTime = 0
  let timer: NodeJS.Timeout | null = null
  
  return function(this: any, ...args: Parameters<T>) {
    const now = Date.now()
    
    if (now - lastTime < wait) {
      if (timer) clearTimeout(timer)
      timer = setTimeout(() => {
        lastTime = now
        func.apply(this, args)
      }, wait)
    } else {
      lastTime = now
      func.apply(this, args)
    }
  }
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 生成UUID
 */
export function generateUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

/**
 * 深拷贝
 */
export function deepClone<T>(obj: T): T {
  if (obj === null || typeof obj !== 'object') {
    return obj
  }
  
  if (obj instanceof Date) {
    return new Date(obj.getTime()) as T
  }
  
  if (obj instanceof Array) {
    return obj.map(item => deepClone(item)) as T
  }
  
  if (typeof obj === 'object') {
    const clonedObj: any = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
  
  return obj
}
```

---

## 🚀 启动配置

### 1. package.json 示例

```json
{
  "name": "qingshou-app",
  "version": "1.0.0",
  "description": "轻瘦 - 科学减脂健康应用",
  "main": "main.js",
  "scripts": {
    "dev": "npm run dev:h5",
    "dev:h5": "uni -p h5",
    "dev:mp-weixin": "uni -p mp-weixin",
    "dev:mp-alipay": "uni -p mp-alipay",
    "build": "npm run build:h5",
    "build:h5": "uni build -p h5",
    "build:mp-weixin": "uni build -p mp-weixin",
    "build:mp-alipay": "uni build -p mp-alipay",
    "lint": "eslint --ext .vue,.js,.ts src/",
    "format": "prettier --write \"src/**/*.{vue,js,ts,json}\""
  },
  "dependencies": {
    "@dcloudio/uni-app": "^3.0.0",
    "@dcloudio/uni-components": "^3.0.0",
    "@dcloudio/uni-h5": "^3.0.0",
    "@dcloudio/uni-mp-weixin": "^3.0.0",
    "axios": "^1.6.0",
    "pinia": "^2.1.0",
    "uview-plus": "^3.0.0",
    "vue": "^3.3.0",
    "vue-router": "^4.2.0"
  },
  "devDependencies": {
    "@dcloudio/types": "^3.0.0",
    "@dcloudio/uni-automator": "^3.0.0",
    "@types/node": "^20.0.0",
    "@typescript-eslint/eslint-plugin": "^6.0.0",
    "@typescript-eslint/parser": "^6.0.0",
    "eslint": "^8.0.0",
    "eslint-plugin-vue": "^9.0.0",
    "prettier": "^3.0.0",
    "sass": "^1.60.0",
    "typescript": "^5.0.0",
    "vite": "^4.0.0"
  },
  "browserslist": [
    "Android >= 4.4",
    "ios >= 9"
  ]
}
```

### 2. uni-app配置文件 (uniapp.config.ts)

```typescript
import { defineUniConfig } from '@dcloudio/uni-app'

export default defineUniConfig({
  // 基础配置
  appid: 'your-appid',
  name: '轻瘦',
  versionName: '1.0.0',
  versionCode: '100',
  
  // 编译配置
  compiler: {
    type: 'vue3',
    vueVersion: '3'
  },
  
  // 平台配置
  h5: {
    title: '轻瘦 - 科学减脂健康应用',
    router: {
      mode: 'hash'
    },
    devServer: {
      port: 3000,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true
        }
      }
    }
  },
  
  // 微信小程序配置
  'mp-weixin': {
    appid: 'your-weixin-appid',
    setting: {
      urlCheck: false,
      es6: true,
      enhance: true,
      postcss: true
    },
    usingComponents: true
  },
  
  // 支付宝小程序配置
  'mp-alipay': {
    usingComponents: true
  },
  
  // 通用配置
  uniStatistics: {
    enable: false
  },
  
  // 性能优化
  optimization: {
    treeShaking: {
      enable: true
    }
  }
})
```

---

**文档版本**: V1.0
**更新时间**: 2026-03-31
**前端负责**: 高琼
