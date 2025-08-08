package com.leadexchange.domain.notification;

/**
 * 发送状态枚举
 * 定义通知发送的各种状态
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum SendStatus {
    
    /**
     * 待发送
     */
    PENDING("PENDING", "待发送", false, false),
    
    /**
     * 发送中
     */
    SENDING("SENDING", "发送中", false, false),
    
    /**
     * 发送成功
     */
    SUCCESS("SUCCESS", "发送成功", true, true),
    
    /**
     * 发送失败
     */
    FAILED("FAILED", "发送失败", true, false),
    
    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消", true, false),
    
    /**
     * 已过期
     */
    EXPIRED("EXPIRED", "已过期", true, false);
    
    private final String code;
    private final String description;
    private final boolean isFinal;
    private final boolean isSuccess;
    
    SendStatus(String code, String description, boolean isFinal, boolean isSuccess) {
        this.code = code;
        this.description = description;
        this.isFinal = isFinal;
        this.isSuccess = isSuccess;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
    public boolean isSuccess() {
        return isSuccess;
    }
    
    /**
     * 根据代码获取发送状态
     * 
     * @param code 状态代码
     * @return 发送状态枚举
     */
    public static SendStatus fromCode(String code) {
        for (SendStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的发送状态代码: " + code);
    }
    
    /**
     * 判断是否可以重试
     * 
     * @return 是否可以重试
     */
    public boolean canRetry() {
        return this == FAILED;
    }
    
    /**
     * 判断是否正在处理中
     * 
     * @return 是否正在处理中
     */
    public boolean isProcessing() {
        return this == PENDING || this == SENDING;
    }
    
    /**
     * 判断是否需要记录日志
     * 
     * @return 是否需要记录日志
     */
    public boolean shouldLog() {
        return isFinal;
    }
}