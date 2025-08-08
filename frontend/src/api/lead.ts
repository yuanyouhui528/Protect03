import request from '@/utils/request'

// 线索相关接口
export const leadApi = {
  // 获取可交换的线索列表
  getExchangeableLeads(params?: {
    page?: number
    size?: number
    search?: string
    rating?: string
    industry?: string
    region?: string
  }) {
    return request({
      url: '/api/leads/exchangeable',
      method: 'get',
      params
    })
  },

  // 获取可交换线索数量
  getExchangeableLeadsCount() {
    return request({
      url: '/api/leads/exchangeable/count',
      method: 'get'
    })
  },

  // 获取我的线索
  getMyLeads(params?: {
    page?: number
    size?: number
    exchangeable?: boolean
    rating?: string
    status?: string
  }) {
    return request({
      url: '/api/leads/my',
      method: 'get',
      params
    })
  },

  // 获取线索详情
  getLeadDetail(id: number) {
    return request({
      url: `/api/leads/${id}`,
      method: 'get'
    })
  },

  // 创建线索
  createLead(data: {
    title: string
    companyName: string
    industry: string
    region: string
    contactName: string
    contactPhone: string
    contactEmail?: string
    description: string
    requirements?: string
  }) {
    return request({
      url: '/api/leads',
      method: 'post',
      data
    })
  },

  // 更新线索
  updateLead(id: number, data: {
    title?: string
    companyName?: string
    industry?: string
    region?: string
    contactName?: string
    contactPhone?: string
    contactEmail?: string
    description?: string
    requirements?: string
  }) {
    return request({
      url: `/api/leads/${id}`,
      method: 'put',
      data
    })
  },

  // 删除线索
  deleteLead(id: number) {
    return request({
      url: `/api/leads/${id}`,
      method: 'delete'
    })
  },

  // 获取线索列表（管理员）
  getLeadsList(params?: {
    page?: number
    size?: number
    search?: string
    rating?: string
    status?: string
    ownerId?: number
  }) {
    return request({
      url: '/api/leads',
      method: 'get',
      params
    })
  },

  // 设置线索为可交换状态
  setLeadExchangeable(id: number, exchangeable: boolean) {
    return request({
      url: `/api/leads/${id}/exchangeable`,
      method: 'put',
      data: { exchangeable }
    })
  },

  // 获取线索交换价值
  getLeadExchangeValue(leadIds: number[]) {
    return request({
      url: '/api/leads/exchange-value',
      method: 'post',
      data: { leadIds }
    })
  },

  // 计算交换价值对比
  calculateExchangeValue(data: {
    offeredLeadIds: number[]
    targetLeadId: number
  }) {
    return request({
      url: '/api/leads/calculate-exchange',
      method: 'post',
      data
    })
  }
}

// 线索状态枚举
export enum LeadStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  EXCHANGED = 'EXCHANGED',
  DELETED = 'DELETED'
}

// 线索评级枚举
export enum LeadRating {
  A = 'A',
  B = 'B',
  C = 'C',
  D = 'D'
}

// 线索类型定义
export interface Lead {
  id: number
  title: string
  companyName: string
  industry: string
  region: string
  contactName: string
  contactPhone: string
  contactEmail?: string
  description: string
  requirements?: string
  rating: LeadRating
  value: number
  status: LeadStatus
  exchangeable: boolean
  ownerId: number
  ownerName: string
  ownerAvatar?: string
  ownerCredit: number
  createdAt: string
  updatedAt: string
}

// 线索创建/更新表单类型
export interface LeadForm {
  title: string
  companyName: string
  industry: string
  region: string
  contactName: string
  contactPhone: string
  contactEmail?: string
  description: string
  requirements?: string
}