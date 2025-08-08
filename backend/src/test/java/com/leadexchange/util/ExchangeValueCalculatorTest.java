package com.leadexchange.util;

import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 交换价值计算器单元测试
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ExchangeValueCalculatorTest {

    @InjectMocks
    private ExchangeValueCalculator exchangeValueCalculator;
    
    private Lead leadA;
    private Lead leadB;
    private Lead leadC;
    private Lead leadD;
    
    @BeforeEach
    void setUp() {
        // 创建不同评级的测试线索
        leadA = new Lead();
        leadA.setId(1L);
        leadA.setTitle("A级线索");
        leadA.setRating(LeadRating.A);
        
        leadB = new Lead();
        leadB.setId(2L);
        leadB.setTitle("B级线索");
        leadB.setRating(LeadRating.B);
        
        leadC = new Lead();
        leadC.setId(3L);
        leadC.setTitle("C级线索");
        leadC.setRating(LeadRating.C);
        
        leadD = new Lead();
        leadD.setId(4L);
        leadD.setTitle("D级线索");
        leadD.setRating(LeadRating.D);
    }
    
    @Test
    void testCalculateLeadValue_ALevel() {
        // When
        BigDecimal value = exchangeValueCalculator.calculateLeadValue(leadA);
        
        // Then
        assertEquals(BigDecimal.valueOf(8), value);
    }
    
    @Test
    void testCalculateLeadValue_BLevel() {
        // When
        BigDecimal value = exchangeValueCalculator.calculateLeadValue(leadB);
        
        // Then
        assertEquals(BigDecimal.valueOf(4), value);
    }
    
    @Test
    void testCalculateLeadValue_CLevel() {
        // When
        BigDecimal value = exchangeValueCalculator.calculateLeadValue(leadC);
        
        // Then
        assertEquals(BigDecimal.valueOf(2), value);
    }
    
    @Test
    void testCalculateLeadValue_DLevel() {
        // When
        BigDecimal value = exchangeValueCalculator.calculateLeadValue(leadD);
        
        // Then
        assertEquals(BigDecimal.valueOf(1), value);
    }
    
    @Test
    void testCalculateLeadValue_NullLead() {
        // When
        BigDecimal value = exchangeValueCalculator.calculateLeadValue(null);
        
        // Then
        assertEquals(BigDecimal.ZERO, value);
    }
    
    @Test
    void testCalculateLeadValue_NullRating() {
        // Given
        Lead leadWithNullRating = new Lead();
        leadWithNullRating.setId(5L);
        leadWithNullRating.setRating(null);
        
        // When
        BigDecimal value = exchangeValueCalculator.calculateLeadValue(leadWithNullRating);
        
        // Then
        assertEquals(BigDecimal.ZERO, value);
    }
    
    @Test
    void testCalculateTotalValue_MultipleLeads() {
        // Given
        List<Lead> leads = Arrays.asList(leadA, leadB, leadC, leadD);
        
        // When
        BigDecimal totalValue = exchangeValueCalculator.calculateTotalValue(leads);
        
        // Then
        // A(8) + B(4) + C(2) + D(1) = 15
        assertEquals(BigDecimal.valueOf(15), totalValue);
    }
    
    @Test
    void testCalculateTotalValue_EmptyList() {
        // When
        BigDecimal totalValue = exchangeValueCalculator.calculateTotalValue(Collections.emptyList());
        
        // Then
        assertEquals(BigDecimal.ZERO, totalValue);
    }
    
    @Test
    void testCalculateTotalValue_NullList() {
        // When
        BigDecimal totalValue = exchangeValueCalculator.calculateTotalValue(null);
        
        // Then
        assertEquals(BigDecimal.ZERO, totalValue);
    }
    
    @Test
    void testIsExchangeFair_ExactMatch() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadB, leadB); // 4 + 4 = 8
        Lead targetLead = leadA; // 8
        
        // When
        boolean isFair = exchangeValueCalculator.isExchangeFair(offeredLeads, targetLead);
        
        // Then
        assertTrue(isFair);
    }
    
    @Test
    void testIsExchangeFair_WithinTolerance() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadB, leadC, leadC); // 4 + 2 + 2 = 8
        Lead targetLead = leadA; // 8，10%容差 = 0.8
        
        // When
        boolean isFair = exchangeValueCalculator.isExchangeFair(offeredLeads, targetLead);
        
        // Then
        assertTrue(isFair);
    }
    
    @Test
    void testIsExchangeFair_ExceedsTolerance() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadD); // 1
        Lead targetLead = leadA; // 8，差异7 > 10%容差0.8
        
        // When
        boolean isFair = exchangeValueCalculator.isExchangeFair(offeredLeads, targetLead);
        
        // Then
        assertFalse(isFair);
    }
    
    @Test
    void testCalculateValueDifference_PositiveDifference() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadA, leadB); // 8 + 4 = 12
        Lead targetLead = leadB; // 4
        
        // When
        BigDecimal difference = exchangeValueCalculator.calculateValueDifference(offeredLeads, targetLead);
        
        // Then
        assertEquals(BigDecimal.valueOf(8), difference); // 12 - 4 = 8
    }
    
    @Test
    void testCalculateValueDifference_NegativeDifference() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadD); // 1
        Lead targetLead = leadA; // 8
        
        // When
        BigDecimal difference = exchangeValueCalculator.calculateValueDifference(offeredLeads, targetLead);
        
        // Then
        assertEquals(BigDecimal.valueOf(-7), difference); // 1 - 8 = -7
    }
    
    @Test
    void testCalculateValueDifference_ZeroDifference() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadB, leadB); // 4 + 4 = 8
        Lead targetLead = leadA; // 8
        
        // When
        BigDecimal difference = exchangeValueCalculator.calculateValueDifference(offeredLeads, targetLead);
        
        // Then
        assertEquals(BigDecimal.ZERO, difference); // 8 - 8 = 0
    }
    
    @Test
    void testCalculateRequiredCredits_NoCreditsNeeded() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadA); // 8
        Lead targetLead = leadB; // 4
        
        // When
        BigDecimal requiredCredits = exchangeValueCalculator.calculateRequiredCredits(offeredLeads, targetLead);
        
        // Then
        assertEquals(BigDecimal.ZERO, requiredCredits);
    }
    
    @Test
    void testCalculateRequiredCredits_CreditsNeeded() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadD); // 1
        Lead targetLead = leadA; // 8
        
        // When
        BigDecimal requiredCredits = exchangeValueCalculator.calculateRequiredCredits(offeredLeads, targetLead);
        
        // Then
        assertEquals(BigDecimal.valueOf(7), requiredCredits); // 需要补充7积分
    }
    
    @Test
    void testValidateExchange_ValidExchange() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadB, leadB); // 4 + 4 = 8
        Lead targetLead = leadA; // 8
        BigDecimal additionalCredits = BigDecimal.ZERO;
        
        // When
        ExchangeValueCalculator.ExchangeValidationResult result = 
            exchangeValueCalculator.validateExchange(offeredLeads, targetLead, additionalCredits);
        
        // Then
        assertTrue(result.isValid());
        assertEquals("交换条件满足", result.getMessage());
    }
    
    @Test
    void testValidateExchange_ValidWithAdditionalCredits() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadD); // 1
        Lead targetLead = leadA; // 8
        BigDecimal additionalCredits = BigDecimal.valueOf(7); // 补充7积分
        
        // When
        ExchangeValueCalculator.ExchangeValidationResult result = 
            exchangeValueCalculator.validateExchange(offeredLeads, targetLead, additionalCredits);
        
        // Then
        assertTrue(result.isValid());
        assertEquals("交换条件满足", result.getMessage());
    }
    
    @Test
    void testValidateExchange_InsufficientValue() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadD); // 1
        Lead targetLead = leadA; // 8
        BigDecimal additionalCredits = BigDecimal.valueOf(5); // 只补充5积分，不够
        
        // When
        ExchangeValueCalculator.ExchangeValidationResult result = 
            exchangeValueCalculator.validateExchange(offeredLeads, targetLead, additionalCredits);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("提供的价值不足"));
    }
    
    @Test
    void testValidateExchange_NullTargetLead() {
        // Given
        List<Lead> offeredLeads = Arrays.asList(leadB);
        Lead targetLead = null;
        BigDecimal additionalCredits = BigDecimal.ZERO;
        
        // When
        ExchangeValueCalculator.ExchangeValidationResult result = 
            exchangeValueCalculator.validateExchange(offeredLeads, targetLead, additionalCredits);
        
        // Then
        assertFalse(result.isValid());
        assertEquals("目标线索不存在", result.getMessage());
    }
    
    @Test
    void testValidateExchange_NoOfferedLeadsOrCredits() {
        // Given
        List<Lead> offeredLeads = Collections.emptyList();
        Lead targetLead = leadA;
        BigDecimal additionalCredits = BigDecimal.ZERO;
        
        // When
        ExchangeValueCalculator.ExchangeValidationResult result = 
            exchangeValueCalculator.validateExchange(offeredLeads, targetLead, additionalCredits);
        
        // Then
        assertFalse(result.isValid());
        assertEquals("必须提供线索或积分", result.getMessage());
    }
    
    @Test
    void testGetRatingValue() {
        // When & Then
        assertEquals(8, exchangeValueCalculator.getRatingValue(LeadRating.A));
        assertEquals(4, exchangeValueCalculator.getRatingValue(LeadRating.B));
        assertEquals(2, exchangeValueCalculator.getRatingValue(LeadRating.C));
        assertEquals(1, exchangeValueCalculator.getRatingValue(LeadRating.D));
        assertEquals(0, exchangeValueCalculator.getRatingValue(null));
    }
    
    @Test
    void testCalculateExchangeableCount() {
        // Given
        BigDecimal availableCredits = BigDecimal.valueOf(10);
        
        // When & Then
        assertEquals(1, exchangeValueCalculator.calculateExchangeableCount(availableCredits, LeadRating.A)); // 10/8 = 1
        assertEquals(2, exchangeValueCalculator.calculateExchangeableCount(availableCredits, LeadRating.B)); // 10/4 = 2
        assertEquals(5, exchangeValueCalculator.calculateExchangeableCount(availableCredits, LeadRating.C)); // 10/2 = 5
        assertEquals(10, exchangeValueCalculator.calculateExchangeableCount(availableCredits, LeadRating.D)); // 10/1 = 10
    }
    
    @Test
    void testCalculateExchangeableCount_ZeroCredits() {
        // Given
        BigDecimal availableCredits = BigDecimal.ZERO;
        
        // When
        int count = exchangeValueCalculator.calculateExchangeableCount(availableCredits, LeadRating.A);
        
        // Then
        assertEquals(0, count);
    }
    
    @Test
    void testCalculateExchangeableCount_NullRating() {
        // Given
        BigDecimal availableCredits = BigDecimal.valueOf(10);
        
        // When
        int count = exchangeValueCalculator.calculateExchangeableCount(availableCredits, null);
        
        // Then
        assertEquals(0, count);
    }
}