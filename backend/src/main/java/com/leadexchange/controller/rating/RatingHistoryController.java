package com.leadexchange.controller.rating;

import com.leadexchange.common.result.Result;
import com.leadexchange.common.result.PageResult;
import com.leadexchange.domain.rating.RatingHistory;
import com.leadexchange.domain.rating.RatingChangeReason;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.service.rating.RatingHistoryService;
import com.leadexchange.service.rating.RatingHistoryService.RatingHistoryStatistics;
import com.leadexchange.service.rating.RatingHistoryService.RatingChangeTrend;
import com.leadexchange.service.rating.RatingHistoryService.OperatorRatingStatistics;
import com.leadexchange.service.rating.RatingHistoryService.RatingRollbackSuggestion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评级历史记录管理控制器
 * 提供评级历史记录相关的REST API接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/rating/history")
@RequiredArgsConstructor
@Validated
@Api(tags = "评级历史记录管理")
public class RatingHistoryController {

    private static final Logger log = LoggerFactory.getLogger(RatingHistoryController.class);
    private final RatingHistoryService ratingHistoryService;

    /**
     * 根据ID获取评级历史记录
     * 
     * @param id 历史记录ID
     * @return 历史记录
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取评级历史记录")
    @PreAuthorize("hasAuthority('rating:history:view')")
    public Result<RatingHistory> getHistoryById(@ApiParam("历史记录ID") @PathVariable @NotNull Long id) {
        log.debug("获取评级历史记录，ID: {}", id);
        Optional<RatingHistory> historyOpt = ratingHistoryService.getHistoryById(id);
        RatingHistory history = historyOpt.orElseThrow(() -> new RuntimeException("评级历史记录不存在"));
        return Result.success(history);
    }

    /**
     * 根据线索ID获取评级历史记录
     * 
     * @param leadId 线索ID
     * @param pageable 分页参数
     * @return 历史记录列表
     */
    @GetMapping("/lead/{leadId}")
    @ApiOperation("根据线索ID获取评级历史记录")
    @PreAuthorize("hasAuthority('rating:history:view')")
    public Result<PageResult<RatingHistory>> getHistoryByLeadId(
            @ApiParam("线索ID") @PathVariable @NotNull Long leadId,
            Pageable pageable) {
        log.debug("获取线索评级历史记录，线索ID: {}, 分页参数: {}", leadId, pageable);
        Page<RatingHistory> page = ratingHistoryService.getLeadRatingHistory(leadId, pageable);
        PageResult<RatingHistory> histories = PageResult.of(page);
        return Result.success(histories);
    }

    /**
     * 根据变更原因获取评级历史记录
     * 
     * @param reason 变更原因
     * @param pageable 分页参数
     * @return 历史记录列表
     */
    @GetMapping("/reason/{reason}")
    @ApiOperation("根据变更原因获取评级历史记录")
    @PreAuthorize("hasAuthority('rating:history:view')")
    public Result<PageResult<RatingHistory>> getHistoryByReason(
            @ApiParam("变更原因") @PathVariable @NotNull RatingChangeReason reason,
            Pageable pageable) {
        log.debug("根据变更原因获取评级历史记录，原因: {}, 分页参数: {}", reason, pageable);
        Page<RatingHistory> page = ratingHistoryService.getHistoryByChangeReason(reason, pageable);
        PageResult<RatingHistory> histories = PageResult.of(page);
        return Result.success(histories);
    }

    /**
     * 根据操作人获取评级历史记录
     * 
     * @param operatorId 操作人ID
     * @param pageable 分页参数
     * @return 历史记录列表
     */
    @GetMapping("/operator/{operatorId}")
    @ApiOperation("根据操作人获取评级历史记录")
    @PreAuthorize("hasAuthority('rating:history:view')")
    public Result<PageResult<RatingHistory>> getHistoryByOperator(
            @ApiParam("操作人ID") @PathVariable @NotNull Long operatorId,
            Pageable pageable) {
        log.debug("根据操作人获取评级历史记录，操作人ID: {}, 分页参数: {}", operatorId, pageable);
        Page<RatingHistory> page = ratingHistoryService.getHistoryByOperator(operatorId, pageable);
        PageResult<RatingHistory> histories = PageResult.of(page);
        return Result.success(histories);
    }

