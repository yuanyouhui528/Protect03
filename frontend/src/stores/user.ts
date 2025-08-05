import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'
import { request } from '@/utils/request'

// 用户信息接口定义
export interface UserInfo {
  id: number
  username: string
  phone: string
  email?: string
  avatar?: string
  realName?: string
  status: number
  createTime: string
  roles: string[]
  permissions: string[]
}

// 登录请求参数
export interface LoginParams {
  username: string
  password: string
  captcha?: string
  rememberMe?: boolean
}

// 注册请求参数
export interface RegisterParams {
  phone: string
  password: string
  confirmPassword: string
  smsCode: string
  username: string
  realName?: string
}

// 登录响应数据
export interface LoginResponse {
  accessToken: string
  refreshToken: string
  userInfo: UserInfo
}

/**
 * 用户状态管理Store
 * 负责用户登录、注册、权限管理、状态持久化等功能
 */
export const useUserStore = defineStore('user', () => {
  // ==================== 状态定义 ====================
  
  // 用户基本信息
  const userInfo = ref<UserInfo | null>(null)
  
  // 访问令牌
  const accessToken = ref<string>('')
  
  // 刷新令牌
  const refreshToken = ref<string>('')
  
  // 登录状态
  const isLoggedIn = computed(() => {
    return !!accessToken.value && !!userInfo.value
  })
  
  // 用户权限列表
  const permissions = computed(() => {
    return userInfo.value?.permissions || []
  })
  
  // 用户角色列表
  const roles = computed(() => {
    return userInfo.value?.roles || []
  })
  
  // ==================== 持久化方法 ====================
  
  /**
   * 保存用户状态到本地存储
   */
  const saveToStorage = () => {
    if (accessToken.value) {
      localStorage.setItem('access_token', accessToken.value)
    }
    if (refreshToken.value) {
      localStorage.setItem('refresh_token', refreshToken.value)
    }
    if (userInfo.value) {
      localStorage.setItem('user_info', JSON.stringify(userInfo.value))
    }
  }
  
  /**
   * 从本地存储恢复用户状态
   */
  const loadFromStorage = () => {
    const token = localStorage.getItem('access_token')
    const refresh = localStorage.getItem('refresh_token')
    const user = localStorage.getItem('user_info')
    
    if (token) {
      accessToken.value = token
    }
    if (refresh) {
      refreshToken.value = refresh
    }
    if (user) {
      try {
        userInfo.value = JSON.parse(user)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        clearStorage()
      }
    }
  }
  
  /**
   * 清除本地存储的用户状态
   */
  const clearStorage = () => {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('user_info')
  }
  
  // ==================== API接口方法 ====================
  
  /**
   * 用户登录
   * @param params 登录参数
   */
  const login = async (params: LoginParams): Promise<boolean> => {
    try {
      const response = await request.post<LoginResponse>('/auth/login', params)
      
      // 保存登录状态
      accessToken.value = response.accessToken
      refreshToken.value = response.refreshToken
      userInfo.value = response.userInfo
      
      // 持久化到本地存储
      saveToStorage()
      
      ElMessage.success('登录成功')
      return true
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }
  
  /**
   * 用户注册
   * @param params 注册参数
   */
  const register = async (params: RegisterParams): Promise<boolean> => {
    try {
      await request.post('/auth/register', params)
      ElMessage.success('注册成功，请登录')
      return true
    } catch (error) {
      console.error('注册失败:', error)
      return false
    }
  }
  
  /**
   * 发送短信验证码
   * @param phone 手机号
   */
  const sendSmsCode = async (phone: string): Promise<boolean> => {
    try {
      await request.post('/auth/sms/send', { phone })
      ElMessage.success('验证码已发送')
      return true
    } catch (error) {
      console.error('发送验证码失败:', error)
      return false
    }
  }
  
  /**
   * 获取用户信息
   */
  const getUserInfo = async (): Promise<boolean> => {
    try {
      const response = await request.get<UserInfo>('/user/info')
      userInfo.value = response
      saveToStorage()
      return true
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return false
    }
  }
  
  /**
   * 更新用户信息
   * @param data 用户信息
   */
  const updateUserInfo = async (data: Partial<UserInfo>): Promise<boolean> => {
    try {
      const response = await request.put<UserInfo>('/user/info', data)
      userInfo.value = response
      saveToStorage()
      ElMessage.success('更新成功')
      return true
    } catch (error) {
      console.error('更新用户信息失败:', error)
      return false
    }
  }
  
  /**
   * 刷新访问令牌
   */
  const refreshAccessToken = async (): Promise<boolean> => {
    try {
      const response = await request.post<{ accessToken: string }>('/auth/refresh', {
        refreshToken: refreshToken.value
      })
      
      accessToken.value = response.accessToken
      saveToStorage()
      return true
    } catch (error) {
      console.error('刷新令牌失败:', error)
      // 刷新失败，清除所有状态
      logout()
      return false
    }
  }
  
  /**
   * 用户登出
   */
  const logout = async (): Promise<void> => {
    try {
      // 调用后端登出接口
      await request.post('/auth/logout')
    } catch (error) {
      console.error('登出接口调用失败:', error)
    } finally {
      // 无论接口是否成功，都清除本地状态
      accessToken.value = ''
      refreshToken.value = ''
      userInfo.value = null
      clearStorage()
      ElMessage.success('已退出登录')
    }
  }
  
  // ==================== 权限检查方法 ====================
  
  /**
   * 检查用户是否拥有指定权限
   * @param permission 权限标识
   */
  const hasPermission = (permission: string): boolean => {
    return permissions.value.includes(permission)
  }
  
  /**
   * 检查用户是否拥有指定角色
   * @param role 角色标识
   */
  const hasRole = (role: string): boolean => {
    return roles.value.includes(role)
  }
  
  /**
   * 检查用户是否拥有任意一个指定权限
   * @param permissionList 权限列表
   */
  const hasAnyPermission = (permissionList: string[]): boolean => {
    return permissionList.some(permission => hasPermission(permission))
  }
  
  /**
   * 检查用户是否拥有所有指定权限
   * @param permissionList 权限列表
   */
  const hasAllPermissions = (permissionList: string[]): boolean => {
    return permissionList.every(permission => hasPermission(permission))
  }
  
  // ==================== 初始化 ====================
  
  // 页面加载时恢复用户状态
  loadFromStorage()
  
  // ==================== 导出 ====================
  
  return {
    // 状态
    userInfo,
    accessToken,
    refreshToken,
    isLoggedIn,
    permissions,
    roles,
    
    // 认证方法
    login,
    register,
    logout,
    sendSmsCode,
    
    // 用户信息方法
    getUserInfo,
    updateUserInfo,
    
    // 令牌方法
    refreshAccessToken,
    
    // 权限检查方法
    hasPermission,
    hasRole,
    hasAnyPermission,
    hasAllPermissions,
    
    // 存储方法
    loadFromStorage,
    clearStorage
  }
})