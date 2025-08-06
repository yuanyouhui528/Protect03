package com.leadexchange.domain.rating;

import com.leadexchange.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * 评级规则实体类
 * 用于配置线索评级的各项规则和权重
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "rating_rules")
@ApiModel(description = "评级规则")
public class RatingRule extends BaseEntity {

    /**
     * 规则名称
     */
    @Column(name = "rule_name", nullable = false, length = 100)
    @NotBlank(message = "规则名称不能为空")
    @ApiModelProperty(value = "规则名称", example = "信息完整度评分")
    private String ruleName;

    /**
     * 规则类型
     */
    @Column(name = "rule_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "规则类型不能为空")
    @ApiModelProperty(value = "规则类型", example = "COMPLETENESS")
    private RatingRuleType ruleType;

    /**
     * 权重（百分比）
     */
    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "权重不能为空")
    @DecimalMin(value = "0.00", message = "权重不能小于0")
    @DecimalMax(value = "1.00", message = "权重不能大于1")
    @ApiModelProperty(value = "权重", example = "0.40")
    private BigDecimal weight;

    /**
     * 计算方法
     */
    @Column(name = "calculation_method", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "计算方法不能为空")
    @ApiModelProperty(value = "计算方法", example = "WEIGHTED_SUM")
    private CalculationMethod calculationMethod;

    /**
     * 规则描述
     */
    @Column(name = "description", length = 500)
    @ApiModelProperty(value = "规则描述", example = "根据线索信息的完整程度进行评分")
    private String description;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled", nullable = false)
    @NotNull(message = "启用状态不能为空")
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean isEnabled;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order", nullable = false)
    @NotNull(message = "排序顺序不能为空")
    @ApiModelProperty(value = "排序顺序", example = "1")
    private Integer sortOrder;

    /**
     * 配置参数（JSON格式）
     */
    @Column(name = "config_params", columnDefinition = "TEXT")
    @ApiModelProperty(value = "配置参数", example = "{\"maxScore\": 100, \"fields\": [\"companyName\", \"contactPerson\"]}")
    private String configParams;

    // 构造函数
    public RatingRule() {}

    public RatingRule(String ruleName, RatingRuleType ruleType, BigDecimal weight, 
                     CalculationMethod calculationMethod, String description, 
                     Boolean isEnabled, Integer sortOrder) {
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.weight = weight;
        this.calculationMethod = calculationMethod;
        this.description = description;
        this.isEnabled = isEnabled;
        this.sortOrder = sortOrder;
    }

    // Getter和Setter方法
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public RatingRuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RatingRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public CalculationMethod getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(CalculationMethod calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getConfigParams() {
        return configParams;
    }

    public void setConfigParams(String configParams) {
        this.configParams = configParams;
    }

    @Override
    public String toString() {
        return "RatingRule{" +
                "id=" + getId() +
                ", ruleName='" + ruleName + '\'' +
                ", ruleType=" + ruleType +
                ", weight=" + weight +
                ", calculationMethod=" + calculationMethod +
                ", description='" + description + '\'' +
                ", isEnabled=" + isEnabled +
                ", sortOrder=" + sortOrder +
                ", configParams='" + configParams + '\'' +
                '}';
    }
}