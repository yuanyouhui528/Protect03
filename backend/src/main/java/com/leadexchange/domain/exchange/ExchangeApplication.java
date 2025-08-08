package com.leadexchange.domain.exchange;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交换申请实体类
 * 记录线索交换申请的详细信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "exchange_applications")
public class ExchangeApplication {
    
    /**
     * 申请ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 申请人ID
     */
    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;
    
    /**
     * 目标线索ID
     */
    @Column(name = "target_lead_id", nullable = false)
    private Long targetLeadId;
    
    /**
     * 目标线索所有者ID
     */
    @Column(name = "target_owner_id", nullable = false)
    private Long targetOwnerId;
    
    /**
     * 提供的线索ID列表（JSON格式存储）
     */
    @Column(name = "offered_lead_ids", columnDefinition = "TEXT")
    private String offeredLeadIds;
    
    /**
     * 申请积分
     */
    @Column(name = "offered_credits", nullable = false, precision = 10, scale = 2)
    private BigDecimal offeredCredits;
    
    /**
     * 目标线索价值
     */
    @Column(name = "target_lead_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetLeadValue;
    
    /**
     * 积分差额
     */
    @Column(name = "credit_difference", nullable = false, precision = 10, scale = 2)
    private BigDecimal creditDifference = BigDecimal.ZERO;
    
    /**
     * 交换理由
     */
    @Column(name = "exchange_reason", length = 500)
    private String exchangeReason;
    
    /**
     * 申请状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExchangeStatus status = ExchangeStatus.PENDING;
    
    /**
     * 响应消息
     */
    @Column(name = "response_message", length = 500)
    private String responseMessage;
    
    /**
     * 过期时间
     */
    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;
    
    /**
     * 同意时间
     */
    @Column(name = "approved_time")
    private LocalDateTime approvedTime;
    
    /**
     * 完成时间
     */
    @Column(name = "completed_time")
    private LocalDateTime completedTime;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    
    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    private Long createBy;
    
    /**
     * 更新人ID
     */
    @Column(name = "update_by")
    private Long updateBy;
    
    // 构造函数
    public ExchangeApplication() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        // 默认7天过期
        this.expireTime = LocalDateTime.now().plusDays(7);
    }
    
    public ExchangeApplication(Long applicantId, Long targetLeadId, Long targetOwnerId) {
        this();
        this.applicantId = applicantId;
        this.targetLeadId = targetLeadId;
        this.targetOwnerId = targetOwnerId;
    }
    
    // 业务方法
    
