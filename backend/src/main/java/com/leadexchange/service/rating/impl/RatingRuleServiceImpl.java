package com.leadexchange.service.rating.impl;

import com.leadexchange.domain.rating.*;
import com.leadexchange.service.rating.RatingRuleService;
import com.leadexchange.repository.rating.RatingRuleRepository;
import com.leadexchange.common.exception.BusinessException;
import com.leadexchange.common.result.ResultCode;
import com.leadexchange.common.result.PageResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评级规则服务实现类
 * 实现评级规则的管理功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
@Transactional
public class RatingRuleServiceImpl implements RatingRuleService {

    @Autowired
    private RatingRuleRepository ratingRuleRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    // 默认评级规则配置
    private static final List<RatingRule> DEFAULT_RULES = Arrays.asList(
        createDefaultRule("信息完整度评估", RatingRuleType.COMPLETENESS, new BigDecimal("0.25"), CalculationMethod.WEIGHTED_SUM, 1),
        createDefaultRule("企业资质评估", RatingRuleType.QUALIFICATION, new BigDecimal("0.20"), CalculationMethod.WEIGHTED_SUM, 2),
        createDefaultRule("企业规模评估", RatingRuleType.SCALE, new BigDecimal("0.20"), CalculationMethod.WEIGHTED_SUM, 3),
        createDefaultRule("产业价值评估", RatingRuleType.INDUSTRY_VALUE, new BigDecimal("0.15"), CalculationMethod.WEIGHTED_SUM, 4),
        createDefaultRule("地理位置评估", RatingRuleType.LOCATION, new BigDecimal("0.10"), CalculationMethod.WEIGHTED_SUM, 5),
        createDefaultRule("时效性评估", RatingRuleType.TIMELINESS, new BigDecimal("0.05"), CalculationMethod.WEIGHTED_SUM, 6),
        createDefaultRule("用户信誉评估", RatingRuleType.USER_REPUTATION, new BigDecimal("0.05"), CalculationMethod.WEIGHTED_SUM, 7)
    );

    @Override
    public RatingRule createRule(RatingRule ratingRule) {
        validateRatingRule(ratingRule);
        
        // 检查规则名称是否重复
        if (ratingRuleRepository.existsByRuleNameIgnoreCase(ratingRule.getRuleName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "规则名称已存在");
        }
        
        // 设置创建时间
        ratingRule.setCreateTime(LocalDateTime.now());
        ratingRule.setUpdateTime(LocalDateTime.now());
        
        // 如果未设置排序顺序，自动设置为最大值+1
        if (ratingRule.getSortOrder() == null) {
            Integer maxSortOrder = ratingRuleRepository.findMaxSortOrder();
            ratingRule.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 1);
        }
        
