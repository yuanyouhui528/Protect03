import { request } from '@/utils/request'
import type { LoginParams, RegisterParams, UserInfo, LoginResponse } from '@/stores/user'

/**
 * 用户管理模块API接口
 * 提供用户认证、信息管理、权限查询等功能的API调用
 */

// ==================== 认证相关接口 ====================

/**
 * 用户登录
 * @param params 登录参数
 */
export const loginApi = (params: LoginParams) => {
  return request.post<LoginResponse>('/auth/login', params)
}

/**
 * 用户注册
 * @param params 注册参数
 */
export const registerApi = (params: RegisterParams) => {
  return request.post('/auth/register', params)
}

/**
 * 用户登出
 */
export const logoutApi = () => {
  return request.post('/auth/logout')
}

/**
 * 刷新访问令牌
 * @param refreshToken 刷新令牌
 */
export const refreshTokenApi = (refreshToken: string) => {
  return request.post<{ accessToken: string }>('/auth/refresh', {
    refreshToken
  })
}

// ==================== 短信验证码接口 ====================

/**
 * 发送注册验证码
 * @param phone 手机号
 */
export const sendRegisterSmsApi = (phone: string) => {
  return request.post('/auth/sms/register', { phone })
}

/**
 * 发送登录验证码
 * @param phone 手机号
 */
export const sendLoginSmsApi = (phone: string) => {
  return request.post('/auth/sms/login', { phone })
}

/**
 * 发送重置密码验证码
 * @param phone 手机号
 */
export const sendResetPasswordSmsApi = (phone: string) => {
  return request.post('/auth/sms/reset-password', { phone })
}

/**
 * 验证短信验证码
 * @param phone 手机号
 * @param code 验证码
 */
export const verifySmsCodeApi = (phone: string, code: string) => {
  return request.post<{ valid: boolean }>('/auth/sms/verify', {
    phone,
    code
  })
}

// ==================== 用户信息接口 ====================

/**
 * 获取当前用户信息
 */
export const getUserInfoApi = () => {
  return request.get<UserInfo>('/user/info')
}

/**
 * 更新用户基本信息
 * @param data 用户信息
 */
export const updateUserInfoApi = (data: Partial<UserInfo>) => {
  return request.put<UserInfo>('/user/info', data)
}

/**
 * 修改用户密码
 * @param oldPassword 原密码
 * @param newPassword 新密码
 */
export const changePasswordApi = (oldPassword: string, newPassword: string) => {
  return request.put('/user/password', {
    oldPassword,
    newPassword
  })
}

/**
 * 重置密码
 * @param phone 手机号
 * @param smsCode 短信验证码
 * @param newPassword 新密码
 */
export const resetPasswordApi = (phone: string, smsCode: string, newPassword: string) => {
  return request.post('/auth/reset-password', {
    phone,
    smsCode,
    newPassword
  })
}

/**
 * 上传用户头像
 * @param file 头像文件
 */
export const uploadAvatarApi = (file: File) => {
  const formData = new FormData()
  formData.append('avatar', file)
  
  return request.post<{ avatarUrl: string }>('/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// ==================== 权限相关接口 ====================

/**
 * 获取用户权限列表
 */
export const getUserPermissionsApi = () => {
  return request.get<string[]>('/user/permissions')
}

/**
 * 获取用户角色列表
 */
export const getUserRolesApi = () => {
  return request.get<string[]>('/user/roles')
}

/**
 * 检查用户是否拥有指定权限
 * @param permission 权限标识
 */
export const checkPermissionApi = (permission: string) => {
  return request.get<{ hasPermission: boolean }>(`/user/check-permission/${permission}`)
}

// ==================== 用户管理接口（管理员功能） ====================

/**
 * 用户列表查询参数
 */
export interface UserListParams {
  page?: number
  size?: number
  keyword?: string
  status?: number
  role?: string
}

/**
 * 用户列表响应数据
 */
export interface UserListResponse {
  content: UserInfo[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

/**
 * 获取用户列表（管理员功能）
 * @param params 查询参数
 */
export const getUserListApi = (params: UserListParams) => {
  return request.get<UserListResponse>('/admin/users', { params })
}

/**
 * 创建用户（管理员功能）
 * @param data 用户数据
 */
export const createUserApi = (data: Partial<UserInfo>) => {
  return request.post<UserInfo>('/admin/users', data)
}

/**
 * 更新用户信息（管理员功能）
 * @param id 用户ID
 * @param data 用户数据
 */
export const updateUserApi = (id: number, data: Partial<UserInfo>) => {
  return request.put<UserInfo>(`/admin/users/${id}`, data)
}

/**
 * 删除用户（管理员功能）
 * @param id 用户ID
 */
export const deleteUserApi = (id: number) => {
  return request.delete(`/admin/users/${id}`)
}

/**
 * 启用/禁用用户（管理员功能）
 * @param id 用户ID
 * @param status 状态（1-启用，0-禁用）
 */
export const toggleUserStatusApi = (id: number, status: number) => {
  return request.put(`/admin/users/${id}/status`, { status })
}

/**
 * 重置用户密码（管理员功能）
 * @param id 用户ID
 * @param newPassword 新密码
 */
export const resetUserPasswordApi = (id: number, newPassword: string) => {
  return request.put(`/admin/users/${id}/reset-password`, { newPassword })
}

// ==================== 角色权限接口 ====================

/**
 * 角色信息
 */
export interface RoleInfo {
  id: number
  name: string
  code: string
  description: string
  permissions: string[]
}

/**
 * 获取所有角色列表
 */
export const getRoleListApi = () => {
  return request.get<RoleInfo[]>('/admin/roles')
}

/**
 * 获取所有权限列表
 */
export const getPermissionListApi = () => {
  return request.get<string[]>('/admin/permissions')
}

/**
 * 为用户分配角色
 * @param userId 用户ID
 * @param roleIds 角色ID列表
 */
export const assignUserRolesApi = (userId: number, roleIds: number[]) => {
  return request.put(`/admin/users/${userId}/roles`, { roleIds })
}