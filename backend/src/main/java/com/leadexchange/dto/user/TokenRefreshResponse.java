package com.leadexchange.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JWT令牌刷新响应DTO
 * 用于返回令牌刷新成功后的信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT令牌刷新响应")
public class TokenRefreshResponse {

    /**
     * 新的访问令牌
     */
    @Schema(description = "新的访问令牌")
    private String accessToken;

    /**
     * 新的刷新令牌（可选，如果需要轮换刷新令牌）
     */
    @Schema(description = "新的刷新令牌")
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
     * 刷新时间
     */
    @Schema(description = "刷新时间")
    private LocalDateTime refreshTime;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 创建成功的令牌刷新响应
     * 
     * @param accessToken 新的访问令牌
     * @param refreshToken 新的刷新令牌
     * @param expiresIn 过期时间
     * @param userId 用户ID
     * @param username 用户名
     * @return 令牌刷新响应
     */
    public static TokenRefreshResponse success(String accessToken, String refreshToken, 
                                             Long expiresIn, Long userId, String username) {
        TokenRefreshResponse response = new TokenRefreshResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(expiresIn);
        response.setRefreshTime(LocalDateTime.now());
        response.setUserId(userId);
        response.setUsername(username);
        return response;
    }

    /**
     * 创建成功的令牌刷新响应（不轮换刷新令牌）
     * 
     * @param accessToken 新的访问令牌
     * @param expiresIn 过期时间
     * @param userId 用户ID
     * @param username 用户名
     * @return 令牌刷新响应
     */
    public static TokenRefreshResponse successWithoutRefreshTokenRotation(String accessToken, 
                                                                        Long expiresIn, 
                                                                        Long userId, 
                                                                        String username) {
        TokenRefreshResponse response = new TokenRefreshResponse();
        response.setAccessToken(accessToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(expiresIn);
        response.setRefreshTime(LocalDateTime.now());
        response.setUserId(userId);
        response.setUsername(username);
        return response;
    }

    /**
     * 创建Builder实例
     * @return Builder实例
     */
    public static TokenRefreshResponseBuilder builder() {
        return new TokenRefreshResponseBuilder();
    }

    /**
     * Builder类
     */
    public static class TokenRefreshResponseBuilder {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
        private LocalDateTime refreshTime;
        private Long userId;
        private String username;

        public TokenRefreshResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public TokenRefreshResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public TokenRefreshResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public TokenRefreshResponseBuilder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public TokenRefreshResponseBuilder refreshTime(LocalDateTime refreshTime) {
            this.refreshTime = refreshTime;
            return this;
        }

        public TokenRefreshResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public TokenRefreshResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public TokenRefreshResponse build() {
            TokenRefreshResponse response = new TokenRefreshResponse();
            response.setAccessToken(this.accessToken);
            response.setRefreshToken(this.refreshToken);
            response.setTokenType(this.tokenType);
            response.setExpiresIn(this.expiresIn);
            response.setRefreshTime(this.refreshTime);
            response.setUserId(this.userId);
            response.setUsername(this.username);
            return response;
        }
    }

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
     * 获取访问令牌
     * @return 访问令牌
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 设置访问令牌
     * @param accessToken 访问令牌
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 获取刷新令牌
     * @return 刷新令牌
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * 设置刷新令牌
     * @param refreshToken 刷新令牌
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 获取令牌类型
     * @return 令牌类型
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * 设置令牌类型
     * @param tokenType 令牌类型
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * 获取过期时间
     * @return 过期时间
     */
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * 设置过期时间
     * @param expiresIn 过期时间
     */
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * 获取刷新时间
     * @return 刷新时间
     */
    public LocalDateTime getRefreshTime() {
        return refreshTime;
    }

    /**
     * 设置刷新时间
     * @param refreshTime 刷新时间
     */
    public void setRefreshTime(LocalDateTime refreshTime) {
        this.refreshTime = refreshTime;
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
}