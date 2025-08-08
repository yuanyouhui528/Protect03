package com.leadexchange.domain.notification;

/**
 * 通知模板类型枚举
 * 定义不同类型的通知模板
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum TemplateType {
    
    /**
     * 系统通知模板
     */
    SYSTEM("系统通知"),
    
    /**
     * 业务通知模板
     */
    BUSINESS("业务通知"),
    
    /**
     * 营销通知模板
     */
    MARKETING("营销通知"),
    
    /**
     * 安全通知模板
     */
    SECURITY("安全通知"),
    
    /**
     * 提醒通知模板
     */
    REMINDER("提醒通知");
    
    private final String description;
    
    TemplateType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据描述获取模板类型
     * 
     * @param description 描述
     * @return 模板类型
     */
    public static TemplateType fromDescription(String description) {
        for (TemplateType type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的模板类型: " + description);
    }
}