package com.leadexchange.service;

import com.leadexchange.domain.exchange.ExchangeApplication;
import com.leadexchange.domain.exchange.ExchangeHistory;
import com.leadexchange.domain.exchange.ExchangeStatus;
import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.repository.ExchangeApplicationRepository;
import com.leadexchange.repository.ExchangeHistoryRepository;
import com.leadexchange.repository.lead.LeadRepository;
import com.leadexchange.service.ExchangeEngineService;
import com.leadexchange.util.ExchangeValueCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 交换引擎服务单元测试
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ExchangeEngineServiceTest {

    @Mock
    private ExchangeApplicationRepository exchangeApplicationRepository;
    
    @Mock
    private ExchangeHistoryRepository exchangeHistoryRepository;
    
    @Mock
    private LeadRepository leadRepository;
    
    @Mock
    private UserCreditService userCreditService;
    
    @Mock
    private ExchangeValueCalculator exchangeValueCalculator;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks
    private ExchangeEngineService exchangeEngineService;
    
    private Lead targetLead;
    private Lead offeredLead1;
    private Lead offeredLead2;
    private ExchangeApplication exchangeApplication;
    
    @BeforeEach
    void setUp() {
        // 创建测试数据
        targetLead = new Lead();
        targetLead.setId(1L);
        targetLead.setTitle("AI医疗诊断系统");
        targetLead.setRating(LeadRating.A);
        targetLead.setOwnerId(2L);
        
        offeredLead1 = new Lead();
        offeredLead1.setId(2L);
        offeredLead1.setTitle("智能制造系统");
        offeredLead1.setRating(LeadRating.B);
        offeredLead1.setOwnerId(1L);
        
        offeredLead2 = new Lead();
        offeredLead2.setId(3L);
        offeredLead2.setTitle("新能源项目");
        offeredLead2.setRating(LeadRating.B);
        offeredLead2.setOwnerId(1L);
        
        exchangeApplication = new ExchangeApplication();
        exchangeApplication.setId(1L);
        exchangeApplication.setApplicantId(1L);
        exchangeApplication.setTargetLeadId(1L);
        exchangeApplication.setTargetLeadOwnerId(2L);
        exchangeApplication.setOfferedLeadIds("2,3");
        exchangeApplication.setStatus(ExchangeStatus.PENDING);
        exchangeApplication.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void testApplyForExchange_Success() {
        // Given
        Long applicantId = 1L;
        Long targetLeadId = 1L;
        List<Long> offeredLeadIds = Arrays.asList(2L, 3L);
        String reason = "希望交换该线索";
        
        when(leadRepository.findById(targetLeadId)).thenReturn(Optional.of(targetLead));
        when(leadRepository.findAllById(offeredLeadIds)).thenReturn(Arrays.asList(offeredLead1, offeredLead2));
        when(exchangeValueCalculator.validateExchange(any(), any(), any()))
            .thenReturn(new ExchangeValueCalculator.ExchangeValidationResult(true, "交换条件满足"));
        when(exchangeApplicationRepository.save(any(ExchangeApplication.class)))
            .thenReturn(exchangeApplication);
        
        // When
        ExchangeApplication result = exchangeEngineService.applyForExchange(
            applicantId, targetLeadId, offeredLeadIds, reason);
        
        // Then
        assertNotNull(result);
        assertEquals(applicantId, result.getApplicantId());
        assertEquals(targetLeadId, result.getTargetLeadId());
        assertEquals(ExchangeStatus.PENDING, result.getStatus());
        
        verify(exchangeApplicationRepository).save(any(ExchangeApplication.class));
        verify(eventPublisher).publishEvent(any());
    }
    
    @Test
    void testApplyForExchange_TargetLeadNotFound() {
        // Given
        Long applicantId = 1L;
        Long targetLeadId = 999L;
        List<Long> offeredLeadIds = Arrays.asList(2L, 3L);
        String reason = "希望交换该线索";
        
        when(leadRepository.findById(targetLeadId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeEngineService.applyForExchange(applicantId, targetLeadId, offeredLeadIds, reason);
        });
        
        verify(exchangeApplicationRepository, never()).save(any());
    }
    
    @Test
    void testApplyForExchange_SelfExchange() {
        // Given
        Long applicantId = 2L; // 与targetLead的ownerId相同
        Long targetLeadId = 1L;
        List<Long> offeredLeadIds = Arrays.asList(2L, 3L);
        String reason = "希望交换该线索";
        
        when(leadRepository.findById(targetLeadId)).thenReturn(Optional.of(targetLead));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeEngineService.applyForExchange(applicantId, targetLeadId, offeredLeadIds, reason);
        });
        
        verify(exchangeApplicationRepository, never()).save(any());
    }
    
    @Test
    void testApproveExchange_Success() {
        // Given
        Long applicationId = 1L;
        Long reviewerId = 2L;
        String responseMessage = "同意交换";
        
        when(exchangeApplicationRepository.findById(applicationId))
            .thenReturn(Optional.of(exchangeApplication));
        when(exchangeApplicationRepository.save(any(ExchangeApplication.class)))
            .thenReturn(exchangeApplication);
        
        // When
        ExchangeApplication result = exchangeEngineService.approveExchange(
            applicationId, reviewerId, responseMessage);
        
        // Then
        assertNotNull(result);
        assertEquals(ExchangeStatus.APPROVED, result.getStatus());
        assertEquals(responseMessage, result.getResponseMessage());
        assertNotNull(result.getReviewedAt());
        
        verify(exchangeApplicationRepository).save(any(ExchangeApplication.class));
        verify(eventPublisher).publishEvent(any());
    }
    
    @Test
    void testApproveExchange_ApplicationNotFound() {
        // Given
        Long applicationId = 999L;
        Long reviewerId = 2L;
        String responseMessage = "同意交换";
        
        when(exchangeApplicationRepository.findById(applicationId))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeEngineService.approveExchange(applicationId, reviewerId, responseMessage);
        });
        
        verify(exchangeApplicationRepository, never()).save(any());
    }
    
    @Test
    void testApproveExchange_UnauthorizedReviewer() {
        // Given
        Long applicationId = 1L;
        Long reviewerId = 3L; // 不是目标线索的所有者
        String responseMessage = "同意交换";
        
        when(exchangeApplicationRepository.findById(applicationId))
            .thenReturn(Optional.of(exchangeApplication));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeEngineService.approveExchange(applicationId, reviewerId, responseMessage);
        });
        
        verify(exchangeApplicationRepository, never()).save(any());
    }
    
    @Test
    void testRejectExchange_Success() {
        // Given
        Long applicationId = 1L;
        Long reviewerId = 2L;
        String responseMessage = "拒绝交换";
        
        when(exchangeApplicationRepository.findById(applicationId))
            .thenReturn(Optional.of(exchangeApplication));
        when(exchangeApplicationRepository.save(any(ExchangeApplication.class)))
            .thenReturn(exchangeApplication);
        
        // When
        ExchangeApplication result = exchangeEngineService.rejectExchange(
            applicationId, reviewerId, responseMessage);
        
        // Then
        assertNotNull(result);
        assertEquals(ExchangeStatus.REJECTED, result.getStatus());
        assertEquals(responseMessage, result.getResponseMessage());
        assertNotNull(result.getReviewedAt());
        
        verify(exchangeApplicationRepository).save(any(ExchangeApplication.class));
        verify(eventPublisher).publishEvent(any());
    }
    
    @Test
    void testCancelExchange_Success() {
        // Given
        Long applicationId = 1L;
        Long applicantId = 1L;
        
        when(exchangeApplicationRepository.findById(applicationId))
            .thenReturn(Optional.of(exchangeApplication));
        when(exchangeApplicationRepository.save(any(ExchangeApplication.class)))
            .thenReturn(exchangeApplication);
        
        // When
        ExchangeApplication result = exchangeEngineService.cancelExchange(applicationId, applicantId);
        
        // Then
        assertNotNull(result);
        assertEquals(ExchangeStatus.CANCELLED, result.getStatus());
        
        verify(exchangeApplicationRepository).save(any(ExchangeApplication.class));
        verify(eventPublisher).publishEvent(any());
    }
    
    @Test
    void testGetUserApplications() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<ExchangeApplication> applications = Arrays.asList(exchangeApplication);
        Page<ExchangeApplication> page = new PageImpl<>(applications, pageable, 1);
        
        when(exchangeApplicationRepository.findByApplicantIdOrderByCreateTimeDesc(userId, pageable))
            .thenReturn(page);
        
        // When
        Page<ExchangeApplication> result = exchangeEngineService.getUserApplications(userId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(exchangeApplication, result.getContent().get(0));
    }
    
    @Test
    void testGetReceivedApplications() {
        // Given
        Long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        List<ExchangeApplication> applications = Arrays.asList(exchangeApplication);
        Page<ExchangeApplication> page = new PageImpl<>(applications, pageable, 1);
        
        when(exchangeApplicationRepository.findByTargetOwnerIdOrderByCreateTimeDesc(userId, pageable))
            .thenReturn(page);
        
        // When
        Page<ExchangeApplication> result = exchangeEngineService.getReceivedApplications(userId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(exchangeApplication, result.getContent().get(0));
    }
    
    @Test
    void testGetUserExchangeHistory() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        ExchangeHistory history = new ExchangeHistory();
        history.setId(1L);
        history.setApplicantId(1L);
        history.setTargetOwnerId(2L);
        
        List<ExchangeHistory> historyList = Arrays.asList(history);
        Page<ExchangeHistory> page = new PageImpl<>(historyList, pageable, 1);
        
        when(exchangeHistoryRepository.findByApplicantIdOrTargetOwnerIdOrderByCreateTimeDesc(
            userId, pageable)).thenReturn(page);
        
        // When
        Page<ExchangeHistory> result = exchangeEngineService.getUserExchangeHistory(userId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(history, result.getContent().get(0));
    }
    
    @Test
    void testProcessExpiredApplications() {
        // Given
        List<ExchangeApplication> expiredApplications = Arrays.asList(exchangeApplication);
        when(exchangeApplicationRepository.findExpiredApplications(any(LocalDateTime.class)))
            .thenReturn(expiredApplications);
        when(exchangeApplicationRepository.saveAll(anyList()))
            .thenReturn(expiredApplications);
        
        // When
        int result = exchangeEngineService.processExpiredApplications();
        
        // Then
        assertEquals(1, result);
        verify(exchangeApplicationRepository).saveAll(anyList());
        verify(eventPublisher, times(1)).publishEvent(any());
    }
}