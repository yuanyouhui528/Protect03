package com.leadexchange.domain.notification;

/**
 * 发送渠道枚举
 * 定义通知发送的各种渠道
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum SendChannel {
    
    /**
     * 系统内通知
     */
    SYSTEM("SYSTEM", "系统通知", true),
    
    /**
     * 短信通知
     */
    SMS("SMS", "短信通知", false),
    
    /**
     * 邮件通知
     */
    EMAIL("EMAIL", "邮件通知", false),
    
    /**
     * 微信通知
     */
    WECHAT("WECHAT", "微信通知", false),
    
    /**
     * 推送通知
     */
    PUSH("PUSH", "推送通知", false);
    
    private final String code;
    private final String description;
    private final boolean isDefault;
    
    SendChannel(String code, String description, boolean isDefault) {
        this.code = code;
        this.description = description;
        this.isDefault = isDefault;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    /**
     * 根据代码获取发送渠道
     * 
     * @param code 渠道代码
     * @return 发送渠道枚举
     */
    public static SendChannel fromCode(String code) {
        for (SendChannel channel : values()) {
            if (channel.code.equals(code)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("未知的发送渠道代码: " + code);
    }
    
    /**
     * 判断是否需要外部接口
     * 
     * @return 是否需要外部接口
     */
    public boolean requiresExternalApi() {
        return this != SYSTEM;
    }
    
    /**
     * 判断是否支持即时发送
     * 
     * @return 是否支持即时发送
     */
    public boolean supportsInstantSend() {
        return this == SYSTEM || this == PUSH;
    }
    
    /**
     * 获取默认发送渠道
     * 
     * @return 默认发送渠道
     */
    public static SendChannel getDefault() {
        return SYSTEM;
    }
}