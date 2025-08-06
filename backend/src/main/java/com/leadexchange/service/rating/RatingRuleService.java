package com.leadexchange.service.rating;

import com.leadexchange.domain.rating.RatingRule;
import com.leadexchange.domain.rating.RatingRuleType;
import com.leadexchange.domain.rating.CalculationMethod;
import com.leadexchange.common.result.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 评级规则服务接口
 * 提供评级规则的管理功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface RatingRuleService {

    /**
     * 创建评级规则
     * 
     * @param ratingRule 评级规则对象
     * @return 创建的评级规则
     */
    RatingRule createRule(RatingRule ratingRule);

    /**
     * 更新评级规则
     * 
     * @param id 规则ID
     * @param ratingRule 更新的评级规则对象
     * @return 更新后的评级规则
     */
    RatingRule updateRule(Long id, RatingRule ratingRule);

    /**
     * 删除评级规则
     * 
     * @param id 规则ID
     */
    void deleteRule(Long id);

    /**
     * 根据ID获取评级规则
     * 
     * @param id 规则ID
     * @return 评级规则
     */
    Optional<RatingRule> getRuleById(Long id);

    /**
     * 获取所有评级规则
     * 
     * @return 评级规则列表
     */
    List<RatingRule> getAllRules();
    
    /**
     * 获取所有评级规则（分页）
     * 
     * @param pageable 分页参数
     * @return 分页的评级规则列表
     */
    PageResult<RatingRule> getAllRules(Pageable pageable);

    /**
     * 获取启用的评级规则
     * 
     * @return 启用的评级规则列表
     */
    List<RatingRule> getEnabledRules();

    /**
     * 根据规则类型获取评级规则
     * 
     * @param ruleType 规则类型
     * @return 评级规则列表
     */
    List<RatingRule> getRulesByType(RatingRuleType ruleType);

    /**
     * 根据计算方法获取评级规则
     * 
     * @param calculationMethod 计算方法
     * @return 评级规则列表
     */
    List<RatingRule> getRulesByCalculationMethod(CalculationMethod calculationMethod);

    /**
     * 启用评级规则
     * 
     * @param id 规则ID
     */
    void enableRule(Long id);

    /**
     * 禁用评级规则
     * 
     * @param id 规则ID
     */
    void disableRule(Long id);

    /**
     * 批量启用评级规则
     * 
     * @param ids 规则ID列表
     */
    void batchEnableRules(List<Long> ids);

    /**
     * 批量禁用评级规则
     * 
     * @param ids 规则ID列表
     */
    void batchDisableRules(List<Long> ids);

    /**
     * 调整规则排序
     * 
     * @param id 规则ID
     * @param newSortOrder 新的排序顺序
     */
    void updateRuleSortOrder(Long id, Integer newSortOrder);

    /**
     * 批量调整规则排序
     * 
     * @param ruleSortOrders 规则ID和排序顺序的映射
     */
    void batchUpdateRuleSortOrder(List<RuleSortOrder> ruleSortOrders);

    /**
     * 验证评级规则配置
     * 
     * @param ratingRule 评级规则
     * @return 验证结果
     */
    RuleValidationResult validateRule(RatingRule ratingRule);

    /**
     * 验证所有评级规则配置
     * 
     * @return 验证结果
     */
    RuleValidationResult validateAllRules();

    /**
     * 导入评级规则
     * 
     * @param rules 评级规则列表
     * @return 导入结果
     */
    RuleImportResult importRules(List<RatingRule> rules);

    /**
     * 导出评级规则
     * 
     * @return 评级规则列表
     */
    List<RatingRule> exportRules();

    /**
     * 重置为默认规则
     * 将评级规则重置为系统默认配置
     */
    void resetToDefaultRules();

    /**
     * 获取规则配置模板
     * 
     * @param ruleType 规则类型
     * @return 配置模板
     */
    String getRuleConfigTemplate(RatingRuleType ruleType);

    /**
     * 获取规则统计信息
     * 
     * @return 规则统计数据
     */
    RuleStatistics getRuleStatistics();

    /**
     * 复制评级规则
     * 
     * @param id 源规则ID
     * @param newRuleName 新规则名称
     * @return 复制的评级规则
     */
    RatingRule copyRule(Long id, String newRuleName);

    /**
     * 测试评级规则
     * 使用测试数据验证规则的有效性
     * 
     * @param ratingRule 评级规则
     * @param testData 测试数据
     * @return 测试结果
     */
    RuleTestResult testRule(RatingRule ratingRule, Object testData);

    /**
     * 规则排序内部类
     */
    class RuleSortOrder {
        private Long ruleId;
        private Integer sortOrder;

        public RuleSortOrder() {}

        public RuleSortOrder(Long ruleId, Integer sortOrder) {
            this.ruleId = ruleId;
            this.sortOrder = sortOrder;
        }

        public Long getRuleId() { return ruleId; }
        public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }

    /**
     * 规则验证结果内部类
     */
    class RuleValidationResult {
        private boolean valid;
        private List<String> errors;
        private List<String> warnings;
        private List<String> suggestions;

        public RuleValidationResult() {}

        public RuleValidationResult(boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    }

    /**
     * 规则导入结果内部类
     */
    class RuleImportResult {
        private int totalCount;
        private int successCount;
        private int failureCount;
        private int skipCount;
        private List<String> errors;
        private List<String> warnings;

        public RuleImportResult() {}

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public int getSkipCount() { return skipCount; }
        public void setSkipCount(int skipCount) { this.skipCount = skipCount; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }

    /**
     * 规则统计信息内部类
     */
    class RuleStatistics {
        private int totalRules;
        private int enabledRules;
        private int disabledRules;
        private java.util.Map<RatingRuleType, Integer> ruleTypeCount;
        private java.util.Map<CalculationMethod, Integer> calculationMethodCount;
        private java.time.LocalDateTime lastUpdateTime;

        public RuleStatistics() {}

        public int getTotalRules() { return totalRules; }
        public void setTotalRules(int totalRules) { this.totalRules = totalRules; }
        public int getEnabledRules() { return enabledRules; }
        public void setEnabledRules(int enabledRules) { this.enabledRules = enabledRules; }
        public int getDisabledRules() { return disabledRules; }
        public void setDisabledRules(int disabledRules) { this.disabledRules = disabledRules; }
        public java.util.Map<RatingRuleType, Integer> getRuleTypeCount() { return ruleTypeCount; }
        public void setRuleTypeCount(java.util.Map<RatingRuleType, Integer> ruleTypeCount) { this.ruleTypeCount = ruleTypeCount; }
        public java.util.Map<CalculationMethod, Integer> getCalculationMethodCount() { return calculationMethodCount; }
        public void setCalculationMethodCount(java.util.Map<CalculationMethod, Integer> calculationMethodCount) { this.calculationMethodCount = calculationMethodCount; }
        public java.time.LocalDateTime getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(java.time.LocalDateTime lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
    }

    /**
     * 规则测试结果内部类
     */
    class RuleTestResult {
        private boolean success;
        private Object result;
        private String message;
        private long executionTime;

        public RuleTestResult() {}

        public RuleTestResult(boolean success, Object result) {
            this.success = success;
            this.result = result;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    }
}