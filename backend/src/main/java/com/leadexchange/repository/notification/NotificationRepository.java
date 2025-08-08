package com.leadexchange.repository.notification;

import com.leadexchange.domain.notification.Notification;
import com.leadexchange.domain.notification.NotificationType;
import com.leadexchange.domain.notification.SendChannel;
import com.leadexchange.domain.notification.SendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 通知记录数据访问接口
 * 提供通知记录的数据库操作方法
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * 根据消息ID查询通知
     * 
     * @param messageId 消息ID
     * @return 通知记录
     */
    Optional<Notification> findByMessageId(String messageId);
    
    /**
     * 根据接收人ID查询通知列表（按创建时间倒序）
     * 
     * @param recipientId 接收人ID
     * @param pageable 分页参数
     * @return 通知列表
     */
    Page<Notification> findByRecipientIdOrderByCreateTimeDesc(Long recipientId, Pageable pageable);
    
    /**
     * 根据接收人ID查询所有通知列表（按创建时间倒序）
     * 
     * @param recipientId 接收人ID
     * @return 通知列表
     */
    List<Notification> findByRecipientIdOrderByCreateTimeDesc(Long recipientId);
    
    /**
     * 根据接收人ID和已读状态查询通知列表
     * 
     * @param recipientId 接收人ID
     * @param isRead 是否已读
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> findByRecipientIdAndIsReadOrderByCreateTimeDesc(Long recipientId, Boolean isRead, Pageable pageable);
    
    /**
     * 根据接收人ID和通知类型查询通知列表
     * 
     * @param recipientId 接收人ID
     * @param notificationType 通知类型
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> findByRecipientIdAndNotificationTypeOrderByCreateTimeDesc(Long recipientId, NotificationType notificationType, Pageable pageable);
    
    /**
     * 根据接收人ID、通知类型和已读状态查询通知列表
     * 
     * @param recipientId 接收人ID
     * @param notificationType 通知类型
     * @param isRead 是否已读
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> findByRecipientIdAndNotificationTypeAndIsReadOrderByCreateTimeDesc(Long recipientId, NotificationType notificationType, Boolean isRead, Pageable pageable);
    
    /**
     * 查询用户未读通知数量
     * 
     * @param recipientId 接收人ID
     * @return 未读通知数量
     */
    long countByRecipientIdAndIsRead(Long recipientId, Boolean isRead);
    
    /**
     * 查询用户指定类型的未读通知数量
     * 
     * @param recipientId 接收人ID
     * @param notificationType 通知类型
     * @return 未读通知数量
     */
    long countByRecipientIdAndNotificationTypeAndIsRead(Long recipientId, NotificationType notificationType, Boolean isRead);
    
    /**
     * 查询待发送的通知列表
     * 
     * @param status 发送状态
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> findByStatusOrderByPriorityDescCreateTimeAsc(SendStatus status, Pageable pageable);
    
    /**
     * 查询需要重试的通知列表
     * 
     * @return 通知记录列表
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < n.maxRetryCount AND (n.expireTime IS NULL OR n.expireTime > :now) ORDER BY n.priority DESC, n.createTime ASC")
    List<Notification> findRetryableNotifications(@Param("now") LocalDateTime now);
    
    /**
     * 查询过期的通知列表
     * 
     * @param now 当前时间
     * @return 通知记录列表
     */
    @Query("SELECT n FROM Notification n WHERE n.expireTime IS NOT NULL AND n.expireTime <= :now AND n.status IN ('PENDING', 'SENDING', 'FAILED')")
    List<Notification> findExpiredNotifications(@Param("now") LocalDateTime now);
    
    /**
     * 根据发送渠道查询通知列表
     * 
     * @param sendChannel 发送渠道
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> findBySendChannelOrderByCreateTimeDesc(SendChannel sendChannel, Pageable pageable);
    
    /**
     * 根据发送状态查询通知列表
     * 
     * @param status 发送状态
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    Page<Notification> findByStatusOrderByCreateTimeDesc(SendStatus status, Pageable pageable);
    
    /**
     * 根据时间范围查询通知列表
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 通知记录分页
     */
    @Query("SELECT n FROM Notification n WHERE n.createTime BETWEEN :startTime AND :endTime ORDER BY n.createTime DESC")
    Page<Notification> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);
    
    /**
     * 批量标记通知为已读
     * 
     * @param recipientId 接收人ID
     * @param notificationIds 通知ID列表
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readTime = :readTime, n.updateTime = :updateTime WHERE n.recipientId = :recipientId AND n.id IN :notificationIds")
    int markAsReadByIds(@Param("recipientId") Long recipientId, @Param("notificationIds") List<Long> notificationIds, @Param("readTime") LocalDateTime readTime, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 批量标记用户所有通知为已读
     * 
     * @param recipientId 接收人ID
     * @param readTime 阅读时间
     * @param updateTime 更新时间
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readTime = :readTime, n.updateTime = :updateTime WHERE n.recipientId = :recipientId AND n.isRead = false")
    int markAllAsRead(@Param("recipientId") Long recipientId, @Param("readTime") LocalDateTime readTime, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 批量更新通知状态为过期
     * 
     * @param notificationIds 通知ID列表
     * @param updateTime 更新时间
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'EXPIRED', n.updateTime = :updateTime WHERE n.id IN :notificationIds")
    int markAsExpired(@Param("notificationIds") List<Long> notificationIds, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 删除指定时间之前的已读通知
     * 
     * @param beforeTime 时间阈值
     * @return 删除数量
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.readTime < :beforeTime")
    int deleteReadNotificationsBefore(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 统计指定时间范围内的通知数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 通知数量
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.createTime BETWEEN :startTime AND :endTime")
    long countByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定状态的通知数量
     * 
     * @param status 发送状态
     * @return 通知数量
     */
    long countByStatus(SendStatus status);
    
    /**
     * 统计指定渠道的通知数量
     * 
     * @param sendChannel 发送渠道
     * @return 通知数量
     */
    long countBySendChannel(SendChannel sendChannel);
    
    /**
     * 统计指定类型的通知数量
     * 
     * @param notificationType 通知类型
     * @return 通知数量
     */
    long countByNotificationType(NotificationType notificationType);
    
    /**
     * 查询用户最近的通知
     * 
     * @param recipientId 接收人ID
     * @param limit 数量限制
     * @return 通知记录列表
     */
    @Query(value = "SELECT * FROM notifications WHERE recipient_id = :recipientId ORDER BY create_time DESC LIMIT :limit", nativeQuery = true)
    List<Notification> findRecentNotifications(@Param("recipientId") Long recipientId, @Param("limit") int limit);
    
    /**
     * 查询高优先级未发送通知
     * 
     * @param priority 优先级阈值
     * @return 通知记录列表
     */
    @Query("SELECT n FROM Notification n WHERE n.priority >= :priority AND n.status = 'PENDING' ORDER BY n.priority DESC, n.createTime ASC")
    List<Notification> findHighPriorityPendingNotifications(@Param("priority") Integer priority);
}