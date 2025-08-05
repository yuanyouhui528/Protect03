package com.leadexchange.dto.user;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserLoginResponse的Builder类
 * 用于构建UserLoginResponse对象
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class UserLoginResponseBuilder {
    
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String companyName;
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private List<String> roles;
    private List<String> permissions;
    private LocalDateTime loginTime;
    private LocalDateTime lastLoginTime;
    private String ipAddress;
    
    /**
     * 设置用户ID
     * 
     * @param userId 用户ID
     * @return Builder对象
     */
    public UserLoginResponseBuilder userId(Long userId) {
        this.userId = userId;
        return this;
    }
    
    /**
     * 设置用户名
     * 
     * @param username 用户名
     * @return Builder对象
     */
    public UserLoginResponseBuilder username(String username) {
        this.username = username;
        return this;
    }
    
    /**
     * 设置真实姓名
     * 
     * @param realName 真实姓名
     * @return Builder对象
     */
    public UserLoginResponseBuilder realName(String realName) {
        this.realName = realName;
        return this;
    }
    
    /**
     * 设置手机号
     * 
     * @param phone 手机号
     * @return Builder对象
     */
    public UserLoginResponseBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }
    
    /**
     * 设置邮箱
     * 
     * @param email 邮箱
     * @return Builder对象
     */
    public UserLoginResponseBuilder email(String email) {
        this.email = email;
        return this;
    }
    
    /**
     * 设置公司名称
     * 
     * @param companyName 公司名称
     * @return Builder对象
     */
    public UserLoginResponseBuilder companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }
    
    /**
     * 设置访问令牌
     * 
     * @param accessToken 访问令牌
     * @return Builder对象
     */
    public UserLoginResponseBuilder accessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
    
    /**
     * 设置刷新令牌
     * 
     * @param refreshToken 刷新令牌
     * @return Builder对象
     */
    public UserLoginResponseBuilder refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
    
    /**
     * 设置令牌类型
     * 
     * @param tokenType 令牌类型
     * @return Builder对象
     */
    public UserLoginResponseBuilder tokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }
    
    /**
     * 设置过期时间
     * 
     * @param expiresIn 过期时间
     * @return Builder对象
     */
    public UserLoginResponseBuilder expiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
    
    /**
     * 设置角色列表
     * 
     * @param roles 角色列表
     * @return Builder对象
     */
    public UserLoginResponseBuilder roles(List<String> roles) {
        this.roles = roles;
        return this;
    }
    
    /**
     * 设置权限列表
     * 
     * @param permissions 权限列表
     * @return Builder对象
     */
    public UserLoginResponseBuilder permissions(List<String> permissions) {
        this.permissions = permissions;
        return this;
    }
    
    /**
     * 设置登录时间
     * 
     * @param loginTime 登录时间
     * @return Builder对象
     */
    public UserLoginResponseBuilder loginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
        return this;
    }
    
    /**
     * 设置上次登录时间
     * 
     * @param lastLoginTime 上次登录时间
     * @return Builder对象
     */
    public UserLoginResponseBuilder lastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        return this;
    }
    
    /**
     * 设置IP地址
     * 
     * @param ipAddress IP地址
     * @return Builder对象
     */
    public UserLoginResponseBuilder ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }
    
    /**
     * 构建UserLoginResponse对象
     * 
     * @return UserLoginResponse对象
     */
    public UserLoginResponse build() {
        UserLoginResponse response = new UserLoginResponse();
        response.setUserId(userId);
        response.setUsername(username);
        response.setRealName(realName);
        response.setPhone(phone);
        response.setEmail(email);
        response.setCompanyName(companyName);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType(tokenType);
        response.setExpiresIn(expiresIn);
        response.setRoles(roles);
        response.setPermissions(permissions);
        response.setLoginTime(loginTime);
        response.setLastLoginTime(lastLoginTime);
        response.setIpAddress(ipAddress);
        return response;
    }
}