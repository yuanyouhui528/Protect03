package com.leadexchange.service.rating;

import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.rating.RatingHistory;
import com.leadexchange.domain.rating.RatingRule;
import com.leadexchange.domain.rating.RatingRuleType;
import com.leadexchange.domain.rating.RatingChangeReason;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 评级引擎服务接口
 * 提供线索评级的核心功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface RatingEngineService {

    /**
     * 计算线索评级
     * 根据配置的评级规则计算线索的综合评级
     * 
     * @param lead 线索对象
     * @return 评级结果
     */
    RatingResult calculateRating(Lead lead);

    /**
     * 批量计算线索评级
     * 
     * @param leadIds 线索ID列表
     * @return 评级结果映射（线索ID -> 评级结果）
     */
    Map<Long, RatingResult> batchCalculateRating(List<Long> leadIds);

    /**
     * 重新计算线索评级
     * 当线索信息更新或评级规则变更时重新计算
     * 
     * @param leadId 线索ID
     * @param reason 重新评级原因
     * @param operatorId 操作人ID（可选）
     * @return 新的评级结果
     */
    RatingResult recalculateRating(Long leadId, RatingChangeReason reason, Long operatorId);

    /**
     * 手动调整线索评级
     * 管理员手动调整线索评级
     * 
     * @param leadId 线索ID
     * @param newRating 新评级
     * @param newScore 新分数
     * @param reason 调整原因
     * @param operatorId 操作人ID
     * @param description 调整说明
     * @return 调整结果
     */
    RatingResult adjustRating(Long leadId, LeadRating newRating, Integer newScore, 
                             RatingChangeReason reason, Long operatorId, String description);

    /**
     * 获取线索评级历史
     * 
     * @param leadId 线索ID
     * @return 评级历史列表
     */
    List<RatingHistory> getRatingHistory(Long leadId);

    /**
     * 获取评级统计信息
     * 
     * @return 评级统计数据
     */
    RatingStatistics getRatingStatistics();

    /**
     * 获取用户评级统计信息
     * 
     * @param userId 用户ID
     * @return 用户评级统计数据
     */
    RatingStatistics getUserRatingStatistics(Long userId);

    /**
     * 获取指定时间范围内的评级统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级统计数据
     */
    RatingStatistics getRatingStatisticsInRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取评级分布数据
     * 
     * @return 评级分布统计
     */
    Map<LeadRating, Long> getRatingDistribution();

    /**
     * 获取评级趋势数据
     * 
     * @param days 统计天数
     * @return 评级趋势数据
     */
    List<RatingTrendData> getRatingTrend(int days);

    /**
     * 验证评级规则配置
     * 
     * @param rules 评级规则列表
     * @return 验证结果
     */
    RuleValidationResult validateRules(List<RatingRule> rules);

    /**
     * 刷新评级规则缓存
     * 当评级规则配置变更时刷新缓存
     */
    void refreshRuleCache();

    /**
     * 批量重新评级
     * 批量重新计算指定条件下的线索评级
     * 
     * @param condition 筛选条件
     * @param reason 重新评级原因
     * @param operatorId 操作人ID
     * @return 批量处理结果
     */
    BatchRatingResult batchRecalculateRating(RatingBatchCondition condition, 
                                            RatingChangeReason reason, Long operatorId);

    /**
     * 获取评级详情
     * 获取线索评级的详细计算过程
     * 
     * @param leadId 线索ID
     * @return 评级详情
     */
    RatingDetail getRatingDetail(Long leadId);

    /**
     * 预览评级结果
     * 在不保存的情况下预览评级结果
     * 
     * @param lead 线索对象
     * @return 预览结果
     */
    RatingResult previewRating(Lead lead);

    /**
     * 导出评级报告
     * 
     * @param condition 导出条件
     * @return 报告数据
     */
    RatingReportData exportRatingReport(RatingReportCondition condition);

    /**
     * 评级结果内部类
     */
    class RatingResult {
        private Long leadId;
        private LeadRating rating;
        private Double score;
        private Map<RatingRuleType, Double> dimensionScores;
        private String calculationDetails;
        private LocalDateTime calculationTime;
        private String ratingVersion;
        private Boolean isManualAdjustment;
        private String adjustmentReason;
        private String version;
        private boolean success;
        private String message;

        // 构造函数、getter和setter方法
        public RatingResult() {}

        public RatingResult(LeadRating rating, Double score, Map<RatingRuleType, Double> dimensionScores) {
            this.rating = rating;
            this.score = score;
            this.dimensionScores = dimensionScores;
            this.success = true;
        }

        public Long getLeadId() { return leadId; }
        public void setLeadId(Long leadId) { this.leadId = leadId; }
        public LeadRating getRating() { return rating; }
        public void setRating(LeadRating rating) { this.rating = rating; }
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
        public Map<RatingRuleType, Double> getDimensionScores() { return dimensionScores; }
        public void setDimensionScores(Map<RatingRuleType, Double> dimensionScores) { this.dimensionScores = dimensionScores; }
        public String getCalculationDetails() { return calculationDetails; }
        public void setCalculationDetails(String calculationDetails) { this.calculationDetails = calculationDetails; }
        public LocalDateTime getCalculationTime() { return calculationTime; }
        public void setCalculationTime(LocalDateTime calculationTime) { this.calculationTime = calculationTime; }
        public String getRatingVersion() { return ratingVersion; }
        public void setRatingVersion(String ratingVersion) { this.ratingVersion = ratingVersion; }
        public Boolean getIsManualAdjustment() { return isManualAdjustment; }
        public void setIsManualAdjustment(Boolean isManualAdjustment) { this.isManualAdjustment = isManualAdjustment; }
        public String getAdjustmentReason() { return adjustmentReason; }
        public void setAdjustmentReason(String adjustmentReason) { this.adjustmentReason = adjustmentReason; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * 评级统计信息内部类
     */
    class RatingStatistics {
        private Long totalCount;
        private Map<LeadRating, Long> ratingCounts;
        private Double averageScore;
        private Map<String, Double> dimensionAverages;
        private Long todayRatingCount;
        private Long weekRatingCount;
        private Long monthRatingCount;

        // getter和setter方法
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Map<LeadRating, Long> getRatingCounts() { return ratingCounts; }
        public void setRatingCounts(Map<LeadRating, Long> ratingCounts) { this.ratingCounts = ratingCounts; }
        public Double getAverageScore() { return averageScore; }
        public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
        public Map<String, Double> getDimensionAverages() { return dimensionAverages; }
        public void setDimensionAverages(Map<String, Double> dimensionAverages) { this.dimensionAverages = dimensionAverages; }
        public Long getTodayRatingCount() { return todayRatingCount; }
        public void setTodayRatingCount(Long todayRatingCount) { this.todayRatingCount = todayRatingCount; }
        public Long getWeekRatingCount() { return weekRatingCount; }
        public void setWeekRatingCount(Long weekRatingCount) { this.weekRatingCount = weekRatingCount; }
        public Long getMonthRatingCount() { return monthRatingCount; }
        public void setMonthRatingCount(Long monthRatingCount) { this.monthRatingCount = monthRatingCount; }
    }

    /**
     * 评级趋势数据内部类
     */
    class RatingTrendData {
        private String date;
        private Map<LeadRating, Long> ratingCounts;
        private Double averageScore;

        // getter和setter方法
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Map<LeadRating, Long> getRatingCounts() { return ratingCounts; }
        public void setRatingCounts(Map<LeadRating, Long> ratingCounts) { this.ratingCounts = ratingCounts; }
        public Double getAverageScore() { return averageScore; }
        public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
    }

    /**
     * 规则验证结果内部类
     */
    class RuleValidationResult {
        private boolean valid;
        private List<String> errors;
        private List<String> warnings;

        // 构造函数
        public RuleValidationResult() {}
        
        public RuleValidationResult(boolean valid) {
            this.valid = valid;
        }

        // getter和setter方法
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }

    /**
     * 批量评级条件内部类
     */
    class RatingBatchCondition {
        private List<Long> leadIds;
        private List<LeadRating> ratings;
        private String dateFrom;
        private String dateTo;
        private Long userId;

        // getter和setter方法
        public List<Long> getLeadIds() { return leadIds; }
        public void setLeadIds(List<Long> leadIds) { this.leadIds = leadIds; }
        public List<LeadRating> getRatings() { return ratings; }
        public void setRatings(List<LeadRating> ratings) { this.ratings = ratings; }
        public String getDateFrom() { return dateFrom; }
        public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }
        public String getDateTo() { return dateTo; }
        public void setDateTo(String dateTo) { this.dateTo = dateTo; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    /**
     * 批量评级结果内部类
     */
    class BatchRatingResult {
        private int totalCount;
        private int successCount;
        private int failureCount;
        private List<String> errors;
        private LocalDateTime processTime;

        // getter和setter方法
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public LocalDateTime getProcessTime() { return processTime; }
        public void setProcessTime(LocalDateTime processTime) { this.processTime = processTime; }
    }

    /**
     * 评级详情内部类
     */
    class RatingDetail {
        private Long leadId;
        private LeadRating rating;
        private Double score;
        private Map<RatingRuleType, Double> dimensionScores;
        private String calculationDetails;
        private LocalDateTime calculationTime;
        private LeadRating finalRating;
        private Integer finalScore;
        private Map<String, Object> dimensionDetails;
        private List<RatingRule> appliedRules;
        private String calculationProcess;

        // getter和setter方法
        public Long getLeadId() { return leadId; }
        public void setLeadId(Long leadId) { this.leadId = leadId; }
        public LeadRating getRating() { return rating; }
        public void setRating(LeadRating rating) { this.rating = rating; }
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
        public Map<RatingRuleType, Double> getDimensionScores() { return dimensionScores; }
        public void setDimensionScores(Map<RatingRuleType, Double> dimensionScores) { this.dimensionScores = dimensionScores; }
        public String getCalculationDetails() { return calculationDetails; }
        public void setCalculationDetails(String calculationDetails) { this.calculationDetails = calculationDetails; }
        public LocalDateTime getCalculationTime() { return calculationTime; }
        public void setCalculationTime(LocalDateTime calculationTime) { this.calculationTime = calculationTime; }
        public LeadRating getFinalRating() { return finalRating; }
        public void setFinalRating(LeadRating finalRating) { this.finalRating = finalRating; }
        public Integer getFinalScore() { return finalScore; }
        public void setFinalScore(Integer finalScore) { this.finalScore = finalScore; }
        public Map<String, Object> getDimensionDetails() { return dimensionDetails; }
        public void setDimensionDetails(Map<String, Object> dimensionDetails) { this.dimensionDetails = dimensionDetails; }
        public List<RatingRule> getAppliedRules() { return appliedRules; }
        public void setAppliedRules(List<RatingRule> appliedRules) { this.appliedRules = appliedRules; }
        public String getCalculationProcess() { return calculationProcess; }
        public void setCalculationProcess(String calculationProcess) { this.calculationProcess = calculationProcess; }
    }

    /**
     * 评级报告条件内部类
     */
    class RatingReportCondition {
        private String dateFrom;
        private String dateTo;
        private List<LeadRating> ratings;
        private Long userId;
        private String format; // excel, pdf, csv

        // getter和setter方法
        public String getDateFrom() { return dateFrom; }
        public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }
        public String getDateTo() { return dateTo; }
        public void setDateTo(String dateTo) { this.dateTo = dateTo; }
        public List<LeadRating> getRatings() { return ratings; }
        public void setRatings(List<LeadRating> ratings) { this.ratings = ratings; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
    }

    /**
     * 评级报告数据内部类
     */
    class RatingReportData {
        private byte[] data;
        private String fileName;
        private String contentType;

        // getter和setter方法
        public byte[] getData() { return data; }
        public void setData(byte[] data) { this.data = data; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
    }
}