package com.leadexchange.domain.notification;

import com.leadexchange.common.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 通知设置实体类
 * 对应数据库notification_settings表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "notification_settings")
public class NotificationSettings extends BaseEntity {
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 通知类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;
    
    /**
     * 系统通知开关
     */
    @Column(name = "system_enabled", nullable = false)
    private Boolean systemEnabled = true;
    
    /**
     * 短信通知开关
     */
    @Column(name = "sms_enabled", nullable = false)
    private Boolean smsEnabled = true;
    
    /**
     * 邮件通知开关
     */
    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled = true;
    
    /**
     * 微信通知开关
     */
    @Column(name = "wechat_enabled", nullable = false)
    private Boolean wechatEnabled = false;
    
    /**
     * 免打扰开始时间
     */
    @Column(name = "quiet_start_time")
    private LocalTime quietStartTime;
    
    /**
     * 免打扰结束时间
     */
    @Column(name = "quiet_end_time")
    private LocalTime quietEndTime;
    
    /**
     * 是否启用免打扰
     */
    @Column(name = "quiet_enabled", nullable = false)
    private Boolean quietEnabled = false;
    
    /**
     * 通知频率限制（分钟）
     */
    @Column(name = "frequency_limit", nullable = false)
    private Integer frequencyLimit = 0;
    
    /**
     * 最后通知时间
     */
    @Column(name = "last_notification_time")
    private LocalDateTime lastNotificationTime;
    
    // ==================== 构造方法 ====================
    
    public NotificationSettings() {}
    
    public NotificationSettings(Long userId, NotificationType notificationType) {
        this.userId = userId;
        this.notificationType = notificationType;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 判断指定渠道是否启用
     * 
     * @param channel 发送渠道
     * @return 是否启用
     */
    public boolean isChannelEnabled(SendChannel channel) {
        switch (channel) {
            case SYSTEM:
                return systemEnabled;
            case SMS:
                return smsEnabled;
            case EMAIL:
                return emailEnabled;
            case WECHAT:
                return wechatEnabled;
            default:
                return false;
        }
    }
    
    /**
     * 启用指定渠道
     * 
     * @param channel 发送渠道
     */
    public void enableChannel(SendChannel channel) {
        switch (channel) {
            case SYSTEM:
                this.systemEnabled = true;
                break;
            case SMS:
                this.smsEnabled = true;
                break;
            case EMAIL:
                this.emailEnabled = true;
                break;
            case WECHAT:
                this.wechatEnabled = true;
                break;
        }
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 禁用指定渠道
     * 
     * @param channel 发送渠道
     */
    public void disableChannel(SendChannel channel) {
        switch (channel) {
            case SYSTEM:
                this.systemEnabled = false;
                break;
            case SMS:
                this.smsEnabled = false;
                break;
            case EMAIL:
                this.emailEnabled = false;
                break;
            case WECHAT:
                this.wechatEnabled = false;
                break;
        }
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 判断当前时间是否在免打扰时间内
     * 
     * @return 是否在免打扰时间内
     */
    public boolean isInQuietTime() {
        if (!quietEnabled || quietStartTime == null || quietEndTime == null) {
            return false;
        }
        
        LocalTime now = LocalTime.now();
        
        // 处理跨天的情况
        if (quietStartTime.isAfter(quietEndTime)) {
            return now.isAfter(quietStartTime) || now.isBefore(quietEndTime);
        } else {
            return now.isAfter(quietStartTime) && now.isBefore(quietEndTime);
        }
    }
    
    /**
     * 判断是否可以发送通知（考虑频率限制）
     * 
     * @return 是否可以发送
     */
    public boolean canSendNotification() {
        if (frequencyLimit <= 0) {
            return true;
        }
        
        if (lastNotificationTime == null) {
            return true;
        }
        
        LocalDateTime nextAllowedTime = lastNotificationTime.plusMinutes(frequencyLimit);
        return LocalDateTime.now().isAfter(nextAllowedTime);
    }
    
    /**
     * 判断指定时间是否可以发送通知（考虑频率限制）
     * 
     * @param sendTime 发送时间
     * @return 是否可以发送
     */
    public boolean canSendNotification(LocalDateTime sendTime) {
        if (frequencyLimit <= 0) {
            return true;
        }
        
        if (lastNotificationTime == null) {
            return true;
        }
        
        LocalDateTime nextAllowedTime = lastNotificationTime.plusMinutes(frequencyLimit);
        return sendTime.isAfter(nextAllowedTime);
    }
    
    /**
     * 判断指定时间是否在免打扰时间内
     * 
     * @param time 指定时间
     * @return 是否在免打扰时间内
     */
    public boolean isInDoNotDisturbTime(LocalTime time) {
        if (!quietEnabled || quietStartTime == null || quietEndTime == null) {
            return false;
        }
        
        // 处理跨天的情况
        if (quietStartTime.isAfter(quietEndTime)) {
            return time.isAfter(quietStartTime) || time.isBefore(quietEndTime);
        } else {
            return time.isAfter(quietStartTime) && time.isBefore(quietEndTime);
        }
    }
    
    /**
     * 更新最后通知时间
     */
    public void updateLastNotificationTime() {
        this.lastNotificationTime = LocalDateTime.now();
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 设置免打扰时间
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public void setQuietTime(LocalTime startTime, LocalTime endTime) {
        this.quietStartTime = startTime;
        this.quietEndTime = endTime;
        this.quietEnabled = true;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 清除免打扰时间
     */
    public void clearQuietTime() {
        this.quietStartTime = null;
        this.quietEndTime = null;
        this.quietEnabled = false;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    // ==================== Getter和Setter ====================
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public NotificationType getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
    
    public Boolean getSystemEnabled() {
        return systemEnabled;
    }
    
    public void setSystemEnabled(Boolean systemEnabled) {
        this.systemEnabled = systemEnabled;
    }
    
    public Boolean getSmsEnabled() {
        return smsEnabled;
    }
    
    public void setSmsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }
    
    public Boolean getEmailEnabled() {
        return emailEnabled;
    }
    
    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }
    
    public Boolean getWechatEnabled() {
        return wechatEnabled;
    }
    
    public void setWechatEnabled(Boolean wechatEnabled) {
        this.wechatEnabled = wechatEnabled;
    }
    
    public LocalTime getQuietStartTime() {
        return quietStartTime;
    }
    
    public void setQuietStartTime(LocalTime quietStartTime) {
        this.quietStartTime = quietStartTime;
    }
    
    public LocalTime getQuietEndTime() {
        return quietEndTime;
    }
    
    public void setQuietEndTime(LocalTime quietEndTime) {
        this.quietEndTime = quietEndTime;
    }
    
    public Boolean getQuietEnabled() {
        return quietEnabled;
    }
    
    public void setQuietEnabled(Boolean quietEnabled) {
        this.quietEnabled = quietEnabled;
    }
    
    public Integer getFrequencyLimit() {
        return frequencyLimit;
    }
    
    public void setFrequencyLimit(Integer frequencyLimit) {
        this.frequencyLimit = frequencyLimit;
    }
    
    public LocalDateTime getLastNotificationTime() {
        return lastNotificationTime;
    }
    
    public void setLastNotificationTime(LocalDateTime lastNotificationTime) {
        this.lastNotificationTime = lastNotificationTime;
    }
}