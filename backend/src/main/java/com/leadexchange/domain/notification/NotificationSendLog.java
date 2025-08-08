package com.leadexchange.domain.notification;

import com.leadexchange.common.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 通知发送日志实体类
 * 对应数据库notification_send_logs表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "notification_send_logs")
public class NotificationSendLog extends BaseEntity {
    
    /**
     * 通知ID
     */
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;
    
    /**
     * 发送渠道
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "send_channel", nullable = false, length = 20)
    private SendChannel sendChannel;
    
    /**
     * 接收方（手机号、邮箱等）
     */
    @Column(name = "recipient", nullable = false, length = 200)
    private String recipient;
    
    /**
     * 发送状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "send_status", nullable = false, length = 20)
    private SendStatus sendStatus;
    
    /**
     * 响应码
     */
    @Column(name = "response_code", length = 50)
    private String responseCode;
    
    /**
     * 响应消息
     */
    @Column(name = "response_message", length = 500)
    private String responseMessage;
    
    /**
     * 发送开始时间
     */
    @Column(name = "send_start_time")
    private LocalDateTime sendStartTime;
    
    /**
     * 发送结束时间
     */
    @Column(name = "send_end_time")
    private LocalDateTime sendEndTime;
    
    /**
     * 响应时间（毫秒）
     */
    @Column(name = "response_time")
    private Long responseTime;
    
    /**
     * 重试次数
     */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;
    
    /**
     * 错误信息
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    /**
     * 服务商信息
     */
    @Column(name = "provider_info", length = 200)
    private String providerInfo;
    
    // ==================== 构造方法 ====================
    
    public NotificationSendLog() {}
    
    public NotificationSendLog(Long notificationId, SendChannel sendChannel, String recipient) {
        this.notificationId = notificationId;
        this.sendChannel = sendChannel;
        this.recipient = recipient;
        this.sendStatus = SendStatus.PENDING;
        this.sendStartTime = LocalDateTime.now();
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 开始发送
     */
    public void startSending() {
        this.sendStatus = SendStatus.SENDING;
        this.sendStartTime = LocalDateTime.now();
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 发送成功
     * 
     * @param responseCode 响应码
     * @param responseMessage 响应消息
     */
    public void sendSuccess(String responseCode, String responseMessage) {
        this.sendStatus = SendStatus.SUCCESS;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.sendEndTime = LocalDateTime.now();
        this.calculateResponseTime();
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 发送失败
     * 
     * @param responseCode 响应码
     * @param errorMessage 错误消息
     */
    public void sendFailed(String responseCode, String errorMessage) {
        this.sendStatus = SendStatus.FAILED;
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
        this.sendEndTime = LocalDateTime.now();
        this.retryCount++;
        this.calculateResponseTime();
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 取消发送
     */
    public void cancel() {
        this.sendStatus = SendStatus.CANCELLED;
        this.sendEndTime = LocalDateTime.now();
        this.calculateResponseTime();
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 计算响应时间
     */
    private void calculateResponseTime() {
        if (sendStartTime != null && sendEndTime != null) {
            this.responseTime = java.time.Duration.between(sendStartTime, sendEndTime).toMillis();
        }
    }
    
    /**
     * 判断发送是否成功
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return sendStatus == SendStatus.SUCCESS;
    }
    
    /**
     * 判断发送是否失败
     * 
     * @return 是否失败
     */
    public boolean isFailed() {
        return sendStatus == SendStatus.FAILED;
    }
    
    /**
     * 判断是否可以重试
     * 
     * @param maxRetryCount 最大重试次数
     * @return 是否可以重试
     */
    public boolean canRetry(int maxRetryCount) {
        return isFailed() && retryCount < maxRetryCount;
    }
    
    /**
     * 获取发送耗时描述
     * 
     * @return 耗时描述
     */
    public String getResponseTimeDescription() {
        if (responseTime == null) {
            return "未知";
        }
        
        if (responseTime < 1000) {
            return responseTime + "ms";
        } else {
            return String.format("%.2fs", responseTime / 1000.0);
        }
    }
    
    /**
     * 创建成功日志
     * 
     * @param notificationId 通知ID
     * @param sendChannel 发送渠道
     * @param recipient 接收方
     * @param responseCode 响应码
     * @param responseMessage 响应消息
     * @return 发送日志
     */
    public static NotificationSendLog createSuccessLog(Long notificationId, SendChannel sendChannel, 
                                                       String recipient, String responseCode, String responseMessage) {
        NotificationSendLog log = new NotificationSendLog(notificationId, sendChannel, recipient);
        log.sendSuccess(responseCode, responseMessage);
        return log;
    }
    
    /**
     * 创建失败日志
     * 
     * @param notificationId 通知ID
     * @param sendChannel 发送渠道
     * @param recipient 接收方
     * @param responseCode 响应码
     * @param errorMessage 错误消息
     * @return 发送日志
     */
    public static NotificationSendLog createFailedLog(Long notificationId, SendChannel sendChannel, 
                                                      String recipient, String responseCode, String errorMessage) {
        NotificationSendLog log = new NotificationSendLog(notificationId, sendChannel, recipient);
        log.sendFailed(responseCode, errorMessage);
        return log;
    }
    
    // ==================== Getter和Setter ====================
    
    public Long getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    
    public SendChannel getSendChannel() {
        return sendChannel;
    }
    
    public void setSendChannel(SendChannel sendChannel) {
        this.sendChannel = sendChannel;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public SendStatus getSendStatus() {
        return sendStatus;
    }
    
    public void setSendStatus(SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }
    
    public String getResponseCode() {
        return responseCode;
    }
    
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
    
    public String getResponseMessage() {
        return responseMessage;
    }
    
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    
    public LocalDateTime getSendStartTime() {
        return sendStartTime;
    }
    
    public void setSendStartTime(LocalDateTime sendStartTime) {
        this.sendStartTime = sendStartTime;
    }
    
    public LocalDateTime getSendEndTime() {
        return sendEndTime;
    }
    
    public void setSendEndTime(LocalDateTime sendEndTime) {
        this.sendEndTime = sendEndTime;
    }
    
    public Long getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getProviderInfo() {
        return providerInfo;
    }
    
    public void setProviderInfo(String providerInfo) {
        this.providerInfo = providerInfo;
    }
}