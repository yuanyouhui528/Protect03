package com.leadexchange.domain.exchange;

/**
 * 交易类型枚举
 * 定义积分交易的各种类型
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum TransactionType {
    
    /**
     * 收入 - 积分增加
     */
    INCOME("收入"),
    
    /**
     * 支出 - 积分减少
     */
    EXPENSE("支出"),
    
    /**
     * 冻结 - 积分被冻结
     */
    FREEZE("冻结"),
    
    /**
     * 解冻 - 积分解冻
     */
    UNFREEZE("解冻"),
    
    /**
     * 退款 - 积分退还
     */
    REFUND("退款");
    
    private final String description;
    
    TransactionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 判断是否为积分增加类型
     * @return true表示积分增加，false表示积分减少或无变化
     */
    public boolean isPositive() {
        return this == INCOME || this == UNFREEZE || this == REFUND;
    }
    
    /**
     * 判断是否为积分减少类型
     * @return true表示积分减少，false表示积分增加或无变化
     */
    public boolean isNegative() {
        return this == EXPENSE || this == FREEZE;
    }
}