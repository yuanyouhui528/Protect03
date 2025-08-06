package com.leadexchange.domain.rating;

/**
 * 评级计算方法枚举
 * 定义不同的评分计算方式
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum CalculationMethod {
    
    /**
     * 加权求和
     * 将各项得分按权重进行加权求和
     */
    WEIGHTED_SUM("加权求和", "各项得分按权重进行加权求和"),
    
    /**
     * 简单求和
     * 将各项得分直接相加
     */
    SIMPLE_SUM("简单求和", "各项得分直接相加"),
    
    /**
     * 平均值
     * 计算各项得分的平均值
     */
    AVERAGE("平均值", "计算各项得分的平均值"),
    
    /**
     * 最大值
     * 取各项得分中的最大值
     */
    MAX_VALUE("最大值", "取各项得分中的最大值"),
    
    /**
     * 最小值
     * 取各项得分中的最小值
     */
    MIN_VALUE("最小值", "取各项得分中的最小值"),
    
    /**
     * 条件计分
     * 根据条件进行计分
     */
    CONDITIONAL("条件计分", "根据预设条件进行计分"),
    
    /**
     * 百分比计分
     * 按百分比进行计分
     */
    PERCENTAGE("百分比计分", "按百分比进行计分"),
    
    /**
     * 阶梯计分
     * 按阶梯区间进行计分
     */
    STEP_SCORING("阶梯计分", "按阶梯区间进行计分"),
    
    /**
     * 自定义公式
     * 使用自定义公式进行计算
     */
    CUSTOM_FORMULA("自定义公式", "使用自定义公式进行计算");
    
    private final String displayName;
    private final String description;
    
    CalculationMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据显示名称获取枚举值
     * 
     * @param displayName 显示名称
     * @return 对应的枚举值
     */
    public static CalculationMethod fromDisplayName(String displayName) {
        for (CalculationMethod method : values()) {
            if (method.displayName.equals(displayName)) {
                return method;
            }
        }
        throw new IllegalArgumentException("未知的计算方法: " + displayName);
    }
    
    /**
     * 判断是否需要权重参数
     * 
     * @return 是否需要权重参数
     */
    public boolean requiresWeight() {
        return this == WEIGHTED_SUM;
    }
    
    /**
     * 判断是否需要配置参数
     * 
     * @return 是否需要配置参数
     */
    public boolean requiresConfig() {
        return this == CONDITIONAL || this == PERCENTAGE || 
               this == STEP_SCORING || this == CUSTOM_FORMULA;
    }
    
    /**
     * 获取默认配置参数示例
     * 
     * @return 默认配置参数的JSON示例
     */
    public String getDefaultConfigExample() {
        switch (this) {
            case CONDITIONAL:
                return "{\"conditions\": [{\"field\": \"registeredCapital\", \"operator\": \">\", \"value\": 10000000, \"score\": 30}]}";
            case PERCENTAGE:
                return "{\"maxValue\": 100000000, \"scoreRatio\": 0.0001}";
            case STEP_SCORING:
                return "{\"steps\": [{\"min\": 0, \"max\": 1000000, \"score\": 10}, {\"min\": 1000000, \"max\": 10000000, \"score\": 20}]}";
            case CUSTOM_FORMULA:
                return "{\"formula\": \"Math.min(100, registeredCapital / 1000000 * 10)\"}";
            default:
                return "{}";
        }
    }
}