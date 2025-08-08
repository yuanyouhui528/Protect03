package com.leadexchange.domain.exchange;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交换历史实体类
 * 记录交换的完整历史信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "exchange_history")
public class ExchangeHistory {
    
    /**
     * 历史记录ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 交换申请ID
     */
    @Column(name = "exchange_id", nullable = false)
    private Long exchangeId;
    
    /**
     * 申请人ID
     */
    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;
    
    /**
     * 申请人姓名
     */
    @Column(name = "applicant_name", nullable = false, length = 50)
    private String applicantName;
    
    /**
     * 目标线索所有者ID
     */
    @Column(name = "target_owner_id", nullable = false)
    private Long targetOwnerId;
    
    /**
     * 目标线索所有者姓名
     */
    @Column(name = "target_owner_name", nullable = false, length = 50)
    private String targetOwnerName;
    
    /**
     * 目标线索ID
     */
    @Column(name = "target_lead_id", nullable = false)
    private Long targetLeadId;
    
    /**
     * 目标线索标题
     */
    @Column(name = "target_lead_title", nullable = false, length = 200)
    private String targetLeadTitle;
    
    /**
     * 提供的线索ID列表（JSON格式）
     */
    @Column(name = "offered_lead_ids", columnDefinition = "TEXT")
    private String offeredLeadIds;
    
    /**
     * 提供的线索标题列表（JSON格式）
     */
    @Column(name = "offered_lead_titles", columnDefinition = "TEXT")
    private String offeredLeadTitles;
    
    /**
     * 交换积分
     */
    @Column(name = "exchange_credits", nullable = false, precision = 10, scale = 2)
    private BigDecimal exchangeCredits;
    