        return ratingRuleRepository.save(ratingRule);
    }

    @Override
    public RatingRule updateRule(Long id, RatingRule ratingRule) {
        RatingRule existingRule = getRuleById(id)
            .orElseThrow(() -> new BusinessException(ResultCode.BAD_REQUEST, "评级规则不存在"));
        
        validateRatingRule(ratingRule);
        
        // 检查规则名称是否与其他规则重复
        if (!existingRule.getRuleName().equals(ratingRule.getRuleName()) &&
            ratingRuleRepository.existsByRuleNameIgnoreCase(ratingRule.getRuleName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "规则名称已存在");
        }
        
        // 更新字段
        existingRule.setRuleName(ratingRule.getRuleName());
        existingRule.setRuleType(ratingRule.getRuleType());
        existingRule.setWeight(ratingRule.getWeight());
        existingRule.setCalculationMethod(ratingRule.getCalculationMethod());
        existingRule.setDescription(ratingRule.getDescription());
        existingRule.setIsEnabled(ratingRule.getIsEnabled());
        existingRule.setSortOrder(ratingRule.getSortOrder());
        existingRule.setConfigParams(ratingRule.getConfigParams());
        existingRule.setUpdateTime(LocalDateTime.now());
        
        return ratingRuleRepository.save(existingRule);
    }

    @Override
    public void deleteRule(Long id) {
        if (!ratingRuleRepository.existsById(id)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "评级规则不存在");
        }
        
        ratingRuleRepository.deleteById(id);
    }

    @Override
    public Optional<RatingRule> getRuleById(Long id) {
        return ratingRuleRepository.findById(id);
    }

    @Override
    public List<RatingRule> getAllRules() {
        return ratingRuleRepository.findAllByOrderBySortOrderAsc();
    }
    
    @Override
    public PageResult<RatingRule> getAllRules(Pageable pageable) {
        Page<RatingRule> page = ratingRuleRepository.findAll(pageable);
        return PageResult.of(page);
    }

    @Override
    public List<RatingRule> getEnabledRules() {
        return ratingRuleRepository.findByIsEnabledTrueOrderBySortOrder();
    }

    @Override
    public List<RatingRule> getRulesByType(RatingRuleType ruleType) {
        return ratingRuleRepository.findByRuleTypeAndIsEnabledTrue(ruleType);
    }

    @Override
    public List<RatingRule> getRulesByCalculationMethod(CalculationMethod calculationMethod) {
        return ratingRuleRepository.findByCalculationMethodOrderBySortOrderAsc(calculationMethod);
    }

    @Override
    public void enableRule(Long id) {
        RatingRule rule = getRuleById(id)
            .orElseThrow(() -> new BusinessException(ResultCode.BAD_REQUEST, "评级规则不存在"));
        
        rule.setIsEnabled(true);
        rule.setUpdateTime(LocalDateTime.now());
        ratingRuleRepository.save(rule);
    }

    @Override
    public void disableRule(Long id) {
        RatingRule rule = getRuleById(id)
            .orElseThrow(() -> new BusinessException(ResultCode.BAD_REQUEST, "评级规则不存在"));
        
        rule.setIsEnabled(false);
        rule.setUpdateTime(LocalDateTime.now());
        ratingRuleRepository.save(rule);
    }

    @Override
    public void batchEnableRules(List<Long> ids) {
        List<RatingRule> rules = ratingRuleRepository.findAllById(ids);
        if (rules.size() != ids.size()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "部分规则不存在");
        }
        
        rules.forEach(rule -> {
            rule.setIsEnabled(true);
            rule.setUpdateTime(LocalDateTime.now());
        });
        
        ratingRuleRepository.saveAll(rules);
    }

    @Override
    public void batchDisableRules(List<Long> ids) {
        List<RatingRule> rules = ratingRuleRepository.findAllById(ids);
        if (rules.size() != ids.size()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "部分规则不存在");
        }
        
        rules.forEach(rule -> {
            rule.setIsEnabled(false);
            rule.setUpdateTime(LocalDateTime.now());
        });
        
        ratingRuleRepository.saveAll(rules);
    }

    @Override
    public void updateRuleSortOrder(Long id, Integer newSortOrder) {
        RatingRule rule = getRuleById(id)
            .orElseThrow(() -> new BusinessException(ResultCode.BAD_REQUEST, "评级规则不存在"));
        
        if (newSortOrder == null || newSortOrder < 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "排序顺序必须大于0");
        }
        
        rule.setSortOrder(newSortOrder);
        rule.setUpdateTime(LocalDateTime.now());
        ratingRuleRepository.save(rule);
    }

    @Override
    public void batchUpdateRuleSortOrder(List<RuleSortOrder> ruleSortOrders) {
        List<Long> ruleIds = ruleSortOrders.stream()
            .map(RuleSortOrder::getRuleId)
            .collect(Collectors.toList());
        
        List<RatingRule> rules = ratingRuleRepository.findAllById(ruleIds);
        if (rules.size() != ruleIds.size()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "部分规则不存在");
        }
        
        Map<Long, Integer> sortOrderMap = ruleSortOrders.stream()
            .collect(Collectors.toMap(RuleSortOrder::getRuleId, RuleSortOrder::getSortOrder));
        
        rules.forEach(rule -> {
            Integer newSortOrder = sortOrderMap.get(rule.getId());
            if (newSortOrder != null && newSortOrder > 0) {
                rule.setSortOrder(newSortOrder);
                rule.setUpdateTime(LocalDateTime.now());
            }
        });
        
        ratingRuleRepository.saveAll(rules);
    }

    @Override
    public RuleValidationResult validateRule(RatingRule ratingRule) {
        RuleValidationResult result = new RuleValidationResult(true);
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        // 验证规则名称
        if (!StringUtils.hasText(ratingRule.getRuleName())) {
            errors.add("规则名称不能为空");
        } else if (ratingRule.getRuleName().length() > 100) {
            errors.add("规则名称长度不能超过100个字符");
        }
        
        // 验证规则类型
        if (ratingRule.getRuleType() == null) {
            errors.add("规则类型不能为空");
        }
        
        // 验证权重
        if (ratingRule.getWeight() == null) {
            errors.add("权重不能为空");
        } else if (ratingRule.getWeight().compareTo(BigDecimal.ZERO) < 0 || ratingRule.getWeight().compareTo(BigDecimal.ONE) > 0) {
            errors.add("权重必须在0-1之间");
        } else if (ratingRule.getWeight().compareTo(BigDecimal.ZERO) == 0) {
            warnings.add("权重为0的规则不会参与评级计算");
        }
        
        // 验证计算方法
        if (ratingRule.getCalculationMethod() == null) {
            errors.add("计算方法不能为空");
        }
        
        // 验证配置参数
        if (StringUtils.hasText(ratingRule.getConfigParams())) {
            try {
                objectMapper.readTree(ratingRule.getConfigParams());
            } catch (Exception e) {
                errors.add("配置参数不是有效的JSON格式");
            }
        }
        
        // 验证排序顺序
        if (ratingRule.getSortOrder() != null && ratingRule.getSortOrder() < 1) {
            errors.add("排序顺序必须大于0");
        }
        
        // 业务逻辑验证
        if (ratingRule.getRuleType() != null && ratingRule.getCalculationMethod() != null) {
            if (!isCalculationMethodSuitableForRuleType(ratingRule.getRuleType(), ratingRule.getCalculationMethod())) {
                warnings.add(String.format("计算方法%s可能不适合规则类型%s", 
                    ratingRule.getCalculationMethod().getDisplayName(), 
                    ratingRule.getRuleType().getDisplayName()));
            }
        }
        
        // 设置验证结果
        result.setValid(errors.isEmpty());
        result.setErrors(errors);
        result.setWarnings(warnings);
        result.setSuggestions(suggestions);
        
        return result;
    }

    @Override
    public RuleValidationResult validateAllRules() {
        List<RatingRule> allRules = getAllRules();
        RuleValidationResult result = new RuleValidationResult(true);
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        // 验证每个规则
        for (RatingRule rule : allRules) {
            RuleValidationResult ruleResult = validateRule(rule);
            if (!ruleResult.isValid()) {
                result.setValid(false);
                if (ruleResult.getErrors() != null) {
                    ruleResult.getErrors().forEach(error -> 
                        errors.add(String.format("规则[%s]: %s", rule.getRuleName(), error)));
                }
            }
            if (ruleResult.getWarnings() != null) {
                ruleResult.getWarnings().forEach(warning -> 
                    warnings.add(String.format("规则[%s]: %s", rule.getRuleName(), warning)));
            }
        }
        
        // 验证权重总和
        List<RatingRule> enabledRules = getEnabledRules();
        BigDecimal totalWeight = enabledRules.stream()
            .map(RatingRule::getWeight)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalWeight.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.01")) > 0) {
            warnings.add(String.format("启用规则的权重总和为%.3f，建议调整为1.0", totalWeight.doubleValue()));
        }
        
        // 检查是否有重复的规则类型
        Map<RatingRuleType, Long> typeCount = enabledRules.stream()
            .collect(Collectors.groupingBy(RatingRule::getRuleType, Collectors.counting()));
        
        typeCount.entrySet().stream()
            .filter(entry -> entry.getValue() > 1)
            .forEach(entry -> warnings.add(String.format("规则类型%s有%d个启用的规则，可能导致重复计算", 
                entry.getKey().getDisplayName(), entry.getValue())));
        
        result.setErrors(errors);
        result.setWarnings(warnings);
        result.setSuggestions(suggestions);
        
        return result;
    }

    @Override
    public RuleImportResult importRules(List<RatingRule> rules) {
        RuleImportResult result = new RuleImportResult();
        result.setTotalCount(rules.size());
        
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        int skipCount = 0;
        
        for (RatingRule rule : rules) {
            try {
                // 验证规则
                RuleValidationResult validation = validateRule(rule);
                if (!validation.isValid()) {
                    errors.add(String.format("规则[%s]验证失败: %s", 
                        rule.getRuleName(), String.join(", ", validation.getErrors())));
                    failureCount++;
                    continue;
                }
                
                // 检查是否已存在同名规则
                if (ratingRuleRepository.existsByRuleNameIgnoreCase(rule.getRuleName())) {
                    warnings.add(String.format("规则[%s]已存在，跳过导入", rule.getRuleName()));
                    skipCount++;
                    continue;
                }
                
                // 导入规则
                rule.setCreateTime(LocalDateTime.now());
                rule.setUpdateTime(LocalDateTime.now());
                ratingRuleRepository.save(rule);
                successCount++;
                
            } catch (Exception e) {
                errors.add(String.format("导入规则[%s]失败: %s", rule.getRuleName(), e.getMessage()));
                failureCount++;
            }
        }
        
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setSkipCount(skipCount);
        result.setErrors(errors);
        result.setWarnings(warnings);
        
        return result;
    }

    @Override
    public List<RatingRule> exportRules() {
        return getAllRules();
    }

    @Override
    public void resetToDefaultRules() {
        // 删除所有现有规则
        ratingRuleRepository.deleteAll();
        
        // 导入默认规则
        for (RatingRule defaultRule : DEFAULT_RULES) {
            defaultRule.setCreateTime(LocalDateTime.now());
            defaultRule.setUpdateTime(LocalDateTime.now());
            ratingRuleRepository.save(defaultRule);
        }
    }

    @Override
    public String getRuleConfigTemplate(RatingRuleType ruleType) {
        return ruleType.getDefaultConfigParams();
    }

    @Override
    public RuleStatistics getRuleStatistics() {
        RuleStatistics statistics = new RuleStatistics();
        
        List<RatingRule> allRules = getAllRules();
        statistics.setTotalRules(allRules.size());
        
        long enabledCount = allRules.stream().filter(RatingRule::getIsEnabled).count();
        statistics.setEnabledRules((int) enabledCount);
        statistics.setDisabledRules(allRules.size() - (int) enabledCount);
        
        // 按规则类型统计
        Map<RatingRuleType, Integer> typeCount = allRules.stream()
            .collect(Collectors.groupingBy(RatingRule::getRuleType, 
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)));
        statistics.setRuleTypeCount(typeCount);
        
        // 按计算方法统计
        Map<CalculationMethod, Integer> methodCount = allRules.stream()
            .collect(Collectors.groupingBy(RatingRule::getCalculationMethod, 
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)));
        statistics.setCalculationMethodCount(methodCount);
        
        // 最后更新时间
        Optional<LocalDateTime> lastUpdate = allRules.stream()
            .map(RatingRule::getUpdateTime)
            .filter(Objects::nonNull)
            .max(LocalDateTime::compareTo);
        statistics.setLastUpdateTime(lastUpdate.orElse(null));
        
        return statistics;
    }

    @Override
    public RatingRule copyRule(Long id, String newRuleName) {
        RatingRule originalRule = getRuleById(id)
            .orElseThrow(() -> new BusinessException(ResultCode.BAD_REQUEST, "评级规则不存在"));
        
        if (ratingRuleRepository.existsByRuleNameIgnoreCase(newRuleName)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "规则名称已存在");
        }
        
        RatingRule copiedRule = new RatingRule();
        copiedRule.setRuleName(newRuleName);
        copiedRule.setRuleType(originalRule.getRuleType());
        copiedRule.setWeight(originalRule.getWeight());
        copiedRule.setCalculationMethod(originalRule.getCalculationMethod());
        copiedRule.setDescription("复制自: " + originalRule.getDescription());
        copiedRule.setIsEnabled(false); // 默认禁用复制的规则
        copiedRule.setConfigParams(originalRule.getConfigParams());
        copiedRule.setCreateTime(LocalDateTime.now());
        copiedRule.setUpdateTime(LocalDateTime.now());
        
        // 设置排序顺序为最大值+1
        Integer maxSortOrder = ratingRuleRepository.findMaxSortOrder();
        copiedRule.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 1);
        
        return ratingRuleRepository.save(copiedRule);
    }

    @Override
    public RuleTestResult testRule(RatingRule ratingRule, Object testData) {
        RuleTestResult result = new RuleTestResult();
        long startTime = System.currentTimeMillis();
        
        try {
            // 验证规则
            RuleValidationResult validation = validateRule(ratingRule);
            if (!validation.isValid()) {
                result.setSuccess(false);
                result.setMessage("规则验证失败: " + String.join(", ", validation.getErrors()));
                return result;
            }
            
            // TODO: 实现具体的规则测试逻辑
            // 这里应该根据规则类型和计算方法，使用测试数据进行实际计算
            
            result.setSuccess(true);
            result.setResult("测试通过");
            result.setMessage("规则测试成功");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("规则测试失败: " + e.getMessage());
        } finally {
            result.setExecutionTime(System.currentTimeMillis() - startTime);
        }
        
        return result;
    }

    /**
     * 验证评级规则的基本信息
     */
    private void validateRatingRule(RatingRule ratingRule) {
        if (ratingRule == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "评级规则不能为空");
        }
        
        RuleValidationResult validation = validateRule(ratingRule);
        if (!validation.isValid()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, 
                "规则验证失败: " + String.join(", ", validation.getErrors()));
        }
    }

    /**
     * 判断计算方法是否适合规则类型
     */
    private boolean isCalculationMethodSuitableForRuleType(RatingRuleType ruleType, CalculationMethod method) {
        // 根据规则类型和计算方法的特点判断是否匹配
        switch (ruleType) {
            case COMPLETENESS:
                return method == CalculationMethod.PERCENTAGE || 
                       method == CalculationMethod.WEIGHTED_SUM;
            case QUALIFICATION:
            case SCALE:
                return method == CalculationMethod.STEP_SCORING || 
                       method == CalculationMethod.WEIGHTED_SUM;
            case INDUSTRY_VALUE:
            case LOCATION:
                return method == CalculationMethod.CONDITIONAL || 
                       method == CalculationMethod.WEIGHTED_SUM;
            case TIMELINESS:
                return method == CalculationMethod.STEP_SCORING || 
                       method == CalculationMethod.CUSTOM_FORMULA;
            case USER_REPUTATION:
                return method == CalculationMethod.AVERAGE || 
                       method == CalculationMethod.WEIGHTED_SUM;
            default:
                return true;
        }
    }

    /**
     * 创建默认规则
     */
    private static RatingRule createDefaultRule(String name, RatingRuleType type, BigDecimal weight, 
                                               CalculationMethod method, int sortOrder) {
        RatingRule rule = new RatingRule();
        rule.setRuleName(name);
        rule.setRuleType(type);
        rule.setWeight(weight);
        rule.setCalculationMethod(method);
        rule.setDescription(type.getDescription());
        rule.setIsEnabled(true);
        rule.setSortOrder(sortOrder);
        rule.setConfigParams(type.getDefaultConfigParams());
        return rule;
    }
}