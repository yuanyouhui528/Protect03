package com.leadexchange.domain.notification;

/**
 * 通知类型枚举
 * 定义系统中各种通知的类型
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum NotificationType {
    
    // ==================== 交换相关通知 ====================
    /**
     * 收到线索交换申请
     */
    EXCHANGE_APPLICATION("EXCHANGE_APPLICATION", "收到线索交换申请"),
    
    /**
     * 交换申请被同意
     */
    EXCHANGE_APPROVED("EXCHANGE_APPROVED", "交换申请被同意"),
    
    /**
     * 交换申请被拒绝
     */
    EXCHANGE_REJECTED("EXCHANGE_REJECTED", "交换申请被拒绝"),
    
    /**
     * 交换申请被取消
     */
    EXCHANGE_CANCELLED("EXCHANGE_CANCELLED", "交换申请被取消"),
    
    /**
     * 交换完成
     */
    EXCHANGE_COMPLETED("EXCHANGE_COMPLETED", "交换完成"),
    
    /**
     * 交换申请过期
     */
    EXCHANGE_EXPIRED("EXCHANGE_EXPIRED", "交换申请过期"),
    
    // ==================== 线索相关通知 ====================
    /**
     * 线索审核通过
     */
    LEAD_APPROVED("LEAD_APPROVED", "线索审核通过"),
    
    /**
     * 线索审核拒绝
     */
    LEAD_REJECTED("LEAD_REJECTED", "线索审核拒绝"),
    
    /**
     * 线索被收藏
     */
    LEAD_FAVORITED("LEAD_FAVORITED", "线索被收藏"),
    
    /**
     * 线索评级变更
     */
    LEAD_RATING_CHANGED("LEAD_RATING_CHANGED", "线索评级变更"),
    
    // ==================== 积分相关通知 ====================
    /**
     * 积分变动
     */
    CREDIT_CHANGED("CREDIT_CHANGED", "积分变动"),
    
    /**
     * 积分不足
     */
    CREDIT_INSUFFICIENT("CREDIT_INSUFFICIENT", "积分不足"),
    
    // ==================== 系统通知 ====================
    /**
     * 系统公告
     */
    SYSTEM_ANNOUNCEMENT("SYSTEM_ANNOUNCEMENT", "系统公告"),
    
    /**
     * 系统维护
     */
    SYSTEM_MAINTENANCE("SYSTEM_MAINTENANCE", "系统维护"),
    
    /**
     * 账户安全
     */
    ACCOUNT_SECURITY("ACCOUNT_SECURITY", "账户安全");
    
    private final String code;
    private final String description;
    
    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取通知类型
     * 
     * @param code 通知类型代码
     * @return 通知类型枚举
     */
    public static NotificationType fromCode(String code) {
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的通知类型代码: " + code);
    }
    
    /**
     * 判断是否为交换相关通知
     * 
     * @return 是否为交换相关通知
     */
    public boolean isExchangeRelated() {
        return this == EXCHANGE_APPLICATION || this == EXCHANGE_APPROVED || 
               this == EXCHANGE_REJECTED || this == EXCHANGE_CANCELLED ||
               this == EXCHANGE_COMPLETED || this == EXCHANGE_EXPIRED;
    }
    
    /**
     * 判断是否为线索相关通知
     * 
     * @return 是否为线索相关通知
     */
    public boolean isLeadRelated() {
        return this == LEAD_APPROVED || this == LEAD_REJECTED ||
               this == LEAD_FAVORITED || this == LEAD_RATING_CHANGED;
    }
    
    /**
     * 判断是否为系统通知
     * 
     * @return 是否为系统通知
     */
    public boolean isSystemNotification() {
        return this == SYSTEM_ANNOUNCEMENT || this == SYSTEM_MAINTENANCE ||
               this == ACCOUNT_SECURITY;
    }
}