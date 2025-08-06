package com.leadexchange.domain.lead;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 线索浏览记录实体类
 * 对应数据库lead_views表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "lead_views")
public class LeadView {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 线索ID
     */
    @Column(name = "lead_id", nullable = false)
    private Long leadId;
    
    /**
     * 用户ID（可为空，支持匿名浏览）
     */
    @Column(name = "user_id")
    private Long userId;
    
    /**
     * 浏览时间
     */
    @Column(name = "view_time", nullable = false)
    private LocalDateTime viewTime;
    
    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    /**
     * 用户代理信息
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;
    
    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    /**
     * 更新人ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    /**
     * 默认构造函数
     */
    public LeadView() {
        this.viewTime = LocalDateTime.now();
        this.createdTime = LocalDateTime.now();
    }
    
    /**
     * 构造函数
     * 
     * @param leadId 线索ID
     * @param userId 用户ID
     * @param ipAddress IP地址
     */
    public LeadView(Long leadId, Long userId, String ipAddress) {
        this();
        this.leadId = leadId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.createdBy = userId;
    }
    
    /**
     * 构造函数
     * 
     * @param leadId 线索ID
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    public LeadView(Long leadId, Long userId, String ipAddress, String userAgent) {
        this(leadId, userId, ipAddress);
        this.userAgent = userAgent;
    }
    
    // Getter和Setter方法
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getLeadId() {
        return leadId;
    }
    
    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getViewTime() {
        return viewTime;
    }
    
    public void setViewTime(LocalDateTime viewTime) {
        this.viewTime = viewTime;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public Long getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    @Override
    public String toString() {
        return "LeadView{" +
                "id=" + id +
                ", leadId=" + leadId +
                ", userId=" + userId +
                ", viewTime=" + viewTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", createdBy=" + createdBy +
                ", createdTime=" + createdTime +
                ", updatedBy=" + updatedBy +
                ", updatedTime=" + updatedTime +
                '}';
    }
}