    /**
     * 目标线索价值
     */
    @Column(name = "target_lead_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetLeadValue;
    
    /**
     * 积分差额
     */
    @Column(name = "credit_difference", nullable = false, precision = 10, scale = 2)
    private BigDecimal creditDifference;
    
    /**
     * 交换理由
     */
    @Column(name = "exchange_reason", length = 500)
    private String exchangeReason;
    
    /**
     * 最终状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "final_status", nullable = false)
    private ExchangeStatus finalStatus;
    
    /**
     * 响应消息
     */
    @Column(name = "response_message", length = 500)
    private String responseMessage;
    
    /**
     * 申请时间
     */
    @Column(name = "apply_time", nullable = false)
    private LocalDateTime applyTime;
    
    /**
     * 响应时间
     */
    @Column(name = "response_time")
    private LocalDateTime responseTime;
    
    /**
     * 完成时间
     */
    @Column(name = "complete_time")
    private LocalDateTime completeTime;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    private Long createBy;
    
    // 构造函数
    public ExchangeHistory() {
        this.createTime = LocalDateTime.now();
    }
    
    /**
     * 从交换申请创建历史记录
     * @param application 交换申请
     * @param applicantName 申请人姓名
     * @param targetOwnerName 目标所有者姓名
     * @param targetLeadTitle 目标线索标题
     * @param offeredLeadTitles 提供的线索标题列表
     * @return 交换历史记录
     */
    public static ExchangeHistory fromApplication(
            ExchangeApplication application,
            String applicantName,
            String targetOwnerName,
            String targetLeadTitle,
            String offeredLeadTitles) {
        
        ExchangeHistory history = new ExchangeHistory();
        history.setExchangeId(application.getId());
        history.setApplicantId(application.getApplicantId());
        history.setApplicantName(applicantName);
        history.setTargetOwnerId(application.getTargetOwnerId());
        history.setTargetOwnerName(targetOwnerName);
        history.setTargetLeadId(application.getTargetLeadId());
        history.setTargetLeadTitle(targetLeadTitle);
        history.setOfferedLeadIds(application.getOfferedLeadIds());
        history.setOfferedLeadTitles(offeredLeadTitles);
        history.setExchangeCredits(application.getOfferedCredits());
        history.setTargetLeadValue(application.getTargetLeadValue());
        history.setCreditDifference(application.getCreditDifference());
        history.setExchangeReason(application.getExchangeReason());
        history.setFinalStatus(application.getStatus());
        history.setResponseMessage(application.getResponseMessage());
        history.setApplyTime(application.getCreateTime());
        
        // 设置响应时间
        if (application.getStatus() == ExchangeStatus.APPROVED || 
            application.getStatus() == ExchangeStatus.REJECTED) {
            history.setResponseTime(application.getUpdateTime());
        }
        
        // 设置完成时间
        if (application.getStatus() == ExchangeStatus.COMPLETED) {
            history.setCompleteTime(application.getCompletedTime());
        }
        
        return history;
    }
    
    /**
     * 判断交换是否成功
     * @return true表示交换成功，false表示交换失败或未完成
     */
    public boolean isSuccessful() {
        return this.finalStatus == ExchangeStatus.COMPLETED;
    }
    
    /**
     * 获取交换耗时（从申请到完成的时间，单位：小时）
     * @return 交换耗时，如果未完成则返回null
     */
    public Long getExchangeDurationHours() {
        if (this.completeTime == null) {
            return null;
        }
        return java.time.Duration.between(this.applyTime, this.completeTime).toHours();
    }
    
    /**
     * 获取响应耗时（从申请到响应的时间，单位：小时）
     * @return 响应耗时，如果未响应则返回null
     */
    public Long getResponseDurationHours() {
        if (this.responseTime == null) {
            return null;
        }
        return java.time.Duration.between(this.applyTime, this.responseTime).toHours();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getExchangeId() {
        return exchangeId;
    }
    
    public void setExchangeId(Long exchangeId) {
        this.exchangeId = exchangeId;
    }
    
    public Long getApplicantId() {
        return applicantId;
    }
    
    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }
    
    public String getApplicantName() {
        return applicantName;
    }
    
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public Long getTargetOwnerId() {
        return targetOwnerId;
    }
    
    public void setTargetOwnerId(Long targetOwnerId) {
        this.targetOwnerId = targetOwnerId;
    }
    
    public String getTargetOwnerName() {
        return targetOwnerName;
    }
    
    public void setTargetOwnerName(String targetOwnerName) {
        this.targetOwnerName = targetOwnerName;
    }
    
    public Long getTargetLeadId() {
        return targetLeadId;
    }
    
    public void setTargetLeadId(Long targetLeadId) {
        this.targetLeadId = targetLeadId;
    }
    
    public String getTargetLeadTitle() {
        return targetLeadTitle;
    }
    
    public void setTargetLeadTitle(String targetLeadTitle) {
        this.targetLeadTitle = targetLeadTitle;
    }
    
    public String getOfferedLeadIds() {
        return offeredLeadIds;
    }
    
    public void setOfferedLeadIds(String offeredLeadIds) {
        this.offeredLeadIds = offeredLeadIds;
    }
    
    public String getOfferedLeadTitles() {
        return offeredLeadTitles;
    }
    
    public void setOfferedLeadTitles(String offeredLeadTitles) {
        this.offeredLeadTitles = offeredLeadTitles;
    }
    
    public BigDecimal getExchangeCredits() {
        return exchangeCredits;
    }
    
    public void setExchangeCredits(BigDecimal exchangeCredits) {
        this.exchangeCredits = exchangeCredits;
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
    
    public ExchangeStatus getFinalStatus() {
        return finalStatus;
    }
    
    public void setFinalStatus(ExchangeStatus finalStatus) {
        this.finalStatus = finalStatus;
    }
    
    public String getResponseMessage() {
        return responseMessage;
    }
    
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    
    public LocalDateTime getApplyTime() {
        return applyTime;
    }
    
    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }
    
    public LocalDateTime getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(LocalDateTime responseTime) {
        this.responseTime = responseTime;
    }
    
    public LocalDateTime getCompleteTime() {
        return completeTime;
    }
    
    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public Long getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
}