    /**
     * 根据时间范围获取评级历史记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 历史记录列表
     */
    @GetMapping("/time-range")
    @ApiOperation("根据时间范围获取评级历史记录")
    @PreAuthorize("hasAuthority('rating:history:view')")
    public Result<PageResult<RatingHistory>> getHistoryByTimeRange(
            @ApiParam("开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            Pageable pageable) {
        log.debug("根据时间范围获取评级历史记录，时间范围: {} - {}, 分页参数: {}", startTime, endTime, pageable);
        Page<RatingHistory> page = ratingHistoryService.getHistoryByTimeRange(startTime, endTime, pageable);
        PageResult<RatingHistory> histories = PageResult.of(page);
        return Result.success(histories);
    }

    /**
     * 根据评级变化获取历史记录
     * 
     * @param oldRating 变更前评级
     * @param newRating 变更后评级
     * @param pageable 分页参数
     * @return 历史记录列表
     */
    @GetMapping("/rating-change")
    @ApiOperation("根据评级变化获取历史记录")
    @PreAuthorize("hasAuthority('rating:history:view')")
    public Result<PageResult<RatingHistory>> getHistoryByRatingChange(
            @ApiParam("变更前评级") @RequestParam(required = false) LeadRating oldRating,
            @ApiParam("变更后评级") @RequestParam @NotNull LeadRating newRating,
            Pageable pageable) {
        log.debug("根据评级变化获取历史记录，变更: {} -> {}, 分页参数: {}", oldRating, newRating, pageable);
        Page<RatingHistory> page = ratingHistoryService.getHistoryByRatingChange(oldRating, newRating, pageable);
        PageResult<RatingHistory> histories = PageResult.of(page);
        return Result.success(histories);
    }

    /**
     * 获取评级历史统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @ApiOperation("获取评级历史统计信息")
    @PreAuthorize("hasAuthority('rating:history:statistics')")
    public Result<RatingHistoryStatistics> getHistoryStatistics(
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.debug("获取评级历史统计信息，时间范围: {} - {}", startTime, endTime);
        RatingHistoryStatistics statistics = ratingHistoryService.getHistoryStatistics(startTime, endTime);
        return Result.success(statistics);
    }

    /**
     * 获取评级历史趋势数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度（DAY/WEEK/MONTH）
     * @return 趋势数据
     */
    @GetMapping("/trend")
    @ApiOperation("获取评级历史趋势数据")
    @PreAuthorize("hasAuthority('rating:history:statistics')")
    public Result<List<RatingChangeTrend>> getHistoryTrend(
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @ApiParam("时间粒度") @RequestParam(defaultValue = "DAY") String granularity) {
        log.debug("获取评级历史趋势数据，时间范围: {} - {}, 粒度: {}", startTime, endTime, granularity);
        List<RatingChangeTrend> trendData = ratingHistoryService.getRatingChangeTrend(startTime, endTime, granularity);
        return Result.success(trendData);
    }

    /**
     * 获取操作人统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作人统计
     */
    @GetMapping("/operator-statistics")
    @ApiOperation("获取操作人统计信息")
    @PreAuthorize("hasAuthority('rating:history:statistics')")
    public Result<List<OperatorRatingStatistics>> getOperatorStatistics(
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.debug("获取操作人统计信息，时间范围: {} - {}", startTime, endTime);
        List<OperatorRatingStatistics> statistics = ratingHistoryService.getOperatorRatingStatistics(startTime, endTime);
        return Result.success(statistics);
    }

    /**
     * 删除过期的历史记录
     * 
     * @param retentionDays 保留天数
     * @return 删除结果
     */
    @DeleteMapping("/expired")
    @ApiOperation("删除过期的历史记录")
    @PreAuthorize("hasAuthority('rating:history:delete')")
    public Result<Map<String, Object>> deleteExpiredHistory(
            @ApiParam("保留天数") @RequestParam(defaultValue = "365") int retentionDays) {
        log.info("删除过期的评级历史记录，保留天数: {}", retentionDays);
        int deletedCount = ratingHistoryService.deleteExpiredHistory(retentionDays);
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        result.put("retentionDays", retentionDays);
        log.info("过期历史记录删除完成，删除数量: {}", deletedCount);
        return Result.success(result);
    }

    /**
     * 批量删除历史记录
     * 
     * @param ids 历史记录ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除历史记录")
    @PreAuthorize("hasAuthority('rating:history:delete')")
    public Result<String> deleteHistoryBatch(@ApiParam("历史记录ID列表") @RequestBody @NotNull List<Long> ids) {
        log.info("批量删除评级历史记录，记录数量: {}", ids.size());
        ratingHistoryService.batchDeleteHistory(ids);
        log.info("评级历史记录批量删除成功");
        return Result.success("批量删除成功");
    }

    /**
     * 导出历史记录数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 导出格式（EXCEL/CSV/JSON）
     * @return 导出数据
     */
    @GetMapping("/export")
    @ApiOperation("导出历史记录数据")
    @PreAuthorize("hasAuthority('rating:history:export')")
    public Result<Map<String, Object>> exportHistoryData(
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @ApiParam("导出格式") @RequestParam(defaultValue = "EXCEL") String format) {
        log.info("导出评级历史记录数据，时间范围: {} - {}, 格式: {}", startTime, endTime, format);
        List<RatingHistory> historyData = ratingHistoryService.exportData(startTime, endTime);
        Map<String, Object> result = new HashMap<>();
        result.put("data", historyData);
        result.put("format", format);
        result.put("count", historyData.size());
        log.info("评级历史记录数据导出完成");
        return Result.success(result);
    }

    /**
     * 获取回滚建议
     * 
     * @param leadId 线索ID
     * @param targetTime 目标时间点
     * @return 回滚建议
     */
    @GetMapping("/rollback-suggestion/{leadId}")
    @ApiOperation("获取回滚建议")
    @PreAuthorize("hasAuthority('rating:history:rollback')")
    public Result<RatingRollbackSuggestion> getRollbackSuggestions(
            @ApiParam("线索ID") @PathVariable @NotNull Long leadId,
            @ApiParam("目标时间点") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime targetTime) {
        log.debug("获取评级回滚建议，线索ID: {}, 目标时间: {}", leadId, targetTime);
        List<RatingRollbackSuggestion> suggestions = ratingHistoryService.getRollbackSuggestions(leadId);
        RatingRollbackSuggestion suggestion = suggestions.isEmpty() ? new RatingRollbackSuggestion() : suggestions.get(0);
        return Result.success(suggestion);
    }

    /**
     * 执行评级回滚
     * 
     * @param leadId 线索ID
     * @param targetHistoryId 目标历史记录ID
     * @param reason 回滚原因
     * @return 回滚结果
     */
    @PostMapping("/rollback/{leadId}")
    @ApiOperation("执行评级回滚")
    @PreAuthorize("hasAuthority('rating:history:rollback')")
    public Result<RatingHistory> executeRollback(
            @ApiParam("线索ID") @PathVariable @NotNull Long leadId,
            @ApiParam("目标历史记录ID") @RequestParam @NotNull Long targetHistoryId,
            @ApiParam("回滚原因") @RequestParam(required = false) String reason) {
        log.info("执行评级回滚，线索ID: {}, 目标历史记录ID: {}, 原因: {}", leadId, targetHistoryId, reason);
        // 获取当前用户信息（这里简化处理，实际应该从SecurityContext获取）
        Long operatorId = 1L; // TODO: 从当前登录用户获取
        String operatorName = "系统管理员"; // TODO: 从当前登录用户获取
        RatingHistory result = ratingHistoryService.rollbackToHistory(leadId, targetHistoryId, operatorName, reason);
        log.info("评级回滚执行完成，线索ID: {}, 新评级: {}", leadId, result.getCurrentRating());
        return Result.success(result);
    }

    /**
     * 获取所有历史记录（分页）
     * 
     * @param pageable 分页参数
     * @return 历史记录列表
     */
    @GetMapping
    @ApiOperation("获取所有历史记录")
    @PreAuthorize("hasAuthority('rating:history:view')")
    public Result<PageResult<RatingHistory>> getAllHistory(Pageable pageable) {
        log.debug("获取所有评级历史记录，分页参数: {}", pageable);
        Page<RatingHistory> page = ratingHistoryService.getAllHistory(pageable);
        PageResult<RatingHistory> histories = PageResult.of(page);
        return Result.success(histories);
    }

    /**
     * 搜索历史记录
     * 
     * @param keyword 搜索关键词
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reason 变更原因
     * @param rating 评级
     * @param pageable 分页参数
     * @return 搜索结果
     */
    @GetMapping("/search")
    @ApiOperation("搜索历史记录")
    @PreAuthorize("hasAuthority('rating:history:view')")
    public Result<PageResult<RatingHistory>> searchHistory(
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @ApiParam("变更原因") @RequestParam(required = false) RatingChangeReason reason,
            @ApiParam("评级") @RequestParam(required = false) LeadRating rating,
            Pageable pageable) {
        log.debug("搜索评级历史记录，关键词: {}, 时间范围: {} - {}, 原因: {}, 评级: {}", 
                keyword, startTime, endTime, reason, rating);
        // 这里可以实现复合搜索逻辑
        Page<RatingHistory> page = ratingHistoryService.getAllHistory(pageable);
        PageResult<RatingHistory> histories = PageResult.of(page);
        return Result.success(histories);
    }
}