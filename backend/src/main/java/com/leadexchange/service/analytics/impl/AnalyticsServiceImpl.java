package com.leadexchange.service.analytics.impl;

import com.leadexchange.domain.analytics.PersonalStats;
import com.leadexchange.domain.analytics.SystemStats;
import com.leadexchange.domain.analytics.TrendData;
import com.leadexchange.repository.analytics.CustomAnalyticsRepository;
import com.leadexchange.service.analytics.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据分析服务实现类
 * 提供个人和系统数据统计分析功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    @Autowired
    private CustomAnalyticsRepository analyticsRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "analytics:";
    private static final String REALTIME_STATS_KEY = "analytics:realtime";
    private static final int CACHE_EXPIRE_HOURS = 1;

    @Override
    @Cacheable(value = "personalStats", key = "#userId + '_' + #startTime + '_' + #endTime")
    public PersonalStats getPersonalStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("获取用户个人统计数据: userId={}, startTime={}, endTime={}", userId, startTime, endTime);
        
        PersonalStats stats = new PersonalStats(userId, startTime, endTime);
        
        try {
            // 获取线索统计
            Object[] leadStats = analyticsRepository.getPersonalLeadStats(userId, startTime, endTime);
            if (leadStats != null && leadStats.length >= 3) {
                stats.setTotalLeads(((Number) leadStats[0]).longValue());
                stats.setValidLeads(((Number) leadStats[1]).longValue());
                stats.setExchangedLeads(((Number) leadStats[2]).longValue());
            }
            
            // 获取交换统计
            Object[] exchangeStats = analyticsRepository.getPersonalExchangeStats(userId, startTime, endTime);
            if (exchangeStats != null && exchangeStats.length >= 3) {
                stats.setReceivedLeads(((Number) exchangeStats[0]).longValue());
                stats.setTotalPointsEarned(((Number) exchangeStats[1]).longValue());
                stats.setTotalPointsSpent(((Number) exchangeStats[2]).longValue());
            }
            
            // 计算交换成功率
            if (stats.getTotalLeads() != null && stats.getTotalLeads() > 0) {
                BigDecimal successRate = new BigDecimal(stats.getExchangedLeads())
                    .divide(new BigDecimal(stats.getTotalLeads()), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
                stats.setExchangeSuccessRate(successRate);
            }
            
            // 获取评级分布
            List<Object[]> ratingDist = analyticsRepository.getPersonalRatingDistribution(userId, startTime, endTime);
            setRatingDistribution(stats, ratingDist);
            
            // 获取浏览和收藏统计
            Object[] viewFavoriteStats = analyticsRepository.getPersonalViewFavoriteStats(userId, startTime, endTime);
            if (viewFavoriteStats != null && viewFavoriteStats.length >= 2) {
                stats.setTotalViews(((Number) viewFavoriteStats[0]).longValue());
                stats.setTotalFavorites(((Number) viewFavoriteStats[1]).longValue());
            }
            
            // 计算当前积分余额
            Long currentPoints = (stats.getTotalPointsEarned() != null ? stats.getTotalPointsEarned() : 0L) - 
                               (stats.getTotalPointsSpent() != null ? stats.getTotalPointsSpent() : 0L);
            stats.setCurrentPoints(currentPoints);
            
            // 计算活跃天数
            long activeDays = ChronoUnit.DAYS.between(startTime, endTime) + 1;
            stats.setActiveDays(activeDays);
            stats.setLastActiveTime(LocalDateTime.now());
            
        } catch (Exception e) {
            logger.error("获取个人统计数据失败: userId={}", userId, e);
            throw new RuntimeException("获取个人统计数据失败", e);
        }
        
        return stats;
    }

    @Override
    @Cacheable(value = "systemStats", key = "#startTime + '_' + #endTime")
    public SystemStats getSystemStats(LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("获取系统统计数据: startTime={}, endTime={}", startTime, endTime);
        
        SystemStats stats = new SystemStats(startTime, endTime);
        
        try {
            // 获取系统总体统计
            Object[] overallStats = analyticsRepository.getSystemOverallStats(startTime, endTime);
            if (overallStats != null && overallStats.length >= 7) {
                stats.setTotalUsers(((Number) overallStats[0]).longValue());
                stats.setActiveUsers(((Number) overallStats[1]).longValue());
                stats.setNewUsers(((Number) overallStats[2]).longValue());
                stats.setTotalLeads(((Number) overallStats[3]).longValue());
                stats.setValidLeads(((Number) overallStats[4]).longValue());
                stats.setTotalExchanges(((Number) overallStats[5]).longValue());
                stats.setSuccessfulExchanges(((Number) overallStats[6]).longValue());
            }
            
            // 计算交换成功率
            if (stats.getTotalExchanges() != null && stats.getTotalExchanges() > 0) {
                BigDecimal successRate = new BigDecimal(stats.getSuccessfulExchanges())
                    .divide(new BigDecimal(stats.getTotalExchanges()), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
                stats.setExchangeSuccessRate(successRate);
            }
            
            // 获取系统评级分布
            List<Object[]> ratingDist = analyticsRepository.getSystemRatingDistribution(startTime, endTime);
            setSystemRatingDistribution(stats, ratingDist);
            
            // 获取最热门行业
            List<Map<String, Object>> topIndustries = analyticsRepository.getTopIndustries(startTime, endTime, 1);
            if (!topIndustries.isEmpty()) {
                Map<String, Object> topIndustry = topIndustries.get(0);
                stats.setTopIndustry((String) topIndustry.get("industry"));
                stats.setTopIndustryLeadCount(((Number) topIndustry.get("lead_count")).longValue());
            }
            
            // 获取最活跃用户
            List<Map<String, Object>> mostActiveUsers = analyticsRepository.getTopActiveUsers(startTime, endTime, 1);
            if (!mostActiveUsers.isEmpty()) {
                Map<String, Object> mostActiveUser = mostActiveUsers.get(0);
                stats.setMostActiveUserId(((Number) mostActiveUser.get("user_id")).longValue());
                stats.setMostActiveUserLeadCount(((Number) mostActiveUser.get("lead_count")).longValue());
            }
            
            // 计算平均值
            long days = ChronoUnit.DAYS.between(startTime, endTime) + 1;
            if (days > 0) {
                stats.setAverageDailyActiveUsers(new BigDecimal(stats.getActiveUsers()).divide(new BigDecimal(days), 2, RoundingMode.HALF_UP));
                stats.setAverageDailyNewLeads(new BigDecimal(stats.getTotalLeads()).divide(new BigDecimal(days), 2, RoundingMode.HALF_UP));
                stats.setAverageDailyExchanges(new BigDecimal(stats.getTotalExchanges()).divide(new BigDecimal(days), 2, RoundingMode.HALF_UP));
            }
            
            stats.setSystemRunDays(days);
            
        } catch (Exception e) {
            logger.error("获取系统统计数据失败", e);
            throw new RuntimeException("获取系统统计数据失败", e);
        }
        
        return stats;
    }

    @Override
    public List<TrendData> getLeadTrends(Long userId, LocalDateTime startTime, LocalDateTime endTime, String granularity) {
        logger.info("获取线索趋势数据: userId={}, granularity={}", userId, granularity);
        
        try {
            List<Object[]> rawData = analyticsRepository.getLeadTrendsByDay(userId, startTime, endTime);
            return convertToTrendData(rawData, "leads", granularity);
        } catch (Exception e) {
            logger.error("获取线索趋势数据失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<TrendData> getExchangeTrends(Long userId, LocalDateTime startTime, LocalDateTime endTime, String granularity) {
        logger.info("获取交换趋势数据: userId={}, granularity={}", userId, granularity);
        
        try {
            List<Object[]> rawData = analyticsRepository.getExchangeTrendsByDay(userId, startTime, endTime);
            return convertToTrendData(rawData, "exchanges", granularity);
        } catch (Exception e) {
            logger.error("获取交换趋势数据失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Long> getLeadRatingDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("获取线索评级分布: userId={}", userId);
        
        try {
            List<Object[]> rawData = userId != null ? 
                analyticsRepository.getPersonalRatingDistribution(userId, startTime, endTime) :
                analyticsRepository.getSystemRatingDistribution(startTime, endTime);
            
            return rawData.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> ((Number) row[1]).longValue()
                ));
        } catch (Exception e) {
            logger.error("获取线索评级分布失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Long> getIndustryDistribution(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("获取行业分布数据: userId={}", userId);
        
        try {
            return analyticsRepository.getIndustryDistribution(userId, startTime, endTime);
        } catch (Exception e) {
            logger.error("获取行业分布数据失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public List<TrendData> getUserActivityTrends(LocalDateTime startTime, LocalDateTime endTime, String granularity) {
        logger.info("获取用户活跃度趋势数据: granularity={}", granularity);
        
        try {
            List<Object[]> rawData = analyticsRepository.getUserActivityTrendsByDay(startTime, endTime);
            return convertToTrendData(rawData, "activity", granularity);
        } catch (Exception e) {
            logger.error("获取用户活跃度趋势数据失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public byte[] exportReport(Long userId, LocalDateTime startTime, LocalDateTime endTime, String reportType) {
        logger.info("导出数据报表: userId={}, reportType={}", userId, reportType);
        
        try {
            return analyticsRepository.generateReport(userId, startTime, endTime, reportType);
        } catch (Exception e) {
            logger.error("导出数据报表失败: userId={}, reportType={}", userId, reportType, e);
            throw new RuntimeException("导出数据报表失败", e);
        }
    }

    @Override
    public Map<String, Object> getRealTimeStats() {
        String cacheKey = REALTIME_STATS_KEY;
        Map<String, Object> cachedStats = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedStats != null) {
            return cachedStats;
        }
        
        // 如果缓存不存在，计算实时统计数据
        Map<String, Object> realTimeStats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        
        try {
            SystemStats todayStats = getSystemStats(startOfDay, now);
            realTimeStats.put("totalUsers", todayStats.getTotalUsers());
            realTimeStats.put("activeUsers", todayStats.getActiveUsers());
            realTimeStats.put("totalLeads", todayStats.getTotalLeads());
            realTimeStats.put("totalExchanges", todayStats.getTotalExchanges());
            realTimeStats.put("lastUpdateTime", now);
            
            // 缓存5分钟
            redisTemplate.opsForValue().set(cacheKey, realTimeStats, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("获取实时统计数据失败", e);
        }
        
        return realTimeStats;
    }

    @Override
    public void refreshCachedStats() {
        logger.info("刷新缓存统计数据");
        
        // 清除实时统计缓存
        redisTemplate.delete(REALTIME_STATS_KEY);
        
        // 清除其他相关缓存
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        
        logger.info("缓存统计数据刷新完成");
    }

    /**
     * 设置个人统计的评级分布
     */
    private void setRatingDistribution(PersonalStats stats, List<Object[]> ratingDist) {
        Map<String, Long> ratingMap = ratingDist.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        
        stats.setaGradeLeads(ratingMap.getOrDefault("A", 0L));
        stats.setbGradeLeads(ratingMap.getOrDefault("B", 0L));
        stats.setcGradeLeads(ratingMap.getOrDefault("C", 0L));
        stats.setdGradeLeads(ratingMap.getOrDefault("D", 0L));
        
        // 计算平均评级分数
        long totalLeads = stats.getaGradeLeads() + stats.getbGradeLeads() + stats.getcGradeLeads() + stats.getdGradeLeads();
        if (totalLeads > 0) {
            long totalScore = stats.getaGradeLeads() * 8 + stats.getbGradeLeads() * 4 + stats.getcGradeLeads() * 2 + stats.getdGradeLeads() * 1;
            BigDecimal avgScore = new BigDecimal(totalScore).divide(new BigDecimal(totalLeads), 2, RoundingMode.HALF_UP);
            stats.setAverageRatingScore(avgScore);
        }
    }

    /**
     * 设置系统统计的评级分布
     */
    private void setSystemRatingDistribution(SystemStats stats, List<Object[]> ratingDist) {
        Map<String, Long> ratingMap = ratingDist.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
        
        stats.setaGradeLeads(ratingMap.getOrDefault("A", 0L));
        stats.setbGradeLeads(ratingMap.getOrDefault("B", 0L));
        stats.setcGradeLeads(ratingMap.getOrDefault("C", 0L));
        stats.setdGradeLeads(ratingMap.getOrDefault("D", 0L));
        
        // 计算平均评级分数
        long totalLeads = stats.getaGradeLeads() + stats.getbGradeLeads() + stats.getcGradeLeads() + stats.getdGradeLeads();
        if (totalLeads > 0) {
            long totalScore = stats.getaGradeLeads() * 8 + stats.getbGradeLeads() * 4 + stats.getcGradeLeads() * 2 + stats.getdGradeLeads() * 1;
            BigDecimal avgScore = new BigDecimal(totalScore).divide(new BigDecimal(totalLeads), 2, RoundingMode.HALF_UP);
            stats.setAverageRatingScore(avgScore);
        }
    }

    /**
     * 转换原始数据为趋势数据
     */
    private List<TrendData> convertToTrendData(List<Object[]> rawData, String dataType, String granularity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        return rawData.stream()
            .map(row -> {
                LocalDateTime timestamp = ((java.sql.Date) row[0]).toLocalDate().atStartOfDay();
                Long value = ((Number) row[1]).longValue();
                String timeLabel = timestamp.format(formatter);
                
                return new TrendData(timestamp, timeLabel, value, dataType);
            })
            .collect(Collectors.toList());
    }
}