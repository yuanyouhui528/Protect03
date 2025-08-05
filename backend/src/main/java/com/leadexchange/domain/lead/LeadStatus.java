package com.leadexchange.domain.lead;

/**
 * 线索状态枚举
 * 定义线索在系统中的各种状态
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum LeadStatus {
    
    /**
     * 草稿状态 - 线索刚创建，尚未发布
     */
    DRAFT("草稿"),
    
    /**
     * 待审核 - 线索已提交，等待审核
     */
    PENDING_AUDIT("待审核"),
    
    /**
     * 已发布 - 线索审核通过，已公开发布
     */
    PUBLISHED("已发布"),
    
    /**
     * 交换中 - 线索正在进行交换流程
     */
    EXCHANGING("交换中"),
    
    /**
     * 已成交 - 线索交换完成
     */
    COMPLETED("已成交"),
    
    /**
     * 已下架 - 线索被下架或删除
     */
    OFFLINE("已下架"),
    
    /**
     * 审核拒绝 - 线索审核未通过
     */
    REJECTED("审核拒绝");
    
    private final String description;
    
    LeadStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据描述获取状态枚举
     * 
     * @param description 状态描述
     * @return 对应的状态枚举
     */
    public static LeadStatus fromDescription(String description) {
        for (LeadStatus status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的线索状态: " + description);
    }
    
    /**
     * 判断是否为可交换状态
     * 
     * @return 是否可交换
     */
    public boolean isExchangeable() {
        return this == PUBLISHED;
    }
    
    /**
     * 判断是否为终态
     * 
     * @return 是否为终态
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == OFFLINE || this == REJECTED;
    }
}