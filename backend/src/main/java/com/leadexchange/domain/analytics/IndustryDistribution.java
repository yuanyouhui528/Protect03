package com.leadexchange.domain.analytics;

import java.math.BigDecimal;

/**
 * 行业分布数据模型
 * 用于表示不同行业的线索数量分布
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class IndustryDistribution {

    /**
     * 行业名称
     */
    private String industry;

    /**
     * 行业代码
     */
    private String industryCode;

    /**
     * 该行业的线索数量
     */
    private Long count;

    /**
     * 占总数的百分比
     */
    private BigDecimal percentage;

    /**
     * 行业描述
     */
    private String description;

    /**
     * 行业热度评分
     */
    private BigDecimal hotScore;

    /**
     * 排名
     */
    private Integer rank;

    // 构造函数
    public IndustryDistribution() {}

    public IndustryDistribution(String industry, Long count) {
        this.industry = industry;
        this.count = count;
    }

    public IndustryDistribution(String industry, String industryCode, Long count) {
        this.industry = industry;
        this.industryCode = industryCode;
        this.count = count;
    }

    public IndustryDistribution(String industry, Long count, BigDecimal percentage) {
        this.industry = industry;
        this.count = count;
        this.percentage = percentage;
    }

    // Getter和Setter方法
    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getHotScore() {
        return hotScore;
    }

    public void setHotScore(BigDecimal hotScore) {
        this.hotScore = hotScore;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    /**
     * 计算百分比
     * 
     * @param totalCount 总数量
     */
    public void calculatePercentage(Long totalCount) {
        if (totalCount != null && totalCount > 0 && this.count != null) {
            this.percentage = new BigDecimal(this.count)
                    .divide(new BigDecimal(totalCount), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100));
        }
    }

    /**
     * 计算热度评分
     * 基于线索数量和其他因素计算行业热度
     * 
     * @param totalCount 总线索数
     * @param avgCount 平均线索数
     */
    public void calculateHotScore(Long totalCount, Long avgCount) {
        if (this.count != null && totalCount != null && totalCount > 0) {
            // 基础分数：基于占比
            BigDecimal baseScore = new BigDecimal(this.count)
                    .divide(new BigDecimal(totalCount), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100));
            
            // 调整分数：与平均值比较
            if (avgCount != null && avgCount > 0) {
                BigDecimal avgRatio = new BigDecimal(this.count)
                        .divide(new BigDecimal(avgCount), 4, BigDecimal.ROUND_HALF_UP);
                baseScore = baseScore.multiply(avgRatio);
            }
            
            this.hotScore = baseScore;
        }
    }

    @Override
    public String toString() {
        return "IndustryDistribution{" +
                "industry='" + industry + '\'' +
                ", industryCode='" + industryCode + '\'' +
                ", count=" + count +
                ", percentage=" + percentage +
                ", description='" + description + '\'' +
                ", hotScore=" + hotScore +
                ", rank=" + rank +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndustryDistribution that = (IndustryDistribution) o;
        return industry != null ? industry.equals(that.industry) : that.industry == null;
    }

    @Override
    public int hashCode() {
        return industry != null ? industry.hashCode() : 0;
    }
}