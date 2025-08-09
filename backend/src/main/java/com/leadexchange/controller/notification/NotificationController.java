package com.leadexchange.controller.notification;

import com.leadexchange.common.ApiResponse;
import com.leadexchange.domain.notification.Notification;
import com.leadexchange.domain.notification.NotificationSettings;
import com.leadexchange.domain.notification.NotificationType;
import com.leadexchange.domain.notification.SendChannel;
import com.leadexchange.dto.notification.NotificationSettingsUpdateRequest;
import com.leadexchange.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 通知服务控制器
 * 处理消息通知相关操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Tag(name = "通知服务", description = "消息通知相关接口")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    private final NotificationService notificationService;
    
    /**
     * 获取用户通知列表
     * 
     * @param authentication 认证信息
     * @param page 页码
     * @param size 每页大小
     * @param notificationType 通知类型
     * @param isRead 是否已读
     * @return 通知列表
     */
    @Operation(summary = "获取通知列表", description = "分页获取用户的通知列表")
    @GetMapping
    public ApiResponse<Page<Notification>> getNotifications(
            Authentication authentication,
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "通知类型") @RequestParam(required = false) NotificationType notificationType,
            @Parameter(description = "是否已读") @RequestParam(required = false) Boolean isRead) {
        
        Long userId = getUserId(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Notification> notifications = notificationService.getUserNotifications(
                userId, notificationType, isRead, pageable);
        
        logger.info("获取用户通知列表: userId={}, page={}, size={}, type={}, isRead={}", 
                   userId, page, size, notificationType, isRead);
        
        return ApiResponse.success(notifications);
    }
    
    /**
     * 获取通知详情
     * 
     * @param authentication 认证信息
     * @param id 通知ID
     * @return 通知详情
     */
    @Operation(summary = "获取通知详情", description = "根据ID获取通知详情")
    @GetMapping("/{id}")
    public ApiResponse<Notification> getNotification(
            Authentication authentication,
            @Parameter(description = "通知ID") @PathVariable Long id) {
        
        Long userId = getUserId(authentication);
        Notification notification = notificationService.getNotificationById(id);
        
        // 验证通知是否属于当前用户
        if (!notification.getRecipientId().equals(userId)) {
            return ApiResponse.error("无权访问该通知");
        }
        
        logger.info("获取通知详情: userId={}, notificationId={}", userId, id);
        
        return ApiResponse.success(notification);
    }
    
    /**
     * 标记通知为已读
     * 
     * @param authentication 认证信息
     * @param id 通知ID
     * @return 操作结果
     */
    @Operation(summary = "标记通知已读", description = "将指定通知标记为已读")
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(
            Authentication authentication,
            @Parameter(description = "通知ID") @PathVariable Long id) {
        
        Long userId = getUserId(authentication);
        notificationService.markAsRead(id, userId);
        
        logger.info("标记通知已读: userId={}, notificationId={}", userId, id);
        
        return ApiResponse.success();
    }
    
    /**
     * 批量标记通知为已读
     * 
     * @param authentication 认证信息
     * @param notificationIds 通知ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量标记已读", description = "批量将通知标记为已读")
    @PutMapping("/batch-read")
    public ApiResponse<Void> batchMarkAsRead(
            Authentication authentication,
            @Parameter(description = "通知ID列表") @RequestBody List<Long> notificationIds) {
        
        Long userId = getUserId(authentication);
        notificationService.batchMarkAsRead(notificationIds, userId);
        
        logger.info("批量标记通知已读: userId={}, count={}", userId, notificationIds.size());
        
        return ApiResponse.success();
    }
    
    /**
     * 标记所有通知为已读
     * 
     * @param authentication 认证信息
     * @return 操作结果
     */
    @Operation(summary = "全部标记已读", description = "将用户所有未读通知标记为已读")
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(Authentication authentication) {
        
        Long userId = getUserId(authentication);
        notificationService.markAllAsRead(userId);
        
        logger.info("标记所有通知已读: userId={}", userId);
        
        return ApiResponse.success();
    }
    
    /**
     * 删除通知
     * 
     * @param authentication 认证信息
     * @param id 通知ID
     * @return 操作结果
     */
    @Operation(summary = "删除通知", description = "删除指定通知")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(
            Authentication authentication,
            @Parameter(description = "通知ID") @PathVariable Long id) {
        
        Long userId = getUserId(authentication);
        notificationService.deleteNotification(id, userId);
        
        logger.info("删除通知: userId={}, notificationId={}", userId, id);
        
        return ApiResponse.success();
    }
    
    /**
     * 批量删除通知
     * 
     * @param authentication 认证信息
     * @param notificationIds 通知ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除通知", description = "批量删除通知")
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDeleteNotifications(
            Authentication authentication,
            @Parameter(description = "通知ID列表") @RequestBody List<Long> notificationIds) {
        
        Long userId = getUserId(authentication);
        notificationService.batchDeleteNotifications(notificationIds, userId);
        
        logger.info("批量删除通知: userId={}, count={}", userId, notificationIds.size());
        
        return ApiResponse.success();
    }
    
    /**
     * 获取未读通知数量
     * 
     * @param authentication 认证信息
     * @return 未读通知数量
     */
    @Operation(summary = "获取未读数量", description = "获取用户未读通知数量")
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(Authentication authentication) {
        
        Long userId = getUserId(authentication);
        long unreadCount = notificationService.getUnreadCount(userId);
        
        logger.debug("获取未读通知数量: userId={}, count={}", userId, unreadCount);
        
        return ApiResponse.success(unreadCount);
    }
    
    /**
     * 获取通知统计信息
     * 
     * @param authentication 认证信息
     * @return 统计信息
     */
    @Operation(summary = "获取通知统计", description = "获取用户通知统计信息")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getNotificationStatistics(Authentication authentication) {
        
        Long userId = getUserId(authentication);
        Map<String, Object> statistics = notificationService.getNotificationStatistics(userId);
        
        logger.info("获取通知统计: userId={}", userId);
        
        return ApiResponse.success(statistics);
    }
    
    /**
     * 获取用户通知设置
     * 
     * @param authentication 认证信息
     * @return 通知设置
     */
    @Operation(summary = "获取通知设置", description = "获取用户的通知设置")
    @GetMapping("/settings")
    public ApiResponse<NotificationSettings> getNotificationSettings(Authentication authentication) {
        
        Long userId = getUserId(authentication);
        NotificationSettings settings = notificationService.getNotificationSettings(userId);
        
        logger.info("获取通知设置: userId={}", userId);
        
        return ApiResponse.success(settings);
    }
    
    /**
     * 更新通知设置
     * 
     * @param authentication 认证信息
     * @param updateRequest 通知设置更新请求
     * @return 更新后的通知设置
     */
    @Operation(summary = "更新通知设置", description = "更新用户的通知设置")
    @PostMapping("/settings")
    public ApiResponse<NotificationSettings> updateNotificationSettings(
            Authentication authentication,
            @Parameter(description = "通知设置更新请求") @Valid @RequestBody NotificationSettingsUpdateRequest updateRequest) {
        
        Long userId = getUserId(authentication);
        
        NotificationSettings updatedSettings = notificationService.updateNotificationSettings(
            userId, 
            updateRequest.getNotificationType(),
            updateRequest.getSystemEnabled(),
            updateRequest.getEmailEnabled(),
            updateRequest.getSmsEnabled()
        );
        
        logger.info("更新通知设置: userId={}, type={}", userId, updateRequest.getNotificationType());
        
        return ApiResponse.success(updatedSettings);
    }
    
    /**
     * 从认证信息中获取用户ID
     * 
     * @param authentication 认证信息
     * @return 用户ID
     */
    private Long getUserId(Authentication authentication) {
        // 这里需要根据实际的认证实现来获取用户ID
        // 假设用户ID存储在认证主体中
        return Long.valueOf(authentication.getName());
    }
}