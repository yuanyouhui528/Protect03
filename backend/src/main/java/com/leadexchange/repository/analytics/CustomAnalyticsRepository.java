package com.leadexchange.repository.analytics;

import com.leadexchange.domain.analytics.SystemStats;
import com.leadexchange.domain.analytics.TrendData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 自定义数据分析Repository接口
 * 提供复杂的统计查询功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface CustomAnalyticsRepository {

    /**
     * 获取用户个人线索统计
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果数组 [总数, 有效数, 已交换数]
     */
    Object[] getPersonalLeadStats(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户个人交换统计
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果数组 [获得线索数, 积分收入, 积分支出]
     */
    Object[] getPersonalExchangeStats(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户线索评级分布
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级分布结果
     */
    Map<String, Long> getRatingDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户线索浏览和收藏统计
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果数组 [总浏览数, 总收藏数]
     */
    Object[] getPersonalViewFavoriteStats(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取系统总体统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果数组
     */
    Object[] getSystemOverallStats(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取行业分布统计
     * 
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 行业分布结果
     */
    Map<String, Long> getIndustryDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取线索趋势数据
     * 
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度
     * @return 趋势数据
     */
    List<TrendData> getLeadTrendData(Long userId, LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 获取交换趋势数据
     * 
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度
     * @return 趋势数据
     */
    List<TrendData> getExchangeTrendData(Long userId, LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 获取用户活跃度趋势数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度
     * @return 活跃度趋势数据
     */
    List<TrendData> getUserActivityTrendData(LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 获取最热门行业
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 返回数量限制
     * @return 最热门行业信息
     */
    List<Map<String, Object>> getTopIndustries(LocalDateTime startTime, LocalDateTime endTime, int limit);

    /**
     * 获取最活跃用户
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 返回数量限制
     * @return 最活跃用户信息
     */
    List<Map<String, Object>> getTopActiveUsers(LocalDateTime startTime, LocalDateTime endTime, int limit);

    /**
     * 按天获取线索趋势数据
     * 
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 原始趋势数据
     */
    List<Object[]> getLeadTrendsByDay(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 按天获取交换趋势数据
     * 
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 原始趋势数据
     */
    List<Object[]> getExchangeTrendsByDay(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取个人评级分布
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 原始评级分布数据
     */
    List<Object[]> getPersonalRatingDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取系统评级分布
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 原始评级分布数据
     */
    List<Object[]> getSystemRatingDistribution(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 按天获取用户活跃度趋势数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 原始活跃度趋势数据
     */
    List<Object[]> getUserActivityTrendsByDay(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取实时统计数据
     * 
     * @return 实时统计数据映射
     */
    Map<String, Object> getRealTimeStats();
    
    /**
     * 获取用户活跃度趋势
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户活跃度趋势数据
     */
    List<TrendData> getUserActivityTrends(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取系统统计数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 系统统计数据
     */
    SystemStats getSystemStats(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 生成分析报告
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reportType 报告类型
     * @return 报告数据（字节数组）
     */
    byte[] generateReport(Long userId, LocalDateTime startTime, LocalDateTime endTime, String reportType);
}