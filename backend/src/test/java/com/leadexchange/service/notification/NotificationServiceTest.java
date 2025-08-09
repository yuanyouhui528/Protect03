package com.leadexchange.service.notification;

import com.leadexchange.domain.notification.*;
import com.leadexchange.repository.notification.NotificationRepository;
import com.leadexchange.repository.notification.NotificationSettingsRepository;
import com.leadexchange.service.notification.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 通知服务单元测试
 * 测试通知的创建、发送、查询等业务操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private NotificationSettingsRepository settingsRepository;
    
    @Mock
    private NotificationTemplateService templateService;
    
    @InjectMocks
    private NotificationServiceImpl notificationService;
    
    private Notification testNotification;
    private NotificationSettings testSettings;
    
    @BeforeEach
    void setUp() {
        // 创建测试通知
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setRecipientId(100L);
        testNotification.setNotificationType(NotificationType.EXCHANGE_APPLICATION);
        testNotification.setSendChannel(SendChannel.SYSTEM);
        testNotification.setTitle("测试通知");
        testNotification.setContent("这是一条测试通知");
        testNotification.setStatus(SendStatus.PENDING);
        testNotification.setIsRead(false);
        testNotification.setPriority(5);
        testNotification.setCreateTime(LocalDateTime.now());
        
        // 创建测试设置
        testSettings = new NotificationSettings();
        testSettings.setId(1L);
        testSettings.setUserId(100L);
        testSettings.setNotificationType(NotificationType.EXCHANGE_APPLICATION);
        testSettings.setSystemEnabled(true);
        testSettings.setEmailEnabled(true);
        testSettings.setSmsEnabled(false);
    }
    
    @Test
    void testCreateNotification() {
        // Given
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // When
        Notification result = notificationService.createNotification(
            100L, NotificationType.EXCHANGE_APPLICATION, SendChannel.SYSTEM,
            "测试通知", "这是一条测试通知", 5
        );
        
        // Then
        assertNotNull(result);
        assertEquals(100L, result.getRecipientId());
        assertEquals(NotificationType.EXCHANGE_APPLICATION, result.getNotificationType());
        assertEquals("测试通知", result.getTitle());
        verify(notificationRepository).save(any(Notification.class));
    }
    
    @Test
    void testSendNotification() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // When
        boolean result = notificationService.sendNotification(1L);
        
        // Then
        assertTrue(result);
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
    }
    
    @Test
    void testMarkAsRead() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // When
        boolean result = notificationService.markAsRead(1L);
        
        // Then
        assertTrue(result);
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
    }
    
    @Test
    void testGetUserNotifications() {
        // Given
        List<Notification> notifications = Arrays.asList(testNotification);
        Page<Notification> page = new PageImpl<>(notifications);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(notificationRepository.findByRecipientIdAndNotificationTypeAndIsReadOrderByCreateTimeDesc(
            eq(100L), eq(NotificationType.EXCHANGE_APPLICATION), eq(false), eq(pageable)
        )).thenReturn(page);
        
        // When
        Page<Notification> result = notificationService.getUserNotifications(
            100L, NotificationType.EXCHANGE_APPLICATION, false, pageable
        );
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testNotification.getId(), result.getContent().get(0).getId());
    }
    
    @Test
    void testGetUnreadCount() {
        // Given
        when(notificationRepository.countByRecipientIdAndIsRead(100L, false)).thenReturn(5L);
        
        // When
        long count = notificationService.getUnreadCount(100L);
        
        // Then
        assertEquals(5L, count);
        verify(notificationRepository).countByRecipientIdAndIsRead(100L, false);
    }
    
    @Test
    void testGetNotificationSettings() {
        // Given
        when(settingsRepository.findByUserId(100L)).thenReturn(Optional.of(testSettings));
        
        // When
        NotificationSettings result = notificationService.getNotificationSettings(100L);
        
        // Then
        assertNotNull(result);
        assertEquals(100L, result.getUserId());
        assertEquals(NotificationType.EXCHANGE_APPLICATION, result.getNotificationType());
        assertTrue(result.getSystemEnabled());
    }
    
    @Test
    void testUpdateNotificationSettings() {
        // Given
        when(settingsRepository.findByUserId(100L)).thenReturn(Optional.of(testSettings));
        when(settingsRepository.save(any(NotificationSettings.class))).thenReturn(testSettings);
        
        // When
        NotificationSettings result = notificationService.updateNotificationSettings(
            100L, NotificationType.EXCHANGE_APPLICATION, true, false, true
        );
        
        // Then
        assertNotNull(result);
        verify(settingsRepository).findByUserId(100L);
        verify(settingsRepository).save(any(NotificationSettings.class));
    }
    
    @Test
    void testDeleteNotification() {
        // Given
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);
        
        // When
        boolean result = notificationService.deleteNotification(1L);
        
        // Then
        assertTrue(result);
        verify(notificationRepository).existsById(1L);
        verify(notificationRepository).deleteById(1L);
    }
    
    @Test
    void testDeleteNotificationNotFound() {
        // Given
        when(notificationRepository.existsById(1L)).thenReturn(false);
        
        // When
        boolean result = notificationService.deleteNotification(1L);
        
        // Then
        assertFalse(result);
        verify(notificationRepository).existsById(1L);
        verify(notificationRepository, never()).deleteById(1L);
    }
}