package com.leadexchange.domain.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户活跃度趋势数据模型
 * 用于表示用户活跃度的时间序列数据
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class UserActivityTrend {

    /**
     * 时间点
     */
    private LocalDateTime timestamp;

    /**
     * 时间标签（用于前端显示）
     */
    private String timeLabel;

    /**
     * 活跃用户数
     */
    private Long activeUsers;

    /**
     * 新增用户数
     */
    private Long newUsers;

    /**
     * 登录用户数
     */
    private Long loginUsers;

    /**
     * 发布线索用户数
     */
    private Long publishUsers;

    /**
     * 交换线索用户数
     */
    private Long exchangeUsers;

    /**
     * 活跃度评分
     */
    private BigDecimal activityScore;

    /**
     * 与上一期对比的增长率
     */
    private BigDecimal growthRate;

    /**
     * 数据类型（daily、weekly、monthly）
     */
    private String granularity;

    // 构造函数
    public UserActivityTrend() {}

    public UserActivityTrend(LocalDateTime timestamp, Long activeUsers) {
        this.timestamp = timestamp;
        this.activeUsers = activeUsers;
    }

    public UserActivityTrend(LocalDateTime timestamp, String timeLabel, Long activeUsers, String granularity) {
        this.timestamp = timestamp;
        this.timeLabel = timeLabel;
        this.activeUsers = activeUsers;
        this.granularity = granularity;
    }

    // Getter和Setter方法
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
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

    public Long getLoginUsers() {
        return loginUsers;
    }

    public void setLoginUsers(Long loginUsers) {
        this.loginUsers = loginUsers;
    }

    public Long getPublishUsers() {
        return publishUsers;
    }

    public void setPublishUsers(Long publishUsers) {
        this.publishUsers = publishUsers;
    }

    public Long getExchangeUsers() {
        return exchangeUsers;
    }

    public void setExchangeUsers(Long exchangeUsers) {
        this.exchangeUsers = exchangeUsers;
    }

    public BigDecimal getActivityScore() {
        return activityScore;
    }

    public void setActivityScore(BigDecimal activityScore) {
        this.activityScore = activityScore;
    }

    public BigDecimal getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(BigDecimal growthRate) {
        this.growthRate = growthRate;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    /**
     * 计算活跃度评分
     * 基于各种用户活动指标计算综合活跃度评分
     */
    public void calculateActivityScore() {
        if (activeUsers == null) {
            this.activityScore = BigDecimal.ZERO;
            return;
        }

        BigDecimal score = new BigDecimal(activeUsers);
        
        // 新增用户权重：1.2
        if (newUsers != null && newUsers > 0) {
            score = score.add(new BigDecimal(newUsers).multiply(new BigDecimal("1.2")));
        }
        
        // 发布线索用户权重：1.5
        if (publishUsers != null && publishUsers > 0) {
            score = score.add(new BigDecimal(publishUsers).multiply(new BigDecimal("1.5")));
        }
        
        // 交换线索用户权重：2.0
        if (exchangeUsers != null && exchangeUsers > 0) {
            score = score.add(new BigDecimal(exchangeUsers).multiply(new BigDecimal("2.0")));
        }
        
        this.activityScore = score;
    }

    /**
     * 计算增长率
     * 
     * @param previousActiveUsers 上一期活跃用户数
     */
    public void calculateGrowthRate(Long previousActiveUsers) {
        if (previousActiveUsers != null && previousActiveUsers > 0 && this.activeUsers != null) {
            BigDecimal current = new BigDecimal(this.activeUsers);
            BigDecimal previous = new BigDecimal(previousActiveUsers);
            this.growthRate = current.subtract(previous)
                    .divide(previous, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100));
        }
    }

    /**
     * 获取总用户活动数
     * 
     * @return 总活动数
     */
    public Long getTotalActivity() {
        long total = activeUsers != null ? activeUsers : 0;
        total += newUsers != null ? newUsers : 0;
        total += publishUsers != null ? publishUsers : 0;
        total += exchangeUsers != null ? exchangeUsers : 0;
        return total;
    }

    @Override
    public String toString() {
        return "UserActivityTrend{" +
                "timestamp=" + timestamp +
                ", timeLabel='" + timeLabel + '\'' +
                ", activeUsers=" + activeUsers +
                ", newUsers=" + newUsers +
                ", loginUsers=" + loginUsers +
                ", publishUsers=" + publishUsers +
                ", exchangeUsers=" + exchangeUsers +
                ", activityScore=" + activityScore +
                ", growthRate=" + growthRate +
                ", granularity='" + granularity + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserActivityTrend that = (UserActivityTrend) o;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        return timestamp != null ? timestamp.hashCode() : 0;
    }
}