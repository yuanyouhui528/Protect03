package com.leadexchange.repository.notification;

import com.leadexchange.domain.notification.NotificationSettings;
import com.leadexchange.domain.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 通知设置数据访问接口
 * 提供用户通知设置的数据库操作方法
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    
    /**
     * 根据用户ID查询通知设置
     * 
     * @param userId 用户ID
     * @return 通知设置
     */
    Optional<NotificationSettings> findByUserId(Long userId);
    
    /**
     * 根据用户ID和通知类型查询通知设置
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @return 通知设置
     */
    Optional<NotificationSettings> findByUserIdAndNotificationType(Long userId, NotificationType notificationType);
    
    /**
     * 根据用户ID查询所有通知设置
     * 
     * @param userId 用户ID
     * @return 通知设置列表
     */
    List<NotificationSettings> findAllByUserId(Long userId);
    
    /**
     * 根据通知类型查询所有用户的设置
     * 
     * @param notificationType 通知类型
     * @return 通知设置列表
     */
    List<NotificationSettings> findAllByNotificationType(NotificationType notificationType);
    
    /**
     * 查询启用了系统通知的用户设置
     * 
     * @param notificationType 通知类型
     * @return 通知设置列表
     */
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.notificationType = :notificationType AND ns.systemEnabled = true")
    List<NotificationSettings> findEnabledSystemNotifications(@Param("notificationType") NotificationType notificationType);
    
    /**
     * 查询启用了短信通知的用户设置
     * 
     * @param notificationType 通知类型
     * @return 通知设置列表
     */
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.notificationType = :notificationType AND ns.smsEnabled = true")
    List<NotificationSettings> findEnabledSmsNotifications(@Param("notificationType") NotificationType notificationType);
    
    /**
     * 查询启用了邮件通知的用户设置
     * 
     * @param notificationType 通知类型
     * @return 通知设置列表
     */
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.notificationType = :notificationType AND ns.emailEnabled = true")
    List<NotificationSettings> findEnabledEmailNotifications(@Param("notificationType") NotificationType notificationType);
    
    /**
     * 查询启用了微信通知的用户设置
     * 
     * @param notificationType 通知类型
     * @return 通知设置列表
     */
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.notificationType = :notificationType AND ns.wechatEnabled = true")
    List<NotificationSettings> findEnabledWechatNotifications(@Param("notificationType") NotificationType notificationType);
    
    /**
     * 查询当前不在免打扰时间的用户设置
     * 
     * @return 通知设置列表
     */
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.quietEnabled = false OR (ns.quietStartTime IS NULL OR ns.quietEndTime IS NULL)")
    List<NotificationSettings> findNotInDoNotDisturbTime();
    
    /**
     * 查找可以发送通知的设置（考虑频率限制）
     * @param notificationType 通知类型
     * @return 通知设置列表
     */
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.notificationType = :notificationType")
    List<NotificationSettings> findCanSendNotifications(@Param("notificationType") NotificationType notificationType);
    
    /**
     * 批量更新最后通知时间
     * 
     * @param userIds 用户ID列表
     * @param notificationType 通知类型
     * @param lastNotificationTime 最后通知时间
     * @param updateTime 更新时间
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE NotificationSettings ns SET ns.lastNotificationTime = :lastNotificationTime, ns.updateTime = :updateTime WHERE ns.userId IN :userIds AND ns.notificationType = :notificationType")
    int updateLastNotificationTime(@Param("userIds") List<Long> userIds, @Param("notificationType") NotificationType notificationType, @Param("lastNotificationTime") LocalDateTime lastNotificationTime, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 批量启用指定渠道的通知
     * 
     * @param userIds 用户ID列表
     * @param notificationType 通知类型
     * @param channel 渠道名称（system/sms/email/wechat）
     * @param updateTime 更新时间
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE NotificationSettings ns SET " +
           "ns.systemEnabled = CASE WHEN :channel = 'system' THEN true ELSE ns.systemEnabled END, " +
           "ns.smsEnabled = CASE WHEN :channel = 'sms' THEN true ELSE ns.smsEnabled END, " +
           "ns.emailEnabled = CASE WHEN :channel = 'email' THEN true ELSE ns.emailEnabled END, " +
           "ns.wechatEnabled = CASE WHEN :channel = 'wechat' THEN true ELSE ns.wechatEnabled END, " +
           "ns.updateTime = :updateTime " +
           "WHERE ns.userId IN :userIds AND ns.notificationType = :notificationType")
    int enableChannelForUsers(@Param("userIds") List<Long> userIds, @Param("notificationType") NotificationType notificationType, @Param("channel") String channel, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 批量禁用指定渠道的通知
     * 
     * @param userIds 用户ID列表
     * @param notificationType 通知类型
     * @param channel 渠道名称（system/sms/email/wechat）
     * @param updateTime 更新时间
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE NotificationSettings ns SET " +
           "ns.systemEnabled = CASE WHEN :channel = 'system' THEN false ELSE ns.systemEnabled END, " +
           "ns.smsEnabled = CASE WHEN :channel = 'sms' THEN false ELSE ns.smsEnabled END, " +
           "ns.emailEnabled = CASE WHEN :channel = 'email' THEN false ELSE ns.emailEnabled END, " +
           "ns.wechatEnabled = CASE WHEN :channel = 'wechat' THEN false ELSE ns.wechatEnabled END, " +
           "ns.updateTime = :updateTime " +
           "WHERE ns.userId IN :userIds AND ns.notificationType = :notificationType")
    int disableChannelForUsers(@Param("userIds") List<Long> userIds, @Param("notificationType") NotificationType notificationType, @Param("channel") String channel, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 统计启用了指定渠道的用户数量
     * 
     * @param notificationType 通知类型
     * @param channel 渠道名称
     * @return 用户数量
     */
    @Query("SELECT COUNT(ns) FROM NotificationSettings ns WHERE ns.notificationType = :notificationType AND " +
           "((:channel = 'system' AND ns.systemEnabled = true) OR " +
           "(:channel = 'sms' AND ns.smsEnabled = true) OR " +
           "(:channel = 'email' AND ns.emailEnabled = true) OR " +
           "(:channel = 'wechat' AND ns.wechatEnabled = true))")
    long countEnabledChannelUsers(@Param("notificationType") NotificationType notificationType, @Param("channel") String channel);
    
    /**
     * 检查用户是否存在指定类型的通知设置
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @return 是否存在
     */
    boolean existsByUserIdAndNotificationType(Long userId, NotificationType notificationType);
    
    /**
     * 删除用户的所有通知设置
     * 
     * @param userId 用户ID
     * @return 删除数量
     */
    @Modifying
    @Query("DELETE FROM NotificationSettings ns WHERE ns.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的默认通知设置
     * 
     * @param userId 用户ID
     * @return 通知设置列表
     */
    @Query("SELECT ns FROM NotificationSettings ns WHERE ns.userId = :userId ORDER BY ns.notificationType")
    List<NotificationSettings> findUserDefaultSettings(@Param("userId") Long userId);
    
    /**
     * 统计用户的通知设置数量
     * 
     * @param userId 用户ID
     * @return 设置数量
     */
    long countByUserId(Long userId);
}