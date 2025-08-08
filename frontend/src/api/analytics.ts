import request from '@/utils/request'
import type { PersonalStats, SystemStats, TrendData } from '@/types/analytics'

/**
 * 数据分析相关API接口
 */
export const analyticsApi = {
  /**
   * 获取个人数据统计
   */
  getPersonalStats(startTime: string, endTime: string) {
    return request<PersonalStats>({
      url: '/api/analytics/personal',
      method: 'get',
      params: {
        startTime,
        endTime
      }
    })
  },

  /**
   * 获取系统数据统计
   */
  getSystemStats(startTime: string, endTime: string) {
    return request<SystemStats>({
      url: '/api/analytics/system',
      method: 'get',
      params: {
        startTime,
        endTime
      }
    })
  },

  /**
   * 获取线索趋势数据
   */
  getLeadTrends(startTime: string, endTime: string, granularity: string, userId?: number) {
    return request<TrendData[]>({
      url: '/api/analytics/trends/leads',
      method: 'get',
      params: {
        startTime,
        endTime,
        granularity,
        userId
      }
    })
  },

  /**
   * 获取交换趋势数据
   */
  getExchangeTrends(startTime: string, endTime: string, granularity: string, userId?: number) {
    return request<TrendData[]>({
      url: '/api/analytics/trends/exchanges',
      method: 'get',
      params: {
        startTime,
        endTime,
        granularity,
        userId
      }
    })
  },

  /**
   * 获取用户活跃度趋势
   */
  getUserActivityTrends(startTime: string, endTime: string, granularity: string) {
    return request<TrendData[]>({
      url: '/api/analytics/trends/activity',
      method: 'get',
      params: {
        startTime,
        endTime,
        granularity
      }
    })
  },

  /**
   * 获取线索评级分布
   */
  getLeadRatingDistribution(startTime: string, endTime: string, userId?: number) {
    return request<Record<string, number>>({
      url: '/api/analytics/distribution/rating',
      method: 'get',
      params: {
        startTime,
        endTime,
        userId
      }
    })
  },

  /**
   * 获取行业分布
   */
  getIndustryDistribution(startTime: string, endTime: string, userId?: number) {
    return request<Record<string, number>>({
      url: '/api/analytics/distribution/industry',
      method: 'get',
      params: {
        startTime,
        endTime,
        userId
      }
    })
  },

  /**
   * 获取实时统计数据
   */
  getRealTimeStats() {
    return request<Record<string, any>>({
      url: '/api/analytics/realtime',
      method: 'get'
    })
  },

  /**
   * 导出数据报表
   */
  exportReport(startTime: string, endTime: string, reportType: string, userId?: number) {
    return request<Blob>({
      url: '/api/analytics/export',
      method: 'post',
      params: {
        startTime,
        endTime,
        reportType,
        userId
      },
      responseType: 'blob'
    })
  },

  /**
   * 刷新缓存统计数据
   */
  refreshCachedStats() {
    return request<string>({
      url: '/api/analytics/refresh',
      method: 'post'
    })
  }
}