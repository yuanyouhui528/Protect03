package com.leadexchange.domain.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 趋势数据模型
 * 用于表示时间序列的统计数据
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class TrendData {

    /**
     * 时间点
     */
    private LocalDateTime timestamp;

    /**
     * 时间标签（用于前端显示）
     */
    private String timeLabel;

    /**
     * 数值
     */
    private Long value;

    /**
     * 百分比值（可选）
     */
    private BigDecimal percentage;

    /**
     * 数据类型标识
     */
    private String dataType;

    /**
     * 额外的数据标签
     */
    private String label;

    /**
     * 比较值（与上一期对比）
     */
    private Long compareValue;

    /**
     * 增长率
     */
    private BigDecimal growthRate;

    // 构造函数
    public TrendData() {}

    public TrendData(LocalDateTime timestamp, Long value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public TrendData(LocalDateTime timestamp, String timeLabel, Long value, String dataType) {
        this.timestamp = timestamp;
        this.timeLabel = timeLabel;
        this.value = value;
        this.dataType = dataType;
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

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getCompareValue() {
        return compareValue;
    }

    public void setCompareValue(Long compareValue) {
        this.compareValue = compareValue;
    }

    public BigDecimal getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(BigDecimal growthRate) {
        this.growthRate = growthRate;
    }

    /**
     * 计算增长率
     * 
     * @param previousValue 上一期数值
     */
    public void calculateGrowthRate(Long previousValue) {
        if (previousValue != null && previousValue > 0 && this.value != null) {
            BigDecimal current = new BigDecimal(this.value);
            BigDecimal previous = new BigDecimal(previousValue);
            this.growthRate = current.subtract(previous)
                    .divide(previous, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100));
            this.compareValue = previousValue;
        }
    }

    @Override
    public String toString() {
        return "TrendData{" +
                "timestamp=" + timestamp +
                ", timeLabel='" + timeLabel + '\'' +
                ", value=" + value +
                ", percentage=" + percentage +
                ", dataType='" + dataType + '\'' +
                ", label='" + label + '\'' +
                ", compareValue=" + compareValue +
                ", growthRate=" + growthRate +
                '}';
    }
}