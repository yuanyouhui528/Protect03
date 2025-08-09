package com.leadexchange.service.notification;

import com.leadexchange.domain.notification.*;
import com.leadexchange.repository.notification.NotificationTemplateRepository;
import com.leadexchange.service.notification.impl.NotificationTemplateServiceImpl;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 通知模板服务单元测试
 * 测试通知模板的创建、管理、渲染等业务操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class NotificationTemplateServiceTest {

    @Mock
    private NotificationTemplateRepository templateRepository;
    
    @InjectMocks
    private NotificationTemplateServiceImpl templateService;
    
    private NotificationTemplate testTemplate;
    
    @BeforeEach
    void setUp() {
        testTemplate = new NotificationTemplate();
        testTemplate.setId(1L);
        testTemplate.setTemplateName("交换申请通知模板");
        testTemplate.setTemplateCode("EXCHANGE_APPLICATION_SYSTEM");
        testTemplate.setTemplateType(TemplateType.SYSTEM);
        testTemplate.setEventType(NotificationType.EXCHANGE_APPLICATION);
        testTemplate.setTemplateTitle("线索交换申请通知");
        testTemplate.setTemplateContent("您有新的线索交换申请，申请人：${applicantName}，申请时间：${applicationTime}");
        testTemplate.setIsEnabled(true);
        testTemplate.setStatus(TemplateStatus.ACTIVE);
        testTemplate.setPriority(5);
        testTemplate.setCreateTime(LocalDateTime.now());
        testTemplate.setUpdateTime(LocalDateTime.now());
    }
    
    @Test
    void testCreateTemplate() {
        // Given
        when(templateRepository.findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM"))
            .thenReturn(Optional.empty());
        when(templateRepository.save(any(NotificationTemplate.class)))
            .thenReturn(testTemplate);
        
        // When
        NotificationTemplate result = templateService.createTemplate(
            "交换申请通知模板",
            "EXCHANGE_APPLICATION_SYSTEM",
            TemplateType.SYSTEM,
            NotificationType.EXCHANGE_APPLICATION,
            "线索交换申请通知",
            "您有新的线索交换申请，申请人：${applicantName}，申请时间：${applicationTime}",
            5,
            "系统通知模板"
        );
        
        // Then
        assertNotNull(result);
        assertEquals("交换申请通知模板", result.getTemplateName());
        assertEquals("EXCHANGE_APPLICATION_SYSTEM", result.getTemplateCode());
        verify(templateRepository).findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM");
        verify(templateRepository).save(any(NotificationTemplate.class));
    }
    
    @Test
    void testCreateTemplateWithDuplicateCode() {
        // Given
        when(templateRepository.findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM"))
            .thenReturn(Optional.of(testTemplate));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            templateService.createTemplate(
                "交换申请通知模板",
                "EXCHANGE_APPLICATION_SYSTEM",
                TemplateType.SYSTEM,
                NotificationType.EXCHANGE_APPLICATION,
                "线索交换申请通知",
                "您有新的线索交换申请",
                5,
                "系统通知模板"
            );
        });
        
        verify(templateRepository).findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM");
        verify(templateRepository, never()).save(any(NotificationTemplate.class));
    }
    
    @Test
    void testUpdateTemplate() {
        // Given
        when(templateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(templateRepository.save(any(NotificationTemplate.class))).thenReturn(testTemplate);
        
        // When
        NotificationTemplate result = templateService.updateTemplate(
            1L,
            "更新后的模板名称",
            "更新后的标题",
            "更新后的内容：${applicantName}",
            true,
            8,
            "更新备注"
        );
        
        // Then
        assertNotNull(result);
        verify(templateRepository).findById(1L);
        verify(templateRepository).save(any(NotificationTemplate.class));
    }
    
    @Test
    void testDeleteTemplate() {
        // Given
        when(templateRepository.existsById(1L)).thenReturn(true);
        doNothing().when(templateRepository).deleteById(1L);
        
        // When
        boolean result = templateService.deleteTemplate(1L);
        
        // Then
        assertTrue(result);
        verify(templateRepository).existsById(1L);
        verify(templateRepository).deleteById(1L);
    }
    
    @Test
    void testGetTemplateById() {
        // Given
        when(templateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        
        // When
        NotificationTemplate result = templateService.getTemplateById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("交换申请通知模板", result.getTemplateName());
        verify(templateRepository).findById(1L);
    }
    
    @Test
    void testGetTemplateByCode() {
        // Given
        when(templateRepository.findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM"))
            .thenReturn(Optional.of(testTemplate));
        
        // When
        NotificationTemplate result = templateService.getTemplateByCode("EXCHANGE_APPLICATION_SYSTEM");
        
        // Then
        assertNotNull(result);
        assertEquals("EXCHANGE_APPLICATION_SYSTEM", result.getTemplateCode());
        verify(templateRepository).findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM");
    }
    
    @Test
    void testGetTemplates() {
        // Given
        List<NotificationTemplate> templates = Arrays.asList(testTemplate);
        Page<NotificationTemplate> page = new PageImpl<>(templates);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(templateRepository.findAll(pageable)).thenReturn(page);
        
        // When
        Page<NotificationTemplate> result = templateService.getTemplates(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testTemplate.getId(), result.getContent().get(0).getId());
        verify(templateRepository).findAll(pageable);
    }
    
    @Test
    void testGetTemplatesByNotificationType() {
        // Given
        List<NotificationTemplate> templates = Arrays.asList(testTemplate);
        when(templateRepository.findByEventTypeAndIsEnabled(NotificationType.EXCHANGE_APPLICATION, true))
            .thenReturn(templates);
        
        // When
        List<NotificationTemplate> result = templateService.getTemplatesByNotificationType(
            NotificationType.EXCHANGE_APPLICATION
        );
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplate.getId(), result.get(0).getId());
        verify(templateRepository).findByEventTypeAndIsEnabled(NotificationType.EXCHANGE_APPLICATION, true);
    }
    
    @Test
    void testRenderTemplate() {
        // Given
        when(templateRepository.findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM"))
            .thenReturn(Optional.of(testTemplate));
        
        Map<String, Object> data = new HashMap<>();
        data.put("applicantName", "张三");
        data.put("applicationTime", "2024-01-15 10:30:00");
        
        // When
        Map<String, String> result = templateService.renderTemplate("EXCHANGE_APPLICATION_SYSTEM", data);
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("title"));
        assertTrue(result.containsKey("content"));
        assertEquals("线索交换申请通知", result.get("title"));
        assertTrue(result.get("content").contains("张三"));
        assertTrue(result.get("content").contains("2024-01-15 10:30:00"));
        verify(templateRepository).findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM");
    }
    
    @Test
    void testRenderTemplateNotFound() {
        // Given
        when(templateRepository.findByTemplateCode("NON_EXISTENT_TEMPLATE"))
            .thenReturn(Optional.empty());
        
        Map<String, Object> data = new HashMap<>();
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            templateService.renderTemplate("NON_EXISTENT_TEMPLATE", data);
        });
        
        verify(templateRepository).findByTemplateCode("NON_EXISTENT_TEMPLATE");
    }
    
    @Test
    void testEnableTemplate() {
        // Given
        testTemplate.setIsEnabled(false);
        when(templateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(templateRepository.save(any(NotificationTemplate.class))).thenReturn(testTemplate);
        
        // When
        boolean result = templateService.enableTemplate(1L);
        
        // Then
        assertTrue(result);
        verify(templateRepository).findById(1L);
        verify(templateRepository).save(any(NotificationTemplate.class));
    }
    
    @Test
    void testDisableTemplate() {
        // Given
        when(templateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(templateRepository.save(any(NotificationTemplate.class))).thenReturn(testTemplate);
        
        // When
        boolean result = templateService.disableTemplate(1L);
        
        // Then
        assertTrue(result);
        verify(templateRepository).findById(1L);
        verify(templateRepository).save(any(NotificationTemplate.class));
    }
    
    @Test
    void testIsTemplateCodeExists() {
        // Given
        when(templateRepository.findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM"))
            .thenReturn(Optional.of(testTemplate));
        
        // When
        boolean result = templateService.isTemplateCodeExists("EXCHANGE_APPLICATION_SYSTEM");
        
        // Then
        assertTrue(result);
        verify(templateRepository).findByTemplateCode("EXCHANGE_APPLICATION_SYSTEM");
    }
}