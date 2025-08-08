package com.leadexchange.event.exchange;

import com.leadexchange.domain.exchange.ExchangeApplication;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 交换申请事件
 * 在交换申请状态变化时发布此事件
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class ExchangeApplicationEvent extends ApplicationEvent {
    
    /**
     * 事件类型枚举
     */
    public enum EventType {
        /** 申请提交 */
        APPLICATION_SUBMITTED("exchange.application.submitted", "交换申请已提交"),
        /** 申请提交（简化别名） */
        SUBMITTED("exchange.application.submitted", "交换申请已提交"),
        /** 申请批准 */
        APPLICATION_APPROVED("exchange.application.approved", "交换申请已批准"),
        /** 申请批准（简化别名） */
        APPROVED("exchange.application.approved", "交换申请已批准"),
        /** 申请拒绝 */
        APPLICATION_REJECTED("exchange.application.rejected", "交换申请已拒绝"),
        /** 申请拒绝（简化别名） */
        REJECTED("exchange.application.rejected", "交换申请已拒绝"),
        /** 申请取消 */
        APPLICATION_CANCELLED("exchange.application.cancelled", "交换申请已取消"),
        /** 申请取消（简化别名） */
        CANCELLED("exchange.application.cancelled", "交换申请已取消"),
        /** 交换完成 */
        EXCHANGE_COMPLETED("exchange.completed", "交换已完成"),
        /** 交换过期 */
        EXCHANGE_EXPIRED("exchange.expired", "交换已过期");
        
        private final String code;
        private final String description;
        
        EventType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /** 事件类型 */
    private final EventType eventType;
    
    /** 交换申请信息 */
    private final ExchangeApplication exchangeApplication;
    
    /** 申请人ID */
    private final Long applicantId;
    
    /** 目标用户ID */
    private final Long targetUserId;
    
    /** 申请的线索ID */
    private final Long requestedLeadId;
    
    /** 提供的线索ID */
    private final Long offeredLeadId;
    
    /** 事件发生时间 */
    private final LocalDateTime eventTime;
    
    /** 额外数据 */
    private final Map<String, Object> extraData;
    
    /** 操作人ID */
    private final Long operatorId;
    
    /** 备注信息 */
    private final String remark;
    
    /**
     * 构造函数
     * 
     * @param source 事件源
     * @param eventType 事件类型
     * @param exchangeApplication 交换申请
     * @param operatorId 操作人ID
     * @param remark 备注信息
     * @param extraData 额外数据
     */
    public ExchangeApplicationEvent(Object source, EventType eventType, 
                                   ExchangeApplication exchangeApplication, 
                                   Long operatorId, String remark, 
                                   Map<String, Object> extraData) {
        super(source);
        this.eventType = eventType;
        this.exchangeApplication = exchangeApplication;
        this.applicantId = exchangeApplication.getApplicantId();
        this.targetUserId = exchangeApplication.getTargetOwnerId();
        this.requestedLeadId = exchangeApplication.getTargetLeadId();
        this.offeredLeadId = parseFirstOfferedLeadId(exchangeApplication.getOfferedLeadIds());
        this.eventTime = LocalDateTime.now();
        this.operatorId = operatorId;
        this.remark = remark;
        this.extraData = extraData;
    }
    
    /**
     * 简化构造函数
     * 
     * @param source 事件源
     * @param eventType 事件类型
     * @param exchangeApplication 交换申请
     */
    public ExchangeApplicationEvent(Object source, EventType eventType, 
                                   ExchangeApplication exchangeApplication) {
        this(source, eventType, exchangeApplication, null, null, null);
    }
    
    /**
     * 带操作人的构造函数
     * 
     * @param source 事件源
     * @param eventType 事件类型
     * @param exchangeApplication 交换申请
     * @param operatorId 操作人ID
     */
    public ExchangeApplicationEvent(Object source, EventType eventType, 
                                   ExchangeApplication exchangeApplication, 
                                   Long operatorId) {
        this(source, eventType, exchangeApplication, operatorId, null, null);
    }
    
    // Getter 方法
    
    public EventType getEventType() {
        return eventType;
    }
    
    public ExchangeApplication getExchangeApplication() {
        return exchangeApplication;
    }
    
    public Long getApplicantId() {
        return applicantId;
    }
    
    public Long getTargetUserId() {
        return targetUserId;
    }
    
    public Long getRequestedLeadId() {
        return requestedLeadId;
    }
    
    public Long getOfferedLeadId() {
        return offeredLeadId;
    }
    
    public LocalDateTime getEventTime() {
        return eventTime;
    }
    
    public Map<String, Object> getExtraData() {
        return extraData;
    }
    
    public Long getOperatorId() {
        return operatorId;
    }
    
    public String getRemark() {
        return remark;
    }
    
    /**
     * 获取事件代码
     * 
     * @return 事件代码
     */
    public String getEventCode() {
        return eventType.getCode();
    }
    
    /**
     * 获取事件描述
     * 
     * @return 事件描述
     */
    public String getEventDescription() {
        return eventType.getDescription();
    }
    
    /**
     * 获取申请ID
     * 
     * @return 申请ID
     */
    public Long getApplicationId() {
        return exchangeApplication != null ? exchangeApplication.getId() : null;
    }
    
    /**
     * 获取申请状态
     * 
     * @return 申请状态
     */
    public String getApplicationStatus() {
        return exchangeApplication != null ? exchangeApplication.getStatus().name() : null;
    }
    
    /**
     * 判断是否为申请相关事件
     * 
     * @return 是否为申请相关事件
     */
    public boolean isApplicationEvent() {
        return eventType == EventType.APPLICATION_SUBMITTED ||
               eventType == EventType.APPLICATION_APPROVED ||
               eventType == EventType.APPLICATION_REJECTED ||
               eventType == EventType.APPLICATION_CANCELLED;
    }
    
    /**
     * 判断是否为交换相关事件
     * 
     * @return 是否为交换相关事件
     */
    public boolean isExchangeEvent() {
        return eventType == EventType.EXCHANGE_COMPLETED ||
               eventType == EventType.EXCHANGE_EXPIRED;
    }
    
    /**
     * 判断是否为成功事件
     * 
     * @return 是否为成功事件
     */
    public boolean isSuccessEvent() {
        return eventType == EventType.APPLICATION_APPROVED ||
               eventType == EventType.EXCHANGE_COMPLETED;
    }
    
    /**
     * 判断是否为失败事件
     * 
     * @return 是否为失败事件
     */
    public boolean isFailureEvent() {
        return eventType == EventType.APPLICATION_REJECTED ||
               eventType == EventType.APPLICATION_CANCELLED ||
               eventType == EventType.EXCHANGE_EXPIRED;
    }
    
    /**
     * 解析第一个提供的线索ID
     * 
     * @param offeredLeadIds 提供的线索ID列表（JSON格式）
     * @return 第一个线索ID，如果解析失败则返回null
     */
    private Long parseFirstOfferedLeadId(String offeredLeadIds) {
        if (offeredLeadIds == null || offeredLeadIds.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 简单解析JSON数组格式的字符串，如 "[1,2,3]" 或 "1,2,3"
            String cleaned = offeredLeadIds.trim().replaceAll("[\\[\\]]", "");
            String[] ids = cleaned.split(",");
            if (ids.length > 0) {
                return Long.parseLong(ids[0].trim());
            }
        } catch (Exception e) {
            // 解析失败，返回null
        }
        
        return null;
    }
    
    /**
     * 获取需要通知的用户ID列表
     * 
     * @return 用户ID列表
     */
    public java.util.List<Long> getNotificationRecipients() {
        java.util.List<Long> recipients = new java.util.ArrayList<>();
        
        // 根据事件类型确定通知对象
        switch (eventType) {
            case APPLICATION_SUBMITTED:
                // 申请提交：通知目标用户
                if (targetUserId != null) {
                    recipients.add(targetUserId);
                }
                break;
            case APPLICATION_APPROVED:
            case APPLICATION_REJECTED:
            case APPLICATION_CANCELLED:
                // 申请处理：通知申请人
                if (applicantId != null) {
                    recipients.add(applicantId);
                }
                break;
            case EXCHANGE_COMPLETED:
            case EXCHANGE_EXPIRED:
                // 交换完成/过期：通知双方
                if (applicantId != null) {
                    recipients.add(applicantId);
                }
                if (targetUserId != null) {
                    recipients.add(targetUserId);
                }
                break;
        }
        
        return recipients;
    }
    
    @Override
    public String toString() {
        return "ExchangeApplicationEvent{" +
                "eventType=" + eventType +
                ", applicationId=" + getApplicationId() +
                ", applicantId=" + applicantId +
                ", targetUserId=" + targetUserId +
                ", requestedLeadId=" + requestedLeadId +
                ", offeredLeadId=" + offeredLeadId +
                ", eventTime=" + eventTime +
                ", operatorId=" + operatorId +
                ", remark='" + remark + '\'' +
                '}';
    }
}