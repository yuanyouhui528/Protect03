package com.leadexchange.util;

import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 交换价值计算引擎
 * 实现线索交换价值计算算法
 * A级=8分, B级=4分, C级=2分, D级=1分
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Component
public class ExchangeValueCalculator {
    
    /**
     * 线索评级对应的积分值
     */
    private static final Map<LeadRating, Integer> RATING_VALUES = Map.of(
        LeadRating.A, 8,
        LeadRating.B, 4,
        LeadRating.C, 2,
        LeadRating.D, 1
    );
    
    /**
     * 计算单个线索的价值
     * @param lead 线索对象
     * @return 线索价值（积分）
     */
    public BigDecimal calculateLeadValue(Lead lead) {
        if (lead == null || lead.getRating() == null) {
            return BigDecimal.ZERO;
        }
        
        Integer baseValue = RATING_VALUES.get(lead.getRating());
        if (baseValue == null) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(baseValue);
    }
    
    /**
     * 计算多个线索的总价值
     * @param leads 线索列表
     * @return 总价值（积分）
     */
    public BigDecimal calculateTotalValue(List<Lead> leads) {
        if (leads == null || leads.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return leads.stream()
                .map(this::calculateLeadValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 计算交换是否公平
     * @param offeredLeads 提供的线索列表
     * @param targetLead 目标线索
     * @return true表示交换公平，false表示不公平
     */
    public boolean isExchangeFair(List<Lead> offeredLeads, Lead targetLead) {
        BigDecimal offeredValue = calculateTotalValue(offeredLeads);
        BigDecimal targetValue = calculateLeadValue(targetLead);
        
        // 允许10%的价值差异
        BigDecimal tolerance = targetValue.multiply(BigDecimal.valueOf(0.1));
        BigDecimal difference = offeredValue.subtract(targetValue).abs();
        
        return difference.compareTo(tolerance) <= 0;
    }
    
    /**
     * 计算交换价值差额
     * @param offeredLeads 提供的线索列表
     * @param targetLead 目标线索
     * @return 价值差额（正数表示提供方价值更高，负数表示目标方价值更高）
     */
    public BigDecimal calculateValueDifference(List<Lead> offeredLeads, Lead targetLead) {
        BigDecimal offeredValue = calculateTotalValue(offeredLeads);
        BigDecimal targetValue = calculateLeadValue(targetLead);
        
        return offeredValue.subtract(targetValue);
    }
    
    /**
     * 计算需要补充的积分
     * @param offeredLeads 提供的线索列表
     * @param targetLead 目标线索
     * @return 需要补充的积分（正数表示申请方需要补充，负数表示目标方需要补充）
     */
    public BigDecimal calculateRequiredCredits(List<Lead> offeredLeads, Lead targetLead) {
        BigDecimal valueDifference = calculateValueDifference(offeredLeads, targetLead);
        
        // 如果提供方价值不足，需要补充积分
        if (valueDifference.compareTo(BigDecimal.ZERO) < 0) {
            return valueDifference.abs();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * 验证交换条件是否满足
     * @param offeredLeads 提供的线索列表
     * @param targetLead 目标线索
     * @param additionalCredits 额外提供的积分
     * @return 验证结果
     */
    public ExchangeValidationResult validateExchange(
            List<Lead> offeredLeads, Lead targetLead, BigDecimal additionalCredits) {
        
        if (targetLead == null) {
            return new ExchangeValidationResult(false, "目标线索不存在");
        }
        
        if (offeredLeads == null || offeredLeads.isEmpty()) {
            if (additionalCredits == null || additionalCredits.compareTo(BigDecimal.ZERO) <= 0) {
                return new ExchangeValidationResult(false, "必须提供线索或积分");
            }
        }
        
        BigDecimal offeredValue = calculateTotalValue(offeredLeads);
        BigDecimal targetValue = calculateLeadValue(targetLead);
        BigDecimal totalOfferedValue = offeredValue;
        
        if (additionalCredits != null && additionalCredits.compareTo(BigDecimal.ZERO) > 0) {
            totalOfferedValue = totalOfferedValue.add(additionalCredits);
        }
        
        if (totalOfferedValue.compareTo(targetValue) < 0) {
            BigDecimal shortage = targetValue.subtract(totalOfferedValue);
            return new ExchangeValidationResult(false, 
                String.format("提供的价值不足，还需要%.2f积分", shortage.doubleValue()));
        }
        
        return new ExchangeValidationResult(true, "交换条件满足");
    }
    
    /**
     * 根据评级获取积分值
     * @param rating 线索评级
     * @return 对应的积分值
     */
    public int getRatingValue(LeadRating rating) {
        return RATING_VALUES.getOrDefault(rating, 0);
    }
    
    /**
     * 计算线索列表的总价值（别名方法，与calculateTotalValue功能相同）
     * @param leads 线索列表
     * @return 总价值（积分）
     */
    public BigDecimal calculateLeadsValue(List<Lead> leads) {
        return calculateTotalValue(leads);
    }
    
    /**
     * 计算可以交换的线索数量
     * @param availableCredits 可用积分
     * @param targetRating 目标线索评级
     * @return 可以交换的线索数量
     */
    public int calculateExchangeableCount(BigDecimal availableCredits, LeadRating targetRating) {
        int ratingValue = getRatingValue(targetRating);
        if (ratingValue == 0) {
            return 0;
        }
        
        return availableCredits.divide(BigDecimal.valueOf(ratingValue), 0, BigDecimal.ROUND_DOWN).intValue();
    }
    
    /**
     * 交换验证结果
     */
    public static class ExchangeValidationResult {
        private final boolean valid;
        private final String message;
        
        public ExchangeValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}