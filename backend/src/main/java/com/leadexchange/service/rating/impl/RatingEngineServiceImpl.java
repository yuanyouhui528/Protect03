package com.leadexchange.service.rating.impl;

import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.rating.*;
import com.leadexchange.domain.user.User;
import com.leadexchange.service.rating.*;
import com.leadexchange.service.lead.LeadService;
import com.leadexchange.service.UserService;
import com.leadexchange.repository.rating.RatingRuleRepository;
import com.leadexchange.common.exception.BusinessException;
import com.leadexchange.common.result.ResultCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 评级引擎服务实现类
 * 实现线索评级的核心业务逻辑
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
@Transactional
public class RatingEngineServiceImpl implements RatingEngineService {

    private static final Logger log = LoggerFactory.getLogger(RatingEngineServiceImpl.class);

    @Autowired
    private LeadService leadService;
    
    @Autowired
    private RatingRuleService ratingRuleService;
    
    @Autowired
    private RatingHistoryService ratingHistoryService;
    
    @Autowired
    private RatingCacheService ratingCacheService;
    
    @Autowired
    private RatingRuleRepository ratingRuleRepository;
    
    @Autowired
    private UserService userService;

    // 评级算法版本
    private static final String RATING_VERSION = "v2.0.0";
    
    // 默认权重配置
    private static final Map<RatingRuleType, Double> DEFAULT_WEIGHTS = Map.of(
        RatingRuleType.COMPLETENESS, 0.25,
        RatingRuleType.QUALIFICATION, 0.20,
        RatingRuleType.SCALE, 0.20,
        RatingRuleType.INDUSTRY_VALUE, 0.15,
        RatingRuleType.LOCATION, 0.10,
        RatingRuleType.TIMELINESS, 0.05,
         RatingRuleType.USER_REPUTATION, 0.05
    );

    @Override
    public RatingResult calculateRating(Lead lead) {
        Long leadId = lead.getId();
        
        // 1. 检查缓存
        Optional<RatingResult> cachedResult = ratingCacheService.getCachedRatingResult(leadId);
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }

        // 2. 获取启用的评级规则
        List<RatingRule> enabledRules = ratingRuleService.getEnabledRules();
        if (enabledRules.isEmpty()) {
            throw new BusinessException(ResultCode.RATING_CALCULATION_ERROR);
        }

        // 3. 计算评级
        RatingResult result = performRatingCalculation(lead, enabledRules);

        // 4. 缓存结果
        ratingCacheService.cacheRatingResult(leadId, result);

        // 5. 记录评级历史（如果评级发生变化）
        if (!Objects.equals(lead.getRating(), result.getRating()) || 
            !Objects.equals(lead.getRatingScore(), result.getScore())) {
            recordRatingChange(lead, result, RatingChangeReason.SYSTEM_AUTO, null, null, "系统自动评级");
        }

