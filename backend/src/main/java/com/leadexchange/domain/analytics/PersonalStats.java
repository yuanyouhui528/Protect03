package com.leadexchange.domain.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 个人统计数据模型
 * 包含用户个人的线索和交换统计信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class PersonalStats {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 统计时间范围开始
     */
    private LocalDateTime startTime;

    /**
     * 统计时间范围结束
     */
    private LocalDateTime endTime;

    /**
     * 发布的线索总数
     */
    private Long totalLeads;

    /**
     * 有效线索数（已审核通过）
     */
    private Long validLeads;

    /**
     * 已交换线索数
     */
    private Long exchangedLeads;

    /**
     * 获得的线索总数
     */
    private Long receivedLeads;

    /**
     * 线索交换成功率
     */
    private BigDecimal exchangeSuccessRate;

    /**
     * 总积分收入
     */
    private Long totalPointsEarned;

    /**
     * 总积分支出
     */
    private Long totalPointsSpent;

    /**
     * 当前积分余额
     */
    private Long currentPoints;

    /**
     * 线索平均评级分数
     */
    private BigDecimal averageRatingScore;

    /**
     * A级线索数量
     */
    private Long aGradeLeads;

    /**
     * B级线索数量
     */
    private Long bGradeLeads;

    /**
     * C级线索数量
     */
    private Long cGradeLeads;

    /**
     * D级线索数量
     */
    private Long dGradeLeads;

    /**
     * 线索浏览总次数
     */
    private Long totalViews;

    /**
     * 线索收藏总次数
     */
    private Long totalFavorites;

    /**
     * 活跃天数
     */
    private Long activeDays;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
    
    /**
     * 活跃线索数（兼容性字段）
     */
    private Long activeLeads;
    
    /**
     * 交换次数（兼容性字段）
     */
    private Long exchangeCount;
    
    /**
     * 成功交换次数（兼容性字段）
     */
    private Long successfulExchanges;
    
    /**
     * 总收入（兼容性字段）
     */
    private BigDecimal totalRevenue;
    
    /**
     * 平均评级（兼容性字段）
     */
    private Double averageRating;

    // 构造函数
    public PersonalStats() {}

    public PersonalStats(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter和Setter方法
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(Long totalLeads) {
        this.totalLeads = totalLeads;
    }

    public Long getValidLeads() {
        return validLeads;
    }

    public void setValidLeads(Long validLeads) {
        this.validLeads = validLeads;
    }

    public Long getExchangedLeads() {
        return exchangedLeads;
    }

    public void setExchangedLeads(Long exchangedLeads) {
        this.exchangedLeads = exchangedLeads;
    }

    public Long getReceivedLeads() {
        return receivedLeads;
    }

    public void setReceivedLeads(Long receivedLeads) {
        this.receivedLeads = receivedLeads;
    }

    public BigDecimal getExchangeSuccessRate() {
        return exchangeSuccessRate;
    }

    public void setExchangeSuccessRate(BigDecimal exchangeSuccessRate) {
        this.exchangeSuccessRate = exchangeSuccessRate;
    }

    public Long getTotalPointsEarned() {
        return totalPointsEarned;
    }

    public void setTotalPointsEarned(Long totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }

    public Long getTotalPointsSpent() {
        return totalPointsSpent;
    }

    public void setTotalPointsSpent(Long totalPointsSpent) {
        this.totalPointsSpent = totalPointsSpent;
    }

    public Long getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Long currentPoints) {
        this.currentPoints = currentPoints;
    }

    public BigDecimal getAverageRatingScore() {
        return averageRatingScore;
    }

    public void setAverageRatingScore(BigDecimal averageRatingScore) {
        this.averageRatingScore = averageRatingScore;
    }

    public Long getaGradeLeads() {
        return aGradeLeads;
    }

    public void setaGradeLeads(Long aGradeLeads) {
        this.aGradeLeads = aGradeLeads;
    }

    public Long getbGradeLeads() {
        return bGradeLeads;
    }

    public void setbGradeLeads(Long bGradeLeads) {
        this.bGradeLeads = bGradeLeads;
    }

    public Long getcGradeLeads() {
        return cGradeLeads;
    }

    public void setcGradeLeads(Long cGradeLeads) {
        this.cGradeLeads = cGradeLeads;
    }

    public Long getdGradeLeads() {
        return dGradeLeads;
    }

    public void setdGradeLeads(Long dGradeLeads) {
        this.dGradeLeads = dGradeLeads;
    }

    public Long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Long totalViews) {
        this.totalViews = totalViews;
    }

    public Long getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Long getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(Long activeDays) {
        this.activeDays = activeDays;
    }

    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    // 兼容性方法
    public Long getActiveLeads() {
        return activeLeads != null ? activeLeads : validLeads;
    }
    
    public void setActiveLeads(Long activeLeads) {
        this.activeLeads = activeLeads;
    }
    
    public Long getExchangeCount() {
        return exchangeCount != null ? exchangeCount : exchangedLeads;
    }
    
    public void setExchangeCount(Long exchangeCount) {
        this.exchangeCount = exchangeCount;
    }
    
    public Long getSuccessfulExchanges() {
        return successfulExchanges;
    }
    
    public void setSuccessfulExchanges(Long successfulExchanges) {
        this.successfulExchanges = successfulExchanges;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    @Override
    public String toString() {
        return "PersonalStats{" +
                "userId=" + userId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalLeads=" + totalLeads +
                ", validLeads=" + validLeads +
                ", exchangedLeads=" + exchangedLeads +
                ", receivedLeads=" + receivedLeads +
                ", exchangeSuccessRate=" + exchangeSuccessRate +
                ", totalPointsEarned=" + totalPointsEarned +
                ", totalPointsSpent=" + totalPointsSpent +
                ", currentPoints=" + currentPoints +
                ", averageRatingScore=" + averageRatingScore +
                ", aGradeLeads=" + aGradeLeads +
                ", bGradeLeads=" + bGradeLeads +
                ", cGradeLeads=" + cGradeLeads +
                ", dGradeLeads=" + dGradeLeads +
                ", totalViews=" + totalViews +
                ", totalFavorites=" + totalFavorites +
                ", activeDays=" + activeDays +
                ", lastActiveTime=" + lastActiveTime +
                '}';
    }
}