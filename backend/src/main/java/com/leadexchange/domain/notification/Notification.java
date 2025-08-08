package com.leadexchange.domain.notification;

import com.leadexchange.common.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 通知记录实体类
 * 对应数据库notifications表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {
    
    /**
     * 消息ID（唯一标识）
     */
    @Column(name = "message_id", nullable = false, length = 50, unique = true)
    private String messageId;
    
    /**
     * 接收人ID
     */
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;
    
    /**
     * 发送人ID
     */
    @Column(name = "sender_id")
    private Long senderId;
    
    /**
     * 通知类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;
    
    /**
     * 事件类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private NotificationType eventType;
    
    /**
     * 发送渠道
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "send_channel", nullable = false, length = 20)
    private SendChannel sendChannel;
    
    /**
     * 优先级（1-10，数字越大优先级越高）
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 5;
    
    /**
     * 重试次数
     */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;
    
    /**
     * 最大重试次数
     */
    @Column(name = "max_retry_count", nullable = false)
    private Integer maxRetryCount = 3;
    
    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private LocalDateTime expireTime;
    
    /**
     * 模板ID
     */
    @Column(name = "template_id")
    private Long templateId;
    
    /**
     * 模板代码
     */
    @Column(name = "template_code", length = 100)
    private String templateCode;
    
    /**
     * 发送服务提供商
     */
    @Column(name = "provider", length = 50)
    private String provider;
    
    /**
     * 通知标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    /**
     * 通知内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    /**
     * 额外数据（JSON格式）
     */
    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData;
    
    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SendStatus status = SendStatus.PENDING;
    
    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private LocalDateTime sendTime;
    
    /**
     * 阅读时间
     */
    @Column(name = "read_time")
    private LocalDateTime readTime;
    
    /**
     * 是否已读
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    // ==================== 构造方法 ====================
    
    public Notification() {
        this.messageId = UUID.randomUUID().toString().replace("-", "");
    }
    
    public Notification(Long recipientId, NotificationType notificationType, 
                       SendChannel sendChannel, String title, String content) {
        this();
        this.recipientId = recipientId;
        this.notificationType = notificationType;
        this.eventType = notificationType;
        this.sendChannel = sendChannel;
        this.title = title;
        this.content = content;
        this.expireTime = LocalDateTime.now().plusHours(24); // 默认24小时过期
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 标记为已读
     */
    public void markAsRead() {
        this.isRead = true;
        this.readTime = LocalDateTime.now();
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 标记为未读
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readTime = null;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 开始发送
     */
    public void startSending() {
        this.status = SendStatus.SENDING;
        this.sendTime = LocalDateTime.now();
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 发送成功
     */
    public void sendSuccess() {
        this.status = SendStatus.SUCCESS;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 发送失败
     */
    public void sendFailed() {
        this.status = SendStatus.FAILED;
        this.retryCount++;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 取消发送
     */
    public void cancel() {
        this.status = SendStatus.CANCELLED;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 标记为过期
     */
    public void expire() {
        this.status = SendStatus.EXPIRED;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 判断是否可以重试
     * 
     * @return 是否可以重试
     */
    public boolean canRetry() {
        return status == SendStatus.FAILED && retryCount < maxRetryCount;
    }
    
    /**
     * 判断是否已过期
     * 
     * @return 是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }
    
    /**
     * 判断是否需要发送
     * 
     * @return 是否需要发送
     */
    public boolean needsSending() {
        return status == SendStatus.PENDING && !isExpired();
    }
    
    /**
     * 判断是否可以发送
     * 
     * @return 是否可以发送
     */
    public boolean canSend() {
        return (status == SendStatus.PENDING || status == SendStatus.FAILED) && !isExpired();
    }
    
    /**
     * 判断是否发送成功
     * 
     * @return 是否发送成功
     */
    public boolean isSendSuccess() {
        return status == SendStatus.SUCCESS;
    }
    
    /**
     * 重置重试次数
     */
    public void resetRetryCount() {
        this.retryCount = 0;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 重置为重试状态
     */
    public void resetForRetry() {
        this.status = SendStatus.PENDING;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    // ==================== Getter和Setter ====================
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public Long getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public NotificationType getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
    
    public NotificationType getEventType() {
        return eventType;
    }
    
    public void setEventType(NotificationType eventType) {
        this.eventType = eventType;
    }
    
    public SendChannel getSendChannel() {
        return sendChannel;
    }
    
    public void setSendChannel(SendChannel sendChannel) {
        this.sendChannel = sendChannel;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }
    
    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    public Long getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
    
    public String getTemplateCode() {
        return templateCode;
    }
    
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getExtraData() {
        return extraData;
    }
    
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
    
    public SendStatus getStatus() {
        return status;
    }
    
    public void setStatus(SendStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getSendTime() {
        return sendTime;
    }
    
    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
    
    public LocalDateTime getReadTime() {
        return readTime;
    }
    
    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
}