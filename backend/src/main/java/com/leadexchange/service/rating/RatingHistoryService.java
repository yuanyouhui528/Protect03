package com.leadexchange.service.rating;

import com.leadexchange.domain.rating.RatingHistory;
import com.leadexchange.domain.rating.RatingChangeReason;
import com.leadexchange.domain.lead.LeadRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 评级历史服务接口
 * 提供线索评级变更历史的管理功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface RatingHistoryService {

    /**
     * 记录评级变更历史
     * 
     * @param leadId 线索ID
     * @param oldRating 原评级
     * @param newRating 新评级
     * @param oldScore 原分数
     * @param newScore 新分数
     * @param changeReason 变更原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param changeDescription 变更说明
     * @param ratingDetails 评级详情
     * @param ratingVersion 评级版本
     * @return 评级历史记录
     */
    RatingHistory recordRatingChange(
            Long leadId,
            LeadRating oldRating,
            LeadRating newRating,
            Double oldScore,
            Double newScore,
            RatingChangeReason changeReason,
            Long operatorId,
            String operatorName,
            String changeDescription,
            String ratingDetails,
            String ratingVersion
    );

    /**
     * 根据ID获取评级历史
     * 
     * @param id 历史记录ID
     * @return 评级历史记录
     */
    Optional<RatingHistory> getHistoryById(Long id);

    /**
     * 获取线索的评级历史
     * 
     * @param leadId 线索ID
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> getLeadRatingHistory(Long leadId, Pageable pageable);

    /**
     * 获取线索的最新评级历史
     * 
     * @param leadId 线索ID
     * @return 最新评级历史记录
     */
    Optional<RatingHistory> getLatestRatingHistory(Long leadId);

    /**
     * 获取线索的评级变更次数
     * 
     * @param leadId 线索ID
     * @return 变更次数
     */
    Long getRatingChangeCount(Long leadId);

    /**
     * 根据变更原因获取评级历史
     * 
     * @param changeReason 变更原因
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> getHistoryByChangeReason(RatingChangeReason changeReason, Pageable pageable);

    /**
     * 根据操作人获取评级历史
     * 
     * @param operatorId 操作人ID
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> getHistoryByOperator(Long operatorId, Pageable pageable);

    /**
     * 根据时间范围获取评级历史
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> getHistoryByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据评级变更获取历史
     * 
     * @param oldRating 原评级
     * @param newRating 新评级
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> getHistoryByRatingChange(LeadRating oldRating, LeadRating newRating, Pageable pageable);

    /**
     * 获取评级提升的历史记录
     * 
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> getRatingUpgradeHistory(Pageable pageable);

    /**
     * 获取评级降级的历史记录
     * 
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> getRatingDowngradeHistory(Pageable pageable);

    /**
     * 获取所有评级历史记录（分页）
     * 
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> getAllHistory(Pageable pageable);

    /**
     * 获取评级历史统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    RatingHistoryStatistics getHistoryStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取评级变更趋势数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度（DAY, WEEK, MONTH）
     * @return 趋势数据
     */
    List<RatingChangeTrend> getRatingChangeTrend(LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 获取操作人评级变更统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作人统计数据
     */
    List<OperatorRatingStatistics> getOperatorRatingStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取评级变更统计数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级变更统计数据，按变更原因和评级类型分组
     */
    java.util.Map<String, java.util.Map<String, Long>> getRatingChangeStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取操作人统计数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作人统计数据，按操作人分组
     */
    java.util.Map<String, Long> getOperatorStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除过期的评级历史记录
     * 
     * @param retentionDays 保留天数
     * @return 删除的记录数
     */
    int deleteExpiredHistory(int retentionDays);

    /**
     * 批量删除评级历史记录
     * 
     * @param ids 历史记录ID列表
     */
    void batchDeleteHistory(List<Long> ids);

    /**
     * 导出评级历史数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 历史记录列表
     */
    List<RatingHistory> exportData(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取评级回滚建议
     * 基于历史记录分析，提供评级回滚建议
     * 
     * @param leadId 线索ID
     * @return 回滚建议列表
     */
    List<RatingRollbackSuggestion> getRollbackSuggestions(long leadId);

    /**
     * 执行评级回滚
     * 
     * @param leadId 线索ID
     * @param targetHistoryId 目标历史记录ID
     * @param operatorId 操作人ID
     * @param rollbackReason 回滚原因
     * @return 回滚后的评级历史记录
     */
    RatingHistory rollbackToHistory(long leadId, long targetHistoryId, String operatorId, String rollbackReason);

    /**
     * 评级历史统计信息内部类
     */
    class RatingHistoryStatistics {
        private long totalChanges;
        private long upgradeCount;
        private long downgradeCount;
        private long noChangeCount;
        private java.util.Map<RatingChangeReason, Long> changeReasonCount;
        private java.util.Map<LeadRating, Long> fromRatingCount;
        private java.util.Map<LeadRating, Long> toRatingCount;
        private double averageScoreChange;
        private LocalDateTime firstChangeTime;
        private LocalDateTime lastChangeTime;

        public RatingHistoryStatistics() {}

        public long getTotalChanges() { return totalChanges; }
        public void setTotalChanges(long totalChanges) { this.totalChanges = totalChanges; }
        public long getUpgradeCount() { return upgradeCount; }
        public void setUpgradeCount(long upgradeCount) { this.upgradeCount = upgradeCount; }
        public long getDowngradeCount() { return downgradeCount; }
        public void setDowngradeCount(long downgradeCount) { this.downgradeCount = downgradeCount; }
        public long getNoChangeCount() { return noChangeCount; }
        public void setNoChangeCount(long noChangeCount) { this.noChangeCount = noChangeCount; }
        public java.util.Map<RatingChangeReason, Long> getChangeReasonCount() { return changeReasonCount; }
        public void setChangeReasonCount(java.util.Map<RatingChangeReason, Long> changeReasonCount) { this.changeReasonCount = changeReasonCount; }
        public java.util.Map<LeadRating, Long> getFromRatingCount() { return fromRatingCount; }
        public void setFromRatingCount(java.util.Map<LeadRating, Long> fromRatingCount) { this.fromRatingCount = fromRatingCount; }
        public java.util.Map<LeadRating, Long> getToRatingCount() { return toRatingCount; }
        public void setToRatingCount(java.util.Map<LeadRating, Long> toRatingCount) { this.toRatingCount = toRatingCount; }
        public double getAverageScoreChange() { return averageScoreChange; }
        public void setAverageScoreChange(double averageScoreChange) { this.averageScoreChange = averageScoreChange; }
        public LocalDateTime getFirstChangeTime() { return firstChangeTime; }
        public void setFirstChangeTime(LocalDateTime firstChangeTime) { this.firstChangeTime = firstChangeTime; }
        public LocalDateTime getLastChangeTime() { return lastChangeTime; }
        public void setLastChangeTime(LocalDateTime lastChangeTime) { this.lastChangeTime = lastChangeTime; }
    }

    /**
     * 评级变更趋势数据内部类
     */
    class RatingChangeTrend {
        private LocalDateTime timePoint;
        private long totalChanges;
        private long upgradeCount;
        private long downgradeCount;
        private double averageScore;

        public RatingChangeTrend() {}

        public RatingChangeTrend(LocalDateTime timePoint, long totalChanges, long upgradeCount, long downgradeCount, double averageScore) {
            this.timePoint = timePoint;
            this.totalChanges = totalChanges;
            this.upgradeCount = upgradeCount;
            this.downgradeCount = downgradeCount;
            this.averageScore = averageScore;
        }

        public LocalDateTime getTimePoint() { return timePoint; }
        public void setTimePoint(LocalDateTime timePoint) { this.timePoint = timePoint; }
        public long getTotalChanges() { return totalChanges; }
        public void setTotalChanges(long totalChanges) { this.totalChanges = totalChanges; }
        public long getUpgradeCount() { return upgradeCount; }
        public void setUpgradeCount(long upgradeCount) { this.upgradeCount = upgradeCount; }
        public long getDowngradeCount() { return downgradeCount; }
        public void setDowngradeCount(long downgradeCount) { this.downgradeCount = downgradeCount; }
        public double getAverageScore() { return averageScore; }
        public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    }

    /**
     * 操作人评级统计内部类
     */
    class OperatorRatingStatistics {
        private Long operatorId;
        private String operatorName;
        private long totalOperations;
        private long upgradeOperations;
        private long downgradeOperations;
        private double averageScoreChange;
        private LocalDateTime lastOperationTime;

        public OperatorRatingStatistics() {}

        public Long getOperatorId() { return operatorId; }
        public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
        public String getOperatorName() { return operatorName; }
        public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
        public long getTotalOperations() { return totalOperations; }
        public void setTotalOperations(long totalOperations) { this.totalOperations = totalOperations; }
        public long getUpgradeOperations() { return upgradeOperations; }
        public void setUpgradeOperations(long upgradeOperations) { this.upgradeOperations = upgradeOperations; }
        public long getDowngradeOperations() { return downgradeOperations; }
        public void setDowngradeOperations(long downgradeOperations) { this.downgradeOperations = downgradeOperations; }
        public double getAverageScoreChange() { return averageScoreChange; }
        public void setAverageScoreChange(double averageScoreChange) { this.averageScoreChange = averageScoreChange; }
        public LocalDateTime getLastOperationTime() { return lastOperationTime; }
        public void setLastOperationTime(LocalDateTime lastOperationTime) { this.lastOperationTime = lastOperationTime; }
    }

    /**
     * 评级回滚建议内部类
     */
    class RatingRollbackSuggestion {
        private boolean canRollback;
        private List<RatingHistory> rollbackOptions;
        private String recommendation;
        private String reason;
        private int maxRollbackSteps;

        public RatingRollbackSuggestion() {}

        public boolean isCanRollback() { return canRollback; }
        public void setCanRollback(boolean canRollback) { this.canRollback = canRollback; }
        public List<RatingHistory> getRollbackOptions() { return rollbackOptions; }
        public void setRollbackOptions(List<RatingHistory> rollbackOptions) { this.rollbackOptions = rollbackOptions; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public int getMaxRollbackSteps() { return maxRollbackSteps; }
        public void setMaxRollbackSteps(int maxRollbackSteps) { this.maxRollbackSteps = maxRollbackSteps; }
    }
}