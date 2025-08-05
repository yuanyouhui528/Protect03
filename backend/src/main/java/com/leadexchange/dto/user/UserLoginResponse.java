package com.leadexchange.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录响应DTO
 * 用于返回用户登录成功后的信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录响应")
public class UserLoginResponse {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 获取用户ID
     * 
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     * 
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

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
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    /**
     * 公司名称
     */
    @Schema(description = "公司名称", example = "示例公司")
    private String companyName;

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌")
    private String accessToken;

    /**
     * 刷新令牌
     */
    @Schema(description = "刷新令牌")
    private String refreshToken;

    /**
     * 令牌类型
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * 令牌过期时间（秒）
     */
    @Schema(description = "令牌过期时间（秒）", example = "3600")
    private Long expiresIn;

    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private List<String> roles;

    /**
     * 用户权限列表
     */
    @Schema(description = "用户权限列表")
    private List<String> permissions;

    /**
     * 登录时间
     */
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;

    /**
     * 上次登录时间
     */
    @Schema(description = "上次登录时间")
    private LocalDateTime lastLoginTime;

    /**
     * 登录IP地址
     */
    @Schema(description = "登录IP地址")
    private String ipAddress;

    /**
     * 创建Builder对象
     * 
     * @return Builder对象
     */
    public static UserLoginResponseBuilder builder() {
        return new UserLoginResponseBuilder();
    }

    /**
     * 设置真实姓名
     * 
     * @param realName 真实姓名
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 设置手机号
     * 
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 设置邮箱
     * 
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 设置公司名称
     * 
     * @param companyName 公司名称
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * 设置访问令牌
     * 
     * @param accessToken 访问令牌
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 设置刷新令牌
     * 
     * @param refreshToken 刷新令牌
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 设置令牌类型
     * 
     * @param tokenType 令牌类型
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * 设置过期时间
     * 
     * @param expiresIn 过期时间
     */
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * 设置角色列表
     * 
     * @param roles 角色列表
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * 设置权限列表
     * 
     * @param permissions 权限列表
     */
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * 设置登录时间
     * 
     * @param loginTime 登录时间
     */
    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 设置上次登录时间
     * 
     * @param lastLoginTime 上次登录时间
     */
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
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
     * 创建成功的登录响应
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param realName 真实姓名
     * @param phone 手机号
     * @param email 邮箱
     * @param companyName 公司名称
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @param expiresIn 过期时间
     * @param roles 角色列表
     * @param permissions 权限列表
     * @param ipAddress IP地址
     * @return 登录响应
     */
    public static UserLoginResponse success(Long userId, String username, String realName, 
                                          String phone, String email, String companyName,
                                          String accessToken, String refreshToken, Long expiresIn,
                                          List<String> roles, List<String> permissions, String ipAddress) {
        return UserLoginResponse.builder()
                .userId(userId)
                .username(username)
                .realName(realName)
                .phone(phone)
                .email(email)
                .companyName(companyName)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .roles(roles)
                .permissions(permissions)
                .loginTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .build();
    }
}