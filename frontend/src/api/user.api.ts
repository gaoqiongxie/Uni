import http from '../utils/request'
import type { UserInfoVO, UserLoginDTO, PhoneLoginDTO, UserRegisterDTO, UserUpdateDTO } from '../types/user'

export const userApi = {
  register: (data: UserRegisterDTO) =>
    http.post<UserInfoVO>('/api/user/register', data),

  login: (data: UserLoginDTO) =>
    http.post<UserInfoVO>('/api/user/login', data),

  loginByPhone: (data: PhoneLoginDTO) =>
    http.post<UserInfoVO>('/api/user/login-by-phone', data),

  getUserInfo: () =>
    http.get<UserInfoVO>('/api/user/info'),

  updateUserInfo: (data: UserUpdateDTO) =>
    http.put<UserInfoVO>('/api/user/update', data),

  updateWeight: (weight: number) =>
    http.put<UserInfoVO>('/api/user/weight', null, { params: { weight } }),

  logout: () =>
    http.post<void>('/api/user/logout'),

  sendSmsCode: (phone: string, type = 'register') =>
    http.post<void>('/api/user/sms-code', null, { params: { phone, type } })
}
