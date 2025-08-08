package com.leadexchange.domain.notification;

import com.leadexchange.common.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知模板实体类
 * 对应数据库notification_templates表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "notification_templates")
public class NotificationTemplate extends BaseEntity {
    
    /**
     * 模板名称
     */
    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;
    
    /**
     * 模板编码
     */
    @Column(name = "template_code", nullable = false, length = 50, unique = true)
    private String templateCode;
    
    /**
     * 模板类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", nullable = false, length = 20)
    private TemplateType templateType;
    
    /**
     * 事件类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private NotificationType eventType;
    
    /**
     * 模板标题
     */
    @Column(name = "template_title", nullable = false, length = 200)
    private String templateTitle;
    
    /**
     * 模板内容
     */
    @Column(name = "template_content", nullable = false, columnDefinition = "TEXT")
    private String templateContent;
    
    /**
     * 模板变量（JSON格式）
     */
    @Column(name = "template_variables", columnDefinition = "TEXT")
    private String templateVariables;
    
    /**
     * 优先级（1-10，数字越大优先级越高）
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 5;
    
    /**
     * 状态（ACTIVE：启用，INACTIVE：禁用）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TemplateStatus status = TemplateStatus.ACTIVE;
    
    /**
     * 最大重试次数
     */
    @Column(name = "max_retry_count", nullable = false)
    private Integer maxRetryCount = 3;
    
    /**
     * 过期时间（小时）
     */
    @Column(name = "expire_hours", nullable = false)
    private Integer expireHours = 24;
    
    /**
     * 是否启用
     */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;
    
    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
    
    // ==================== 构造方法 ====================
    
    public NotificationTemplate() {}
    
    public NotificationTemplate(String templateName, String templateCode, 
                              TemplateType templateType, NotificationType eventType,
                              String templateTitle, String templateContent) {
        this.templateName = templateName;
        this.templateCode = templateCode;
        this.templateType = templateType;
        this.eventType = eventType;
        this.templateTitle = templateTitle;
        this.templateContent = templateContent;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 判断模板是否可用
     * 
     * @return 是否可用
     */
    public boolean isAvailable() {
        return isEnabled && TemplateStatus.ACTIVE.equals(status);
    }
    
    /**
     * 启用模板
     */
    public void enable() {
        this.isEnabled = true;
        this.status = TemplateStatus.ACTIVE;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 禁用模板
     */
    public void disable() {
        this.isEnabled = false;
        this.status = TemplateStatus.DISABLED;
        this.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 渲染模板内容
     * 
     * @param variables 变量映射
     * @return 渲染后的内容
     */
    public String renderContent(Map<String, Object> variables) {
        String content = this.templateContent;
        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                content = content.replace(placeholder, value);
            }
        }
        return content;
    }
    
    /**
     * 渲染模板标题
     * 
     * @param variables 变量映射
     * @return 渲染后的标题
     */
    public String renderTitle(Map<String, Object> variables) {
        String title = this.templateTitle;
        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                title = title.replace(placeholder, value);
            }
        }
        return title;
    }
    
    // ==================== Getter和Setter ====================
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public String getTemplateCode() {
        return templateCode;
    }
    
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
    
    public TemplateType getTemplateType() {
        return templateType;
    }
    
    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }
    
    public NotificationType getEventType() {
        return eventType;
    }
    
    public void setEventType(NotificationType eventType) {
        this.eventType = eventType;
    }
    
    public String getTemplateTitle() {
        return templateTitle;
    }
    
    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }
    
    public String getTemplateContent() {
        return templateContent;
    }
    
    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }
    
    public String getTemplateVariables() {
        return templateVariables;
    }
    
    public void setTemplateVariables(String templateVariables) {
        this.templateVariables = templateVariables;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public TemplateStatus getStatus() {
        return status;
    }
    
    public void setStatus(TemplateStatus status) {
        this.status = status;
    }
    
    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }
    
    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
    
    public Integer getExpireHours() {
        return expireHours;
    }
    
    public void setExpireHours(Integer expireHours) {
        this.expireHours = expireHours;
    }
    
    public Boolean getIsEnabled() {
        return isEnabled;
    }
    
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    // ==================== 简化的Getter方法 ====================
    
    /**
     * 获取模板标题（简化方法）
     * 
     * @return 模板标题
     */
    public String getTitle() {
        return templateTitle;
    }
    
    /**
     * 设置模板标题（简化方法）
     * 
     * @param title 模板标题
     */
    public void setTitle(String title) {
        this.templateTitle = title;
    }
    
    /**
     * 获取模板内容（简化方法）
     * 
     * @return 模板内容
     */
    public String getContent() {
        return templateContent;
    }
    
    /**
     * 设置模板内容（简化方法）
     * 
     * @param content 模板内容
     */
    public void setContent(String content) {
        this.templateContent = content;
    }
    
    /**
     * 获取模板变量（简化方法）
     * 
     * @return 模板变量
     */
    public String getVariables() {
        return templateVariables;
    }
    
    /**
     * 设置模板变量（简化方法）
     * 
     * @param variables 模板变量
     */
    public void setVariables(String variables) {
        this.templateVariables = variables;
    }
    
    /**
     * 获取通知类型（eventType的别名方法）
     * 
     * @return 通知类型
     */
    public NotificationType getNotificationType() {
        return eventType;
    }
    
    /**
     * 设置通知类型（eventType的别名方法）
     * 
     * @param notificationType 通知类型
     */
    public void setNotificationType(NotificationType notificationType) {
        this.eventType = notificationType;
    }
    
    /**
     * 获取过期时间（基于创建时间和过期小时数计算）
     * 
     * @return 过期时间
     */
    public LocalDateTime getExpireTime() {
        if (getCreateTime() != null && expireHours != null) {
            return getCreateTime().plusHours(expireHours);
        }
        return null;
    }
    
    /**
     * 设置过期时间（通过计算小时数设置）
     * 
     * @param expireTime 过期时间
     */
    public void setExpireTime(LocalDateTime expireTime) {
        if (expireTime != null && getCreateTime() != null) {
            long hours = java.time.Duration.between(getCreateTime(), expireTime).toHours();
            this.expireHours = (int) Math.max(1, hours); // 至少1小时
        }
    }
}