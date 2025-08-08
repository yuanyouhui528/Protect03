package com.leadexchange.repository.analytics;

import com.leadexchange.domain.analytics.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 数据分析Repository单元测试
 * 测试数据查询功能的正确性
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AnalyticsRepositoryImpl analyticsRepository;

    private PersonalStats mockPersonalStats;
    private SystemStats mockSystemStats;
    private List<TrendData> mockTrendData;
    private List<RatingDistribution> mockRatingDistribution;
    private List<IndustryDistribution> mockIndustryDistribution;
    private List<UserActivityTrend> mockUserActivityTrend;
    private Map<String, Object> mockRealTimeStats;

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

        mockTrendData = Arrays.asList(
            new TrendData(LocalDateTime.now().minusDays(6), 10L),
            new TrendData(LocalDateTime.now().minusDays(5), 15L),
            new TrendData(LocalDateTime.now().minusDays(4), 20L)
        );

        mockRatingDistribution = Arrays.asList(
            new RatingDistribution("A", 100L),
            new RatingDistribution("B", 200L),
            new RatingDistribution("C", 150L),
            new RatingDistribution("D", 50L)
        );

        mockIndustryDistribution = Arrays.asList(
            new IndustryDistribution("科技", 300L),
            new IndustryDistribution("金融", 200L),
            new IndustryDistribution("制造", 150L)
        );

        mockUserActivityTrend = Arrays.asList(
            new UserActivityTrend(LocalDateTime.now().minusDays(6), 100L),
            new UserActivityTrend(LocalDateTime.now().minusDays(5), 120L),
            new UserActivityTrend(LocalDateTime.now().minusDays(4), 110L)
        );

        mockRealTimeStats = Map.of(
            "onlineUsers", 100L,
            "todayLeads", 50L,
            "todayExchanges", 20L
        );
    }

    /**
     * 测试获取个人统计数据
     */
    @Test
    void testGetPersonalStats() {
        // 准备测试数据
        Long userId = 1L;
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(userId)))
            .thenReturn(mockPersonalStats);

        // 执行测试
        PersonalStats result = analyticsRepository.getPersonalStats(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(100L, result.getTotalLeads());
        assertEquals(80L, result.getActiveLeads());
        assertEquals(50L, result.getExchangeCount());
        assertEquals(40L, result.getSuccessfulExchanges());
        assertEquals(new BigDecimal("10000.00"), result.getTotalRevenue());
        assertEquals(4.5, result.getAverageRating());

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(RowMapper.class), eq(userId));
    }

    /**
     * 测试获取系统统计数据
     */
    @Test
    void testGetSystemStats() {
        // 准备测试数据
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
            .thenReturn(mockSystemStats);

        // 执行测试
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        SystemStats result = analyticsRepository.getSystemStats(startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(1000L, result.getTotalUsers());
        assertEquals(800L, result.getActiveUsers());
        assertEquals(5000L, result.getTotalLeads());
        assertEquals(2000L, result.getTotalExchanges());
        assertEquals(new BigDecimal("500000.00"), result.getTotalRevenue());
        assertEquals(4.2, result.getAverageRating());

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(RowMapper.class));
    }

    /**
     * 测试获取线索趋势数据
     */
    @Test
    void testGetLeadTrends() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(userId), eq(startTime), eq(endTime)))
            .thenReturn(mockTrendData);

        // 执行测试
        List<TrendData> result = analyticsRepository.getLeadTrends(userId, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(10L, result.get(0).getValue());
        assertEquals(15L, result.get(1).getValue());
        assertEquals(20L, result.get(2).getValue());

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(userId), eq(startTime), eq(endTime));
    }

    /**
     * 测试获取交换趋势数据
     */
    @Test
    void testGetExchangeTrends() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(userId), eq(startTime), eq(endTime)))
            .thenReturn(mockTrendData);

        // 执行测试
        List<TrendData> result = analyticsRepository.getExchangeTrends(userId, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(userId), eq(startTime), eq(endTime));
    }

    /**
     * 测试获取线索评级分布
     */
    @Test
    void testGetRatingDistribution() {
        // 准备测试数据
        Long userId = 1L;
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(userId)))
            .thenReturn(mockRatingDistribution);

        // 执行测试
        List<RatingDistribution> result = analyticsRepository.getRatingDistribution(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("A", result.get(0).getRating());
        assertEquals(100L, result.get(0).getCount());
        assertEquals("B", result.get(1).getRating());
        assertEquals(200L, result.get(1).getCount());

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(userId));
    }

    /**
     * 测试获取行业分布
     */
    @Test
    void testGetIndustryDistribution() {
        // 准备测试数据
        Long userId = 1L;
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(userId)))
            .thenReturn(mockIndustryDistribution);

        // 执行测试
        List<IndustryDistribution> result = analyticsRepository.getIndustryDistribution(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("科技", result.get(0).getIndustry());
        assertEquals(300L, result.get(0).getCount());
        assertEquals("金融", result.get(1).getIndustry());
        assertEquals(200L, result.get(1).getCount());

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(userId));
    }

    /**
     * 测试获取用户活跃度趋势
     */
    @Test
    void testGetUserActivityTrends() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(startTime), eq(endTime)))
            .thenReturn(mockUserActivityTrend);

        // 执行测试
        List<UserActivityTrend> result = analyticsRepository.getUserActivityTrends(startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(100L, result.get(0).getActiveUsers());
        assertEquals(120L, result.get(1).getActiveUsers());
        assertEquals(110L, result.get(2).getActiveUsers());

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(startTime), eq(endTime));
    }

    /**
     * 测试获取实时统计数据
     */
    @Test
    void testGetRealTimeStats() {
        // 准备测试数据
        when(jdbcTemplate.queryForMap(anyString())).thenReturn(mockRealTimeStats);

        // 执行测试
        Map<String, Object> result = analyticsRepository.getRealTimeStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(100L, result.get("onlineUsers"));
        assertEquals(50L, result.get("todayLeads"));
        assertEquals(20L, result.get("todayExchanges"));

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).queryForMap(anyString());
    }

    /**
     * 测试生成报表数据
     */
    @Test
    void testGenerateReport() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        String reportType = "personal";
        byte[] mockReportData = "mock report data".getBytes();
        
        // 模拟复杂的报表生成逻辑
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(mockPersonalStats));

        // 执行测试
        byte[] result = analyticsRepository.generateReport(userId, startTime, endTime, reportType);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.length > 0);

        // 验证SQL查询被调用
        verify(jdbcTemplate, atLeastOnce()).query(anyString(), any(RowMapper.class), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试数据库异常处理
     */
    @Test
    void testGetPersonalStats_DatabaseException() {
        // 准备测试数据
        Long userId = 1L;
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(userId)))
            .thenThrow(new RuntimeException("Database connection failed"));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            analyticsRepository.getPersonalStats(userId);
        });

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).queryForObject(anyString(), any(RowMapper.class), eq(userId));
    }

    /**
     * 测试空结果处理
     */
    @Test
    void testGetLeadTrends_EmptyResult() {
        // 准备测试数据
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(userId), eq(startTime), eq(endTime)))
            .thenReturn(Arrays.asList());

        // 执行测试
        List<TrendData> result = analyticsRepository.getLeadTrends(userId, startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // 验证SQL查询被调用
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class), eq(userId), eq(startTime), eq(endTime));
    }

    /**
     * 测试参数验证
     */
    @Test
    void testGetPersonalStats_NullUserId() {
        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            analyticsRepository.getPersonalStats(null);
        });

        // 验证SQL查询未被调用
        verify(jdbcTemplate, never()).queryForObject(anyString(), any(RowMapper.class), any());
    }

    /**
     * 测试时间范围验证
     */
    @Test
    void testGetLeadTrends_InvalidTimeRange() {
        // 准备测试数据 - 结束时间早于开始时间
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(7);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            analyticsRepository.getLeadTrends(userId, startTime, endTime);
        });

        // 验证SQL查询未被调用
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), any(), any(), any());
    }

    /**
     * 测试系统级别的统计查询性能
     */
    @Test
    void testGetSystemStats_Performance() {
        // 准备测试数据
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class)))
            .thenReturn(mockSystemStats);

        // 执行多次测试以验证性能
        LocalDateTime testStartTime = LocalDateTime.now().minusDays(30);
        LocalDateTime testEndTime = LocalDateTime.now();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            analyticsRepository.getSystemStats(testStartTime, testEndTime);
        }
        long endTime = System.currentTimeMillis();

        // 验证执行时间合理（这里只是示例，实际项目中可能需要更精确的性能测试）
        assertTrue(endTime - startTime < 5000, "系统统计查询性能应该在可接受范围内");

        // 验证SQL查询被调用了100次
        verify(jdbcTemplate, times(100)).queryForObject(anyString(), any(RowMapper.class));
    }
}