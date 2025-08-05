package com.leadexchange.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * JWT令牌刷新请求DTO
 * 用于接收令牌刷新时的请求参数
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Schema(description = "JWT令牌刷新请求")
public class TokenRefreshRequest {

    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌", required = true)
    private String refreshToken;

    /**
     * 设备信息（可选）
     */
    @Schema(description = "设备信息")
    private String deviceInfo;

    /**
     * IP地址（可选）
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 获取刷新令牌
     * 
     * @return 刷新令牌
     */
    public String getRefreshToken() {
        return refreshToken;
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
     * 设置IP地址
     * 
     * @param ipAddress IP地址
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * 设置设备信息
     * 
     * @param deviceInfo 设备信息
     */
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}