import { request } from '@/utils/request'

// 通知类型枚举
export enum NotificationType {
  EXCHANGE_APPLICATION = 'EXCHANGE_APPLICATION',
  EXCHANGE_APPROVAL = 'EXCHANGE_APPROVAL',
  EXCHANGE_REJECTION = 'EXCHANGE_REJECTION',
  EXCHANGE_COMPLETION = 'EXCHANGE_COMPLETION',
  SYSTEM_ANNOUNCEMENT = 'SYSTEM_ANNOUNCEMENT',
  LEAD_EXPIRY_REMINDER = 'LEAD_EXPIRY_REMINDER',
  RATING_UPDATE = 'RATING_UPDATE'
}

// 发送渠道枚举
export enum SendChannel {
  SYSTEM = 'SYSTEM',
  EMAIL = 'EMAIL',
  SMS = 'SMS'
}

// 发送状态枚举
export enum SendStatus {
  PENDING = 'PENDING',
  SENT = 'SENT',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED'
}

// 通知接口
export interface Notification {
  id: number
  recipientId: number
  notificationType: NotificationType
  sendChannel: SendChannel
  title: string
  content: string
  status: SendStatus
  isRead: boolean
  priority: number
  messageId?: string
  createTime: string
  updateTime?: string
  readTime?: string
  sendTime?: string
}

// 通知设置接口
export interface NotificationSettings {
  id: number
  userId: number
  notificationType: NotificationType
  systemEnabled: boolean
  emailEnabled: boolean
  smsEnabled: boolean
  doNotDisturbStart?: string
  doNotDisturbEnd?: string
  frequencyLimit?: number
  createTime: string
  updateTime?: string
}

// 分页查询参数
export interface NotificationQueryParams {
  page?: number
  size?: number
  notificationType?: NotificationType
  isRead?: boolean
  startTime?: string
  endTime?: string
}

// 分页响应
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

// 发送通知请求
export interface SendNotificationRequest {
  recipientId: number
  notificationType: NotificationType
  sendChannel: SendChannel
  title: string
  content: string
  priority?: number
}

// 更新通知设置请求
export interface UpdateNotificationSettingsRequest {
  notificationType: NotificationType
  systemEnabled: boolean
  emailEnabled: boolean
  smsEnabled: boolean
  doNotDisturbStart?: string
  doNotDisturbEnd?: string
  frequencyLimit?: number
}

/**
 * 通知API服务
 */
export class NotificationAPI {
  /**
   * 获取通知列表
   */
  static async getNotifications(params: NotificationQueryParams = {}): Promise<PageResponse<Notification>> {
    const response = await request.get('/api/notifications', { params })
    return response.data
  }

  /**
   * 获取通知详情
   */
  static async getNotificationDetail(id: number): Promise<Notification> {
    const response = await request.get(`/api/notifications/${id}`)
    return response.data
  }

  /**
   * 标记通知为已读
   */
  static async markAsRead(id: number): Promise<boolean> {
    const response = await request.put(`/api/notifications/${id}/read`)
    return response.data
  }

  /**
   * 标记所有通知为已读
   */
  static async markAllAsRead(): Promise<number> {
    const response = await request.put('/api/notifications/read-all')
    return response.data
  }

  /**
   * 获取未读通知数量
   */
  static async getUnreadCount(): Promise<number> {
    const response = await request.get('/api/notifications/unread-count')
    return response.data
  }

  /**
   * 删除通知
   */
  static async deleteNotification(id: number): Promise<boolean> {
    const response = await request.delete(`/api/notifications/${id}`)
    return response.data
  }

  /**
   * 批量删除通知
   */
  static async batchDeleteNotifications(ids: number[]): Promise<boolean> {
    const response = await request.delete('/api/notifications/batch', {
      data: { ids }
    })
    return response.data
  }

  /**
   * 发送通知
   */
  static async sendNotification(request: SendNotificationRequest): Promise<Notification> {
    const response = await request.post('/api/notifications/send', request)
    return response.data
  }

  /**
   * 获取通知设置
   */
  static async getNotificationSettings(): Promise<NotificationSettings[]> {
    const response = await request.get('/api/notifications/settings')
    return response.data
  }

  /**
   * 更新通知设置
   */
  static async updateNotificationSettings(
    settingsRequest: UpdateNotificationSettingsRequest
  ): Promise<NotificationSettings> {
    const response = await request.put('/api/notifications/settings', settingsRequest)
    return response.data
  }

  /**
   * 获取通知统计信息
   */
  static async getNotificationStats(): Promise<{
    totalCount: number
    unreadCount: number
    todayCount: number
    weekCount: number
  }> {
    const response = await request.get('/api/notifications/stats')
    return response.data
  }

  /**
   * 清空所有已读通知
   */
  static async clearReadNotifications(): Promise<number> {
    const response = await request.delete('/api/notifications/clear-read')
    return response.data
  }

  /**
   * 获取通知类型列表
   */
  static async getNotificationTypes(): Promise<{
    type: NotificationType
    name: string
    description: string
  }[]> {
    const response = await request.get('/api/notifications/types')
    return response.data
  }

  /**
   * 测试通知发送
   */
  static async testNotification(
    notificationType: NotificationType,
    sendChannel: SendChannel
  ): Promise<boolean> {
    const response = await request.post('/api/notifications/test', {
      notificationType,
      sendChannel
    })
    return response.data
  }
}

// 导出默认实例
export default NotificationAPI

// 导出常用方法的简化版本
export const {
  getNotifications,
  getNotificationDetail,
  markAsRead,
  markAllAsRead,
  getUnreadCount,
  deleteNotification,
  batchDeleteNotifications,
  sendNotification,
  getNotificationSettings,
  updateNotificationSettings,
  getNotificationStats,
  clearReadNotifications,
  getNotificationTypes,
  testNotification
} = NotificationAPI