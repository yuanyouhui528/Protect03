package com.leadexchange.domain.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统统计数据模型
 * 包含整个系统的运行统计信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class SystemStats {

    /**
     * 统计时间范围开始
     */
    private LocalDateTime startTime;

    /**
     * 统计时间范围结束
     */
    private LocalDateTime endTime;

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 活跃用户数
     */
    private Long activeUsers;

    /**
     * 新注册用户数
     */
    private Long newUsers;

    /**
     * 总线索数
     */
    private Long totalLeads;

    /**
     * 有效线索数（已审核通过）
     */
    private Long validLeads;

    /**
     * 待审核线索数
     */
    private Long pendingLeads;

    /**
     * 已下架线索数
     */
    private Long offlineLeads;

    /**
     * 总交换次数
     */
    private Long totalExchanges;

    /**
     * 成功交换次数
     */
    private Long successfulExchanges;

    /**
     * 交换成功率
     */
    private BigDecimal exchangeSuccessRate;

    /**
     * 总积分流通量
     */
    private Long totalPointsCirculation;

    /**
     * 平均线索评级分数
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
     * 总浏览次数
     */
    private Long totalViews;

    /**
     * 总收藏次数
     */
    private Long totalFavorites;

    /**
     * 平均每日活跃用户数
     */
    private BigDecimal averageDailyActiveUsers;

    /**
     * 平均每日新增线索数
     */
    private BigDecimal averageDailyNewLeads;

    /**
     * 平均每日交换次数
     */
    private BigDecimal averageDailyExchanges;

    /**
     * 系统运行天数
     */
    private Long systemRunDays;

    /**
     * 最热门行业
     */
    private String topIndustry;

    /**
     * 最热门行业线索数
     */
    private Long topIndustryLeadCount;

    /**
     * 最活跃用户ID
     */
    private Long mostActiveUserId;

    /**
     * 最活跃用户线索数
     */
    private Long mostActiveUserLeadCount;
    
    /**
     * 总收入（兼容性字段）
     */
    private BigDecimal totalRevenue;
    
    /**
     * 平均评级（兼容性字段）
     */
    private Double averageRating;

    // 构造函数
    public SystemStats() {}

    public SystemStats(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter和Setter方法
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

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(Long newUsers) {
        this.newUsers = newUsers;
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

    public Long getPendingLeads() {
        return pendingLeads;
    }

    public void setPendingLeads(Long pendingLeads) {
        this.pendingLeads = pendingLeads;
    }

    public Long getOfflineLeads() {
        return offlineLeads;
    }

    public void setOfflineLeads(Long offlineLeads) {
        this.offlineLeads = offlineLeads;
    }

    public Long getTotalExchanges() {
        return totalExchanges;
    }

    public void setTotalExchanges(Long totalExchanges) {
        this.totalExchanges = totalExchanges;
    }

    public Long getSuccessfulExchanges() {
        return successfulExchanges;
    }

    public void setSuccessfulExchanges(Long successfulExchanges) {
        this.successfulExchanges = successfulExchanges;
    }

    public BigDecimal getExchangeSuccessRate() {
        return exchangeSuccessRate;
    }

    public void setExchangeSuccessRate(BigDecimal exchangeSuccessRate) {
        this.exchangeSuccessRate = exchangeSuccessRate;
    }

    public Long getTotalPointsCirculation() {
        return totalPointsCirculation;
    }

    public void setTotalPointsCirculation(Long totalPointsCirculation) {
        this.totalPointsCirculation = totalPointsCirculation;
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

    public BigDecimal getAverageDailyActiveUsers() {
        return averageDailyActiveUsers;
    }

    public void setAverageDailyActiveUsers(BigDecimal averageDailyActiveUsers) {
        this.averageDailyActiveUsers = averageDailyActiveUsers;
    }

    public BigDecimal getAverageDailyNewLeads() {
        return averageDailyNewLeads;
    }

    public void setAverageDailyNewLeads(BigDecimal averageDailyNewLeads) {
        this.averageDailyNewLeads = averageDailyNewLeads;
    }

    public BigDecimal getAverageDailyExchanges() {
        return averageDailyExchanges;
    }

    public void setAverageDailyExchanges(BigDecimal averageDailyExchanges) {
        this.averageDailyExchanges = averageDailyExchanges;
    }

    public Long getSystemRunDays() {
        return systemRunDays;
    }

    public void setSystemRunDays(Long systemRunDays) {
        this.systemRunDays = systemRunDays;
    }

    public String getTopIndustry() {
        return topIndustry;
    }

    public void setTopIndustry(String topIndustry) {
        this.topIndustry = topIndustry;
    }

    public Long getTopIndustryLeadCount() {
        return topIndustryLeadCount;
    }

    public void setTopIndustryLeadCount(Long topIndustryLeadCount) {
        this.topIndustryLeadCount = topIndustryLeadCount;
    }

    public Long getMostActiveUserId() {
        return mostActiveUserId;
    }

    public void setMostActiveUserId(Long mostActiveUserId) {
        this.mostActiveUserId = mostActiveUserId;
    }

    public Long getMostActiveUserLeadCount() {
        return mostActiveUserLeadCount;
    }

    public void setMostActiveUserLeadCount(Long mostActiveUserLeadCount) {
        this.mostActiveUserLeadCount = mostActiveUserLeadCount;
    }
    
    // 兼容性方法
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
        return "SystemStats{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalUsers=" + totalUsers +
                ", activeUsers=" + activeUsers +
                ", newUsers=" + newUsers +
                ", totalLeads=" + totalLeads +
                ", validLeads=" + validLeads +
                ", pendingLeads=" + pendingLeads +
                ", offlineLeads=" + offlineLeads +
                ", totalExchanges=" + totalExchanges +
                ", successfulExchanges=" + successfulExchanges +
                ", exchangeSuccessRate=" + exchangeSuccessRate +
                ", totalPointsCirculation=" + totalPointsCirculation +
                ", averageRatingScore=" + averageRatingScore +
                ", aGradeLeads=" + aGradeLeads +
                ", bGradeLeads=" + bGradeLeads +
                ", cGradeLeads=" + cGradeLeads +
                ", dGradeLeads=" + dGradeLeads +
                ", totalViews=" + totalViews +
                ", totalFavorites=" + totalFavorites +
                ", averageDailyActiveUsers=" + averageDailyActiveUsers +
                ", averageDailyNewLeads=" + averageDailyNewLeads +
                ", averageDailyExchanges=" + averageDailyExchanges +
                ", systemRunDays=" + systemRunDays +
                ", topIndustry='" + topIndustry + '\'' +
                ", topIndustryLeadCount=" + topIndustryLeadCount +
                ", mostActiveUserId=" + mostActiveUserId +
                ", mostActiveUserLeadCount=" + mostActiveUserLeadCount +
                '}';
    }
}