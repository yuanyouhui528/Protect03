package com.leadexchange.controller.rating;

import com.leadexchange.common.result.Result;
import com.leadexchange.common.result.PageResult;
import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.rating.RatingChangeReason;
import com.leadexchange.service.rating.RatingEngineService;
import com.leadexchange.service.lead.LeadService;
import com.leadexchange.service.rating.RatingEngineService.RatingResult;
import com.leadexchange.service.rating.RatingEngineService.RatingStatistics;
import com.leadexchange.service.rating.RatingEngineService.RatingTrendData;
import com.leadexchange.service.rating.RatingEngineService.RatingDetail;
import com.leadexchange.domain.rating.RatingRule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 评级引擎控制器
 * 提供线索评级相关的REST API接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
@Validated
@Api(tags = "评级引擎管理")
public class RatingEngineController {

    private static final Logger log = LoggerFactory.getLogger(RatingEngineController.class);
    private final RatingEngineService ratingEngineService;
    private final LeadService leadService;

    /**
     * 计算线索评级
     * 
     * @param leadId 线索ID
     * @return 评级结果
     */
    @PostMapping("/calculate/{leadId}")
    @ApiOperation("计算线索评级")
    @PreAuthorize("hasAuthority('rating:calculate')")
    public Result<RatingResult> calculateRating(
            @ApiParam("线索ID") @PathVariable @NotNull Long leadId) {
        log.info("开始计算线索评级，线索ID: {}", leadId);
        // 需要先获取Lead对象
        Lead lead = getLeadById(leadId);
        RatingResult result = ratingEngineService.calculateRating(lead);
        log.info("线索评级计算完成，线索ID: {}, 评级: {}, 分数: {}", leadId, result.getRating(), result.getScore());
        return Result.success(result);
    }

    /**
     * 批量计算线索评级
     * 
     * @param leadIds 线索ID列表
     * @return 评级结果列表
     */
    @PostMapping("/calculate/batch")
    @ApiOperation("批量计算线索评级")
    @PreAuthorize("hasAuthority('rating:calculate')")
    public Result<List<RatingResult>> calculateRatingBatch(
            @ApiParam("线索ID列表") @RequestBody @NotNull List<Long> leadIds) {
        log.info("开始批量计算线索评级，线索数量: {}", leadIds.size());
        // 直接使用线索ID列表进行批量计算
        Map<Long, RatingResult> resultMap = ratingEngineService.batchCalculateRating(leadIds);
        List<RatingResult> results = new ArrayList<>(resultMap.values());
        log.info("批量线索评级计算完成，成功数量: {}", results.size());
        return Result.success(results);
    }

    /**
     * 重新计算线索评级
     * 
     * @param leadId 线索ID
     * @param reason 重新计算原因
     * @return 评级结果
     */
    @PostMapping("/recalculate/{leadId}")
    @ApiOperation("重新计算线索评级")
    @PreAuthorize("hasAuthority('rating:recalculate')")
    public Result<RatingResult> recalculateRating(
            @ApiParam("线索ID") @PathVariable @NotNull Long leadId,
            @ApiParam("重新计算原因") @RequestParam(defaultValue = "RULE_CHANGE") RatingChangeReason reason) {
        log.info("开始重新计算线索评级，线索ID: {}, 原因: {}", leadId, reason);
        // 使用当前用户ID作为操作人ID
        Long operatorId = getCurrentUserId();
        RatingResult result = ratingEngineService.recalculateRating(leadId, reason, operatorId);
        log.info("线索评级重新计算完成，线索ID: {}, 评级: {}, 分数: {}", leadId, result.getRating(), result.getScore());
        return Result.success(result);
    }

    /**
     * 手动调整线索评级
     * 
     * @param leadId 线索ID
     * @param newRating 新评级
     * @param reason 调整原因
     * @return 评级结果
     */
    @PostMapping("/adjust/{leadId}")
    @ApiOperation("手动调整线索评级")
    @PreAuthorize("hasAuthority('rating:adjust')")
    public Result<RatingResult> adjustRating(
            @ApiParam("线索ID") @PathVariable @NotNull Long leadId,
            @ApiParam("新评级") @RequestParam @NotNull LeadRating newRating,
            @ApiParam("调整原因") @RequestParam(required = false) String reason) {
        log.info("开始手动调整线索评级，线索ID: {}, 新评级: {}, 原因: {}", leadId, newRating, reason);
        // 使用当前用户ID作为操作人ID，默认分数为null
        Long operatorId = getCurrentUserId();
        RatingResult result = ratingEngineService.adjustRating(leadId, newRating, null, 
                RatingChangeReason.MANUAL_ADJUSTMENT, operatorId, reason);
        log.info("线索评级手动调整完成，线索ID: {}, 评级: {}, 分数: {}", leadId, result.getRating(), result.getScore());
        return Result.success(result);
    }

