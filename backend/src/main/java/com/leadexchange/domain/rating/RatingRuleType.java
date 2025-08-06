package com.leadexchange.domain.rating;

/**
 * 评级规则类型枚举
 * 定义不同的评级维度和规则类型
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum RatingRuleType {
    
    /**
     * 信息完整度评分
     * 评估线索信息的完整程度
     */
    COMPLETENESS("信息完整度", "根据线索信息字段的完整程度进行评分"),
    
    /**
     * 企业资质评分
     * 评估企业的资质和信誉
     */
    QUALIFICATION("企业资质", "根据企业注册资本、成立时间等资质信息进行评分"),
    
    /**
     * 企业规模评分
     * 评估企业的规模大小
     */
    SCALE("企业规模", "根据企业投资金额、员工数量等规模信息进行评分"),
    
    /**
     * 产业价值评分
     * 评估所属产业的价值和前景
     */
    INDUSTRY_VALUE("产业价值", "根据所属行业的发展前景和政策支持进行评分"),
    
    /**
     * 地理位置评分
     * 评估项目地理位置的优势
     */
    LOCATION("地理位置", "根据项目所在地区的经济发展水平进行评分"),
    
    /**
     * 时效性评分
     * 评估线索的时效性
     */
    TIMELINESS("时效性", "根据线索发布时间和更新频率进行评分"),
    
    /**
     * 用户信誉评分
     * 评估发布用户的信誉度
     */
    USER_REPUTATION("用户信誉", "根据发布用户的历史交易记录和信誉度进行评分");
    
    private final String displayName;
    private final String description;
    
    RatingRuleType(String displayName, String description) {
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
    public static RatingRuleType fromDisplayName(String displayName) {
        for (RatingRuleType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的评级规则类型: " + displayName);
    }
    
    /**
     * 判断是否为核心评级规则
     * 核心规则包括：信息完整度、企业资质、企业规模、产业价值
     * 
     * @return 是否为核心规则
     */
    public boolean isCoreRule() {
        return this == COMPLETENESS || this == QUALIFICATION || 
               this == SCALE || this == INDUSTRY_VALUE;
    }
    
    /**
     * 获取默认权重
     * 
     * @return 默认权重值
     */
    public double getDefaultWeight() {
        switch (this) {
            case COMPLETENESS:
                return 0.40; // 40%
            case QUALIFICATION:
                return 0.30; // 30%
            case SCALE:
                return 0.20; // 20%
            case INDUSTRY_VALUE:
                return 0.10; // 10%
            case LOCATION:
                return 0.05; // 5%
            case TIMELINESS:
                return 0.03; // 3%
            case USER_REPUTATION:
                return 0.02; // 2%
            default:
                return 0.01; // 1%
        }
    }
    
    /**
     * 获取默认配置参数
     * 
     * @return 默认配置参数的JSON字符串
     */
    public String getDefaultConfigParams() {
        switch (this) {
            case COMPLETENESS:
                return "{\"requiredFields\": [\"companyName\", \"contactPerson\", \"contactPhone\", \"projectDescription\"], \"weightPerField\": 25}";
            case QUALIFICATION:
                return "{\"steps\": [{\"min\": 0, \"max\": 1000000, \"score\": 10}, {\"min\": 1000000, \"max\": 10000000, \"score\": 20}, {\"min\": 10000000, \"max\": 100000000, \"score\": 30}]}";
            case SCALE:
                return "{\"steps\": [{\"min\": 0, \"max\": 50, \"score\": 10}, {\"min\": 50, \"max\": 200, \"score\": 20}, {\"min\": 200, \"max\": 1000, \"score\": 30}]}";
            case INDUSTRY_VALUE:
                return "{\"industryScores\": {\"高新技术\": 30, \"制造业\": 25, \"服务业\": 20, \"传统行业\": 15}}";
            case LOCATION:
                return "{\"regionScores\": {\"一线城市\": 30, \"二线城市\": 25, \"三线城市\": 20, \"其他地区\": 15}}";
            case TIMELINESS:
                return "{\"timeDecay\": {\"days\": 30, \"decayRate\": 0.1}}";
            case USER_REPUTATION:
                return "{\"reputationLevels\": {\"优秀\": 30, \"良好\": 25, \"一般\": 20, \"较差\": 10}}";
            default:
                return "{}";
        }
    }
}