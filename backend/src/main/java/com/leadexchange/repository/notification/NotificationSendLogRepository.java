package com.leadexchange.repository.notification;

import com.leadexchange.domain.notification.NotificationSendLog;
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

/**
 * 通知发送日志数据访问接口
 * 提供通知发送日志的数据库操作方法
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface NotificationSendLogRepository extends JpaRepository<NotificationSendLog, Long> {
    
    /**
     * 根据通知ID查询发送日志列表
     * 
     * @param notificationId 通知ID
     * @return 发送日志列表
     */
    List<NotificationSendLog> findByNotificationIdOrderByCreateTimeDesc(Long notificationId);
    
    /**
     * 根据通知ID和发送渠道查询发送日志
     * 
     * @param notificationId 通知ID
     * @param sendChannel 发送渠道
     * @return 发送日志列表
     */
    List<NotificationSendLog> findByNotificationIdAndSendChannelOrderByCreateTimeDesc(Long notificationId, SendChannel sendChannel);
    
    /**
     * 根据发送状态查询发送日志（分页）
     * 
     * @param sendStatus 发送状态
     * @param pageable 分页参数
     * @return 发送日志分页
     */
    Page<NotificationSendLog> findBySendStatusOrderByCreateTimeDesc(SendStatus sendStatus, Pageable pageable);
    
    /**
     * 根据发送渠道查询发送日志（分页）
     * 
     * @param sendChannel 发送渠道
     * @param pageable 分页参数
     * @return 发送日志分页
     */
    Page<NotificationSendLog> findBySendChannelOrderByCreateTimeDesc(SendChannel sendChannel, Pageable pageable);
    
    /**
     * 根据接收方查询发送日志（分页）
     * 
     * @param recipient 接收方
     * @param pageable 分页参数
     * @return 发送日志分页
     */
    Page<NotificationSendLog> findByRecipientOrderByCreateTimeDesc(String recipient, Pageable pageable);
    
    /**
     * 根据时间范围查询发送日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 发送日志分页
     */
    @Query("SELECT nsl FROM NotificationSendLog nsl WHERE nsl.createTime BETWEEN :startTime AND :endTime ORDER BY nsl.createTime DESC")
    Page<NotificationSendLog> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);
    
    /**
     * 查询发送失败的日志（可重试）
     * 
     * @param maxRetryCount 最大重试次数
     * @return 发送日志列表
     */
    @Query("SELECT nsl FROM NotificationSendLog nsl WHERE nsl.sendStatus = 'FAILED' AND nsl.retryCount < :maxRetryCount ORDER BY nsl.createTime ASC")
    List<NotificationSendLog> findRetryableLogs(@Param("maxRetryCount") Integer maxRetryCount);
    
    /**
     * 查询响应时间超过阈值的日志
     * 
     * @param responseTimeThreshold 响应时间阈值（毫秒）
     * @param pageable 分页参数
     * @return 发送日志分页
     */
    @Query("SELECT nsl FROM NotificationSendLog nsl WHERE nsl.responseTime > :responseTimeThreshold ORDER BY nsl.responseTime DESC")
    Page<NotificationSendLog> findSlowLogs(@Param("responseTimeThreshold") Long responseTimeThreshold, Pageable pageable);
    
    /**
     * 统计指定时间范围内的发送成功率
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param sendChannel 发送渠道（可选）
     * @return 成功率统计结果
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN nsl.sendStatus = 'SUCCESS' THEN 1 END) as successCount, " +
           "COUNT(CASE WHEN nsl.sendStatus = 'FAILED' THEN 1 END) as failedCount, " +
           "COUNT(*) as totalCount " +
           "FROM NotificationSendLog nsl " +
           "WHERE nsl.createTime BETWEEN :startTime AND :endTime " +
           "AND (:sendChannel IS NULL OR nsl.sendChannel = :sendChannel)")
    Object[] getSuccessRateStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("sendChannel") SendChannel sendChannel);
    
    /**
     * 统计各渠道的发送数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 渠道统计结果
     */
    @Query("SELECT nsl.sendChannel, COUNT(*) FROM NotificationSendLog nsl WHERE nsl.createTime BETWEEN :startTime AND :endTime GROUP BY nsl.sendChannel")
    List<Object[]> getChannelStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计各状态的发送数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 状态统计结果
     */
    @Query("SELECT nsl.sendStatus, COUNT(*) FROM NotificationSendLog nsl WHERE nsl.createTime BETWEEN :startTime AND :endTime GROUP BY nsl.sendStatus")
    List<Object[]> getStatusStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计平均响应时间
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param sendChannel 发送渠道（可选）
     * @return 平均响应时间（毫秒）
     */
    @Query("SELECT AVG(nsl.responseTime) FROM NotificationSendLog nsl WHERE nsl.createTime BETWEEN :startTime AND :endTime AND (:sendChannel IS NULL OR nsl.sendChannel = :sendChannel) AND nsl.responseTime IS NOT NULL")
    Double getAverageResponseTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("sendChannel") SendChannel sendChannel);
    
    /**
     * 查询指定服务商的发送日志
     * 
     * @param provider 服务商
     * @param pageable 分页参数
     * @return 发送日志分页
     */
    Page<NotificationSendLog> findByProviderOrderByCreateTimeDesc(String provider, Pageable pageable);
    
    /**
     * 统计各服务商的发送数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 服务商统计结果
     */
    @Query("SELECT nsl.provider, COUNT(*) FROM NotificationSendLog nsl WHERE nsl.createTime BETWEEN :startTime AND :endTime AND nsl.provider IS NOT NULL GROUP BY nsl.provider")
    List<Object[]> getProviderStatistics(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询错误信息包含指定关键词的日志
     * 
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 发送日志分页
     */
    @Query("SELECT nsl FROM NotificationSendLog nsl WHERE nsl.errorMessage LIKE %:keyword% ORDER BY nsl.createTime DESC")
    Page<NotificationSendLog> findByErrorMessageContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 删除指定时间之前的日志
     * 
     * @param beforeTime 时间阈值
     * @return 删除数量
     */
    @Modifying
    @Query("DELETE FROM NotificationSendLog nsl WHERE nsl.createTime < :beforeTime")
    int deleteLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 删除指定通知的所有日志
     * 
     * @param notificationId 通知ID
     * @return 删除数量
     */
    @Modifying
    @Query("DELETE FROM NotificationSendLog nsl WHERE nsl.notificationId = :notificationId")
    int deleteByNotificationId(@Param("notificationId") Long notificationId);
    
    /**
     * 统计指定通知的发送次数
     * 
     * @param notificationId 通知ID
     * @return 发送次数
     */
    long countByNotificationId(Long notificationId);
    
    /**
     * 统计指定通知的成功发送次数
     * 
     * @param notificationId 通知ID
     * @return 成功发送次数
     */
    long countByNotificationIdAndSendStatus(Long notificationId, SendStatus sendStatus);
    
    /**
     * 查询最近的发送日志
     * 
     * @param limit 数量限制
     * @return 发送日志列表
     */
    @Query(value = "SELECT * FROM notification_send_logs ORDER BY create_time DESC LIMIT :limit", nativeQuery = true)
    List<NotificationSendLog> findRecentLogs(@Param("limit") int limit);
    
    /**
     * 查询指定时间范围内发送失败的日志数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 失败日志数量
     */
    @Query("SELECT COUNT(nsl) FROM NotificationSendLog nsl WHERE nsl.createTime BETWEEN :startTime AND :endTime AND nsl.sendStatus = 'FAILED'")
    long countFailedLogsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询指定时间范围内发送成功的日志数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成功日志数量
     */
    @Query("SELECT COUNT(nsl) FROM NotificationSendLog nsl WHERE nsl.createTime BETWEEN :startTime AND :endTime AND nsl.sendStatus = 'SUCCESS'")
    long countSuccessLogsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}