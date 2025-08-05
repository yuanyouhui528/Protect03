package com.leadexchange.domain.user;

import com.leadexchange.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * 对应数据库users表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
@TableName("users")
@ApiModel(description = "用户实体")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    @TableField("username")
    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @ApiModelProperty(value = "用户名", required = true, example = "zhangsan")
    private String username;

    /**
     * 密码（加密存储）
     */
    @Column(name = "password", nullable = false)
    @TableField("password")
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true, example = "$2a$10$...")
    private String password;

    /**
     * 手机号（加密存储）
     */
    @Column(name = "phone", nullable = false, unique = true)
    @TableField("phone")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号", required = true, example = "13800138000")
    private String phone;

    /**
     * 邮箱（加密存储）
     */
    @Column(name = "email", unique = true)
    @TableField("email")
    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱", example = "zhangsan@example.com")
    private String email;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 100)
    @TableField("real_name")
    @Length(max = 100, message = "真实姓名长度不能超过100个字符")
    @ApiModelProperty(value = "真实姓名", example = "张三")
    private String realName;

    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 500)
    @TableField("avatar")
    @ApiModelProperty(value = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 企业名称
     */
    @Column(name = "company_name", length = 200)
    @TableField("company_name")
    @Length(max = 200, message = "企业名称长度不能超过200个字符")
    @ApiModelProperty(value = "企业名称", example = "某某科技有限公司")
    private String companyName;

    /**
     * 企业类型
     */
    @Column(name = "company_type", length = 50)
    @TableField("company_type")
    @ApiModelProperty(value = "企业类型", example = "有限责任公司")
    private String companyType;

    /**
     * 所属行业
     */
    @Column(name = "industry", length = 100)
    @TableField("industry")
    @ApiModelProperty(value = "所属行业", example = "信息技术")
    private String industry;

    /**
     * 职位
     */
    @Column(name = "position", length = 100)
    @TableField("position")
    @ApiModelProperty(value = "职位", example = "总经理")
    private String position;

    /**
     * 地区编码
     */
    @Column(name = "region_code", length = 20)
    @TableField("region_code")
    @ApiModelProperty(value = "地区编码", example = "110000")
    private String regionCode;

    /**
     * 地区名称
     */
    @Column(name = "region_name", length = 100)
    @TableField("region_name")
    @ApiModelProperty(value = "地区名称", example = "北京市")
    private String regionName;

    /**
     * 所在地区
     */
    @Column(name = "location", length = 100)
    @TableField("location")
    @ApiModelProperty(value = "所在地区", example = "北京市朝阳区")
    private String location;

    /**
     * 企业规模
     */
    @Column(name = "company_size", length = 20)
    @TableField("company_size")
    @ApiModelProperty(value = "企业规模", example = "100-500人")
    private String companySize;

    /**
     * 营业执照号
     */
    @Column(name = "business_license", length = 100)
    @TableField("business_license")
    @ApiModelProperty(value = "营业执照号", example = "91110000000000000X")
    private String businessLicense;

    /**
     * 统一社会信用代码
     */
    @Column(name = "credit_code", length = 50)
    @TableField("credit_code")
    @ApiModelProperty(value = "统一社会信用代码", example = "91110000000000000X")
    private String creditCode;

    /**
     * 用户状态（0：禁用，1：正常，2：待审核）
     */
    @Column(name = "status", nullable = false)
    @TableField("status")
    @ApiModelProperty(value = "用户状态", example = "1", notes = "0：禁用，1：正常，2：待审核")
    private Integer status = 1;

    /**
     * 认证状态（0：未认证，1：已认证）
     */
    @Column(name = "verified", nullable = false)
    @TableField("verified")
    @ApiModelProperty(value = "认证状态", example = "0", notes = "0：未认证，1：已认证")
    private Integer verified = 0;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    @TableField("last_login_time")
    @ApiModelProperty(value = "最后登录时间", example = "2024-01-01 12:00:00")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    @TableField("last_login_ip")
    @ApiModelProperty(value = "最后登录IP", example = "192.168.1.1")
    private String lastLoginIp;

    /**
     * 登录次数
     */
    @Column(name = "login_count", nullable = false)
    @TableField("login_count")
    @ApiModelProperty(value = "登录次数", example = "10")
    private Integer loginCount = 0;

    /**
     * 用户角色关联（一对多）
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @TableField(exist = false)
    private List<UserRole> userRoles;

    // 构造函数
    public User() {
    }

    public User(String username, String password, String phone) {
        this.username = username;
        this.password = password;
        this.phone = phone;
    }

    // Getter and Setter methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompanySize() {
        return companySize;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVerified() {
        return verified;
    }

    public void setVerified(Integer verified) {
        this.verified = verified;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", status=" + status +
                ", verified=" + verified +
                ", lastLoginTime=" + lastLoginTime +
                ", loginCount=" + loginCount +
                '}';
    }
}