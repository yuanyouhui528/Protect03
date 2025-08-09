package com.leadexchange.service.notification;

import com.leadexchange.domain.notification.Notification;
import com.leadexchange.domain.notification.NotificationSettings;
import com.leadexchange.domain.notification.NotificationType;
import com.leadexchange.domain.notification.SendChannel;
import com.leadexchange.domain.notification.SendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 * 提供通知的创建、发送、查询等业务操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface NotificationService {
    
    /**
     * 创建通知
     * 
     * @param recipientId 接收人ID
     * @param notificationType 通知类型
     * @param sendChannel 发送渠道
     * @param templateCode 模板编码
     * @param templateData 模板数据
     * @param priority 优先级（1-10，数字越大优先级越高）
     * @param expireMinutes 过期时间（分钟）
     * @return 通知记录
     */
    Notification createNotification(Long recipientId, NotificationType notificationType, 
                                  SendChannel sendChannel, String templateCode, 
                                  Map<String, Object> templateData, Integer priority, 
                                  Integer expireMinutes);
    
    /**
     * 创建通知（使用默认参数）
     * 
     * @param recipientId 接收人ID
     * @param notificationType 通知类型
     * @param templateCode 模板编码
     * @param templateData 模板数据
     * @return 通知记录
     */
    Notification createNotification(Long recipientId, NotificationType notificationType, 
                                  String templateCode, Map<String, Object> templateData);
    
    /**
     * 批量创建通知
     * 
     * @param recipientIds 接收人ID列表
     * @param notificationType 通知类型
     * @param sendChannel 发送渠道
     * @param templateCode 模板编码
     * @param templateData 模板数据
     * @param priority 优先级
     * @param expireMinutes 过期时间（分钟）
     * @return 通知记录列表
     */
    List<Notification> createBatchNotifications(List<Long> recipientIds, NotificationType notificationType, 
                                               SendChannel sendChannel, String templateCode, 
                                               Map<String, Object> templateData, Integer priority, 
                                               Integer expireMinutes);
    
    /**
     * 发送通知
     * 
     * @param notificationId 通知ID
     * @return 发送结果
     */
    boolean sendNotification(Long notificationId);
    
    /**
     * 异步发送通知
     * 
     * @param notificationId 通知ID
     */
    void sendNotificationAsync(Long notificationId);
    
    /**
     * 批量发送通知
     * 
     * @param notificationIds 通知ID列表
     * @return 发送结果统计
     */
    Map<String, Integer> sendBatchNotifications(List<Long> notificationIds);
    
    /**
     * 重试发送失败的通知
     * 
     * @param notificationId 通知ID
     * @return 重试结果
     */
    boolean retryNotification(Long notificationId);
    
    /**
     * 取消通知
     * 
     * @param notificationId 通知ID
     * @return 取消结果
     */
    boolean cancelNotification(Long notificationId);
    
    /**
     * 标记通知为已读
     * 
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 标记结果
     */
    boolean markAsRead(Long notificationId, Long userId);
    
    /**
     * 批量标记通知为已读
     * 
     * @param notificationIds 通知ID列表
     * @param userId 用户ID
     * @return 标记数量
     */
    int markBatchAsRead(List<Long> notificationIds, Long userId);
    
    /**
     * 标记用户所有通知为已读
     * 
     * @param userId 用户ID
     * @return 标记数量
     */
    int markAllAsRead(Long userId);
    
    /**
     * 删除通知
     * 
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteNotification(Long notificationId, Long userId);
    
    /**
     * 批量删除通知
     * 
     * @param notificationIds 通知ID列表
     * @param userId 用户ID
     * @return 删除数量
     */
    int deleteBatchNotifications(List<Long> notificationIds, Long userId);
    
    /**
     * 批量标记通知为已读（别名方法）
     * 
     * @param notificationIds 通知ID列表
     * @param userId 用户ID
     * @return 标记数量
     */
    default int batchMarkAsRead(List<Long> notificationIds, Long userId) {
        return markBatchAsRead(notificationIds, userId);
    }
    
    /**
     * 批量删除通知（别名方法）
     * 
     * @param notificationIds 通知ID列表
     * @param userId 用户ID
     * @return 删除数量
     */
    default int batchDeleteNotifications(List<Long> notificationIds, Long userId) {
        return deleteBatchNotifications(notificationIds, userId);
    }
    
    /**
     * 根据ID查询通知
     * 
     * @param notificationId 通知ID
     * @return 通知记录
     */
    Notification getNotificationById(Long notificationId);
    
    /**
     * 根据消息ID查询通知
     * 
     * @param messageId 消息ID
     * @return 通知记录
     */
    Notification getNotificationByMessageId(String messageId);
    
    /**
     * 查询用户通知列表
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> getUserNotifications(Long userId, Pageable pageable);
    
    /**
     * 查询用户未读通知列表
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> getUserUnreadNotifications(Long userId, Pageable pageable);
    
    /**
     * 查询用户指定类型的通知列表
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> getUserNotificationsByType(Long userId, NotificationType notificationType, Pageable pageable);
    
    /**
     * 查询用户通知列表（支持类型和已读状态过滤）
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型（可选）
     * @param isRead 是否已读（可选）
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> getUserNotifications(Long userId, NotificationType notificationType, Boolean isRead, Pageable pageable);
    
    /**
     * 查询用户未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读通知数量
     */
    long getUserUnreadCount(Long userId);
    
    /**
     * 查询用户指定类型的未读通知数量
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @return 未读通知数量
     */
    long getUserUnreadCountByType(Long userId, NotificationType notificationType);
    
    /**
     * 查询待发送的通知列表
     * 
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> getPendingNotifications(Pageable pageable);
    
    /**
     * 查询需要重试的通知列表
     * 
     * @return 通知记录列表
     */
    List<Notification> getRetryableNotifications();
    
    /**
     * 查询过期的通知列表
     * 
     * @return 通知记录列表
     */
    List<Notification> getExpiredNotifications();
    
    /**
     * 处理过期通知
     * 
     * @return 处理数量
     */
    int processExpiredNotifications();
    
    /**
     * 自动重试失败的通知
     * 
     * @return 重试数量
     */
    int autoRetryFailedNotifications();
    
    /**
     * 清理已读的历史通知
     * 
     * @param beforeDays 保留天数
     * @return 清理数量
     */
    int cleanupReadNotifications(int beforeDays);
    
    /**
     * 查询通知统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    Map<String, Object> getNotificationStatistics(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询用户通知统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getNotificationStatistics(Long userId);
    
    /**
     * 获取用户未读通知数量
     * 
     * @param userId 用户ID
     * @return 未读通知数量
     */
    long getUnreadCount(Long userId);
    
    /**
     * 查询用户最近的通知
     * 
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 通知记录列表
     */
    List<Notification> getUserRecentNotifications(Long userId, int limit);
    
    /**
     * 查询高优先级待发送通知
     * 
     * @param priority 优先级阈值
     * @return 通知记录列表
     */
    List<Notification> getHighPriorityPendingNotifications(Integer priority);
    
    /**
     * 检查用户是否可以接收指定类型的通知
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @param sendChannel 发送渠道
     * @return 是否可以接收
     */
    boolean canReceiveNotification(Long userId, NotificationType notificationType, SendChannel sendChannel);
    
    /**
     * 获取用户的有效发送渠道列表
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @return 发送渠道列表
     */
    List<SendChannel> getUserEnabledChannels(Long userId, NotificationType notificationType);
    
    /**
     * 创建并立即发送通知
     * 
     * @param recipientId 接收人ID
     * @param notificationType 通知类型
     * @param sendChannel 发送渠道
     * @param templateCode 模板编码
     * @param templateData 模板数据
     * @param priority 优先级
     * @return 发送结果
     */
    boolean createAndSendNotification(Long recipientId, NotificationType notificationType, 
                                    SendChannel sendChannel, String templateCode, 
                                    Map<String, Object> templateData, Integer priority);
    
    /**
     * 批量创建并发送通知
     * 
     * @param recipientIds 接收人ID列表
     * @param notificationType 通知类型
     * @param sendChannel 发送渠道
     * @param templateCode 模板编码
     * @param templateData 模板数据
     * @param priority 优先级
     * @return 发送结果统计
     */
    Map<String, Integer> createAndSendBatchNotifications(List<Long> recipientIds, NotificationType notificationType, 
                                                        SendChannel sendChannel, String templateCode, 
                                                        Map<String, Object> templateData, Integer priority);
    
    /**
     * 获取用户通知设置
     * 
     * @param userId 用户ID
     * @return 通知设置
     */
    NotificationSettings getNotificationSettings(Long userId);
    
    /**
     * 更新用户通知设置
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @param systemEnabled 系统通知是否启用
     * @param emailEnabled 邮件通知是否启用
     * @param smsEnabled 短信通知是否启用
     * @return 更新后的通知设置
     */
    NotificationSettings updateNotificationSettings(Long userId, NotificationType notificationType, 
                                                   Boolean systemEnabled, Boolean emailEnabled, Boolean smsEnabled);
}