package com.leadexchange.service.notification.impl;

import com.leadexchange.domain.notification.*;
import com.leadexchange.repository.notification.NotificationRepository;
import com.leadexchange.repository.notification.NotificationSettingsRepository;
import com.leadexchange.repository.notification.NotificationSendLogRepository;
import com.leadexchange.service.notification.NotificationService;
import com.leadexchange.service.notification.NotificationTemplateService;
import com.leadexchange.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 * 提供通知的创建、发送、查询等业务操作的具体实现
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationSettingsRepository settingsRepository;
    
    @Autowired
    private NotificationSendLogRepository sendLogRepository;
    
    @Autowired
    private NotificationTemplateService templateService;
    
    @Autowired
    private SmsService smsService;
    
    // 默认配置
    private static final Integer DEFAULT_PRIORITY = 5;
    private static final Integer DEFAULT_EXPIRE_MINUTES = 7 * 24 * 60; // 7天
    private static final Integer DEFAULT_MAX_RETRY_COUNT = 3;
    
    @Override
    public Notification createNotification(Long recipientId, NotificationType notificationType, 
                                         SendChannel sendChannel, String templateCode, 
                                         Map<String, Object> templateData, Integer priority, 
                                         Integer expireMinutes) {
        
        logger.info("创建通知: recipientId={}, type={}, channel={}, template={}", 
                   recipientId, notificationType, sendChannel, templateCode);
        
        // 参数验证
        if (recipientId == null || notificationType == null || sendChannel == null || !StringUtils.hasText(templateCode)) {
            throw new IllegalArgumentException("创建通知的必要参数不能为空");
        }
        
        // 检查用户是否可以接收此类型的通知
        if (!canReceiveNotification(recipientId, notificationType, sendChannel)) {
            logger.info("用户 {} 未启用 {} 渠道的 {} 类型通知", recipientId, sendChannel, notificationType);
            return null;
        }
        
        // 渲染模板
        Map<String, String> renderedContent = templateService.renderTemplate(templateCode, templateData);
        if (renderedContent == null || renderedContent.isEmpty()) {
            logger.error("模板渲染失败: {}", templateCode);
            throw new RuntimeException("模板渲染失败");
        }
        
        // 创建通知记录
        Notification notification = new Notification();
        notification.setMessageId(generateMessageId());
        notification.setRecipientId(recipientId);
        notification.setNotificationType(notificationType);
        notification.setSendChannel(sendChannel);
        notification.setTemplateCode(templateCode);
        notification.setTitle(renderedContent.get("title"));
        notification.setContent(renderedContent.get("content"));
        notification.setPriority(priority != null ? priority : DEFAULT_PRIORITY);
        notification.setMaxRetryCount(DEFAULT_MAX_RETRY_COUNT);
        notification.setStatus(SendStatus.PENDING);
        notification.setIsRead(false);
        
        // 设置过期时间
        if (expireMinutes != null && expireMinutes > 0) {
            notification.setExpireTime(LocalDateTime.now().plusMinutes(expireMinutes));
        } else {
            notification.setExpireTime(LocalDateTime.now().plusMinutes(DEFAULT_EXPIRE_MINUTES));
        }
        
        // 设置额外数据
        if (templateData != null && !templateData.isEmpty()) {
            notification.setExtraData(templateData.toString());
        }
        
        // 保存通知
        notification = notificationRepository.save(notification);
        
        logger.info("通知创建成功: id={}, messageId={}", notification.getId(), notification.getMessageId());
        return notification;
    }
    
    @Override
    public Notification createNotification(Long recipientId, NotificationType notificationType, 
                                         String templateCode, Map<String, Object> templateData) {
        return createNotification(recipientId, notificationType, SendChannel.SYSTEM, 
                                templateCode, templateData, DEFAULT_PRIORITY, DEFAULT_EXPIRE_MINUTES);
    }
    
    @Override
    public List<Notification> createBatchNotifications(List<Long> recipientIds, NotificationType notificationType, 
                                                      SendChannel sendChannel, String templateCode, 
                                                      Map<String, Object> templateData, Integer priority, 
                                                      Integer expireMinutes) {
        
        logger.info("批量创建通知: recipients={}, type={}, channel={}, template={}", 
                   recipientIds.size(), notificationType, sendChannel, templateCode);
        
        List<Notification> notifications = new ArrayList<>();
        
        for (Long recipientId : recipientIds) {
            try {
                Notification notification = createNotification(recipientId, notificationType, 
                                                             sendChannel, templateCode, 
                                                             templateData, priority, expireMinutes);
                if (notification != null) {
                    notifications.add(notification);
                }
            } catch (Exception e) {
                logger.error("为用户 {} 创建通知失败", recipientId, e);
            }
        }
        
        logger.info("批量创建通知完成: 成功={}, 总数={}", notifications.size(), recipientIds.size());
        return notifications;
    }
    
    @Override
    public boolean sendNotification(Long notificationId) {
        logger.info("发送通知: id={}", notificationId);
        
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (!optionalNotification.isPresent()) {
            logger.error("通知不存在: id={}", notificationId);
            return false;
        }
        
        Notification notification = optionalNotification.get();
        
        // 检查通知状态
        if (!notification.canSend()) {
            logger.warn("通知无法发送: id={}, status={}", notificationId, notification.getStatus());
            return false;
        }
        
        // 检查是否过期
        if (notification.isExpired()) {
            logger.warn("通知已过期: id={}", notificationId);
            notification.expire();
            notificationRepository.save(notification);
            return false;
        }
        
        // 开始发送
        notification.startSending();
        notificationRepository.save(notification);
        
        // 创建发送日志
        NotificationSendLog sendLog = new NotificationSendLog();
        sendLog.setNotificationId(notificationId);
        sendLog.setSendChannel(notification.getSendChannel());
        sendLog.setRecipient(getRecipientAddress(notification));
        sendLog.startSending();
        sendLog = sendLogRepository.save(sendLog);
        
        boolean sendResult = false;
        
        try {
            // 根据渠道发送
            switch (notification.getSendChannel()) {
                case SYSTEM:
                    sendResult = sendSystemNotification(notification, sendLog);
                    break;
                case SMS:
                    sendResult = sendSmsNotification(notification, sendLog);
                    break;
                case EMAIL:
                    sendResult = sendEmailNotification(notification, sendLog);
                    break;
                case WECHAT:
                    sendResult = sendWechatNotification(notification, sendLog);
                    break;
                case PUSH:
                    sendResult = sendPushNotification(notification, sendLog);
                    break;
                default:
                    logger.error("不支持的发送渠道: {}", notification.getSendChannel());
                    sendResult = false;
            }
            
            // 更新通知状态
            if (sendResult) {
                notification.sendSuccess();
                sendLog.sendSuccess("发送成功", null);
                logger.info("通知发送成功: id={}", notificationId);
            } else {
                notification.sendFailed();
                sendLog.sendFailed("发送失败", "未知错误");
                logger.error("通知发送失败: id={}", notificationId);
            }
            
        } catch (Exception e) {
            logger.error("通知发送异常: id={}", notificationId, e);
            notification.sendFailed();
            sendLog.sendFailed("发送异常", e.getMessage());
            sendResult = false;
        } finally {
            notificationRepository.save(notification);
            sendLogRepository.save(sendLog);
        }
        
        return sendResult;
    }
    
    @Override
    @Async
    public void sendNotificationAsync(Long notificationId) {
        sendNotification(notificationId);
    }
    
    @Override
    public Map<String, Integer> sendBatchNotifications(List<Long> notificationIds) {
        logger.info("批量发送通知: count={}", notificationIds.size());
        
        Map<String, Integer> result = new HashMap<>();
        result.put("total", notificationIds.size());
        result.put("success", 0);
        result.put("failed", 0);
        
        for (Long notificationId : notificationIds) {
            try {
                boolean sendResult = sendNotification(notificationId);
                if (sendResult) {
                    result.put("success", result.get("success") + 1);
                } else {
                    result.put("failed", result.get("failed") + 1);
                }
            } catch (Exception e) {
                logger.error("批量发送通知失败: id={}", notificationId, e);
                result.put("failed", result.get("failed") + 1);
            }
        }
        
        logger.info("批量发送通知完成: {}", result);
        return result;
    }
    
    @Override
    public boolean retryNotification(Long notificationId) {
        logger.info("重试发送通知: id={}", notificationId);
        
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (!optionalNotification.isPresent()) {
            logger.error("通知不存在: id={}", notificationId);
            return false;
        }
        
        Notification notification = optionalNotification.get();
        
        // 检查是否可以重试
        if (!notification.canRetry()) {
            logger.warn("通知无法重试: id={}, retryCount={}, maxRetryCount={}", 
                       notificationId, notification.getRetryCount(), notification.getMaxRetryCount());
            return false;
        }
        
        // 重置状态并重试
        notification.resetForRetry();
        notificationRepository.save(notification);
        
        return sendNotification(notificationId);
    }
    
    @Override
    public boolean cancelNotification(Long notificationId) {
        logger.info("取消通知: id={}", notificationId);
        
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (!optionalNotification.isPresent()) {
            logger.error("通知不存在: id={}", notificationId);
            return false;
        }
        
        Notification notification = optionalNotification.get();
        notification.cancel();
        notificationRepository.save(notification);
        
        logger.info("通知已取消: id={}", notificationId);
        return true;
    }
    
    @Override
    public boolean markAsRead(Long notificationId, Long userId) {
        logger.debug("标记通知已读: id={}, userId={}", notificationId, userId);
        
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (!optionalNotification.isPresent()) {
            logger.error("通知不存在: id={}", notificationId);
            return false;
        }
        
        Notification notification = optionalNotification.get();
        
        // 验证用户权限
        if (!notification.getRecipientId().equals(userId)) {
            logger.error("用户无权限操作此通知: notificationId={}, userId={}, recipientId={}", 
                        notificationId, userId, notification.getRecipientId());
            return false;
        }
        
        notification.markAsRead();
        notificationRepository.save(notification);
        
        logger.debug("通知已标记为已读: id={}", notificationId);
        return true;
    }
    
    @Override
    public int markBatchAsRead(List<Long> notificationIds, Long userId) {
        logger.info("批量标记通知已读: count={}, userId={}", notificationIds.size(), userId);
        
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = notificationRepository.markAsReadByIds(userId, notificationIds, now, now);
        
        logger.info("批量标记通知已读完成: updated={}, total={}", updatedCount, notificationIds.size());
        return updatedCount;
    }
    
    @Override
    public int markAllAsRead(Long userId) {
        logger.info("标记用户所有通知已读: userId={}", userId);
        
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = notificationRepository.markAllAsRead(userId, now, now);
        
        logger.info("标记用户所有通知已读完成: updated={}, userId={}", updatedCount, userId);
        return updatedCount;
    }
    
    @Override
    public boolean deleteNotification(Long notificationId, Long userId) {
        logger.info("删除通知: id={}, userId={}", notificationId, userId);
        
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (!optionalNotification.isPresent()) {
            logger.error("通知不存在: id={}", notificationId);
            return false;
        }
        
        Notification notification = optionalNotification.get();
        
        // 验证用户权限
        if (!notification.getRecipientId().equals(userId)) {
            logger.error("用户无权限删除此通知: notificationId={}, userId={}, recipientId={}", 
                        notificationId, userId, notification.getRecipientId());
            return false;
        }
        
        // 删除相关的发送日志
        sendLogRepository.deleteByNotificationId(notificationId);
        
        // 删除通知
        notificationRepository.deleteById(notificationId);
        
        logger.info("通知已删除: id={}", notificationId);
        return true;
    }
    
    @Override
    public int deleteBatchNotifications(List<Long> notificationIds, Long userId) {
        logger.info("批量删除通知: count={}, userId={}", notificationIds.size(), userId);
        
        int deletedCount = 0;
        for (Long notificationId : notificationIds) {
            try {
                if (deleteNotification(notificationId, userId)) {
                    deletedCount++;
                }
            } catch (Exception e) {
                logger.error("删除通知失败: id={}", notificationId, e);
            }
        }
        
        logger.info("批量删除通知完成: deleted={}, total={}", deletedCount, notificationIds.size());
        return deletedCount;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationByMessageId(String messageId) {
        return notificationRepository.findByMessageId(messageId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreateTimeDesc(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getUserUnreadNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByRecipientIdAndIsReadOrderByCreateTimeDesc(userId, false, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotificationsByType(Long userId, NotificationType notificationType, Pageable pageable) {
        return notificationRepository.findByRecipientIdAndNotificationTypeOrderByCreateTimeDesc(userId, notificationType, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(Long userId, NotificationType notificationType, Boolean isRead, Pageable pageable) {
        if (notificationType != null && isRead != null) {
            return notificationRepository.findByRecipientIdAndNotificationTypeAndIsReadOrderByCreateTimeDesc(userId, notificationType, isRead, pageable);
        } else if (notificationType != null) {
            return notificationRepository.findByRecipientIdAndNotificationTypeOrderByCreateTimeDesc(userId, notificationType, pageable);
        } else if (isRead != null) {
            return notificationRepository.findByRecipientIdAndIsReadOrderByCreateTimeDesc(userId, isRead, pageable);
        } else {
            return notificationRepository.findByRecipientIdOrderByCreateTimeDesc(userId, pageable);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUserUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsRead(userId, false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUserUnreadCountByType(Long userId, NotificationType notificationType) {
        return notificationRepository.countByRecipientIdAndNotificationTypeAndIsRead(userId, notificationType, false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getPendingNotifications(Pageable pageable) {
        return notificationRepository.findByStatusOrderByPriorityDescCreateTimeAsc(SendStatus.PENDING, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getRetryableNotifications() {
        return notificationRepository.findRetryableNotifications(LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getExpiredNotifications() {
        return notificationRepository.findExpiredNotifications(LocalDateTime.now());
    }
    
    @Override
    public int processExpiredNotifications() {
        logger.info("处理过期通知");
        
        List<Notification> expiredNotifications = getExpiredNotifications();
        if (expiredNotifications.isEmpty()) {
            logger.info("没有过期的通知");
            return 0;
        }
        
        List<Long> expiredIds = expiredNotifications.stream()
                .map(Notification::getId)
                .collect(Collectors.toList());
        
        int updatedCount = notificationRepository.markAsExpired(expiredIds, LocalDateTime.now());
        
        logger.info("处理过期通知完成: count={}", updatedCount);
        return updatedCount;
    }
    
    @Override
    public int autoRetryFailedNotifications() {
        logger.info("自动重试失败的通知");
        
        List<Notification> retryableNotifications = getRetryableNotifications();
        if (retryableNotifications.isEmpty()) {
            logger.info("没有需要重试的通知");
            return 0;
        }
        
        int retryCount = 0;
        for (Notification notification : retryableNotifications) {
            try {
                if (retryNotification(notification.getId())) {
                    retryCount++;
                }
            } catch (Exception e) {
                logger.error("自动重试通知失败: id={}", notification.getId(), e);
            }
        }
        
        logger.info("自动重试失败通知完成: retried={}, total={}", retryCount, retryableNotifications.size());
        return retryCount;
    }
    
    @Override
    public int cleanupReadNotifications(int beforeDays) {
        logger.info("清理已读历史通知: beforeDays={}", beforeDays);
        
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(beforeDays);
        int deletedCount = notificationRepository.deleteReadNotificationsBefore(beforeTime);
        
        logger.info("清理已读历史通知完成: deleted={}", deletedCount);
        return deletedCount;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总数统计
        long totalCount = notificationRepository.countByCreateTimeBetween(startTime, endTime);
        statistics.put("totalCount", totalCount);
        
        // 状态统计
        for (SendStatus status : SendStatus.values()) {
            long count = notificationRepository.countByStatus(status);
            statistics.put(status.name().toLowerCase() + "Count", count);
        }
        
        // 渠道统计
        for (SendChannel channel : SendChannel.values()) {
            long count = notificationRepository.countBySendChannel(channel);
            statistics.put(channel.name().toLowerCase() + "Count", count);
        }
        
        // 类型统计
        for (NotificationType type : NotificationType.values()) {
            long count = notificationRepository.countByNotificationType(type);
            statistics.put(type.name().toLowerCase() + "Count", count);
        }
        
        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsRead(userId, false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取用户所有通知
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreateTimeDesc(userId);
        
        // 统计各种类型的通知数量
        Map<NotificationType, Long> typeCount = notifications.stream()
            .collect(Collectors.groupingBy(Notification::getNotificationType, Collectors.counting()));
        
        // 统计已读未读数量
        long readCount = notifications.stream().mapToLong(n -> n.getIsRead() ? 1 : 0).sum();
        long unreadCount = notifications.size() - readCount;
        
        // 统计最近7天的通知数量
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long recentCount = notifications.stream()
            .mapToLong(n -> n.getCreateTime().isAfter(sevenDaysAgo) ? 1 : 0)
            .sum();
        
        statistics.put("totalCount", notifications.size());
        statistics.put("readCount", readCount);
        statistics.put("unreadCount", unreadCount);
        statistics.put("recentCount", recentCount);
        statistics.put("typeStatistics", typeCount);
        statistics.put("userId", userId);
        
        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUserRecentNotifications(Long userId, int limit) {
        return notificationRepository.findRecentNotifications(userId, limit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getHighPriorityPendingNotifications(Integer priority) {
        return notificationRepository.findHighPriorityPendingNotifications(priority);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canReceiveNotification(Long userId, NotificationType notificationType, SendChannel sendChannel) {
        // 查询用户的通知设置
        Optional<NotificationSettings> settingsOpt = settingsRepository.findByUserIdAndNotificationType(userId, notificationType);
        
        if (!settingsOpt.isPresent()) {
            // 如果没有设置，使用默认设置（系统通知默认开启）
            return sendChannel == SendChannel.SYSTEM;
        }
        
        NotificationSettings settings = settingsOpt.get();
        
        // 检查渠道是否启用
        if (!settings.isChannelEnabled(sendChannel)) {
            return false;
        }
        
        // 检查是否在免打扰时间内
        if (settings.isInDoNotDisturbTime(LocalTime.now())) {
            return false;
        }
        
        // 检查频率限制
        if (!settings.canSendNotification(LocalDateTime.now())) {
            return false;
        }
        
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SendChannel> getUserEnabledChannels(Long userId, NotificationType notificationType) {
        List<SendChannel> enabledChannels = new ArrayList<>();
        
        for (SendChannel channel : SendChannel.values()) {
            if (canReceiveNotification(userId, notificationType, channel)) {
                enabledChannels.add(channel);
            }
        }
        
        return enabledChannels;
    }
    
    @Override
    public boolean createAndSendNotification(Long recipientId, NotificationType notificationType, 
                                           SendChannel sendChannel, String templateCode, 
                                           Map<String, Object> templateData, Integer priority) {
        
        Notification notification = createNotification(recipientId, notificationType, 
                                                     sendChannel, templateCode, 
                                                     templateData, priority, null);
        
        if (notification == null) {
            return false;
        }
        
        return sendNotification(notification.getId());
    }
    
    @Override
    public Map<String, Integer> createAndSendBatchNotifications(List<Long> recipientIds, NotificationType notificationType, 
                                                              SendChannel sendChannel, String templateCode, 
                                                              Map<String, Object> templateData, Integer priority) {
        
        List<Notification> notifications = createBatchNotifications(recipientIds, notificationType, 
                                                                   sendChannel, templateCode, 
                                                                   templateData, priority, null);
        
        List<Long> notificationIds = notifications.stream()
                .map(Notification::getId)
                .collect(Collectors.toList());
        
        return sendBatchNotifications(notificationIds);
    }
    
    // 私有辅助方法
    
    /**
     * 生成消息ID
     */
    private String generateMessageId() {
        return "MSG_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 获取接收方地址
     */
    private String getRecipientAddress(Notification notification) {
        // 这里应该根据用户ID和发送渠道获取具体的接收地址
        // 例如：手机号、邮箱地址、微信openid等
        // 暂时返回用户ID作为占位符
        return "user_" + notification.getRecipientId();
    }
    
    /**
     * 发送系统通知
     */
    private boolean sendSystemNotification(Notification notification, NotificationSendLog sendLog) {
        // 系统通知直接保存到数据库即可，无需外部接口
        logger.debug("发送系统通知: id={}", notification.getId());
        return true;
    }
    
    /**
     * 发送短信通知
     */
    private boolean sendSmsNotification(Notification notification, NotificationSendLog sendLog) {
        try {
            logger.debug("发送短信通知: id={}", notification.getId());
            
            // 获取用户手机号（这里需要调用用户服务）
            String phoneNumber = getRecipientAddress(notification);
            
            // 调用短信服务发送
            boolean result = smsService.sendSms(phoneNumber, notification.getContent());
            
            if (result) {
                sendLog.setProviderInfo("SMS_PROVIDER");
                sendLog.setResponseCode("200");
                sendLog.setResponseMessage("发送成功");
            } else {
                sendLog.setResponseCode("500");
                sendLog.setResponseMessage("发送失败");
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("发送短信通知异常: id={}", notification.getId(), e);
            sendLog.setResponseCode("500");
            sendLog.setResponseMessage("发送异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 发送邮件通知
     */
    private boolean sendEmailNotification(Notification notification, NotificationSendLog sendLog) {
        // TODO: 实现邮件发送逻辑
        logger.debug("发送邮件通知: id={}", notification.getId());
        return true;
    }
    
    /**
     * 发送微信通知
     */
    private boolean sendWechatNotification(Notification notification, NotificationSendLog sendLog) {
        // TODO: 实现微信发送逻辑
        logger.debug("发送微信通知: id={}", notification.getId());
        return true;
    }
    
    /**
     * 发送推送通知
     */
    private boolean sendPushNotification(Notification notification, NotificationSendLog sendLog) {
        // TODO: 实现推送发送逻辑
        logger.debug("发送推送通知: id={}", notification.getId());
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public NotificationSettings getNotificationSettings(Long userId) {
        logger.info("获取用户通知设置: userId={}", userId);
        
        // 查询用户的通知设置，如果不存在则创建默认设置
        Optional<NotificationSettings> settingsOpt = settingsRepository.findByUserId(userId);
        
        if (settingsOpt.isPresent()) {
            return settingsOpt.get();
        }
        
        // 创建默认设置
        NotificationSettings defaultSettings = new NotificationSettings();
        defaultSettings.setUserId(userId);
        defaultSettings.setNotificationType(NotificationType.SYSTEM);
        defaultSettings.setSystemEnabled(true);
        defaultSettings.setEmailEnabled(false);
        defaultSettings.setSmsEnabled(false);
        defaultSettings.setWechatEnabled(false);
        defaultSettings.setPushEnabled(true);
        defaultSettings.setDoNotDisturbEnabled(false);
        defaultSettings.setFrequencyLimit(100); // 默认每天最多100条
        defaultSettings.setCreatedAt(LocalDateTime.now());
        defaultSettings.setUpdatedAt(LocalDateTime.now());
        
        return settingsRepository.save(defaultSettings);
    }
    
    @Override
    @Transactional
    public NotificationSettings updateNotificationSettings(Long userId, NotificationType notificationType, 
                                                          Boolean systemEnabled, Boolean emailEnabled, Boolean smsEnabled) {
        logger.info("更新用户通知设置: userId={}, type={}, system={}, email={}, sms={}", 
                   userId, notificationType, systemEnabled, emailEnabled, smsEnabled);
        
        // 查询现有设置
        Optional<NotificationSettings> settingsOpt = settingsRepository.findByUserIdAndNotificationType(userId, notificationType);
        
        NotificationSettings settings;
        if (settingsOpt.isPresent()) {
            settings = settingsOpt.get();
        } else {
            // 创建新设置
            settings = new NotificationSettings();
            settings.setUserId(userId);
            settings.setNotificationType(notificationType);
            settings.setDoNotDisturbEnabled(false);
            settings.setFrequencyLimit(100);
            settings.setCreatedAt(LocalDateTime.now());
        }
        
        // 更新设置
        if (systemEnabled != null) {
            settings.setSystemEnabled(systemEnabled);
        }
        if (emailEnabled != null) {
            settings.setEmailEnabled(emailEnabled);
        }
        if (smsEnabled != null) {
            settings.setSmsEnabled(smsEnabled);
        }
        
        settings.setUpdatedAt(LocalDateTime.now());
        
        return settingsRepository.save(settings);
    }
}