        return result;
    }
    
    /**
     * 根据线索ID计算评级（内部方法）
     * 
     * @param leadId 线索ID
     * @return 评级结果
     */
    public RatingResult calculateLeadRating(Long leadId) {
        // 获取线索信息
        Lead lead = leadService.getLeadById(leadId)
            .orElseThrow(() -> new BusinessException(ResultCode.LEAD_NOT_FOUND));
        
        // 调用接口方法
        return calculateRating(lead);
    }

    @Override
    public Map<Long, RatingResult> batchCalculateRating(List<Long> leadIds) {
        Map<Long, RatingResult> results = new HashMap<>();
        
        // 1. 批量检查缓存
        Map<Long, RatingResult> cachedResults = ratingCacheService.batchGetCachedRatingResults(leadIds);
        results.putAll(cachedResults);
        
        // 2. 计算未缓存的线索评级
        List<Long> uncachedLeadIds = leadIds.stream()
            .filter(id -> !cachedResults.containsKey(id))
            .collect(Collectors.toList());
            
        if (!uncachedLeadIds.isEmpty()) {
            List<RatingRule> enabledRules = ratingRuleService.getEnabledRules();
            if (enabledRules.isEmpty()) {
                throw new BusinessException(ResultCode.RATING_CALCULATION_ERROR);
            }
            
            for (Long leadId : uncachedLeadIds) {
                try {
                    Lead lead = leadService.getLeadById(leadId).orElse(null);
                    if (lead != null) {
                        RatingResult result = performRatingCalculation(lead, enabledRules);
                        results.put(leadId, result);
                        
                        // 缓存结果
                        ratingCacheService.cacheRatingResult(leadId, result);
                        
                        // 记录评级历史
                        if (!Objects.equals(lead.getRating(), result.getRating()) || 
                            !Objects.equals(lead.getRatingScore(), result.getScore())) {
                            recordRatingChange(lead, result, RatingChangeReason.SYSTEM_AUTO, null, null, "批量系统自动评级");
                        }
                    }
                } catch (Exception e) {
                    // 记录错误但继续处理其他线索
                    System.err.println("计算线索评级失败，线索ID: " + leadId + ", 错误: " + e.getMessage());
                }
            }
        }
        
        return results;
    }

    @Override
    public RatingResult recalculateRating(Long leadId, RatingChangeReason reason, Long operatorId) {
        // 清除缓存
        ratingCacheService.evictRatingResult(leadId);
        
        // 重新计算评级
        RatingResult result = calculateLeadRating(leadId);
        
        // 记录评级变更历史
        if (result != null && result.isSuccess()) {
            Lead lead = leadService.getLeadById(leadId)
                .orElseThrow(() -> new BusinessException(ResultCode.LEAD_NOT_FOUND));
            String operatorName = "系统";
            if (operatorId != null) {
                User operator = userService.getUserById(operatorId);
                operatorName = operator != null ? operator.getUsername() : "系统";
            }
            recordRatingChange(lead, result, reason, operatorId, operatorName, reason.getDescription());
        }
        
        return result;
    }



    @Override
    public RatingResult adjustRating(Long leadId, LeadRating newRating, Integer newScore, 
                                   RatingChangeReason reason, Long operatorId, String description) {
        // 1. 获取当前线索信息
        Lead lead = leadService.getLeadById(leadId)
            .orElseThrow(() -> new BusinessException(ResultCode.LEAD_NOT_FOUND));

        // 2. 验证新评级和分数的合理性
        validateRatingAdjustment(newRating, newScore.doubleValue());

        // 3. 创建调整后的评级结果
        RatingResult result = new RatingResult();
        result.setLeadId(leadId);
        result.setRating(newRating);
        result.setScore(newScore.doubleValue());
        result.setCalculationTime(LocalDateTime.now());
        result.setRatingVersion(RATING_VERSION);
        result.setIsManualAdjustment(true);
        result.setAdjustmentReason(description);
        
        // 4. 更新线索评级
        leadService.updateLeadRating(leadId, newRating, newScore.doubleValue());
        
        // 5. 清除缓存
        ratingCacheService.evictRatingResult(leadId);
        
        // 6. 记录评级历史
        String operatorName = "系统";
        if (operatorId != null) {
            User operator = userService.getUserById(operatorId);
            operatorName = operator != null ? operator.getUsername() : "系统";
        }
        recordRatingChange(lead, result, reason, operatorId, operatorName, description);
        
        return result;
    }

    @Override
    public List<RatingHistory> getRatingHistory(Long leadId) {
        return ratingHistoryService.getLeadRatingHistory(leadId, Pageable.unpaged()).getContent();
    }

    @Override
    public RatingStatistics getRatingStatistics() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(30); // 默认统计30天数据
        return getRatingStatisticsInRange(startTime, endTime);
    }

    @Override
    public RatingStatistics getUserRatingStatistics(Long userId) {
        log.debug("获取用户评级统计信息，用户ID: {}", userId);
        
        RatingStatistics statistics = new RatingStatistics();
        
        try {
            // 使用现有的评级分布方法获取总体统计
            Map<LeadRating, Long> ratingDistribution = leadService.getRatingDistribution();
            statistics.setRatingCounts(ratingDistribution);
            
            // 计算总数
            Long totalCount = ratingDistribution.values().stream().mapToLong(Long::longValue).sum();
            statistics.setTotalCount(totalCount);
            
            // 计算平均分（简化实现）
            double totalScore = 0.0;
            long totalLeads = 0;
            for (Map.Entry<LeadRating, Long> entry : ratingDistribution.entrySet()) {
                LeadRating rating = entry.getKey();
                Long count = entry.getValue();
                // 根据评级计算分数：A=8, B=4, C=2, D=1
                double ratingScore = getRatingScore(rating);
                totalScore += ratingScore * count;
                totalLeads += count;
            }
            Double averageScore = totalLeads > 0 ? totalScore / totalLeads : 0.0;
            statistics.setAverageScore(averageScore);
            
            // 时间范围统计（简化实现，设置为0）
            statistics.setTodayRatingCount(0L);
            statistics.setWeekRatingCount(0L);
            statistics.setMonthRatingCount(0L);
            
            log.info("用户评级统计信息获取成功，用户ID: {}, 总数: {}", userId, totalCount);
            
        } catch (Exception e) {
            log.error("获取用户评级统计信息失败，用户ID: {}", userId, e);
            throw new RuntimeException("获取用户评级统计信息失败: " + e.getMessage());
        }
        
        return statistics;
    }
    
    /**
     * 根据评级获取对应分数
     * 
     * @param rating 评级
     * @return 分数
     */
    private double getRatingScore(LeadRating rating) {
        switch (rating) {
            case A: return 8.0;
            case B: return 4.0;
            case C: return 2.0;
            case D: return 1.0;
            default: return 0.0;
        }
    }

    @Override
    public RatingStatistics getRatingStatisticsInRange(LocalDateTime startTime, LocalDateTime endTime) {
        // 构建缓存键
        String cacheKey = String.format("rating_statistics_%s_%s", 
            startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
        // 检查缓存
        Optional<RatingStatistics> cached = ratingCacheService.getCachedRatingStatistics(cacheKey, RatingStatistics.class);
        if (cached.isPresent()) {
            return cached.get();
        }
        
        // 计算统计数据
        RatingStatistics statistics = calculateRatingStatistics(startTime, endTime);
        
        // 缓存结果
        ratingCacheService.cacheRatingStatistics(cacheKey, statistics, java.time.Duration.ofMinutes(30));
        
        return statistics;
    }

    @Override
    public Map<LeadRating, Long> getRatingDistribution() {
        String cacheKey = "rating_distribution_" + LocalDateTime.now().toLocalDate();
        
        Optional<Map> cached = ratingCacheService.getCachedRatingStatistics(cacheKey, Map.class);
        if (cached.isPresent()) {
            @SuppressWarnings("unchecked")
            Map<LeadRating, Long> cachedMap = (Map<LeadRating, Long>) cached.get();
            return cachedMap;
        }
        
        Map<LeadRating, Long> distribution = leadService.getRatingDistribution();
        
        ratingCacheService.cacheRatingStatistics(cacheKey, distribution, java.time.Duration.ofHours(1));
        
        return distribution;
    }

    @Override
    public List<RatingTrendData> getRatingTrend(int days) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        return leadService.getRatingTrend(startTime, endTime, "day");
    }

    @Override
    public RuleValidationResult validateRules(List<RatingRule> rules) {
        try {
            RuleValidationResult overallResult = new RuleValidationResult(true);
            List<String> allErrors = new ArrayList<>();
            List<String> allWarnings = new ArrayList<>();
            
            // 逐个验证规则
            for (RatingRule rule : rules) {
                com.leadexchange.service.rating.RatingRuleService.RuleValidationResult ruleResult = ratingRuleService.validateRule(rule);
                if (!ruleResult.isValid()) {
                    overallResult.setValid(false);
                }
                if (ruleResult.getErrors() != null) {
                    allErrors.addAll(ruleResult.getErrors());
                }
                if (ruleResult.getWarnings() != null) {
                    allWarnings.addAll(ruleResult.getWarnings());
                }
            }
            
            overallResult.setErrors(allErrors);
            overallResult.setWarnings(allWarnings);
            return overallResult;
        } catch (Exception e) {
            RuleValidationResult result = new RuleValidationResult();
            result.setValid(false);
            result.setErrors(Arrays.asList("验证过程中发生错误: " + e.getMessage()));
            return result;
        }
    }

    @Override
    public void refreshRuleCache() {
        ratingCacheService.clearAllRuleConfigs();
        ratingCacheService.warmupRuleConfigCache();
    }

    @Override
    public BatchRatingResult batchRecalculateRating(RatingBatchCondition condition, 
                                                   RatingChangeReason reason, Long operatorId) {
        // 根据条件获取需要重新评级的线索ID列表
        List<Long> leadIds = getLeadIdsByCondition(condition);
        return batchRecalculateRatingsInternal(leadIds, reason, operatorId);
    }

    private BatchRatingResult batchRecalculateRatingsInternal(List<Long> leadIds, 
                                                             RatingChangeReason reason, Long operatorId) {
        int successCount = 0;
        
        // 清除缓存
        ratingCacheService.batchEvictRatingResults(leadIds);
        
        // 批量重新计算
        Map<Long, RatingResult> results = batchCalculateRating(leadIds);
        
        // 更新数据库
        for (Map.Entry<Long, RatingResult> entry : results.entrySet()) {
            try {
                Long leadId = entry.getKey();
                RatingResult result = entry.getValue();
                
                leadService.updateLeadRating(leadId, result.getRating(), result.getScore());
                successCount++;
            } catch (Exception e) {
                System.err.println("更新线索评级失败，线索ID: " + entry.getKey() + ", 错误: " + e.getMessage());
            }
        }
        
        // 构建批量处理结果
        BatchRatingResult result = new BatchRatingResult();
        result.setTotalCount(leadIds.size());
        result.setSuccessCount(successCount);
        result.setFailureCount(leadIds.size() - successCount);
        result.setProcessTime(LocalDateTime.now());
        
        return result;
    }

    @Override
    public RatingDetail getRatingDetail(Long leadId) {
        RatingResult result = calculateLeadRating(leadId);
        
        // 转换为RatingDetail
        RatingDetail detail = new RatingDetail();
        detail.setLeadId(leadId);
        detail.setRating(result.getRating());
        detail.setScore(result.getScore());
        detail.setDimensionScores(result.getDimensionScores());
        detail.setCalculationDetails(result.getCalculationDetails());
        detail.setCalculationTime(result.getCalculationTime());
        
        return detail;
    }

    @Override
    public RatingResult previewRating(Lead lead) {
        // 获取当前有效的评级规则
        List<RatingRule> rules = ratingRuleService.getEnabledRules();
        return performRatingCalculation(lead, rules);
    }

    @Override
    public RatingReportData exportRatingReport(RatingReportCondition condition) {
        // TODO: 实现评级报告导出功能
        throw new BusinessException(ResultCode.NOT_FOUND, "功能暂未实现");
    }

    /**
     * 执行评级计算的核心逻辑
     * 
     * @param lead 线索对象
     * @param rules 评级规则列表
     * @return 评级结果
     */
    private RatingResult performRatingCalculation(Lead lead, List<RatingRule> rules) {
        Map<RatingRuleType, Double> scores = new HashMap<>();
        Map<RatingRuleType, String> details = new HashMap<>();
        
        // 按规则类型分组
        Map<RatingRuleType, List<RatingRule>> rulesByType = rules.stream()
            .collect(Collectors.groupingBy(RatingRule::getRuleType));
        
        double totalScore = 0.0;
        double totalWeight = 0.0;
        
        // 计算各维度得分
        for (Map.Entry<RatingRuleType, List<RatingRule>> entry : rulesByType.entrySet()) {
            RatingRuleType ruleType = entry.getKey();
            List<RatingRule> typeRules = entry.getValue();
            
            double dimensionScore = calculateDimensionScore(lead, ruleType, typeRules);
            double weight = getDimensionWeight(ruleType, typeRules);
            
            scores.put(ruleType, dimensionScore);
            details.put(ruleType, generateDimensionDetails(lead, ruleType, dimensionScore));
            
            totalScore += dimensionScore * weight;
            totalWeight += weight;
        }
        
        // 标准化总分
        if (totalWeight > 0) {
            totalScore = totalScore / totalWeight;
        }
        
        // 确保分数在0-100范围内
        totalScore = Math.max(0, Math.min(100, totalScore));
        
        // 根据分数确定评级
        LeadRating rating = LeadRating.fromScore((int) Math.round(totalScore));
        
        // 构建评级结果
        RatingResult result = new RatingResult();
        result.setLeadId(lead.getId());
        result.setRating(rating);
        result.setScore(BigDecimal.valueOf(totalScore).setScale(2, RoundingMode.HALF_UP).doubleValue());
        result.setCalculationTime(LocalDateTime.now());
        result.setRatingVersion(RATING_VERSION);
        result.setDimensionScores(scores);
        result.setCalculationDetails(generateCalculationDetails(scores, details, totalScore));
        result.setIsManualAdjustment(false);
        
        return result;
    }

    /**
     * 计算单个维度的得分
     */
    private double calculateDimensionScore(Lead lead, RatingRuleType ruleType, List<RatingRule> rules) {
        switch (ruleType) {
            case COMPLETENESS:
                return calculateInformationCompletenessScore(lead);
            case QUALIFICATION:
                return calculateEnterpriseQualificationScore(lead);
            case SCALE:
                return calculateEnterpriseScaleScore(lead);
            case INDUSTRY_VALUE:
                return calculateIndustryValueScore(lead);
            case LOCATION:
                return calculateGeographicLocationScore(lead);
            case TIMELINESS:
                return calculateTimelinessScore(lead);
            case USER_REPUTATION:
                 return calculateUserReputationScore(lead);
            default:
                return 0.0;
        }
    }

    /**
     * 计算信息完整度得分
     */
    private double calculateInformationCompletenessScore(Lead lead) {
        int totalFields = 10; // 总字段数
        int completedFields = 0;
        
        if (StringUtils.hasText(lead.getCompanyName())) completedFields++;
        if (StringUtils.hasText(lead.getContactPerson())) completedFields++;
        if (StringUtils.hasText(lead.getContactPhone())) completedFields++;
        // 注意：Lead类中没有contactEmail字段，跳过此项检查
        if (StringUtils.hasText(lead.getDescription())) completedFields++;
        if (StringUtils.hasText(lead.getIndustryDirection())) completedFields++;
        if (StringUtils.hasText(lead.getIntendedRegion())) completedFields++;
        if (lead.getRegisteredCapital() != null && lead.getRegisteredCapital().compareTo(BigDecimal.ZERO) > 0) completedFields++;
        if (lead.getInvestmentAmount() != null && lead.getInvestmentAmount().compareTo(BigDecimal.ZERO) > 0) completedFields++;
        // 注意：Lead类中没有businessScope字段，跳过此项检查
        
        return (double) completedFields / totalFields * 100;
    }

    /**
     * 计算企业资质得分
     */
    private double calculateEnterpriseQualificationScore(Lead lead) {
        double score = 50.0; // 基础分
        
        // 根据企业类型调整分数
        if (StringUtils.hasText(lead.getCompanyType())) {
            if (lead.getCompanyType().contains("有限责任公司") || lead.getCompanyType().contains("股份有限公司")) {
                score += 20;
            } else if (lead.getCompanyType().contains("个体工商户")) {
                score += 10;
            }
        }
        
        // 注意：Lead类中没有businessLicense和industryQualification字段
        // 可以根据其他字段进行评估，这里暂时跳过
        
        return Math.min(100, score);
    }

    /**
     * 计算企业规模得分
     */
    private double calculateEnterpriseScaleScore(Lead lead) {
        double score = 0.0;
        
        // 注册资本评分
        if (lead.getRegisteredCapital() != null) {
            BigDecimal capital = lead.getRegisteredCapital();
            if (capital.compareTo(new BigDecimal("10000000")) >= 0) { // 1000万以上
                score += 40;
            } else if (capital.compareTo(new BigDecimal("1000000")) >= 0) { // 100万以上
                score += 30;
            } else if (capital.compareTo(new BigDecimal("100000")) >= 0) { // 10万以上
                score += 20;
            } else {
                score += 10;
            }
        }
        
        // 投资金额评分
        if (lead.getInvestmentAmount() != null) {
            BigDecimal investment = lead.getInvestmentAmount();
            if (investment.compareTo(new BigDecimal("50000000")) >= 0) { // 5000万以上
                score += 40;
            } else if (investment.compareTo(new BigDecimal("10000000")) >= 0) { // 1000万以上
                score += 30;
            } else if (investment.compareTo(new BigDecimal("1000000")) >= 0) { // 100万以上
                score += 20;
            } else {
                score += 10;
            }
        }
        
        // 注意：Lead类中没有employeeCount字段，跳过员工规模评分
        
        return Math.min(100, score);
    }

    /**
     * 计算产业价值得分
     */
    private double calculateIndustryValueScore(Lead lead) {
        double score = 50.0; // 基础分
        
        // 根据行业类型调整分数
        if (StringUtils.hasText(lead.getIndustryDirection())) {
            String industry = lead.getIndustryDirection().toLowerCase();
            if (industry.contains("高新技术") || industry.contains("人工智能") || 
                industry.contains("新能源") || industry.contains("生物医药")) {
                score += 30;
            } else if (industry.contains("制造业") || industry.contains("服务业")) {
                score += 20;
            } else if (industry.contains("传统") || industry.contains("零售")) {
                score += 10;
            }
        }
        
        // 注意：Lead类中没有projectType字段，跳过项目类型评分
        
        return Math.min(100, score);
    }

    /**
     * 计算地理位置得分
     */
    private double calculateGeographicLocationScore(Lead lead) {
        double score = 50.0; // 基础分
        
        if (StringUtils.hasText(lead.getIntendedRegion())) {
            String location = lead.getIntendedRegion();
            if (location.contains("北京") || location.contains("上海") || 
                location.contains("深圳") || location.contains("广州")) {
                score += 30;
            } else if (location.contains("杭州") || location.contains("南京") || 
                       location.contains("成都") || location.contains("武汉")) {
                score += 20;
            } else {
                score += 10;
            }
        }
        
        return Math.min(100, score);
    }

    /**
     * 计算时效性得分
     */
    private double calculateTimelinessScore(Lead lead) {
        // 注意：Lead类中没有createdAt字段，使用默认分数
        // 可以考虑使用其他时间字段如createTime等
        return 50.0;
    }

    /**
     * 计算用户信誉得分
     */
    private double calculateUserReputationScore(Lead lead) {
        // TODO: 根据用户历史交易记录、评价等计算信誉得分
        return 75.0; // 暂时返回固定值
    }

    /**
     * 获取维度权重
     */
    private double getDimensionWeight(RatingRuleType ruleType, List<RatingRule> rules) {
        return rules.stream()
            .mapToDouble(rule -> rule.getWeight().doubleValue())
            .average()
            .orElse(DEFAULT_WEIGHTS.getOrDefault(ruleType, 0.1));
    }

    /**
     * 生成维度详情说明
     */
    private String generateDimensionDetails(Lead lead, RatingRuleType ruleType, double score) {
        return String.format("%s得分: %.2f分", ruleType.getDisplayName(), score);
    }

    /**
     * 生成计算详情
     */
    private String generateCalculationDetails(Map<RatingRuleType, Double> scores, 
                                            Map<RatingRuleType, String> details, 
                                            double totalScore) {
        StringBuilder sb = new StringBuilder();
        sb.append("评级计算详情:\n");
        
        for (Map.Entry<RatingRuleType, Double> entry : scores.entrySet()) {
            sb.append(String.format("- %s: %.2f分\n", 
                entry.getKey().getDisplayName(), entry.getValue()));
        }
        
        sb.append(String.format("\n总分: %.2f分", totalScore));
        sb.append(String.format("\n评级版本: %s", RATING_VERSION));
        sb.append(String.format("\n计算时间: %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        
        return sb.toString();
    }

    /**
     * 记录评级变更历史
     */
    private void recordRatingChange(Lead lead, RatingResult newResult, RatingChangeReason reason,
                                  Long operatorId, String operatorName, String description) {
        ratingHistoryService.recordRatingChange(
            lead.getId(),
            lead.getRating(),
            newResult.getRating(),
            lead.getRatingScore() != null ? lead.getRatingScore().doubleValue() : 0.0,
            newResult.getScore(),
            reason,
            operatorId,
            operatorName,
            description,
            newResult.getCalculationDetails(),
            newResult.getRatingVersion()
        );
    }

    /**
     * 验证评级调整的合理性
     */
    private void validateRatingAdjustment(LeadRating newRating, Double newScore) {
        if (newRating == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新评级不能为空");
        }
        
        if (newScore == null || newScore < 0 || newScore > 100) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "评级分数必须在0-100之间");
        }
        
        // 验证评级和分数的一致性
        LeadRating expectedRating = LeadRating.fromScore((int) Math.round(newScore));
        if (!expectedRating.equals(newRating)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, 
                String.format("评级(%s)与分数(%.2f)不匹配，期望评级为%s", 
                    newRating.getDescription(), newScore, expectedRating.getDescription()));
        }
    }

    /**
     * 计算评级统计数据
     */
    private RatingStatistics calculateRatingStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        // TODO: 实现评级统计数据计算
        RatingStatistics statistics = new RatingStatistics();
        statistics.setTotalCount(0L);
        statistics.setRatingCounts(new HashMap<>());
        statistics.setAverageScore(0.0);
        statistics.setTodayRatingCount(0L);
        statistics.setWeekRatingCount(0L);
        statistics.setMonthRatingCount(0L);
        return statistics;
    }

    /**
     * 根据条件获取线索ID列表
     */
    private List<Long> getLeadIdsByCondition(RatingBatchCondition condition) {
        // TODO: 根据条件查询线索ID
        return leadService.getLeadIdsByCondition(condition);
    }
}