package com.leadexchange.controller.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadexchange.common.result.Result;
import com.leadexchange.domain.analytics.*;
import com.leadexchange.service.analytics.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 数据分析控制器单元测试
 * 测试数据分析API接口的正确性
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Autowired
    private ObjectMapper objectMapper;

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
     * 测试获取个人统计数据API
     */
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetPersonalStats() throws Exception {
        // 准备测试数据
        when(analyticsService.getPersonalStats(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockPersonalStats);

        // 执行测试
        mockMvc.perform(get("/api/analytics/personal/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalLeads").value(100))
                .andExpect(jsonPath("$.data.activeLeads").value(80))
                .andExpect(jsonPath("$.data.exchangeCount").value(50))
                .andExpect(jsonPath("$.data.successfulExchanges").value(40))
                .andExpect(jsonPath("$.data.totalRevenue").value(10000.00))
                .andExpect(jsonPath("$.data.averageRating").value(4.5));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).getPersonalStats(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试获取系统统计数据API
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void testGetSystemStats() throws Exception {
        // 准备测试数据
        when(analyticsService.getSystemStats(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockSystemStats);

        // 执行测试
        mockMvc.perform(get("/api/analytics/system/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").value(1000))
                .andExpect(jsonPath("$.data.activeUsers").value(800))
                .andExpect(jsonPath("$.data.totalLeads").value(5000))
                .andExpect(jsonPath("$.data.totalExchanges").value(2000))
                .andExpect(jsonPath("$.data.totalRevenue").value(500000.00))
                .andExpect(jsonPath("$.data.averageRating").value(4.2));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).getSystemStats(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试获取线索趋势数据API
     */
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetLeadTrends() throws Exception {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        when(analyticsService.getLeadTrends(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
            .thenReturn(mockTrendData);

        // 执行测试
        mockMvc.perform(get("/api/analytics/personal/lead-trends")
                .param("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].value").value(10))
                .andExpect(jsonPath("$.data[1].value").value(15))
                .andExpect(jsonPath("$.data[2].value").value(20));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).getLeadTrends(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), anyString());
    }

    /**
     * 测试获取交换趋势数据API
     */
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetExchangeTrends() throws Exception {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        when(analyticsService.getExchangeTrends(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
            .thenReturn(mockTrendData);

        // 执行测试
        mockMvc.perform(get("/api/analytics/personal/exchange-trends")
                .param("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).getExchangeTrends(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), anyString());
    }

    /**
     * 测试获取用户活跃度趋势API
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void testGetUserActivityTrends() throws Exception {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        when(analyticsService.getUserActivityTrends(any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
            .thenReturn(mockTrendData);

        // 执行测试
        mockMvc.perform(get("/api/analytics/system/user-activity-trends")
                .param("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].activeUsers").value(100));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).getUserActivityTrends(any(LocalDateTime.class), any(LocalDateTime.class), anyString());
    }

    /**
     * 测试获取线索评级分布API
     */
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetRatingDistribution() throws Exception {
        // 准备测试数据
        Map<String, Long> mockRatingMap = Map.of("A", 100L, "B", 200L, "C", 150L, "D", 50L);
        when(analyticsService.getLeadRatingDistribution(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockRatingMap);

        // 执行测试
        mockMvc.perform(get("/api/analytics/personal/rating-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(4))
                .andExpect(jsonPath("$.data[0].rating").value("A"))
                .andExpect(jsonPath("$.data[0].count").value(100));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).getLeadRatingDistribution(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试获取行业分布API
     */
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetIndustryDistribution() throws Exception {
        // 准备测试数据
        Map<String, Long> mockIndustryMap = Map.of("科技", 300L, "金融", 200L, "制造", 150L);
        when(analyticsService.getIndustryDistribution(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockIndustryMap);

        // 执行测试
        mockMvc.perform(get("/api/analytics/personal/industry-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].industry").value("科技"))
                .andExpect(jsonPath("$.data[0].count").value(300));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).getIndustryDistribution(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试获取实时统计数据API
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void testGetRealTimeStats() throws Exception {
        // 准备测试数据
        when(analyticsService.getRealTimeStats()).thenReturn(mockRealTimeStats);

        // 执行测试
        mockMvc.perform(get("/api/analytics/system/realtime-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.onlineUsers").value(100))
                .andExpect(jsonPath("$.data.todayLeads").value(50))
                .andExpect(jsonPath("$.data.todayExchanges").value(20));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).getRealTimeStats();
    }

    /**
     * 测试导出数据报表API
     */
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testExportDataReport() throws Exception {
        // 准备测试数据
        byte[] mockReportData = "mock report data".getBytes();
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        String reportType = "personal";
        
        when(analyticsService.exportReport(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), eq(reportType)))
            .thenReturn(mockReportData);

        // 执行测试
        mockMvc.perform(get("/api/analytics/export")
                .param("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("reportType", reportType)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().bytes(mockReportData));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).exportReport(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), eq(reportType));
    }

    /**
     * 测试刷新缓存统计数据API
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void testRefreshCachedStats() throws Exception {
        // 准备测试数据
        doNothing().when(analyticsService).refreshCachedStats();
        when(analyticsService.getRealTimeStats()).thenReturn(mockRealTimeStats);

        // 执行测试
        mockMvc.perform(post("/api/analytics/system/refresh-cache")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.onlineUsers").value(100))
                .andExpect(jsonPath("$.data.todayLeads").value(50))
                .andExpect(jsonPath("$.data.todayExchanges").value(20));

        // 验证服务方法被调用
        verify(analyticsService, times(1)).refreshCachedStats();
    }

    /**
     * 测试未授权访问系统统计数据
     */
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetSystemStats_Unauthorized() throws Exception {
        // 执行测试 - 普通用户访问系统统计应该被拒绝
        mockMvc.perform(get("/api/analytics/system/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

        // 验证服务方法未被调用
        verify(analyticsService, never()).getSystemStats(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    /**
     * 测试参数验证 - 时间范围无效
     */
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetLeadTrends_InvalidTimeRange() throws Exception {
        // 执行测试 - 结束时间早于开始时间
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(7);

        mockMvc.perform(get("/api/analytics/personal/lead-trends")
                .param("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .param("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 验证服务方法未被调用
        verify(analyticsService, never()).getLeadTrends(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), anyString());
    }
}