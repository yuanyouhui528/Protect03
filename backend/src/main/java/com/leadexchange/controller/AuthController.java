package com.leadexchange.controller;

import com.leadexchange.common.ApiResponse;
import com.leadexchange.dto.user.*;
import com.leadexchange.service.UserService;
import com.leadexchange.common.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证控制器
 * 提供用户认证相关的REST API接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Validated
@Tag(name = "用户认证", description = "用户登录、登出、令牌刷新等认证相关接口")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtils jwtUtils;
    
    // 构造器注入
    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @param httpRequest HTTP请求对象
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过用户名/手机号/邮箱和密码进行登录")
    public ApiResponse<UserLoginResponse> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("收到用户登录请求，用户名: {}", request.getUsername());
        
        try {
            // 设置IP地址
            String ipAddress = getClientIpAddress(httpRequest);
            request.setIpAddress(ipAddress);
            
            // 设置设备信息
            String userAgent = httpRequest.getHeader("User-Agent");
            request.setDeviceInfo(userAgent);
            
            // 执行登录
            UserLoginResponse response = userService.login(request);
            
            log.info("用户登录成功，用户ID: {}, 用户名: {}", response.getUserId(), response.getUsername());
            return ApiResponse.success(response, "登录成功");
            
        } catch (Exception e) {
            log.error("用户登录失败，用户名: {}, 错误: {}", request.getUsername(), e.getMessage(), e);
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     * 
     * @param httpRequest HTTP请求对象
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，将令牌加入黑名单")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest) {
        
        try {
            // 从请求头中获取令牌
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ApiResponse.error("未找到有效的访问令牌");
            }
            
            // 从令牌中提取用户ID
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("令牌无效");
            }
            
            // 执行登出
            boolean success = userService.logout(userId, token);
            
            if (success) {
                log.info("用户登出成功，用户ID: {}", userId);
                return ApiResponse.success(null, "登出成功");
            } else {
                log.warn("用户登出失败，用户ID: {}", userId);
                return ApiResponse.error("登出失败");
            }
            
        } catch (Exception e) {
            log.error("用户登出异常，错误: {}", e.getMessage(), e);
            return ApiResponse.error("登出失败: " + e.getMessage());
        }
    }

    /**
     * 刷新JWT令牌
     * 
     * @param request 令牌刷新请求
     * @param httpRequest HTTP请求对象
     * @return 刷新后的令牌信息
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新JWT令牌", description = "使用刷新令牌获取新的访问令牌")
    public ApiResponse<TokenRefreshResponse> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("收到令牌刷新请求");
        
        try {
            // 设置IP地址
            String ipAddress = getClientIpAddress(httpRequest);
            request.setIpAddress(ipAddress);
            
            // 设置设备信息
            String userAgent = httpRequest.getHeader("User-Agent");
            request.setDeviceInfo(userAgent);
            
            // 执行令牌刷新
            TokenRefreshResponse response = userService.refreshToken(request);
            
            log.info("令牌刷新成功，用户ID: {}", response.getUserId());
            return ApiResponse.success(response, "令牌刷新成功");
            
        } catch (Exception e) {
            log.error("令牌刷新失败，错误: {}", e.getMessage(), e);
            return ApiResponse.<TokenRefreshResponse>error("令牌刷新失败: " + e.getMessage());
        }
    }

    /**
     * 验证令牌有效性
     * 
     * @param httpRequest HTTP请求对象
     * @return 验证结果
     */
    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证当前访问令牌的有效性")
    public ApiResponse<Boolean> validateToken(HttpServletRequest httpRequest) {
        
        try {
            // 从请求头中获取令牌
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ApiResponse.success(false, "未找到访问令牌");
            }
            
            // 验证令牌
            boolean isValid = jwtUtils.validateTokenFormat(token) && !jwtUtils.isTokenExpired(token);
            
            if (isValid) {
                Long userId = jwtUtils.getUserIdFromToken(token);
                log.debug("令牌验证成功，用户ID: {}", userId);
                return ApiResponse.success(true, "令牌有效");
            } else {
                log.debug("令牌验证失败");
                return ApiResponse.success(false, "令牌无效或已过期");
            }
            
        } catch (Exception e) {
            log.error("令牌验证异常，错误: {}", e.getMessage(), e);
            return ApiResponse.success(false, "令牌验证失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     * 
     * @param httpRequest HTTP请求对象
     * @return 用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的基本信息")
    public ApiResponse<UserLoginResponse> getCurrentUser(HttpServletRequest httpRequest) {
        
        try {
            // 从请求头中获取令牌
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ApiResponse.<UserLoginResponse>error("未找到有效的访问令牌");
            }
            
            // 验证令牌并获取用户ID
            if (!jwtUtils.validateTokenFormat(token) || jwtUtils.isTokenExpired(token)) {
                return ApiResponse.<UserLoginResponse>error("令牌无效或已过期");
            }
            
            Long userId = jwtUtils.getUserIdFromToken(token);
            String username = jwtUtils.getUsernameFromToken(token);
            
            // 获取用户权限信息
            UserPermissionResponse permissionResponse = userService.getUserPermissions(userId);
            
            // 构建用户信息响应（复用登录响应结构）
            UserLoginResponse response = UserLoginResponse.builder()
                    .userId(userId)
                    .username(username)
                    .roles(permissionResponse.getRoles().stream()
                            .map(UserPermissionResponse.RoleInfo::getRoleCode)
                            .collect(java.util.stream.Collectors.toList()))
                    .permissions(permissionResponse.getPermissionCodes())
                    .accessToken(token)
                    .tokenType("Bearer")
                    .build();
            
            log.debug("获取当前用户信息成功，用户ID: {}", userId);
            return ApiResponse.success(response, "获取用户信息成功");
            
        } catch (Exception e) {
            log.error("获取当前用户信息失败，错误: {}", e.getMessage(), e);
            return ApiResponse.<UserLoginResponse>error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 从HTTP请求中提取JWT令牌
     * 
     * @param request HTTP请求对象
     * @return JWT令牌，如果未找到则返回null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取客户端IP地址
     * 
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}