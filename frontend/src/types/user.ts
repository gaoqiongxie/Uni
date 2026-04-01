// 用户相关类型定义

export interface UserInfoVO {
  id: number
  username: string
  phone: string
  nickname: string
  avatarUrl: string
  gender: number
  birthday?: string
  height?: number
  currentWeight?: number
  targetWeight?: number
  bmi?: number
  activityLevel: number
  calorieGoal: number
  userStatus: number
  lastLoginTime?: string
  token?: string
  refreshToken?: string
}

export interface UserLoginDTO {
  phone: string
  password: string
}

export interface PhoneLoginDTO {
  phone: string
  smsCode: string
}

export interface UserRegisterDTO {
  phone: string
  password: string
  nickname: string
  smsCode: string
}

export interface UserUpdateDTO {
  nickname?: string
  gender?: number
  birthday?: string
  height?: number
  targetWeight?: number
  activityLevel?: number
  calorieGoal?: number
}
