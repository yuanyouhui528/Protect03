package com.leadexchange.service.rating.impl;

import com.leadexchange.common.exception.BusinessException;
import com.leadexchange.common.result.ResultCode;
import com.leadexchange.domain.rating.RatingHistory;
import com.leadexchange.domain.rating.RatingChangeReason;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.repository.rating.RatingHistoryRepository;
import com.leadexchange.service.rating.RatingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * 评级历史服务实现类
 * 提供线索评级变更历史的管理功能实现
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
@Transactional
public class RatingHistoryServiceImpl implements RatingHistoryService {

    private static final int MAX_ROLLBACK_STEPS = 10;

    @Autowired
    private RatingHistoryRepository ratingHistoryRepository;

    @Override
    public RatingHistory recordRatingChange(Long leadId, LeadRating oldRating, LeadRating newRating,
                                           Double oldScore, Double newScore, RatingChangeReason changeReason,
                                           Long operatorId, String operatorName, String changeDescription,
                                           String ratingDetails, String ratingVersion) {
        
        // 参数验证
        if (leadId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "线索ID不能为空");
        }
        if (changeReason == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "变更原因不能为空");
        }
        
        // 创建评级历史记录
        RatingHistory history = new RatingHistory();
        history.setLeadId(leadId);
        history.setPreviousRating(oldRating);
        history.setCurrentRating(newRating);
        history.setPreviousScore(oldScore != null ? oldScore.intValue() : null);
        history.setCurrentScore(newScore != null ? newScore.intValue() : null);
        history.setChangeReason(changeReason);
        history.setOperatorId(operatorId);
        history.setOperatorName(operatorName);
        history.setChangeDescription(changeDescription);
        history.setRatingDetails(ratingDetails);
        history.setRatingVersion(ratingVersion);
        history.setRatingTime(LocalDateTime.now());
        
        // 设置基础字段
        history.setDeleted(0);
        history.setVersion(1);
        
        return ratingHistoryRepository.save(history);
    }

    @Override
    public Optional<RatingHistory> getHistoryById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return ratingHistoryRepository.findById(id);
    }

    @Override
    public Page<RatingHistory> getLeadRatingHistory(Long leadId, Pageable pageable) {
        if (leadId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "线索ID不能为空");
        }
        return ratingHistoryRepository.findByLeadIdOrderByRatingTimeDesc(leadId, pageable);
    }

    @Override
    public Optional<RatingHistory> getLatestRatingHistory(Long leadId) {
        if (leadId == null) {
            return Optional.empty();
        }
        return ratingHistoryRepository.findFirstByLeadIdOrderByRatingTimeDesc(leadId);
    }

    @Override
    public Long getRatingChangeCount(Long leadId) {
        if (leadId == null) {
            return 0L;
        }
        return ratingHistoryRepository.countByLeadId(leadId);
    }

    @Override
    public Page<RatingHistory> getHistoryByChangeReason(RatingChangeReason changeReason, Pageable pageable) {
        if (changeReason == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "变更原因不能为空");
        }
        return ratingHistoryRepository.findByChangeReasonOrderByRatingTimeDesc(changeReason, pageable);
    }

    @Override
    public Page<RatingHistory> getHistoryByOperator(Long operatorId, Pageable pageable) {
        if (operatorId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "操作人ID不能为空");
        }
        return ratingHistoryRepository.findByOperatorIdOrderByRatingTimeDesc(operatorId, pageable);
    }

    @Override
    public Page<RatingHistory> getHistoryByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        if (startTime == null || endTime == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "时间范围不能为空");
        }
        if (startTime.isAfter(endTime)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "开始时间不能晚于结束时间");
        }
        return ratingHistoryRepository.findByRatingTimeBetweenOrderByRatingTimeDesc(startTime, endTime, pageable);
    }

    @Override
    public Page<RatingHistory> getHistoryByRatingChange(LeadRating oldRating, LeadRating newRating, Pageable pageable) {
        return ratingHistoryRepository.findByPreviousRatingAndCurrentRatingOrderByRatingTimeDesc(oldRating, newRating, pageable);
    }

    @Override
    public Page<RatingHistory> getRatingUpgradeHistory(Pageable pageable) {
        // 这里需要自定义查询来找出评级提升的记录
        // 暂时返回所有记录，实际应该根据评级等级比较
        return ratingHistoryRepository.findAll(pageable);
    }

    @Override
    public Page<RatingHistory> getRatingDowngradeHistory(Pageable pageable) {
        // 这里需要自定义查询来找出评级降级的记录
        // 暂时返回所有记录，实际应该根据评级等级比较
        return ratingHistoryRepository.findAll(pageable);
    }

    @Override
    public Page<RatingHistory> getAllHistory(Pageable pageable) {
        return ratingHistoryRepository.findAll(pageable);
    }

    @Override
    public RatingHistoryStatistics getHistoryStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<RatingHistory> histories = ratingHistoryRepository.findByRatingTimeBetween(startTime, endTime);
        
        RatingHistoryStatistics statistics = new RatingHistoryStatistics();
        statistics.setTotalChanges(histories.size());
        
        // 统计升级、降级、无变化的数量
        long upgradeCount = histories.stream().filter(RatingHistory::isRatingUpgrade).count();
        long downgradeCount = histories.stream().filter(RatingHistory::isRatingDowngrade).count();
        long noChangeCount = histories.size() - upgradeCount - downgradeCount;
        
        statistics.setUpgradeCount(upgradeCount);
        statistics.setDowngradeCount(downgradeCount);
        statistics.setNoChangeCount(noChangeCount);
        
        // 按变更原因统计
        Map<RatingChangeReason, Long> changeReasonCount = histories.stream()
            .collect(Collectors.groupingBy(RatingHistory::getChangeReason, Collectors.counting()));
        statistics.setChangeReasonCount(changeReasonCount);
        
        // 按原评级统计
        Map<LeadRating, Long> fromRatingCount = histories.stream()
            .filter(h -> h.getPreviousRating() != null)
            .collect(Collectors.groupingBy(RatingHistory::getPreviousRating, Collectors.counting()));
        statistics.setFromRatingCount(fromRatingCount);
        
        // 按新评级统计
        Map<LeadRating, Long> toRatingCount = histories.stream()
            .filter(h -> h.getCurrentRating() != null)
            .collect(Collectors.groupingBy(RatingHistory::getCurrentRating, Collectors.counting()));
        statistics.setToRatingCount(toRatingCount);
        
        // 计算平均分数变化
        double averageScoreChange = histories.stream()
            .filter(h -> h.getPreviousScore() != null && h.getCurrentScore() != null)
            .mapToDouble(h -> h.getCurrentScore() - h.getPreviousScore())
            .average()
            .orElse(0.0);
        statistics.setAverageScoreChange(averageScoreChange);
        
        // 设置时间范围
        Optional<LocalDateTime> firstTime = histories.stream()
            .map(RatingHistory::getRatingTime)
            .min(LocalDateTime::compareTo);
        Optional<LocalDateTime> lastTime = histories.stream()
            .map(RatingHistory::getRatingTime)
            .max(LocalDateTime::compareTo);
        
        statistics.setFirstChangeTime(firstTime.orElse(null));
        statistics.setLastChangeTime(lastTime.orElse(null));
        
        return statistics;
    }

    @Override
    public List<RatingChangeTrend> getRatingChangeTrend(LocalDateTime startTime, LocalDateTime endTime, String granularity) {
        List<RatingChangeTrend> trendData = new ArrayList<>();
        
        // 根据时间粒度确定时间间隔
        ChronoUnit unit;
        int amount;
        switch (granularity.toUpperCase()) {
            case "DAY":
                unit = ChronoUnit.DAYS;
                amount = 1;
                break;
            case "WEEK":
                unit = ChronoUnit.WEEKS;
                amount = 1;
                break;
            case "MONTH":
                unit = ChronoUnit.MONTHS;
                amount = 1;
                break;
            default:
                throw new BusinessException(ResultCode.BAD_REQUEST, "不支持的时间粒度: " + granularity);
        }
        
        // 按时间段统计
        LocalDateTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            LocalDateTime nextTime = currentTime.plus(amount, unit);
            if (nextTime.isAfter(endTime)) {
                nextTime = endTime;
            }
            
            List<RatingHistory> periodHistories = ratingHistoryRepository
                .findByRatingTimeBetween(currentTime, nextTime);
            
            long totalChanges = periodHistories.size();
            long upgradeCount = periodHistories.stream().filter(RatingHistory::isRatingUpgrade).count();
            long downgradeCount = periodHistories.stream().filter(RatingHistory::isRatingDowngrade).count();
            
            double averageScore = periodHistories.stream()
                .filter(h -> h.getCurrentScore() != null)
                .mapToDouble(h -> h.getCurrentScore().doubleValue())
                .average()
                .orElse(0.0);
            
            RatingChangeTrend trend = new RatingChangeTrend(currentTime, totalChanges, upgradeCount, downgradeCount, averageScore);
            trendData.add(trend);
            
            currentTime = nextTime;
        }
        
        return trendData;
    }

    @Override
    public List<OperatorRatingStatistics> getOperatorRatingStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<RatingHistory> histories = ratingHistoryRepository.findByRatingTimeBetween(startTime, endTime);
        
        // 按操作人分组统计
        Map<Long, List<RatingHistory>> operatorHistories = histories.stream()
            .filter(h -> h.getOperatorId() != null)
            .collect(Collectors.groupingBy(RatingHistory::getOperatorId));
        
        return operatorHistories.entrySet().stream()
            .map(entry -> {
                Long operatorId = entry.getKey();
                List<RatingHistory> operatorHistoryList = entry.getValue();
                
                OperatorRatingStatistics stats = new OperatorRatingStatistics();
                stats.setOperatorId(operatorId);
                stats.setOperatorName(operatorHistoryList.get(0).getOperatorName());
                stats.setTotalOperations(operatorHistoryList.size());
                
                long upgradeOps = operatorHistoryList.stream().filter(RatingHistory::isRatingUpgrade).count();
                long downgradeOps = operatorHistoryList.stream().filter(RatingHistory::isRatingDowngrade).count();
                
                stats.setUpgradeOperations(upgradeOps);
                stats.setDowngradeOperations(downgradeOps);
                
                double avgScoreChange = operatorHistoryList.stream()
                    .filter(h -> h.getPreviousScore() != null && h.getCurrentScore() != null)
                    .mapToDouble(h -> h.getCurrentScore() - h.getPreviousScore())
                    .average()
                    .orElse(0.0);
                stats.setAverageScoreChange(avgScoreChange);
                
                Optional<LocalDateTime> lastOpTime = operatorHistoryList.stream()
                    .map(RatingHistory::getRatingTime)
                    .max(LocalDateTime::compareTo);
                stats.setLastOperationTime(lastOpTime.orElse(null));
                
                return stats;
            })
            .sorted((s1, s2) -> Long.compare(s2.getTotalOperations(), s1.getTotalOperations()))
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Map<String, Long>> getRatingChangeStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<RatingHistory> histories = ratingHistoryRepository.findByRatingTimeBetween(startTime, endTime);
        
        Map<String, Map<String, Long>> statistics = new HashMap<>();
        
        // 按变更原因分组统计
        Map<String, Long> reasonStats = histories.stream()
            .collect(Collectors.groupingBy(
                h -> h.getChangeReason().getDisplayName(),
                Collectors.counting()
            ));
        statistics.put("changeReason", reasonStats);
        
        // 按评级类型分组统计
        Map<String, Long> ratingStats = histories.stream()
            .collect(Collectors.groupingBy(
                h -> h.getCurrentRating().name(),
                Collectors.counting()
            ));
        statistics.put("ratingType", ratingStats);
        
        // 按变更类型分组统计
        Map<String, Long> changeTypeStats = new HashMap<>();
        long upgradeCount = histories.stream().filter(RatingHistory::isRatingUpgrade).count();
        long downgradeCount = histories.stream().filter(RatingHistory::isRatingDowngrade).count();
        long noChangeCount = histories.size() - upgradeCount - downgradeCount;
        
        changeTypeStats.put("upgrade", upgradeCount);
        changeTypeStats.put("downgrade", downgradeCount);
        changeTypeStats.put("noChange", noChangeCount);
        statistics.put("changeType", changeTypeStats);
        
        return statistics;
    }

    @Override
    public Map<String, Long> getOperatorStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<RatingHistory> histories = ratingHistoryRepository.findByRatingTimeBetween(startTime, endTime);
        
        // 按操作人分组统计
        return histories.stream()
            .filter(h -> h.getOperatorName() != null && !h.getOperatorName().trim().isEmpty())
            .collect(Collectors.groupingBy(
                RatingHistory::getOperatorName,
                Collectors.counting()
            ));
    }

    @Override
    public int deleteExpiredHistory(int retentionDays) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
        Long deletedCount = ratingHistoryRepository.deleteByRatingTimeBefore(cutoffTime);
        return deletedCount != null ? deletedCount.intValue() : 0;
    }

    @Override
    public void batchDeleteHistory(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        List<RatingHistory> histories = ratingHistoryRepository.findAllById(ids);
        if (histories.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分历史记录不存在");
        }
        
        ratingHistoryRepository.deleteAllById(ids);
    }

    @Override
    public List<RatingHistory> exportData(LocalDateTime startTime, LocalDateTime endTime) {
        return ratingHistoryRepository.findByRatingTimeBetweenOrderByRatingTimeDesc(startTime, endTime);
    }

    @Override
    public List<RatingRollbackSuggestion> getRollbackSuggestions(long leadId) {
        RatingRollbackSuggestion suggestion = new RatingRollbackSuggestion();
        
        // 获取最近的历史记录
        List<RatingHistory> recentHistories = ratingHistoryRepository
            .findTop10ByLeadIdOrderByRatingTimeDesc(leadId);
        
        if (recentHistories.isEmpty()) {
            suggestion.setCanRollback(false);
            suggestion.setReason("没有找到评级历史记录");
            return Arrays.asList(suggestion);
        }
        
        // 检查是否可以回滚
        if (recentHistories.size() < 2) {
            suggestion.setCanRollback(false);
            suggestion.setReason("评级历史记录不足，无法回滚");
            return Arrays.asList(suggestion);
        }
        
        // 过滤出可回滚的选项（排除最新的记录）
        List<RatingHistory> rollbackOptions = recentHistories.subList(1, 
            Math.min(recentHistories.size(), MAX_ROLLBACK_STEPS + 1));
        
        suggestion.setCanRollback(true);
        suggestion.setRollbackOptions(rollbackOptions);
        suggestion.setMaxRollbackSteps(Math.min(rollbackOptions.size(), MAX_ROLLBACK_STEPS));
        
        // 生成回滚建议
        RatingHistory latestHistory = recentHistories.get(0);
        if (rollbackOptions.size() > 0) {
            RatingHistory suggestedTarget = rollbackOptions.get(0);
            if (latestHistory.getChangeReason() == RatingChangeReason.MANUAL_ADJUSTMENT) {
                suggestion.setRecommendation("建议回滚到上一次系统自动评级结果");
            } else {
                suggestion.setRecommendation("建议回滚到上一个稳定的评级状态");
            }
            suggestion.setReason(String.format("当前评级: %s (%d分)，建议回滚到: %s (%d分)",
                latestHistory.getCurrentRating().getDescription(),
                latestHistory.getCurrentScore(),
                suggestedTarget.getCurrentRating().getDescription(),
                suggestedTarget.getCurrentScore()));
        }
        
        return Arrays.asList(suggestion);
    }

    @Override
    public RatingHistory rollbackToHistory(long leadId, long targetHistoryId, String operatorId, String rollbackReason) {
        
        // 获取目标历史记录
        RatingHistory targetHistory = getHistoryById(targetHistoryId)
            .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "评级历史记录不存在"));
        
        if (!targetHistory.getLeadId().equals(leadId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "历史记录与线索ID不匹配");
        }
        
        // 获取当前最新的评级历史
        RatingHistory currentHistory = getLatestRatingHistory(leadId)
            .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "未找到当前评级历史"));
        
        // 检查回滚的合理性
        if (targetHistory.getRatingTime().isAfter(currentHistory.getRatingTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不能回滚到未来的评级状态");
        }
        
        // 记录回滚操作
        return recordRatingChange(
            leadId,
            currentHistory.getCurrentRating(),
            targetHistory.getCurrentRating(),
            currentHistory.getCurrentScore().doubleValue(),
            targetHistory.getCurrentScore().doubleValue(),
            RatingChangeReason.ROLLBACK,
            Long.parseLong(operatorId),
            operatorId, // 使用operatorId作为operatorName
            String.format("回滚到历史记录[%d]: %s", targetHistoryId, rollbackReason),
            targetHistory.getRatingDetails(),
            targetHistory.getRatingVersion()
        );
    }
}