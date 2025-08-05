package com.leadexchange.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户登录请求DTO
 * 用于接收用户登录时的请求参数
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginRequest {

    /**
     * 用户名（可以是用户名、手机号或邮箱）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    @Schema(description = "用户名（支持用户名、手机号、邮箱）", example = "admin")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码", example = "123456")
    private String password;

    /**
     * 记住我（可选）
     */
    @Schema(description = "是否记住登录状态", example = "false")
    private Boolean rememberMe = false;

    /**
     * 验证码（可选，用于安全验证）
     */
    @Schema(description = "验证码", example = "1234")
    private String captcha;

    /**
     * 验证码标识（可选）
     */
    @Schema(description = "验证码标识")
    private String captchaKey;

    /**
     * 登录设备信息（可选）
     */
    @Schema(description = "登录设备信息")
    private String deviceInfo;

    /**
     * 登录IP地址（可选）
     */
    @Schema(description = "登录IP地址")
    private String ipAddress;

    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * 
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取IP地址
     * 
     * @return IP地址
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * 设置IP地址
     * 
     * @param ipAddress IP地址
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * 获取设备信息
     * 
     * @return 设备信息
     */
    public String getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * 设置设备信息
     * @param deviceInfo 设备信息
     */
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
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
}