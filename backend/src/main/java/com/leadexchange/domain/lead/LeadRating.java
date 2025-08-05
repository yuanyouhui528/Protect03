package com.leadexchange.domain.lead;

/**
 * 线索评级枚举
 * 定义线索的质量等级，用于交换价值计算
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum LeadRating {
    
    /**
     * A级线索 - 最高质量，价值8分
     * 信息完整度高，企业资质优秀，投资规模大
     */
    A("A级", 8, 90),
    
    /**
     * B级线索 - 较高质量，价值4分
     * 信息较完整，企业资质良好，投资规模中等
     */
    B("B级", 4, 70),
    
    /**
     * C级线索 - 中等质量，价值2分
     * 信息基本完整，企业资质一般，投资规模较小
     */
    C("C级", 2, 50),
    
    /**
     * D级线索 - 基础质量，价值1分
     * 信息不够完整，企业资质待确认，投资规模小
     */
    D("D级", 1, 30);
    
    private final String description;
    private final int exchangeValue;
    private final int minScore;
    
    LeadRating(String description, int exchangeValue, int minScore) {
        this.description = description;
        this.exchangeValue = exchangeValue;
        this.minScore = minScore;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getExchangeValue() {
        return exchangeValue;
    }
    
    public int getMinScore() {
        return minScore;
    }
    
    /**
     * 根据评分获取对应的评级
     * 
     * @param score 评分
     * @return 对应的评级
     */
    public static LeadRating fromScore(int score) {
        if (score >= A.minScore) {
            return A;
        } else if (score >= B.minScore) {
            return B;
        } else if (score >= C.minScore) {
            return C;
        } else {
            return D;
        }
    }
    
    /**
     * 根据描述获取评级枚举
     * 
     * @param description 评级描述
     * @return 对应的评级枚举
     */
    public static LeadRating fromDescription(String description) {
        for (LeadRating rating : values()) {
            if (rating.description.equals(description)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("未知的线索评级: " + description);
    }
    
    /**
     * 计算交换比例
     * 例如：1个A级 = 2个B级 = 4个C级 = 8个D级
     * 
     * @param targetRating 目标评级
     * @return 交换比例
     */
    public double getExchangeRatio(LeadRating targetRating) {
        return (double) this.exchangeValue / targetRating.exchangeValue;
    }
    
    /**
     * 判断是否为高质量线索
     * 
     * @return 是否为高质量线索（A级或B级）
     */
    public boolean isHighQuality() {
        return this == A || this == B;
    }
}