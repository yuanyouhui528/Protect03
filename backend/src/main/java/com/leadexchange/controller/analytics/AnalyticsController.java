package com.leadexchange.controller.analytics;

import com.leadexchange.common.result.Result;
import com.leadexchange.domain.analytics.PersonalStats;
import com.leadexchange.domain.analytics.SystemStats;
import com.leadexchange.domain.analytics.TrendData;
import com.leadexchange.service.analytics.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据分析控制器
 * 处理业务数据统计分析相关操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Tag(name = "数据分析", description = "数据统计分析相关接口")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * 获取个人数据统计
     */
    @Operation(summary = "获取个人数据统计", description = "获取当前用户的个人数据统计信息")
    @GetMapping("/personal")
    public Result<PersonalStats> getPersonalStats(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Authentication authentication) {
        try {
            // 从认证信息中获取用户ID
            Long userId = getUserIdFromAuthentication(authentication);
            PersonalStats stats = analyticsService.getPersonalStats(userId, startTime, endTime);
            return Result.success(stats);
        } catch (Exception e) {
            logger.error("获取个人数据统计失败", e);
            return Result.error("获取个人数据统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统数据统计
     */
    @Operation(summary = "获取系统数据统计", description = "获取系统整体数据统计信息")
    @GetMapping("/system")
    public Result<SystemStats> getSystemStats(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            SystemStats stats = analyticsService.getSystemStats(startTime, endTime);
            return Result.success(stats);
        } catch (Exception e) {
            logger.error("获取系统数据统计失败", e);
            return Result.error("获取系统数据统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取线索趋势数据
     */
    @Operation(summary = "获取线索趋势数据", description = "获取线索发布趋势统计数据")
    @GetMapping("/trends/leads")
    public Result<List<TrendData>> getLeadTrends(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "时间粒度") @RequestParam(defaultValue = "day") String granularity,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        try {
            List<TrendData> trends = analyticsService.getLeadTrends(userId, startTime, endTime, granularity);
            return Result.success(trends);
        } catch (Exception e) {
            logger.error("获取线索趋势数据失败", e);
            return Result.error("获取线索趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取交换趋势数据
     */
    @Operation(summary = "获取交换趋势数据", description = "获取线索交换趋势统计数据")
    @GetMapping("/trends/exchanges")
    public Result<List<TrendData>> getExchangeTrends(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "时间粒度") @RequestParam(defaultValue = "day") String granularity,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        try {
            List<TrendData> trends = analyticsService.getExchangeTrends(userId, startTime, endTime, granularity);
            return Result.success(trends);
        } catch (Exception e) {
            logger.error("获取交换趋势数据失败", e);
            return Result.error("获取交换趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户活跃度趋势
     */
    @Operation(summary = "获取用户活跃度趋势", description = "获取用户活跃度趋势统计数据")
    @GetMapping("/trends/activity")
    public Result<List<TrendData>> getUserActivityTrends(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "时间粒度") @RequestParam(defaultValue = "day") String granularity) {
        try {
            List<TrendData> trends = analyticsService.getUserActivityTrends(startTime, endTime, granularity);
            return Result.success(trends);
        } catch (Exception e) {
            logger.error("获取用户活跃度趋势失败", e);
            return Result.error("获取用户活跃度趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取线索评级分布
     */
    @Operation(summary = "获取线索评级分布", description = "获取线索评级分布统计数据")
    @GetMapping("/distribution/rating")
    public Result<Map<String, Long>> getLeadRatingDistribution(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        try {
            Map<String, Long> distribution = analyticsService.getLeadRatingDistribution(userId, startTime, endTime);
            return Result.success(distribution);
        } catch (Exception e) {
            logger.error("获取线索评级分布失败", e);
            return Result.error("获取线索评级分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取行业分布
     */
    @Operation(summary = "获取行业分布", description = "获取线索行业分布统计数据")
    @GetMapping("/distribution/industry")
    public Result<Map<String, Long>> getIndustryDistribution(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        try {
            Map<String, Long> distribution = analyticsService.getIndustryDistribution(userId, startTime, endTime);
            return Result.success(distribution);
        } catch (Exception e) {
            logger.error("获取行业分布失败", e);
            return Result.error("获取行业分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取实时统计数据
     */
    @Operation(summary = "获取实时统计数据", description = "获取系统实时统计数据")
    @GetMapping("/realtime")
    public Result<Map<String, Object>> getRealTimeStats() {
        try {
            Map<String, Object> stats = analyticsService.getRealTimeStats();
            return Result.success(stats);
        } catch (Exception e) {
            logger.error("获取实时统计数据失败", e);
            return Result.error("获取实时统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 导出数据报表
     */
    @Operation(summary = "导出数据报表", description = "导出个人或系统数据报表")
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "报表类型") @RequestParam String reportType,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        try {
            byte[] reportData = analyticsService.exportReport(userId, startTime, endTime, reportType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "analytics_report.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reportData);
        } catch (Exception e) {
            logger.error("导出数据报表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 刷新缓存统计数据
     */
    @Operation(summary = "刷新缓存统计数据", description = "手动刷新缓存的统计数据")
    @PostMapping("/refresh")
    public Result<String> refreshCachedStats() {
        try {
            analyticsService.refreshCachedStats();
            return Result.success("缓存刷新成功");
        } catch (Exception e) {
            logger.error("刷新缓存统计数据失败", e);
            return Result.error("刷新缓存统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 从认证信息中获取用户ID
     * 
     * @param authentication 认证信息
     * @return 用户ID
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // TODO: 根据实际的认证实现来获取用户ID
        // 这里假设用户名就是用户ID，实际项目中需要根据具体实现调整
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            // 如果无法解析，返回默认值或抛出异常
            logger.warn("无法从认证信息中解析用户ID: {}", authentication.getName());
            return 1L; // 临时返回默认用户ID
        }
    }
}