    /**
     * 获取线索评级详情
     * 
     * @param leadId 线索ID
     * @return 评级详情
     */
    @GetMapping("/details/{leadId}")
    @ApiOperation("获取线索评级详情")
    @PreAuthorize("hasAuthority('rating:view')")
    public Result<RatingResult> getRatingDetails(
            @ApiParam("线索ID") @PathVariable @NotNull Long leadId) {
        log.debug("获取线索评级详情，线索ID: {}", leadId);
        RatingDetail detail = ratingEngineService.getRatingDetail(leadId);
        RatingResult result = convertDetailToResult(detail);
        return Result.success(result);
    }

    /**
     * 预览评级结果
     * 
     * @param leadId 线索ID
     * @return 预览结果
     */
    @GetMapping("/preview/{leadId}")
    @ApiOperation("预览评级结果")
    @PreAuthorize("hasAuthority('rating:view')")
    public Result<RatingResult> previewRating(
            @ApiParam("线索ID") @PathVariable @NotNull Long leadId) {
        log.debug("预览线索评级结果，线索ID: {}", leadId);
        // 需要先获取Lead对象
        Lead lead = getLeadById(leadId);
        RatingResult result = ratingEngineService.previewRating(lead);
        return Result.success(result);
    }

    /**
     * 获取评级统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @ApiOperation("获取评级统计信息")
    @PreAuthorize("hasAuthority('rating:statistics')")
    public Result<RatingStatistics> getRatingStatistics(
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.debug("获取评级统计信息，时间范围: {} - {}", startTime, endTime);
        RatingStatistics statistics = ratingEngineService.getRatingStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取评级分布数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分布数据
     */
    @GetMapping("/distribution")
    @ApiOperation("获取评级分布数据")
    @PreAuthorize("hasAuthority('rating:statistics')")
    public Result<Map<LeadRating, Long>> getRatingDistribution(
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.debug("获取评级分布数据，时间范围: {} - {}", startTime, endTime);
        Map<LeadRating, Long> distribution = ratingEngineService.getRatingDistribution();
        return Result.success(distribution);
    }

    /**
     * 获取评级趋势数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度（DAY/WEEK/MONTH）
     * @return 趋势数据
     */
    @GetMapping("/trend")
    @ApiOperation("获取评级趋势数据")
    @PreAuthorize("hasAuthority('rating:statistics')")
    public Result<List<RatingTrendData>> getRatingTrend(
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @ApiParam("时间粒度") @RequestParam(defaultValue = "DAY") String granularity) {
        log.debug("获取评级趋势数据，时间范围: {} - {}, 粒度: {}", startTime, endTime, granularity);
        // 计算天数，默认30天
        int days = calculateDaysBetween(startTime, endTime, 30);
        List<RatingTrendData> trendData = ratingEngineService.getRatingTrend(days);
        return Result.success(trendData);
    }

    /**
     * 批量重新评级
     * 
     * @param reason 重新评级原因
     * @return 处理结果
     */
    @PostMapping("/batch-rerating")
    @ApiOperation("批量重新评级")
    @PreAuthorize("hasAuthority('rating:batch-rerating')")
    public Result<Map<String, Object>> batchRerating(
            @ApiParam("重新评级原因") @RequestParam(defaultValue = "RULE_CHANGE") RatingChangeReason reason) {
        log.info("开始批量重新评级，原因: {}", reason);
        // 创建批量条件和操作人ID
        RatingEngineService.RatingBatchCondition condition = new RatingEngineService.RatingBatchCondition();
        Long operatorId = getCurrentUserId();
        RatingEngineService.BatchRatingResult batchResult = ratingEngineService.batchRecalculateRating(condition, reason, operatorId);
        Map<String, Object> result = convertBatchResultToMap(batchResult);
        log.info("批量重新评级完成，处理结果: {}", result);
        return Result.success(result);
    }

    /**
     * 验证评级规则配置
     * 
     * @return 验证结果
     */
    @GetMapping("/validate-rules")
    @ApiOperation("验证评级规则配置")
    @PreAuthorize("hasAuthority('rating:validate')")
    public Result<Map<String, Object>> validateRatingRules() {
        log.debug("开始验证评级规则配置");
        // 获取所有规则并验证
        List<RatingRule> rules = getAllRatingRules();
        RatingEngineService.RuleValidationResult validationResult = ratingEngineService.validateRules(rules);
        Map<String, Object> result = convertValidationResultToMap(validationResult);
        log.debug("评级规则配置验证完成，结果: {}", result);
        return Result.success(result);
    }

    /**
     * 刷新评级规则缓存
     * 
     * @return 刷新结果
     */
    @PostMapping("/refresh-cache")
    @ApiOperation("刷新评级规则缓存")
    @PreAuthorize("hasAuthority('rating:cache-refresh')")
    public Result<String> refreshRatingCache() {
        log.info("开始刷新评级规则缓存");
        ratingEngineService.refreshRuleCache();
        log.info("评级规则缓存刷新完成");
        return Result.success("缓存刷新成功");
    }

