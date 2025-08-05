package com.leadexchange.domain.lead;

/**
 * 审核状态枚举
 * 定义线索审核的各种状态
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum AuditStatus {
    
    /**
     * 待审核 - 线索已提交，等待审核
     */
    PENDING("待审核"),
    
    /**
     * 审核中 - 线索正在审核过程中
     */
    REVIEWING("审核中"),
    
    /**
     * 审核通过 - 线索审核通过，可以发布
     */
    APPROVED("审核通过"),
    
    /**
     * 审核拒绝 - 线索审核未通过
     */
    REJECTED("审核拒绝"),
    
    /**
     * 需要补充 - 线索信息不完整，需要补充
     */
    NEED_SUPPLEMENT("需要补充");
    
    private final String description;
    
    AuditStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据描述获取审核状态枚举
     * 
     * @param description 状态描述
     * @return 对应的审核状态枚举
     */
    public static AuditStatus fromDescription(String description) {
        for (AuditStatus status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的审核状态: " + description);
    }
    
    /**
     * 判断是否为待处理状态
     * 
     * @return 是否为待处理状态
     */
    public boolean isPending() {
        return this == PENDING || this == REVIEWING || this == NEED_SUPPLEMENT;
    }
    
    /**
     * 判断是否为最终状态
     * 
     * @return 是否为最终状态
     */
    public boolean isFinalStatus() {
        return this == APPROVED || this == REJECTED;
    }
    
    /**
     * 判断是否审核通过
     * 
     * @return 是否审核通过
     */
    public boolean isApproved() {
        return this == APPROVED;
    }
}