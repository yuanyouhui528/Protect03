package com.leadexchange.controller.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadexchange.common.result.Result;
import com.leadexchange.domain.exchange.ExchangeApplication;
import com.leadexchange.domain.exchange.ExchangeHistory;
import com.leadexchange.domain.exchange.ExchangeStatus;
import com.leadexchange.domain.exchange.UserCredit;
import com.leadexchange.service.ExchangeEngineService;
import com.leadexchange.service.UserCreditService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 交换控制器单元测试
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@WebMvcTest(ExchangeController.class)
class ExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ExchangeEngineService exchangeEngineService;
    
    @MockBean
    private UserCreditService userCreditService;
    
    private ExchangeApplication exchangeApplication;
    private ExchangeHistory exchangeHistory;
    private UserCredit userCredit;
    
    @BeforeEach
    void setUp() {
        // 创建测试数据
        exchangeApplication = new ExchangeApplication();
        exchangeApplication.setId(1L);
        exchangeApplication.setApplicantId(1L);
        exchangeApplication.setTargetLeadId(1L);
        exchangeApplication.setTargetLeadOwnerId(2L);
        exchangeApplication.setOfferedLeadIds("2,3");
        exchangeApplication.setExchangeReason("希望交换该线索");
        exchangeApplication.setStatus(ExchangeStatus.PENDING);
        exchangeApplication.setCreatedAt(LocalDateTime.now());
        
        exchangeHistory = new ExchangeHistory();
        exchangeHistory.setId(1L);
        exchangeHistory.setApplicantId(1L);
        exchangeHistory.setTargetLeadOwnerId(2L);
        exchangeHistory.setCreatedAt(LocalDateTime.now());
        
        userCredit = new UserCredit();
        userCredit.setId(1L);
        userCredit.setUserId(1L);
        userCredit.setBalance(BigDecimal.valueOf(100));
        userCredit.setTotalEarned(BigDecimal.valueOf(200));
        userCredit.setTotalSpent(BigDecimal.valueOf(100));
    }
    
    @Test
    @WithMockUser
    void testApplyForExchange_Success() throws Exception {
        // Given
        ExchangeController.ExchangeApplicationRequest request = new ExchangeController.ExchangeApplicationRequest();
        request.setTargetLeadId(1L);
        request.setOfferedLeadIds(Arrays.asList(2L, 3L));
        request.setReason("希望交换该线索");
        
        when(exchangeEngineService.applyForExchange(anyLong(), anyLong(), anyList(), anyString()))
            .thenReturn(exchangeApplication);
        
        // When & Then
        mockMvc.perform(post("/api/exchanges/apply")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.applicantId").value(1L))
                .andExpect(jsonPath("$.data.targetLeadId").value(1L))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }
    
    @Test
    @WithMockUser
    void testApplyForExchange_InvalidRequest() throws Exception {
        // Given - 缺少必填字段的请求
        ExchangeController.ExchangeApplicationRequest request = new ExchangeController.ExchangeApplicationRequest();
        // 不设置任何字段，触发验证错误
        
        // When & Then
        mockMvc.perform(post("/api/exchanges/apply")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void testGetExchangeApplication_Success() throws Exception {
        // Given
        Long applicationId = 1L;
        when(exchangeEngineService.getApplicationById(applicationId))
            .thenReturn(exchangeApplication);
        
        // When & Then
        mockMvc.perform(get("/api/exchanges/{id}", applicationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.applicantId").value(1L));
    }
    
    @Test
    @WithMockUser
    void testGetExchangeApplication_Unauthorized() throws Exception {
        // Given - 创建一个当前用户无权访问的申请
        ExchangeApplication unauthorizedApplication = new ExchangeApplication();
        unauthorizedApplication.setId(1L);
        unauthorizedApplication.setApplicantId(999L); // 不是当前用户
        unauthorizedApplication.setTargetLeadOwnerId(888L); // 也不是当前用户
        
        when(exchangeEngineService.getApplicationById(1L))
            .thenReturn(unauthorizedApplication);
        
        // When & Then
        mockMvc.perform(get("/api/exchanges/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无权限查看此交换申请"));
    }
    
    @Test
    @WithMockUser
    void testApproveExchange_Success() throws Exception {
        // Given
        Long applicationId = 1L;
        ExchangeController.ExchangeReviewRequest request = new ExchangeController.ExchangeReviewRequest();
        request.setResponseMessage("同意交换");
        
        ExchangeApplication approvedApplication = new ExchangeApplication();
        approvedApplication.setId(1L);
        approvedApplication.setStatus(ExchangeStatus.APPROVED);
        approvedApplication.setResponseMessage("同意交换");
        
        when(exchangeEngineService.approveExchange(anyLong(), anyLong(), anyString()))
            .thenReturn(approvedApplication);
        
        // When & Then
        mockMvc.perform(post("/api/exchanges/{id}/approve", applicationId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.responseMessage").value("同意交换"));
    }
    
    @Test
    @WithMockUser
    void testRejectExchange_Success() throws Exception {
        // Given
        Long applicationId = 1L;
        ExchangeController.ExchangeReviewRequest request = new ExchangeController.ExchangeReviewRequest();
        request.setResponseMessage("拒绝交换");
        
        ExchangeApplication rejectedApplication = new ExchangeApplication();
        rejectedApplication.setId(1L);
        rejectedApplication.setStatus(ExchangeStatus.REJECTED);
        rejectedApplication.setResponseMessage("拒绝交换");
        
        when(exchangeEngineService.rejectExchange(anyLong(), anyLong(), anyString()))
            .thenReturn(rejectedApplication);
        
        // When & Then
        mockMvc.perform(post("/api/exchanges/{id}/reject", applicationId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.responseMessage").value("拒绝交换"));
    }
    
    @Test
    @WithMockUser
    void testCancelExchange_Success() throws Exception {
        // Given
        Long applicationId = 1L;
        
        ExchangeApplication cancelledApplication = new ExchangeApplication();
        cancelledApplication.setId(1L);
        cancelledApplication.setStatus(ExchangeStatus.CANCELLED);
        
        when(exchangeEngineService.cancelExchange(anyLong(), anyLong()))
            .thenReturn(cancelledApplication);
        
        // When & Then
        mockMvc.perform(post("/api/exchanges/{id}/cancel", applicationId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }
    
    @Test
    @WithMockUser
    void testGetMyApplications_Success() throws Exception {
        // Given
        List<ExchangeApplication> applications = Arrays.asList(exchangeApplication);
        Page<ExchangeApplication> page = new PageImpl<>(applications, PageRequest.of(0, 10), 1);
        
        when(exchangeEngineService.getUserApplications(anyLong(), any()))
            .thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/exchanges/my/applications")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpected(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @WithMockUser
    void testGetReceivedApplications_Success() throws Exception {
        // Given
        List<ExchangeApplication> applications = Arrays.asList(exchangeApplication);
        Page<ExchangeApplication> page = new PageImpl<>(applications, PageRequest.of(0, 10), 1);
        
        when(exchangeEngineService.getReceivedApplications(anyLong(), any()))
            .thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/exchanges/my/received")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @WithMockUser
    void testGetMyExchangeHistory_Success() throws Exception {
        // Given
        List<ExchangeHistory> historyList = Arrays.asList(exchangeHistory);
        Page<ExchangeHistory> page = new PageImpl<>(historyList, PageRequest.of(0, 10), 1);
        
        when(exchangeEngineService.getUserExchangeHistory(anyLong(), any()))
            .thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/exchanges/my/history")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
    
    @Test
    @WithMockUser
    void testGetUserCreditInfo_Success() throws Exception {
        // Given
        when(userCreditService.getUserCredit(anyLong()))
            .thenReturn(userCredit);
        
        // When & Then
        mockMvc.perform(get("/api/exchanges/credits/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.balance").value(100));
    }
    
    @Test
    @WithMockUser
    void testGetCreditRanking_Success() throws Exception {
        // Given
        List<UserCredit> ranking = Arrays.asList(userCredit);
        when(userCreditService.getCreditRanking(anyInt()))
            .thenReturn(ranking);
        
        // When & Then
        mockMvc.perform(get("/api/exchanges/credits/ranking")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].userId").value(1L));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testProcessExpiredApplications_Success() throws Exception {
        // Given
        when(exchangeEngineService.processExpiredApplications())
            .thenReturn(5);
        
        // When & Then
        mockMvc.perform(post("/api/exchanges/admin/process-expired")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(5));
    }
    
    @Test
    @WithMockUser
    void testApplyForExchange_EmptyOfferedLeads() throws Exception {
        // Given
        ExchangeController.ExchangeApplicationRequest request = new ExchangeController.ExchangeApplicationRequest();
        request.setTargetLeadId(1L);
        request.setOfferedLeadIds(Arrays.asList()); // 空列表
        request.setReason("希望交换该线索");
        
        // When & Then
        mockMvc.perform(post("/api/exchanges/apply")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void testApproveExchange_EmptyResponseMessage() throws Exception {
        // Given
        Long applicationId = 1L;
        ExchangeController.ExchangeReviewRequest request = new ExchangeController.ExchangeReviewRequest();
        request.setResponseMessage(""); // 空消息
        
        // When & Then
        mockMvc.perform(post("/api/exchanges/{id}/approve", applicationId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}