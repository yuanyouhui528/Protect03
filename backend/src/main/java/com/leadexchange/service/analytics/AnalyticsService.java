package com.leadexchange.service.analytics;

import com.leadexchange.domain.analytics.PersonalStats;
import com.leadexchange.domain.analytics.SystemStats;
import com.leadexchange.domain.analytics.TrendData;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据分析服务接口
 * 提供个人和系统数据统计分析功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface AnalyticsService {

    /**
     * 获取个人数据统计
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 个人统计数据
     */
    PersonalStats getPersonalStats(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取系统数据统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 系统统计数据
     */
    SystemStats getSystemStats(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取线索趋势数据
     * 
     * @param userId 用户ID（可选，为null时获取系统趋势）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度（day/week/month）
     * @return 趋势数据列表
     */
    List<TrendData> getLeadTrends(Long userId, LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 获取交换趋势数据
     * 
     * @param userId 用户ID（可选，为null时获取系统趋势）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度（day/week/month）
     * @return 趋势数据列表
     */
    List<TrendData> getExchangeTrends(Long userId, LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 获取线索评级分布
     * 
     * @param userId 用户ID（可选，为null时获取系统分布）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级分布数据
     */
    Map<String, Long> getLeadRatingDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取行业分布数据
     * 
     * @param userId 用户ID（可选，为null时获取系统分布）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 行业分布数据
     */
    Map<String, Long> getIndustryDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户活跃度数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度（day/week/month）
     * @return 活跃度数据列表
     */
    List<TrendData> getUserActivityTrends(LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 导出数据报表
     * 
     * @param userId 用户ID（可选，为null时导出系统报表）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reportType 报表类型（personal/system）
     * @return 报表文件字节数组
     */
    byte[] exportReport(Long userId, LocalDateTime startTime, LocalDateTime endTime, String reportType);

    /**
     * 获取实时统计数据（用于缓存）
     * 
     * @return 实时统计数据
     */
    Map<String, Object> getRealTimeStats();

    /**
     * 刷新缓存统计数据
     */
    void refreshCachedStats();
}