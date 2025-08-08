package com.leadexchange.event.handler;

import com.leadexchange.domain.notification.NotificationType;
import com.leadexchange.domain.notification.SendChannel;
import com.leadexchange.event.exchange.ExchangeApplicationEvent;
import com.leadexchange.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知事件处理器
 * 监听各种业务事件并发送相应的通知
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Component
public class NotificationEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationEventHandler.class);
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 处理交换申请事件
     * 
     * @param event 交换申请事件
     */
    @EventListener
    @Async
    public void handleExchangeApplicationEvent(ExchangeApplicationEvent event) {
        try {
            logger.info("处理交换申请事件: {}", event.getEventType());
            
            // 根据事件类型发送不同的通知
            switch (event.getEventType()) {
                case APPLICATION_SUBMITTED:
                    handleApplicationSubmitted(event);
                    break;
                case APPLICATION_APPROVED:
                    handleApplicationApproved(event);
                    break;
                case APPLICATION_REJECTED:
                    handleApplicationRejected(event);
                    break;
                case APPLICATION_CANCELLED:
                    handleApplicationCancelled(event);
                    break;
                case EXCHANGE_COMPLETED:
                    handleExchangeCompleted(event);
                    break;
                case EXCHANGE_EXPIRED:
                    handleExchangeExpired(event);
                    break;
                default:
                    logger.warn("未知的交换事件类型: {}", event.getEventType());
            }
            
        } catch (Exception e) {
            logger.error("处理交换申请事件失败: {}", event.getEventType(), e);
        }
    }
    
    /**
     * 处理申请提交事件
     * 通知目标用户有新的交换申请
     * 
     * @param event 事件信息
     */
    private void handleApplicationSubmitted(ExchangeApplicationEvent event) {
        Long targetUserId = event.getTargetUserId();
        if (targetUserId == null) {
            logger.warn("交换申请提交事件缺少目标用户ID: {}", event.getApplicationId());
            return;
        }
        
        // 准备模板数据
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("applicationId", event.getApplicationId());
        templateData.put("applicantId", event.getApplicantId());
        templateData.put("requestedLeadId", event.getRequestedLeadId());
        templateData.put("offeredLeadId", event.getOfferedLeadId());
        templateData.put("eventTime", event.getEventTime());
        
        // 发送系统通知
        notificationService.createAndSendNotification(
            targetUserId,
            NotificationType.EXCHANGE_APPLICATION,
            SendChannel.SYSTEM,
            "EXCHANGE_APPLICATION_SUBMITTED",
            templateData,
            7 // 高优先级
        );
        
        // 如果用户启用了短信通知，也发送短信
        if (notificationService.canReceiveNotification(targetUserId, NotificationType.EXCHANGE_APPLICATION, SendChannel.SMS)) {
            notificationService.createAndSendNotification(
                targetUserId,
                NotificationType.EXCHANGE_APPLICATION,
                SendChannel.SMS,
                "EXCHANGE_APPLICATION_SUBMITTED_SMS",
                templateData,
                7
            );
        }
        
        logger.info("已发送交换申请提交通知给用户: {}", targetUserId);
    }
    
    /**
     * 处理申请批准事件
     * 通知申请人申请已被批准
     * 
     * @param event 事件信息
     */
    private void handleApplicationApproved(ExchangeApplicationEvent event) {
        Long applicantId = event.getApplicantId();
        if (applicantId == null) {
            logger.warn("交换申请批准事件缺少申请人ID: {}", event.getApplicationId());
            return;
        }
        
        // 准备模板数据
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("applicationId", event.getApplicationId());
        templateData.put("targetUserId", event.getTargetUserId());
        templateData.put("requestedLeadId", event.getRequestedLeadId());
        templateData.put("offeredLeadId", event.getOfferedLeadId());
        templateData.put("eventTime", event.getEventTime());
        templateData.put("operatorId", event.getOperatorId());
        
        // 发送系统通知
        notificationService.createAndSendNotification(
            applicantId,
            NotificationType.EXCHANGE_APPROVED,
            SendChannel.SYSTEM,
            "EXCHANGE_APPLICATION_APPROVED",
            templateData,
            8 // 高优先级
        );
        
        // 发送短信通知
        if (notificationService.canReceiveNotification(applicantId, NotificationType.EXCHANGE_APPROVED, SendChannel.SMS)) {
            notificationService.createAndSendNotification(
                applicantId,
                NotificationType.EXCHANGE_APPROVED,
                SendChannel.SMS,
                "EXCHANGE_APPLICATION_APPROVED_SMS",
                templateData,
                8
            );
        }
        
        logger.info("已发送交换申请批准通知给用户: {}", applicantId);
    }
    
    /**
     * 处理申请拒绝事件
     * 通知申请人申请已被拒绝
     * 
     * @param event 事件信息
     */
    private void handleApplicationRejected(ExchangeApplicationEvent event) {
        Long applicantId = event.getApplicantId();
        if (applicantId == null) {
            logger.warn("交换申请拒绝事件缺少申请人ID: {}", event.getApplicationId());
            return;
        }
        
        // 准备模板数据
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("applicationId", event.getApplicationId());
        templateData.put("targetUserId", event.getTargetUserId());
        templateData.put("requestedLeadId", event.getRequestedLeadId());
        templateData.put("offeredLeadId", event.getOfferedLeadId());
        templateData.put("eventTime", event.getEventTime());
        templateData.put("operatorId", event.getOperatorId());
        templateData.put("remark", event.getRemark());
        
        // 发送系统通知
        notificationService.createAndSendNotification(
            applicantId,
            NotificationType.EXCHANGE_REJECTED,
            SendChannel.SYSTEM,
            "EXCHANGE_APPLICATION_REJECTED",
            templateData,
            6 // 中等优先级
        );
        
        logger.info("已发送交换申请拒绝通知给用户: {}", applicantId);
    }
    
    /**
     * 处理申请取消事件
     * 通知相关用户申请已被取消
     * 
     * @param event 事件信息
     */
    private void handleApplicationCancelled(ExchangeApplicationEvent event) {
        // 准备模板数据
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("applicationId", event.getApplicationId());
        templateData.put("applicantId", event.getApplicantId());
        templateData.put("targetUserId", event.getTargetUserId());
        templateData.put("requestedLeadId", event.getRequestedLeadId());
        templateData.put("offeredLeadId", event.getOfferedLeadId());
        templateData.put("eventTime", event.getEventTime());
        templateData.put("operatorId", event.getOperatorId());
        templateData.put("remark", event.getRemark());
        
        // 通知申请人（如果不是申请人自己取消的）
        if (event.getApplicantId() != null && !event.getApplicantId().equals(event.getOperatorId())) {
            notificationService.createAndSendNotification(
                event.getApplicantId(),
                NotificationType.EXCHANGE_CANCELLED,
                SendChannel.SYSTEM,
                "EXCHANGE_APPLICATION_CANCELLED",
                templateData,
                5
            );
        }
        
        // 通知目标用户（如果不是目标用户自己取消的）
        if (event.getTargetUserId() != null && !event.getTargetUserId().equals(event.getOperatorId())) {
            notificationService.createAndSendNotification(
                event.getTargetUserId(),
                NotificationType.EXCHANGE_CANCELLED,
                SendChannel.SYSTEM,
                "EXCHANGE_APPLICATION_CANCELLED",
                templateData,
                5
            );
        }
        
        logger.info("已发送交换申请取消通知");
    }
    
    /**
     * 处理交换完成事件
     * 通知双方用户交换已完成
     * 
     * @param event 事件信息
     */
    private void handleExchangeCompleted(ExchangeApplicationEvent event) {
        // 准备模板数据
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("applicationId", event.getApplicationId());
        templateData.put("applicantId", event.getApplicantId());
        templateData.put("targetUserId", event.getTargetUserId());
        templateData.put("requestedLeadId", event.getRequestedLeadId());
        templateData.put("offeredLeadId", event.getOfferedLeadId());
        templateData.put("eventTime", event.getEventTime());
        
        // 获取需要通知的用户列表
        List<Long> recipients = event.getNotificationRecipients();
        
        // 批量发送通知
        if (!recipients.isEmpty()) {
            notificationService.createAndSendBatchNotifications(
                recipients,
                NotificationType.EXCHANGE_COMPLETED,
                SendChannel.SYSTEM,
                "EXCHANGE_COMPLETED",
                templateData,
                9 // 最高优先级
            );
            
            // 发送短信通知
            for (Long userId : recipients) {
                if (notificationService.canReceiveNotification(userId, NotificationType.EXCHANGE_COMPLETED, SendChannel.SMS)) {
                    notificationService.createAndSendNotification(
                        userId,
                        NotificationType.EXCHANGE_COMPLETED,
                        SendChannel.SMS,
                        "EXCHANGE_COMPLETED_SMS",
                        templateData,
                        9
                    );
                }
            }
        }
        
        logger.info("已发送交换完成通知给用户: {}", recipients);
    }
    
    /**
     * 处理交换过期事件
     * 通知相关用户交换已过期
     * 
     * @param event 事件信息
     */
    private void handleExchangeExpired(ExchangeApplicationEvent event) {
        // 准备模板数据
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("applicationId", event.getApplicationId());
        templateData.put("applicantId", event.getApplicantId());
        templateData.put("targetUserId", event.getTargetUserId());
        templateData.put("requestedLeadId", event.getRequestedLeadId());
        templateData.put("offeredLeadId", event.getOfferedLeadId());
        templateData.put("eventTime", event.getEventTime());
        
        // 获取需要通知的用户列表
        List<Long> recipients = event.getNotificationRecipients();
        
        // 批量发送通知
        if (!recipients.isEmpty()) {
            notificationService.createAndSendBatchNotifications(
                recipients,
                NotificationType.EXCHANGE_EXPIRED,
                SendChannel.SYSTEM,
                "EXCHANGE_EXPIRED",
                templateData,
                6 // 中等优先级
            );
        }
        
        logger.info("已发送交换过期通知给用户: {}", recipients);
    }
}