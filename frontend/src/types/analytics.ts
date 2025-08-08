/**
 * 数据分析相关类型定义
 */

/**
 * 个人统计数据
 */
export interface PersonalStats {
  /** 用户ID */
  userId: number
  /** 统计时间范围开始 */
  startTime: string
  /** 统计时间范围结束 */
  endTime: string
  /** 线索总数 */
  totalLeads: number
  /** 交换线索数 */
  exchangedLeads: number
  /** 当前积分 */
  currentPoints: number
  /** 平均线索评级 */
  averageRating: string
  /** 线索浏览次数 */
  leadViews: number
  /** 线索收藏次数 */
  leadFavorites: number
  /** 发布的线索被浏览次数 */
  publishedLeadViews: number
  /** 发布的线索被收藏次数 */
  publishedLeadFavorites: number
  /** 交换成功率 */
  exchangeSuccessRate: number
  /** 线索通过率 */
  leadApprovalRate: number
}

/**
 * 系统统计数据
 */
export interface SystemStats {
  /** 统计时间范围开始 */
  startTime: string
  /** 统计时间范围结束 */
  endTime: string
  /** 总用户数 */
  totalUsers: number
  /** 活跃用户数 */
  activeUsers: number
  /** 线索总数 */
  totalLeads: number
  /** 交换总次数 */
  totalExchanges: number
  /** 积分流通总量 */
  totalPointsCirculation: number
  /** 线索评级分布 */
  ratingDistribution: Record<string, number>
  /** 总浏览次数 */
  totalViews: number
  /** 总收藏次数 */
  totalFavorites: number
  /** 平均日活跃用户数 */
  averageDailyActiveUsers: number
  /** 系统运行天数 */
  systemRunningDays: number
  /** 最热门行业 */
  topIndustries: Array<{ industry: string; count: number }>
  /** 最活跃用户 */
  topActiveUsers: Array<{ userId: number; username: string; activityScore: number }>
}

/**
 * 趋势数据
 */
export interface TrendData {
  /** 时间点 */
  time: string
  /** 数值 */
  value: number
  /** 百分比值 */
  percentage?: number
  /** 数据类型标识 */
  type?: string
  /** 额外数据标签 */
  label?: string
  /** 比较值（用于同比、环比） */
  compareValue?: number
  /** 增长率 */
  growthRate?: number
}

/**
 * 图表配置选项
 */
export interface ChartOptions {
  /** 图表标题 */
  title?: string
  /** X轴标签 */
  xAxisLabel?: string
  /** Y轴标签 */
  yAxisLabel?: string
  /** 是否显示图例 */
  showLegend?: boolean
  /** 图表颜色主题 */
  colorTheme?: string[]
  /** 图表高度 */
  height?: number
  /** 是否显示数据标签 */
  showDataLabels?: boolean
}

/**
 * 时间范围预设
 */
export interface TimeRangePreset {
  /** 预设名称 */
  label: string
  /** 预设值 */
  value: string
  /** 开始时间 */
  startTime: Date
  /** 结束时间 */
  endTime: Date
}

/**
 * 数据筛选条件
 */
export interface AnalyticsFilter {
  /** 时间范围 */
  timeRange: {
    startTime: string
    endTime: string
  }
  /** 用户ID（可选，用于个人数据筛选） */
  userId?: number
  /** 数据粒度（日、周、月） */
  granularity?: 'day' | 'week' | 'month'
  /** 行业筛选 */
  industry?: string
  /** 评级筛选 */
  rating?: string
}

/**
 * 导出报表类型
 */
export type ReportType = 'personal' | 'system' | 'leads' | 'exchanges' | 'users'

/**
 * API响应数据格式
 */
export interface ApiResponse<T = any> {
  /** 响应码 */
  code: number
  /** 响应消息 */
  message: string
  /** 响应数据 */
  data: T
  /** 时间戳 */
  timestamp: number
}