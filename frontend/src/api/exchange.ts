import request from '@/utils/request'

// 交换申请相关接口
export const exchangeApi = {
  // 申请线索交换
  applyForExchange(data: {
    targetLeadId: number
    offeredLeadIds: number[]
    reason: string
  }) {
    return request({
      url: '/api/exchanges/apply',
      method: 'post',
      data
    })
  },

  // 获取交换申请详情
  getExchangeApplication(id: number) {
    return request({
      url: `/api/exchanges/${id}`,
      method: 'get'
    })
  },

  // 审核通过交换申请
  approveExchange(id: number, data: { responseMessage?: string }) {
    return request({
      url: `/api/exchanges/${id}/approve`,
      method: 'post',
      data
    })
  },

  // 拒绝交换申请
  rejectExchange(id: number, data: { responseMessage?: string }) {
    return request({
      url: `/api/exchanges/${id}/reject`,
      method: 'post',
      data
    })
  },

  // 取消交换申请
  cancelExchange(id: number) {
    return request({
      url: `/api/exchanges/${id}/cancel`,
      method: 'post'
    })
  },

  // 获取我发起的交换申请
  getMyApplications(params?: {
    page?: number
    size?: number
    status?: string
  }) {
    return request({
      url: '/api/exchanges/my/applications',
      method: 'get',
      params
    })
  },

  // 获取我收到的交换申请
  getReceivedApplications(params?: {
    page?: number
    size?: number
    status?: string
  }) {
    return request({
      url: '/api/exchanges/my/received',
      method: 'get',
      params
    })
  },

  // 获取我的交换历史
  getMyExchangeHistory(params?: {
    page?: number
    size?: number
  }) {
    return request({
      url: '/api/exchanges/my/history',
      method: 'get',
      params
    })
  },

  // 获取用户积分信息
  getUserCreditInfo() {
    return request({
      url: '/api/exchanges/credits/info',
      method: 'get'
    })
  },

  // 获取积分排行榜
  getCreditRanking(limit: number = 10) {
    return request({
      url: '/api/exchanges/credits/ranking',
      method: 'get',
      params: { limit }
    })
  },

  // 管理员：批量处理过期申请
  processExpiredApplications() {
    return request({
      url: '/api/exchanges/admin/process-expired',
      method: 'post'
    })
  }
}

// 交换状态枚举
export enum ExchangeStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  CANCELLED = 'CANCELLED',
  EXPIRED = 'EXPIRED'
}

// 交换申请类型定义
export interface ExchangeApplication {
  id: number
  applicantId: number
  applicantName: string
  targetLeadId: number
  targetLeadTitle: string
  targetLeadOwnerId: number
  targetLeadOwnerName: string
  offeredLeadIds: string
  offeredLeadTitles: string
  exchangeReason: string
  status: ExchangeStatus
  responseMessage?: string
  createdAt: string
  updatedAt: string
  expiresAt: string
}

// 交换历史类型定义
export interface ExchangeHistory {
  id: number
  applicantId: number
  applicantName: string
  targetLeadOwnerId: number
  targetLeadOwnerName: string
  exchangedLeadTitles: string
  exchangeValue: number
  createdAt: string
}

// 用户积分类型定义
export interface UserCredit {
  id: number
  userId: number
  userName?: string
  balance: number
  totalEarned: number
  totalSpent: number
  lastUpdated: string
}

// 线索交换价值计算结果
export interface ExchangeValueResult {
  offeredValue: number
  targetValue: number
  valueDifference: number
  isFair: boolean
  requiredCredits: number
}