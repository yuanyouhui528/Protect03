package com.leadexchange.service.analytics;

import com.leadexchange.domain.analytics.*;
import com.leadexchange.repository.analytics.CustomAnalyticsRepository;
import com.leadexchange.service.analytics.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 数据分析服务单元测试
 * 测试数据统计分析功能的正确性
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private CustomAnalyticsRepository analyticsRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private PersonalStats mockPersonalStats;
    private SystemStats mockSystemStats;
    private List<Object[]> mockTrendData;
    private List<Object[]> mockRatingDistribution;
    private Map<String, Long> mockIndustryDistribution;
    private List<Object[]> mockSystemOverallStats;
    private List<Object[]> mockTopIndustries;
    private List<Object[]> mockTopActiveUsers;

    @BeforeEach
    void setUp() {
        
        // 准备测试数据
        mockPersonalStats = new PersonalStats();
        mockPersonalStats.setTotalLeads(100L);
        mockPersonalStats.setActiveLeads(80L);
        mockPersonalStats.setExchangeCount(50L);
        mockPersonalStats.setSuccessfulExchanges(40L);
        mockPersonalStats.setTotalRevenue(new BigDecimal("10000.00"));
        mockPersonalStats.setAverageRating(4.5);

        mockSystemStats = new SystemStats();
        mockSystemStats.setTotalUsers(1000L);
        mockSystemStats.setActiveUsers(800L);
        mockSystemStats.setTotalLeads(5000L);
        mockSystemStats.setTotalExchanges(2000L);
        mockSystemStats.setTotalRevenue(new BigDecimal("500000.00"));
        mockSystemStats.setAverageRating(4.2);

        // 模拟趋势数据 (日期, 数值)
        Object[] trend1 = {java.sql.Date.valueOf(LocalDateTime.now().minusDays(6).toLocalDate()), 10L};
        Object[] trend2 = {java.sql.Date.valueOf(LocalDateTime.now().minusDays(5).toLocalDate()), 15L};
        Object[] trend3 = {java.sql.Date.valueOf(LocalDateTime.now().minusDays(4).toLocalDate()), 20L};
        mockTrendData = new ArrayList<>();
        mockTrendData.add(trend1);
        mockTrendData.add(trend2);
        mockTrendData.add(trend3);

        // 模拟评级分布数据 (评级, 数量)
        Object[] rating1 = {"A", 100L};
        Object[] rating2 = {"B", 200L};
        Object[] rating3 = {"C", 150L};
        Object[] rating4 = {"D", 50L};
        mockRatingDistribution = new ArrayList<>();
        mockRatingDistribution.add(rating1);
        mockRatingDistribution.add(rating2);
        mockRatingDistribution.add(rating3);
        mockRatingDistribution.add(rating4);

        // 模拟行业分布数据
        mockIndustryDistribution = Map.of(
            "科技", 300L,
            "金融", 200L,
            "制造", 150L
        );

        // 模拟系统总体统计数据
        Object[] systemStatsRow = {1000L, 800L, 100L, 5000L, 4000L, 2000L, 1500L};
        mockSystemOverallStats = new ArrayList<>();
        mockSystemOverallStats.add(systemStatsRow);

        // 模拟热门行业数据
        Object[] industry1 = {"科技", 300L};
        Object[] industry2 = {"金融", 200L};
        mockTopIndustries = new ArrayList<>();
        mockTopIndustries.add(industry1);
        mockTopIndustries.add(industry2);

        // 模拟活跃用户数据
        Object[] user1 = {"用户A", 50L};
        Object[] user2 = {"用户B", 40L};
        mockTopActiveUsers = new ArrayList<>();
        mockTopActiveUsers.add(user1);
        mockTopActiveUsers.add(user2);
    }

    /**
     * 测试获取个人统计数据
     */
    @Test
    void testGetPersonalStats() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        
        // 模拟repository方法返回
        Object[] leadStats = {100L, 80L, 50L};
        Object[] exchangeStats = {40L, 10000L, 5000L};
        Object[] viewFavoriteStats = {200L, 30L};
        
        when(analyticsRepository.getPersonalLeadStats(userId, startTime, endTime)).thenReturn(leadStats);
        when(analyticsRepository.getPersonalExchangeStats(userId, startTime, endTime)).thenReturn(exchangeStats);
        when(analyticsRepository.getPersonalViewFavoriteStats(userId, startTime, endTime)).thenReturn(viewFavoriteStats);
        when(analyticsRepository.getPersonalRatingDistribution(userId, startTime, endTime)).thenReturn(mockRatingDistribution);

        // 执行测试
        PersonalStats result = analyticsService.getPersonalStats(userId, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(100L, result.getTotalLeads());
        assertEquals(80L, result.getValidLeads());
        assertEquals(50L, result.getExchangedLeads());
        assertEquals(40L, result.getReceivedLeads());
        assertEquals(10000L, result.getTotalPointsEarned());
        assertEquals(5000L, result.getTotalPointsSpent());

        // 验证方法调用
        verify(analyticsRepository, times(1)).getPersonalLeadStats(userId, startTime, endTime);
        verify(analyticsRepository, times(1)).getPersonalExchangeStats(userId, startTime, endTime);
        verify(analyticsRepository, times(1)).getPersonalViewFavoriteStats(userId, startTime, endTime);
        verify(analyticsRepository, times(1)).getPersonalRatingDistribution(userId, startTime, endTime);
    }

    /**
     * 测试获取系统统计数据
     */
    @Test
    void testGetSystemStats() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        when(analyticsRepository.getSystemOverallStats(startTime, endTime)).thenReturn(mockSystemOverallStats.get(0));
        when(analyticsRepository.getSystemRatingDistribution(startTime, endTime)).thenReturn(mockRatingDistribution);
        when(analyticsRepository.getTopIndustries(startTime, endTime, 1)).thenReturn(List.of(Map.of("industry", "科技", "lead_count", 300L)));
        when(analyticsRepository.getTopActiveUsers(startTime, endTime, 1)).thenReturn(List.of(Map.of("user_id", 1L, "lead_count", 50L)));

        // 执行测试
        SystemStats result = analyticsService.getSystemStats(startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(1000L, result.getTotalUsers());
        assertEquals(800L, result.getActiveUsers());
        assertEquals(5000L, result.getTotalLeads());
        assertEquals(2000L, result.getTotalExchanges());

        // 验证方法调用
        verify(analyticsRepository, times(1)).getSystemOverallStats(startTime, endTime);
        verify(analyticsRepository, times(1)).getSystemRatingDistribution(startTime, endTime);
        verify(analyticsRepository, times(1)).getTopIndustries(startTime, endTime, 1);
        verify(analyticsRepository, times(1)).getTopActiveUsers(startTime, endTime, 1);
    }

    /**
     * 测试获取线索趋势数据
     */
    @Test
    void testGetLeadTrends() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        String period = "day";
        when(analyticsRepository.getLeadTrendsByDay(userId, startTime, endTime)).thenReturn(mockTrendData);

        // 执行测试
        List<TrendData> result = analyticsService.getLeadTrends(userId, startTime, endTime, period);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10L, result.get(0).getValue());

        // 验证方法调用
        verify(analyticsRepository, times(1)).getLeadTrendsByDay(userId, startTime, endTime);
    }

    /**
     * 测试获取交换趋势数据
     */
    @Test
    void testGetExchangeTrends() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        String period = "day";
        when(analyticsRepository.getExchangeTrendsByDay(userId, startTime, endTime)).thenReturn(mockTrendData);

        // 执行测试
        List<TrendData> result = analyticsService.getExchangeTrends(userId, startTime, endTime, period);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10L, result.get(0).getValue());

        // 验证方法调用
        verify(analyticsRepository, times(1)).getExchangeTrendsByDay(userId, startTime, endTime);
    }

    /**
     * 测试获取线索评级分布
     */
    @Test
    void testGetLeadRatingDistribution() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        List<Object[]> mockRatingData = List.of(
            new Object[]{"A", 10L},
            new Object[]{"B", 15L},
            new Object[]{"C", 8L},
            new Object[]{"D", 7L}
        );
        when(analyticsRepository.getPersonalRatingDistribution(userId, startTime, endTime)).thenReturn(mockRatingData);

        // 执行测试
        Map<String, Long> result = analyticsService.getLeadRatingDistribution(userId, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(10L, result.get("A"));
        assertEquals(15L, result.get("B"));
        assertEquals(8L, result.get("C"));
        assertEquals(7L, result.get("D"));

        // 验证方法调用
        verify(analyticsRepository, times(1)).getPersonalRatingDistribution(userId, startTime, endTime);
    }

    /**
     * 测试获取行业分布
     */
    @Test
    void testGetIndustryDistribution() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        Map<String, Long> mockIndustryData = Map.of(
            "科技", 300L,
            "金融", 200L,
            "制造", 100L
        );
        when(analyticsRepository.getIndustryDistribution(userId, startTime, endTime)).thenReturn(mockIndustryData);

        // 执行测试
        Map<String, Long> result = analyticsService.getIndustryDistribution(userId, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(300L, result.get("科技"));
        assertEquals(200L, result.get("金融"));
        assertEquals(100L, result.get("制造"));

        // 验证方法调用
        verify(analyticsRepository, times(1)).getIndustryDistribution(userId, startTime, endTime);
    }

    /**
     * 测试获取用户活跃度趋势
     */
    @Test
    void testGetUserActivityTrends() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        String period = "day";
        when(analyticsRepository.getUserActivityTrendsByDay(startTime, endTime)).thenReturn(mockTrendData);

        // 执行测试
        List<TrendData> result = analyticsService.getUserActivityTrends(startTime, endTime, period);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10L, result.get(0).getValue());

        // 验证方法调用
        verify(analyticsRepository, times(1)).getUserActivityTrendsByDay(startTime, endTime);
    }

    /**
     * 测试获取实时统计数据（缓存命中）
     */
    @Test
    void testGetRealTimeStats_CacheHit() {
        // 模拟Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        
        // 准备测试数据
        String cacheKey = "analytics:realtime";
        Map<String, Object> cachedStats = Map.of(
            "onlineUsers", 100L,
            "todayLeads", 50L,
            "todayExchanges", 20L
        );
        when(valueOperations.get(cacheKey)).thenReturn(cachedStats);

        // 执行测试
        Map<String, Object> result = analyticsService.getRealTimeStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(cachedStats, result);

        // 验证缓存被访问
        verify(valueOperations, times(1)).get(cacheKey);
        // 验证没有查询数据库
        verify(analyticsRepository, never()).getRealTimeStats();
    }

    /**
     * 测试获取实时统计数据（缓存未命中）
     */
    @Test
    void testGetRealTimeStats_CacheMiss() {
        // 模拟Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        
        // 准备测试数据
        String cacheKey = "analytics:realtime";
        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(analyticsRepository.getSystemOverallStats(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockSystemOverallStats.get(0));
        when(analyticsRepository.getSystemRatingDistribution(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockRatingDistribution);
        when(analyticsRepository.getTopIndustries(any(LocalDateTime.class), any(LocalDateTime.class), eq(1))).thenReturn(List.of(Map.of("industry", "科技", "lead_count", 300L)));
        when(analyticsRepository.getTopActiveUsers(any(LocalDateTime.class), any(LocalDateTime.class), eq(1))).thenReturn(List.of(Map.of("user_id", 1L, "lead_count", 50L)));

        // 执行测试
        Map<String, Object> result = analyticsService.getRealTimeStats();

        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("totalUsers"));
        assertTrue(result.containsKey("activeUsers"));
        assertTrue(result.containsKey("totalLeads"));
        assertTrue(result.containsKey("totalExchanges"));
        assertTrue(result.containsKey("lastUpdateTime"));

        // 验证缓存被访问
        verify(valueOperations, times(1)).get(cacheKey);
        // 验证数据被缓存
        verify(valueOperations, times(1)).set(eq(cacheKey), any(Map.class), eq(5L), eq(TimeUnit.MINUTES));
    }

    /**
     * 测试刷新缓存统计数据
     */
    @Test
    void testRefreshCachedStats() {
        // 准备测试数据
        Set<String> mockKeys = Set.of("analytics:key1", "analytics:key2");
        when(redisTemplate.keys("analytics:*")).thenReturn(mockKeys);

        // 执行测试
        analyticsService.refreshCachedStats();

        // 验证实时统计缓存被删除
        verify(redisTemplate, times(1)).delete("analytics:realtime");
        // 验证其他缓存键被查询
        verify(redisTemplate, times(1)).keys("analytics:*");
        // 验证其他缓存被删除
        verify(redisTemplate, times(1)).delete(mockKeys);
    }

    /**
     * 测试导出数据报表
     */
    @Test
    void testExportReport() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        String reportType = "personal";
        byte[] mockReportData = "mock report data".getBytes();
        
        when(analyticsRepository.generateReport(userId, startTime, endTime, reportType))
            .thenReturn(mockReportData);

        // 执行测试
        byte[] result = analyticsService.exportReport(userId, startTime, endTime, reportType);

        // 验证结果
        assertNotNull(result);
        assertArrayEquals(mockReportData, result);

        // 验证方法调用
        verify(analyticsRepository, times(1)).generateReport(userId, startTime, endTime, reportType);
    }

}