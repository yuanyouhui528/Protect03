package com.leadexchange.domain.notification;

/**
 * 通知模板状态枚举
 * 定义通知模板的不同状态
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum TemplateStatus {
    
    /**
     * 草稿状态
     */
    DRAFT("草稿"),
    
    /**
     * 激活状态
     */
    ACTIVE("激活"),
    
    /**
     * 禁用状态
     */
    DISABLED("禁用"),
    
    /**
     * 已过期状态
     */
    EXPIRED("已过期"),
    
    /**
     * 已删除状态
     */
    DELETED("已删除");
    
    private final String description;
    
    TemplateStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据描述获取模板状态
     * 
     * @param description 描述
     * @return 模板状态
     */
    public static TemplateStatus fromDescription(String description) {
        for (TemplateStatus status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的模板状态: " + description);
    }
    
    /**
     * 判断是否为激活状态
     * 
     * @return 是否激活
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    /**
     * 判断是否为禁用状态
     * 
     * @return 是否禁用
     */
    public boolean isDisabled() {
        return this == DISABLED;
    }
    
    /**
     * 判断是否为已过期状态
     * 
     * @return 是否过期
     */
    public boolean isExpired() {
        return this == EXPIRED;
    }
}