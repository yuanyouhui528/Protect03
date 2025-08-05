package com.leadexchange.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 用户注册请求DTO
 * 用于接收用户注册时提交的数据
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Schema(description = "用户注册请求")
public class UserRegisterRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$", 
             message = "密码必须包含至少一个大写字母、一个小写字母和一个数字")
    @Schema(description = "密码", example = "Password123")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码", example = "Password123")
    private String confirmPassword;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须为6位数字")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须为6位数字")
    @Schema(description = "短信验证码", example = "123456")
    private String smsCode;

    /**
     * 邮箱（可选）
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 10, message = "真实姓名长度必须在2-10个字符之间")
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    @Size(min = 2, max = 100, message = "企业名称长度必须在2-100个字符之间")
    @Schema(description = "企业名称", example = "北京科技有限公司")
    private String companyName;

    /**
     * 职位
     */
    @Size(max = 50, message = "职位长度不能超过50个字符")
    @Schema(description = "职位", example = "销售经理")
    private String position;

    /**
     * 所在地区
     */
    @Size(max = 100, message = "所在地区长度不能超过100个字符")
    @Schema(description = "所在地区", example = "北京市朝阳区")
    private String location;

    /**
     * 行业
     */
    @Size(max = 50, message = "行业长度不能超过50个字符")
    @Schema(description = "行业", example = "互联网")
    private String industry;

    /**
     * 企业规模
     */
    @Size(max = 20, message = "企业规模长度不能超过20个字符")
    @Schema(description = "企业规模", example = "100-500人")
    private String companySize;

    /**
     * 用户协议同意标识
     */
    @NotNull(message = "必须同意用户协议")
    @AssertTrue(message = "必须同意用户协议")
    @Schema(description = "是否同意用户协议", example = "true")
    private Boolean agreeTerms;

    /**
     * 隐私政策同意标识
     */
    @NotNull(message = "必须同意隐私政策")
    @AssertTrue(message = "必须同意隐私政策")
    @Schema(description = "是否同意隐私政策", example = "true")
    private Boolean agreePrivacy;

    /**
     * 验证密码是否一致
     * @return 密码是否一致
     */
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * 验证是否同意所有协议
     * @return 是否同意所有协议
     */
    public boolean isAgreeAllTerms() {
        return Boolean.TRUE.equals(agreeTerms) && Boolean.TRUE.equals(agreePrivacy);
    }

    /**
     * 获取所在地区
     * @return 所在地区
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置所在地区
     * @param location 所在地区
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 获取行业
     * @return 行业
     */
    public String getIndustry() {
        return industry;
    }

    /**
     * 设置行业
     * @param industry 行业
     */
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    /**
     * 获取企业规模
     * @return 企业规模
     */
    public String getCompanySize() {
        return companySize;
    }

    /**
     * 设置企业规模
     * @param companySize 企业规模
     */
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    /**
     * 获取邮箱
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取真实姓名
     * @return 真实姓名
     */
    public String getRealName() {
        return realName;
    }

    /**
     * 设置真实姓名
     * @param realName 真实姓名
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 获取企业名称
     * @return 企业名称
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * 设置企业名称
     * @param companyName 企业名称
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * 获取职位
     * @return 职位
     */
    public String getPosition() {
        return position;
    }

    /**
     * 设置职位
     * @param position 职位
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取手机号
     * @return 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取短信验证码
     * @return 短信验证码
     */
    public String getSmsCode() {
        return smsCode;
    }

    /**
     * 设置短信验证码
     * @param smsCode 短信验证码
     */
    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}