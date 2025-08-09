package com.leadexchange.dto.notification;

import com.leadexchange.domain.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 通知设置更新请求DTO
 * 用于接收前端传递的通知设置更新参数
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Schema(description = "通知设置更新请求")
public class NotificationSettingsUpdateRequest {
    
    /**
     * 通知类型
     */
    @NotNull(message = "通知类型不能为空")
    @Schema(description = "通知类型", example = "SYSTEM")
    private NotificationType notificationType;
    
    /**
     * 是否启用系统通知
     */
    @Schema(description = "是否启用系统通知", example = "true")
    private Boolean systemEnabled;
    
    /**
     * 是否启用邮件通知
     */
    @Schema(description = "是否启用邮件通知", example = "false")
    private Boolean emailEnabled;
    
    /**
     * 是否启用短信通知
     */
    @Schema(description = "是否启用短信通知", example = "false")
    private Boolean smsEnabled;
    
    /**
     * 是否启用微信通知
     */
    @Schema(description = "是否启用微信通知", example = "false")
    private Boolean wechatEnabled;
    
    /**
     * 是否启用推送通知
     */
    @Schema(description = "是否启用推送通知", example = "true")
    private Boolean pushEnabled;
    
    /**
     * 是否启用免打扰模式
     */
    @Schema(description = "是否启用免打扰模式", example = "false")
    private Boolean doNotDisturbEnabled;
    
    /**
     * 频率限制（每天最大通知数量）
     */
    @Schema(description = "频率限制（每天最大通知数量）", example = "100")
    private Integer frequencyLimit;
}