    /**
     * 审核通过
     * @param responseMessage 响应消息
     */
    public void approve(String responseMessage) {
        if (!this.status.canReview()) {
            throw new IllegalStateException("当前状态不允许审核：" + this.status.getDescription());
        }
        this.status = ExchangeStatus.APPROVED;
        this.responseMessage = responseMessage;
        this.approvedTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 审核拒绝
     * @param responseMessage 拒绝原因
     */
    public void reject(String responseMessage) {
        if (!this.status.canReview()) {
            throw new IllegalStateException("当前状态不允许审核：" + this.status.getDescription());
        }
        this.status = ExchangeStatus.REJECTED;
        this.responseMessage = responseMessage;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 取消申请
     * @param reason 取消原因
     */
    public void cancel(String reason) {
        if (!this.status.canCancel()) {
            throw new IllegalStateException("当前状态不允许取消：" + this.status.getDescription());
        }
        this.status = ExchangeStatus.CANCELLED;
        this.responseMessage = reason;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 完成交换
     */
    public void complete() {
        if (this.status != ExchangeStatus.APPROVED) {
            throw new IllegalStateException("只有已同意的申请才能完成交换：" + this.status.getDescription());
        }
        this.status = ExchangeStatus.COMPLETED;
        this.completedTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 设置为过期状态
     */
    public void expire() {
        if (this.status.isFinalStatus()) {
            return; // 已经是终态，不需要设置过期
        }
        this.status = ExchangeStatus.EXPIRED;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 检查是否已过期
     * @return true表示已过期，false表示未过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expireTime) && !this.status.isFinalStatus();
    }
    
    /**
     * 计算积分差额
     * @return 积分差额（正数表示申请人需要补差价，负数表示目标方需要补差价）
     */
    public BigDecimal calculateCreditDifference() {
        return this.targetLeadValue.subtract(this.offeredCredits);
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getApplicantId() {
        return applicantId;
    }
    
    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }
    
    public Long getTargetLeadId() {
        return targetLeadId;
    }
    
    public void setTargetLeadId(Long targetLeadId) {
        this.targetLeadId = targetLeadId;
    }
    
    public Long getTargetOwnerId() {
        return targetOwnerId;
    }
    
    public void setTargetOwnerId(Long targetOwnerId) {
        this.targetOwnerId = targetOwnerId;
    }
    
    public String getOfferedLeadIds() {
        return offeredLeadIds;
    }
    
    public void setOfferedLeadIds(String offeredLeadIds) {
        this.offeredLeadIds = offeredLeadIds;
    }
    
    public BigDecimal getOfferedCredits() {
        return offeredCredits;
    }
    
    public void setOfferedCredits(BigDecimal offeredCredits) {
        this.offeredCredits = offeredCredits;
    }
    
    public BigDecimal getTargetLeadValue() {
        return targetLeadValue;
    }
    
    public void setTargetLeadValue(BigDecimal targetLeadValue) {
        this.targetLeadValue = targetLeadValue;
    }
    
    public BigDecimal getCreditDifference() {
        return creditDifference;
    }
    
    public void setCreditDifference(BigDecimal creditDifference) {
        this.creditDifference = creditDifference;
    }
    
    public String getExchangeReason() {
        return exchangeReason;
    }
    
    public void setExchangeReason(String exchangeReason) {
        this.exchangeReason = exchangeReason;
    }
    
    public ExchangeStatus getStatus() {
        return status;
    }
    
    public void setStatus(ExchangeStatus status) {
        this.status = status;
    }
    
    public String getResponseMessage() {
        return responseMessage;
    }
    
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    public LocalDateTime getApprovedTime() {
        return approvedTime;
    }
    
    public void setApprovedTime(LocalDateTime approvedTime) {
        this.approvedTime = approvedTime;
    }
    
    public LocalDateTime getCompletedTime() {
        return completedTime;
    }
    
    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public Long getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
    
    public Long getUpdateBy() {
        return updateBy;
    }
    
    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }
    
    /**
     * 设置所需积分
     * @param requiredCredits 所需积分
     */
    public void setRequiredCredits(BigDecimal requiredCredits) {
        this.offeredCredits = requiredCredits;
    }
    
    /**
     * 获取所需积分
     * @return 所需积分
     */
    public BigDecimal getRequiredCredits() {
        return this.offeredCredits;
    }
    
    /**
     * 设置交换原因
     * @param reason 交换原因
     */
    public void setReason(String reason) {
        this.exchangeReason = reason;
    }
    
    /**
     * 获取交换原因
     * @return 交换原因
     */
    public String getReason() {
        return this.exchangeReason;
    }
    
    /**
     * 设置过期时间
     * @param expiryTime 过期时间
     */
    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expireTime = expiryTime;
    }
    
    /**
     * 获取过期时间
     * @return 过期时间
     */
    public LocalDateTime getExpiryTime() {
        return this.expireTime;
    }
    
    /**
     * 设置创建人
     * @param createdBy 创建人ID
     */
    public void setCreatedBy(Long createdBy) {
        this.createBy = createdBy;
    }
    
    /**
     * 获取创建人
     * @return 创建人ID
     */
    public Long getCreatedBy() {
        return this.createBy;
    }
    
    /**
     * 设置更新人
     * @param updatedBy 更新人ID
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updateBy = updatedBy;
    }
    
    /**
     * 获取更新人
     * @return 更新人ID
     */
    public Long getUpdatedBy() {
        return this.updateBy;
    }
    
    /**
     * 获取目标线索所有者ID
     * @return 目标线索所有者ID
     */
    public Long getTargetLeadOwnerId() {
        return this.targetOwnerId;
    }
    
    /**
     * 设置目标线索所有者ID
     * @param targetLeadOwnerId 目标线索所有者ID
     */
    public void setTargetLeadOwnerId(Long targetLeadOwnerId) {
        this.targetOwnerId = targetLeadOwnerId;
    }
    
    /**
     * 设置创建时间（兼容性方法）
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createTime = createdAt;
    }
    
    /**
     * 获取创建时间（兼容性方法）
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return this.createTime;
    }
    
    /**
     * 获取审核时间（兼容性方法）
     * @return 审核时间
     */
    public LocalDateTime getReviewedAt() {
        return this.approvedTime;
    }
    
    /**
     * 设置审核时间（兼容性方法）
     * @param reviewedAt 审核时间
     */
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.approvedTime = reviewedAt;
    }
}