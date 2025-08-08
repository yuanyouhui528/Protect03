package com.leadexchange.domain.exchange;

/**
 * 交换申请状态枚举
 * 定义交换申请的各种状态
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum ExchangeStatus {
    
    /**
     * 待处理 - 交换申请已提交，等待对方响应
     */
    PENDING("待处理"),
    
    /**
     * 已同意 - 对方已同意交换申请
     */
    APPROVED("已同意"),
    
    /**
     * 已拒绝 - 对方拒绝了交换申请
     */
    REJECTED("已拒绝"),
    
    /**
     * 已取消 - 申请人主动取消了交换申请
     */
    CANCELLED("已取消"),
    
    /**
     * 已完成 - 交换已成功完成
     */
    COMPLETED("已完成"),
    
    /**
     * 已过期 - 交换申请已过期
     */
    EXPIRED("已过期");
    
    private final String description;
    
    ExchangeStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 判断是否为终态状态
     * @return true表示为终态，false表示可以继续流转
     */
    public boolean isFinalStatus() {
        return this == COMPLETED || this == REJECTED || 
               this == CANCELLED || this == EXPIRED;
    }
    
    /**
     * 判断是否可以取消
     * @return true表示可以取消，false表示不可取消
     */
    public boolean canCancel() {
        return this == PENDING;
    }
    
    /**
     * 判断是否可以审核
     * @return true表示可以审核，false表示不可审核
     */
    public boolean canReview() {
        return this == PENDING;
    }
}