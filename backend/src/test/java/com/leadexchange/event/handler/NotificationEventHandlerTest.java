package com.leadexchange.event.handler;

import com.leadexchange.domain.notification.NotificationType;
import com.leadexchange.domain.notification.SendChannel;
import com.leadexchange.event.ExchangeApplicationEvent;
import com.leadexchange.event.ExchangeApprovalEvent;
import com.leadexchange.event.ExchangeCompletionEvent;
import com.leadexchange.event.ExchangeRejectionEvent;
import com.leadexchange.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 通知事件处理器单元测试
 * 测试各种业务事件的通知处理逻辑
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class NotificationEventHandlerTest {

    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private NotificationEventHandler eventHandler;
    
    private ExchangeApplicationEvent applicationEvent;
    private ExchangeApprovalEvent approvalEvent;
    private ExchangeRejectionEvent rejectionEvent;
    private ExchangeCompletionEvent completionEvent;
    
    @BeforeEach
    void setUp() {
        // 创建交换申请事件
        applicationEvent = new ExchangeApplicationEvent();
        applicationEvent.setExchangeId(1L);
        applicationEvent.setApplicantId(100L);
        applicationEvent.setApplicantName("张三");
        applicationEvent.setTargetUserId(200L);
        applicationEvent.setTargetUserName("李四");
        applicationEvent.setLeadTitle("优质客户线索");
        applicationEvent.setApplicationTime(LocalDateTime.now());
        applicationEvent.setApplicationReason("希望交换此线索");
        
        // 创建交换审批事件
        approvalEvent = new ExchangeApprovalEvent();
        approvalEvent.setExchangeId(1L);
        approvalEvent.setApplicantId(100L);
        approvalEvent.setApplicantName("张三");
        approvalEvent.setApproverId(200L);
        approvalEvent.setApproverName("李四");
        approvalEvent.setLeadTitle("优质客户线索");
        approvalEvent.setApprovalTime(LocalDateTime.now());
        approvalEvent.setApprovalComment("同意交换");
        
        // 创建交换拒绝事件
        rejectionEvent = new ExchangeRejectionEvent();
        rejectionEvent.setExchangeId(1L);
        rejectionEvent.setApplicantId(100L);
        rejectionEvent.setApplicantName("张三");
        rejectionEvent.setRejecterId(200L);
        rejectionEvent.setRejecterName("李四");
        rejectionEvent.setLeadTitle("优质客户线索");
        rejectionEvent.setRejectionTime(LocalDateTime.now());
        rejectionEvent.setRejectionReason("暂不交换");
        
        // 创建交换完成事件
        completionEvent = new ExchangeCompletionEvent();
        completionEvent.setExchangeId(1L);
        completionEvent.setApplicantId(100L);
        completionEvent.setApplicantName("张三");
        completionEvent.setTargetUserId(200L);
        completionEvent.setTargetUserName("李四");
        completionEvent.setLeadTitle("优质客户线索");
        completionEvent.setCompletionTime(LocalDateTime.now());
        completionEvent.setExchangeValue(85.5);
    }
    
    @Test
    void testHandleExchangeApplicationEvent() {
        // Given
        when(notificationService.sendTemplateNotification(
            eq(200L),
            eq(NotificationType.EXCHANGE_APPLICATION),
            eq(SendChannel.SYSTEM),
            eq("EXCHANGE_APPLICATION_SUBMITTED_SYSTEM"),
            any(Map.class),
            eq(7)
        )).thenReturn(true);
        
        when(notificationService.sendTemplateNotification(
            eq(200L),
            eq(NotificationType.EXCHANGE_APPLICATION),
            eq(SendChannel.EMAIL),
            eq("EXCHANGE_APPLICATION_SUBMITTED_EMAIL"),
            any(Map.class),
            eq(7)
        )).thenReturn(true);
        
        when(notificationService.sendTemplateNotification(
            eq(200L),
            eq(NotificationType.EXCHANGE_APPLICATION),
            eq(SendChannel.SMS),
            eq("EXCHANGE_APPLICATION_SUBMITTED_SMS"),
            any(Map.class),
            eq(7)
        )).thenReturn(true);
        
        // When
        eventHandler.handleExchangeApplicationEvent(applicationEvent);
        
        // Then
        verify(notificationService, times(3)).sendTemplateNotification(
            eq(200L),
            eq(NotificationType.EXCHANGE_APPLICATION),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            eq(7)
        );
    }
    
    @Test
    void testHandleExchangeApprovalEvent() {
        // Given
        when(notificationService.sendTemplateNotification(
            eq(100L),
            eq(NotificationType.EXCHANGE_APPROVAL),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            eq(8)
        )).thenReturn(true);
        
        // When
        eventHandler.handleExchangeApprovalEvent(approvalEvent);
        
        // Then
        verify(notificationService, times(3)).sendTemplateNotification(
            eq(100L),
            eq(NotificationType.EXCHANGE_APPROVAL),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            eq(8)
        );
    }
    
    @Test
    void testHandleExchangeRejectionEvent() {
        // Given
        when(notificationService.sendTemplateNotification(
            eq(100L),
            eq(NotificationType.EXCHANGE_REJECTION),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            eq(6)
        )).thenReturn(true);
        
        // When
        eventHandler.handleExchangeRejectionEvent(rejectionEvent);
        
        // Then
        verify(notificationService, times(3)).sendTemplateNotification(
            eq(100L),
            eq(NotificationType.EXCHANGE_REJECTION),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            eq(6)
        );
    }
    
    @Test
    void testHandleExchangeCompletionEvent() {
        // Given
        when(notificationService.sendTemplateNotification(
            anyLong(),
            eq(NotificationType.EXCHANGE_COMPLETION),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            eq(9)
        )).thenReturn(true);
        
        // When
        eventHandler.handleExchangeCompletionEvent(completionEvent);
        
        // Then
        // 应该给申请人和目标用户都发送通知，每人3个渠道，共6次调用
        verify(notificationService, times(6)).sendTemplateNotification(
            anyLong(),
            eq(NotificationType.EXCHANGE_COMPLETION),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            eq(9)
        );
    }
    
    @Test
    void testHandleExchangeApplicationEventWithNotificationFailure() {
        // Given
        when(notificationService.sendTemplateNotification(
            anyLong(),
            any(NotificationType.class),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            anyInt()
        )).thenReturn(false);
        
        // When
        eventHandler.handleExchangeApplicationEvent(applicationEvent);
        
        // Then
        // 即使发送失败，也应该尝试发送所有渠道的通知
        verify(notificationService, times(3)).sendTemplateNotification(
            eq(200L),
            eq(NotificationType.EXCHANGE_APPLICATION),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            eq(7)
        );
    }
    
    @Test
    void testTemplateDataCreation() {
        // Given
        when(notificationService.sendTemplateNotification(
            anyLong(),
            any(NotificationType.class),
            any(SendChannel.class),
            anyString(),
            any(Map.class),
            anyInt()
        )).thenReturn(true);
        
        // When
        eventHandler.handleExchangeApplicationEvent(applicationEvent);
        
        // Then
        // 验证模板数据包含必要的字段
        verify(notificationService, times(3)).sendTemplateNotification(
            eq(200L),
            eq(NotificationType.EXCHANGE_APPLICATION),
            any(SendChannel.class),
            anyString(),
            argThat(templateData -> {
                Map<String, Object> data = (Map<String, Object>) templateData;
                return data.containsKey("applicantName") &&
                       data.containsKey("leadTitle") &&
                       data.containsKey("applicationTime") &&
                       data.containsKey("applicationReason") &&
                       data.get("applicantName").equals("张三") &&
                       data.get("leadTitle").equals("优质客户线索");
            }),
            eq(7)
        );
    }
    
    @Test
    void testEventHandlerWithNullEvent() {
        // When & Then - 应该优雅处理null事件，不抛出异常
        try {
            eventHandler.handleExchangeApplicationEvent(null);
            eventHandler.handleExchangeApprovalEvent(null);
            eventHandler.handleExchangeRejectionEvent(null);
            eventHandler.handleExchangeCompletionEvent(null);
        } catch (Exception e) {
            // 如果抛出异常，测试失败
            throw new AssertionError("Event handler should handle null events gracefully", e);
        }
        
        // 验证没有调用通知服务
        verify(notificationService, never()).sendTemplateNotification(
            anyLong(), any(), any(), anyString(), any(), anyInt()
        );
    }
}