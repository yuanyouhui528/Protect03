package com.leadexchange.controller.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadexchange.domain.notification.*;
import com.leadexchange.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 通知控制器单元测试
 * 测试通知相关的HTTP接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private NotificationService notificationService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Notification testNotification;
    private NotificationSettings testSettings;
    
    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setRecipientId(100L);
        testNotification.setNotificationType(NotificationType.EXCHANGE_APPLICATION);
        testNotification.setSendChannel(SendChannel.SYSTEM);
        testNotification.setTitle("测试通知");
        testNotification.setContent("这是一条测试通知");
        testNotification.setStatus(SendStatus.SENT);
        testNotification.setIsRead(false);
        testNotification.setPriority(5);
        testNotification.setCreateTime(LocalDateTime.now());
        
        testSettings = new NotificationSettings();
        testSettings.setId(1L);
        testSettings.setUserId(100L);
        testSettings.setNotificationType(NotificationType.EXCHANGE_APPLICATION);
        testSettings.setSystemEnabled(true);
        testSettings.setEmailEnabled(true);
        testSettings.setSmsEnabled(false);
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetNotifications() throws Exception {
        // Given
        List<Notification> notifications = Arrays.asList(testNotification);
        Page<Notification> page = new PageImpl<>(notifications, PageRequest.of(0, 10), 1);
        
        when(notificationService.getUserNotifications(anyLong(), any(), any(), any()))
            .thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/notifications")
                .param("page", "0")
                .param("size", "10")
                .param("notificationType", "EXCHANGE_APPLICATION")
                .param("isRead", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("测试通知"));
        
        verify(notificationService).getUserNotifications(anyLong(), any(), any(), any());
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetNotificationDetail() throws Exception {
        // Given
        when(notificationService.getNotificationById(1L)).thenReturn(testNotification);
        
        // When & Then
        mockMvc.perform(get("/api/notifications/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("测试通知"))
                .andExpect(jsonPath("$.data.content").value("这是一条测试通知"));
        
        verify(notificationService).getNotificationById(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testMarkAsRead() throws Exception {
        // Given
        when(notificationService.markAsRead(1L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(put("/api/notifications/1/read")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(notificationService).markAsRead(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testMarkAllAsRead() throws Exception {
        // Given
        when(notificationService.markAllAsRead(anyLong())).thenReturn(5);
        
        // When & Then
        mockMvc.perform(put("/api/notifications/read-all")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(5));
        
        verify(notificationService).markAllAsRead(anyLong());
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetUnreadCount() throws Exception {
        // Given
        when(notificationService.getUnreadCount(anyLong())).thenReturn(3L);
        
        // When & Then
        mockMvc.perform(get("/api/notifications/unread-count")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(3));
        
        verify(notificationService).getUnreadCount(anyLong());
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testDeleteNotification() throws Exception {
        // Given
        when(notificationService.deleteNotification(1L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(delete("/api/notifications/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
        
        verify(notificationService).deleteNotification(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testGetNotificationSettings() throws Exception {
        // Given
        when(notificationService.getNotificationSettings(anyLong())).thenReturn(testSettings);
        
        // When & Then
        mockMvc.perform(get("/api/notifications/settings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(100))
                .andExpect(jsonPath("$.data.systemEnabled").value(true))
                .andExpect(jsonPath("$.data.emailEnabled").value(true))
                .andExpect(jsonPath("$.data.smsEnabled").value(false));
        
        verify(notificationService).getNotificationSettings(anyLong());
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testUpdateNotificationSettings() throws Exception {
        // Given
        when(notificationService.updateNotificationSettings(anyLong(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
            .thenReturn(testSettings);
        
        String requestBody = objectMapper.writeValueAsString(Map.of(
            "notificationType", "EXCHANGE_APPLICATION",
            "systemEnabled", true,
            "emailEnabled", false,
            "smsEnabled", true
        ));
        
        // When & Then
        mockMvc.perform(put("/api/notifications/settings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(100));
        
        verify(notificationService).updateNotificationSettings(anyLong(), any(), anyBoolean(), anyBoolean(), anyBoolean());
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"USER"})
    void testSendNotification() throws Exception {
        // Given
        when(notificationService.createNotification(anyLong(), any(), any(), anyString(), anyString(), anyInt()))
            .thenReturn(testNotification);
        when(notificationService.sendNotification(anyLong())).thenReturn(true);
        
        String requestBody = objectMapper.writeValueAsString(Map.of(
            "recipientId", 100,
            "notificationType", "EXCHANGE_APPLICATION",
            "sendChannel", "SYSTEM",
            "title", "测试通知",
            "content", "这是一条测试通知",
            "priority", 5
        ));
        
        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(notificationService).createNotification(anyLong(), any(), any(), anyString(), anyString(), anyInt());
        verify(notificationService).sendNotification(anyLong());
    }
    
    @Test
    void testGetNotificationsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        
        verify(notificationService, never()).getUserNotifications(anyLong(), any(), any(), any());
    }
}