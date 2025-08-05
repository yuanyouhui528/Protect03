package com.leadexchange.domain.lead;

import com.leadexchange.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 线索实体类
 * 对应数据库leads表和Elasticsearch索引
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "leads")
@TableName("leads")
@Document(indexName = "leads")
@ApiModel(description = "线索实体")
public class Lead extends BaseEntity {

    /**
     * 所有者ID
     */
    @Column(name = "owner_id", nullable = false)
    @Field(type = FieldType.Long)
    @ApiModelProperty(value = "所有者ID", example = "1")
    private Long ownerId;

    /**
     * 企业/项目名称
     */
    @Column(name = "company_name", nullable = false, length = 200)
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    @ApiModelProperty(value = "企业/项目名称", example = "阿里巴巴集团")
    private String companyName;

    /**
     * 联系人
     */
    @Column(name = "contact_person", nullable = false, length = 100)
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "联系人", example = "张三")
    private String contactPerson;

    /**
     * 联系方式
     */
    @Column(name = "contact_phone", nullable = false, length = 20)
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "联系方式", example = "13800138000")
    private String contactPhone;

    /**
     * 企业类型
     */
    @Column(name = "company_type", nullable = false, length = 50)
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "企业类型", example = "互联网科技")
    private String companyType;

    /**
     * 意向面积
     */
    @Column(name = "intended_area", precision = 10, scale = 2)
    @Field(type = FieldType.Double)
    @ApiModelProperty(value = "意向面积(平方米)", example = "1000.00")
    private BigDecimal intendedArea;

    /**
     * 办公类型
     */
    @Column(name = "office_type", nullable = false, length = 50)
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "办公类型", example = "写字楼")
    private String officeType;

    /**
     * 意向投资额
     */
    @Column(name = "investment_amount", precision = 15, scale = 2)
    @Field(type = FieldType.Double)
    @ApiModelProperty(value = "意向投资额(万元)", example = "5000.00")
    private BigDecimal investmentAmount;

    /**
     * 注册资本
     */
    @Column(name = "registered_capital", precision = 15, scale = 2)
    @Field(type = FieldType.Double)
    @ApiModelProperty(value = "注册资本(万元)", example = "1000.00")
    private BigDecimal registeredCapital;

    /**
     * 行业方向
     */
    @Column(name = "industry_direction", length = 100)
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    @ApiModelProperty(value = "行业方向", example = "人工智能")
    private String industryDirection;

    /**
     * 意向区域
     */
    @Column(name = "intended_region", length = 100)
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "意向区域", example = "杭州市西湖区")
    private String intendedRegion;

    /**
     * 线索状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "线索状态", example = "PUBLISHED")
    private LeadStatus status;

    /**
     * 线索评级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rating", length = 10)
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "线索评级", example = "A")
    private LeadRating rating;

    /**
     * 评级分数
     */
    @Column(name = "rating_score")
    @Field(type = FieldType.Integer)
    @ApiModelProperty(value = "评级分数", example = "85")
    private Integer ratingScore;

    /**
     * 审核状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status", nullable = false, length = 20)
    @Field(type = FieldType.Keyword)
    @ApiModelProperty(value = "审核状态", example = "APPROVED")
    private AuditStatus auditStatus;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    @Field(type = FieldType.Date)
    @ApiModelProperty(value = "审核时间")
    private LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    @Column(name = "auditor_id")
    @Field(type = FieldType.Long)
    @ApiModelProperty(value = "审核人ID", example = "1")
    private Long auditorId;

    /**
     * 审核备注
     */
    @Column(name = "audit_remark", length = 500)
    @Field(type = FieldType.Text)
    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

    /**
     * 线索描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    @ApiModelProperty(value = "线索描述")
    private String description;

    // 构造函数
    public Lead() {}

    // Getter和Setter方法
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public BigDecimal getIntendedArea() {
        return intendedArea;
    }

    public void setIntendedArea(BigDecimal intendedArea) {
        this.intendedArea = intendedArea;
    }

    public String getOfficeType() {
        return officeType;
    }

    public void setOfficeType(String officeType) {
        this.officeType = officeType;
    }

    public BigDecimal getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(BigDecimal investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public BigDecimal getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(BigDecimal registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getIndustryDirection() {
        return industryDirection;
    }

    public void setIndustryDirection(String industryDirection) {
        this.industryDirection = industryDirection;
    }

    public String getIntendedRegion() {
        return intendedRegion;
    }

    public void setIntendedRegion(String intendedRegion) {
        this.intendedRegion = intendedRegion;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    public LeadRating getRating() {
        return rating;
    }

    public void setRating(LeadRating rating) {
        this.rating = rating;
    }

    public Integer getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
    }

    public AuditStatus getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(AuditStatus auditStatus) {
        this.auditStatus = auditStatus;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Lead{" +
                "id=" + getId() +
                ", ownerId=" + ownerId +
                ", companyName='" + companyName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", companyType='" + companyType + '\'' +
                ", status=" + status +
                ", rating=" + rating +
                ", auditStatus=" + auditStatus +
                '}';
    }
}