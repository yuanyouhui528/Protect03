package com.leadexchange.domain.analytics;

import java.math.BigDecimal;

/**
 * 线索评级分布数据模型
 * 用于表示不同评级的线索数量分布
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class RatingDistribution {

    /**
     * 评级等级（A、B、C、D）
     */
    private String rating;

    /**
     * 该评级的线索数量
     */
    private Long count;

    /**
     * 占总数的百分比
     */
    private BigDecimal percentage;

    /**
     * 评级描述
     */
    private String description;

    /**
     * 评级对应的分值
     */
    private Integer score;

    // 构造函数
    public RatingDistribution() {}

    public RatingDistribution(String rating, Long count) {
        this.rating = rating;
        this.count = count;
        this.score = getRatingScore(rating);
        this.description = getRatingDescription(rating);
    }

    public RatingDistribution(String rating, Long count, BigDecimal percentage) {
        this.rating = rating;
        this.count = count;
        this.percentage = percentage;
        this.score = getRatingScore(rating);
        this.description = getRatingDescription(rating);
    }

    // Getter和Setter方法
    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
        this.score = getRatingScore(rating);
        this.description = getRatingDescription(rating);
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * 根据评级获取对应分值
     * 
     * @param rating 评级等级
     * @return 分值
     */
    private Integer getRatingScore(String rating) {
        if (rating == null) return 0;
        switch (rating.toUpperCase()) {
            case "A": return 8;
            case "B": return 4;
            case "C": return 2;
            case "D": return 1;
            default: return 0;
        }
    }

    /**
     * 根据评级获取描述
     * 
     * @param rating 评级等级
     * @return 评级描述
     */
    private String getRatingDescription(String rating) {
        if (rating == null) return "未知";
        switch (rating.toUpperCase()) {
            case "A": return "优质线索";
            case "B": return "良好线索";
            case "C": return "一般线索";
            case "D": return "基础线索";
            default: return "未知等级";
        }
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

    @Override
    public String toString() {
        return "RatingDistribution{" +
                "rating='" + rating + '\'' +
                ", count=" + count +
                ", percentage=" + percentage +
                ", description='" + description + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatingDistribution that = (RatingDistribution) o;
        return rating != null ? rating.equals(that.rating) : that.rating == null;
    }

    @Override
    public int hashCode() {
        return rating != null ? rating.hashCode() : 0;
    }
}