    /**
     * 导出评级报告
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 导出格式（EXCEL/PDF/CSV）
     * @return 报告数据
     */
    @GetMapping("/export-report")
    @ApiOperation("导出评级报告")
    @PreAuthorize("hasAuthority('rating:export')")
    public Result<Map<String, Object>> exportRatingReport(
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @ApiParam("导出格式") @RequestParam(defaultValue = "EXCEL") String format) {
        log.info("开始导出评级报告，时间范围: {} - {}, 格式: {}", startTime, endTime, format);
        // 创建报告条件
        RatingEngineService.RatingReportCondition condition = new RatingEngineService.RatingReportCondition();
        condition.setDateFrom(startTime != null ? startTime.toString() : null);
        condition.setDateTo(endTime != null ? endTime.toString() : null);
        condition.setFormat(format);
        RatingEngineService.RatingReportData reportData = ratingEngineService.exportRatingReport(condition);
        Map<String, Object> report = convertReportDataToMap(reportData);
        log.info("评级报告导出完成");
        return Result.success(report);
    }

    /**
     * 根据ID获取线索对象
     * 
     * @param leadId 线索ID
     * @return 线索对象
     */
    private Lead getLeadById(Long leadId) {
        // 从LeadService获取线索对象
        Optional<Lead> leadOpt = leadService.getLeadById(leadId);
        return leadOpt.orElse(null);
    }

    /**
     * 根据ID列表获取线索对象列表
     * 
     * @param leadIds 线索ID列表
     * @return 线索对象列表
     */
    private List<Lead> getLeadsByIds(List<Long> leadIds) {
        // TODO: 实现从LeadService批量获取线索对象的逻辑
        List<Lead> leads = new ArrayList<>();
        for (Long leadId : leadIds) {
            Optional<Lead> leadOpt = leadService.getLeadById(leadId);
            leadOpt.ifPresent(leads::add);
        }
        return leads;
    }

    /**
     * 获取当前用户ID
     * 
     * @return 当前用户ID
     */
    private Long getCurrentUserId() {
        // TODO: 实现从SecurityContext获取当前用户ID的逻辑
        return 1L; // 临时返回固定值
    }

    /**
     * 将RatingDetail转换为RatingResult
     * 
     * @param detail 评级详情
     * @return 评级结果
     */
    private RatingResult convertDetailToResult(RatingEngineService.RatingDetail detail) {
        RatingResult result = new RatingResult();
        result.setLeadId(detail.getLeadId());
        result.setRating(detail.getRating());
        result.setScore(detail.getScore());
        result.setDimensionScores(detail.getDimensionScores());
        result.setCalculationDetails(detail.getCalculationDetails());
        result.setCalculationTime(detail.getCalculationTime());
        result.setSuccess(true);
        return result;
    }

    /**
     * 计算两个时间之间的天数
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param defaultDays 默认天数
     * @return 天数
     */
    private int calculateDaysBetween(LocalDateTime startTime, LocalDateTime endTime, int defaultDays) {
        if (startTime != null && endTime != null) {
            return (int) java.time.Duration.between(startTime, endTime).toDays();
        }
        return defaultDays;
    }

    /**
     * 将批量结果转换为Map
     * 
     * @param batchResult 批量结果
     * @return Map格式的结果
     */
    private Map<String, Object> convertBatchResultToMap(RatingEngineService.BatchRatingResult batchResult) {
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", batchResult.getTotalCount());
        result.put("successCount", batchResult.getSuccessCount());
        result.put("failureCount", batchResult.getFailureCount());
        result.put("errors", batchResult.getErrors());
        result.put("processTime", batchResult.getProcessTime());
        return result;
    }

    /**
     * 获取所有评级规则
     * 
     * @return 评级规则列表
     */
    private List<RatingRule> getAllRatingRules() {
        // TODO: 实现获取所有评级规则的逻辑
        return new ArrayList<>();
    }

    /**
     * 将验证结果转换为Map
     * 
     * @param validationResult 验证结果
     * @return Map格式的结果
     */
    private Map<String, Object> convertValidationResultToMap(RatingEngineService.RuleValidationResult validationResult) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", validationResult.isValid());
        result.put("errors", validationResult.getErrors());
        result.put("warnings", validationResult.getWarnings());
        return result;
    }

    /**
     * 将报告数据转换为Map
     * 
     * @param reportData 报告数据
     * @return Map格式的结果
     */
    private Map<String, Object> convertReportDataToMap(RatingEngineService.RatingReportData reportData) {
        Map<String, Object> result = new HashMap<>();
        result.put("fileName", reportData.getFileName());
        result.put("contentType", reportData.getContentType());
        result.put("dataSize", reportData.getData() != null ? reportData.getData().length : 0);
        return result;
    }
}