package com.leadexchange.controller;

import com.leadexchange.common.ApiResponse;
import com.leadexchange.common.result.Result;
import com.leadexchange.dto.user.UserRegisterRequest;
import com.leadexchange.dto.user.UserRegisterResponse;
import com.leadexchange.dto.user.UserPermissionResponse;
import com.leadexchange.service.SmsService;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 用户管理控制器
 * 提供用户相关的REST API接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "用户管理", description = "用户信息管理相关接口")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    private final SmsService smsService;
    private final JwtUtils jwtUtils;
    
    /**
     * 构造器注入依赖
     */
    public UserController(UserService userService, SmsService smsService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.smsService = smsService;
        this.jwtUtils = jwtUtils;
    }
    
    /**
     * 发送注册验证码
     */
    @PostMapping("/register/send-code")
    @Operation(summary = "发送注册验证码", description = "向指定手机号发送注册验证码")
    public Result<String> sendRegisterCode(
            @Parameter(description = "手机号", required = true, example = "13800138000")
            @RequestParam 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") 
            String phone) {
        
        log.info("发送注册验证码请求，手机号: {}", phone);
        
        try {
            // 检查发送限制
            if (!smsService.checkSendLimit(phone)) {
                return Result.error("发送过于频繁，请稍后再试");
            }
            
            // 发送验证码
            boolean success = smsService.sendRegisterCode(phone);
            if (success) {
                return Result.success("验证码发送成功");
            } else {
                return Result.error("验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("发送注册验证码异常，手机号: {}", phone, e);
            return Result.error("系统异常，请稍后重试");
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口，需要手机验证码验证")
    public Result<UserRegisterResponse> register(
            @Parameter(description = "注册请求参数", required = true)
            @Valid @RequestBody UserRegisterRequest request) {
        
        log.info("用户注册请求，用户名: {}, 手机号: {}", request.getUsername(), request.getPhone());
        
        try {
            UserRegisterResponse response = userService.register(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("用户注册失败，用户名: {}, 手机号: {}, 错误: {}", 
                     request.getUsername(), request.getPhone(), e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    // TODO: 实现用户个人信息查询接口
    @GetMapping("/profile")
    @Operation(summary = "获取用户个人信息", description = "获取当前登录用户的个人信息")
    public Object getUserProfile() {
        // TODO: 实现获取用户个人信息逻辑
        return "TODO: 获取用户个人信息";
    }
    
    // TODO: 实现用户个人信息更新接口
    @PutMapping("/profile")
    @Operation(summary = "更新用户个人信息", description = "更新当前登录用户的个人信息")
    public Object updateUserProfile() {
        // TODO: 实现更新用户个人信息逻辑
        return "TODO: 更新用户个人信息";
    }
    
    // TODO: 实现密码修改接口
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public Object changePassword() {
        // TODO: 实现密码修改逻辑
        return "TODO: 修改密码";
    }
    
    // TODO: 实现管理员用户管理接口
    @GetMapping("/admin/list")
    @Operation(summary = "获取用户列表", description = "管理员获取用户列表（分页）")
    public Object getUserList() {
        // TODO: 实现获取用户列表逻辑
        return "TODO: 获取用户列表";
    }
    
    @PutMapping("/admin/{userId}/status")
    @Operation(summary = "更新用户状态", description = "管理员更新用户状态（启用/禁用）")
    public Object updateUserStatus(@PathVariable Long userId) {
        // TODO: 实现更新用户状态逻辑
        return "TODO: 更新用户状态";
    }
    
    // ==================== 权限管理相关接口 ====================
    
    /**
     * 获取用户权限信息
     * 
     * @param httpRequest HTTP请求对象
     * @return 用户权限信息
     */
    @GetMapping("/permissions")
    @Operation(summary = "获取用户权限信息", description = "获取当前登录用户的角色和权限信息")
    public ApiResponse<UserPermissionResponse> getUserPermissions(HttpServletRequest httpRequest) {
        
        try {
            // 从请求头中获取令牌并验证
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ApiResponse.error("未找到有效的访问令牌");
            }
            
            if (!jwtUtils.validateTokenFormat(token)) {
                return ApiResponse.error("令牌无效或已过期");
            }
            
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            // 获取用户权限信息
            UserPermissionResponse response = userService.getUserPermissions(userId);
            
            log.debug("获取用户权限信息成功，用户ID: {}, 角色数: {}, 权限数: {}", 
                     userId, response.getRoles().size(), response.getPermissions().size());
            
            return ApiResponse.success(response, "获取权限信息成功");
            
        } catch (Exception e) {
            log.error("获取用户权限信息失败，错误: {}", e.getMessage(), e);
            return ApiResponse.error("获取权限信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否具有特定权限
     * 
     * @param permissionCode 权限代码
     * @param httpRequest HTTP请求对象
     * @return 权限检查结果
     */
    @GetMapping("/permissions/check")
    @Operation(summary = "检查用户权限", description = "检查当前登录用户是否具有指定权限")
    public ApiResponse<Boolean> checkPermission(
            @Parameter(description = "权限代码", required = true, example = "user:create")
            @RequestParam String permissionCode,
            HttpServletRequest httpRequest) {
        
        try {
            // 从请求头中获取令牌并验证
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ApiResponse.success(false, "未找到有效的访问令牌");
            }
            
            if (!jwtUtils.validateTokenFormat(token)) {
                return ApiResponse.success(false, "令牌无效或已过期");
            }
            
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            // 检查权限
            boolean hasPermission = userService.hasPermission(userId, permissionCode);
            
            log.debug("权限检查完成，用户ID: {}, 权限代码: {}, 结果: {}", 
                     userId, permissionCode, hasPermission);
            
            return ApiResponse.success(hasPermission, 
                    hasPermission ? "用户具有该权限" : "用户不具有该权限");
            
        } catch (Exception e) {
            log.error("权限检查失败，权限代码: {}, 错误: {}", permissionCode, e.getMessage(), e);
            return ApiResponse.success(false, "权限检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户权限代码列表
     * 
     * @param httpRequest HTTP请求对象
     * @return 权限代码列表
     */
    @GetMapping("/permissions/codes")
    @Operation(summary = "获取用户权限代码", description = "获取当前登录用户的所有权限代码列表")
    public ApiResponse<List<String>> getUserPermissionCodes(HttpServletRequest httpRequest) {
        
        try {
            // 从请求头中获取令牌并验证
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ApiResponse.error("未找到有效的访问令牌");
            }
            
            if (!jwtUtils.validateTokenFormat(token)) {
                return ApiResponse.error("令牌无效或已过期");
            }
            
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            // 获取权限代码列表
            List<String> permissionCodes = userService.getUserPermissionCodes(userId);
            
            log.debug("获取用户权限代码成功，用户ID: {}, 权限数量: {}", userId, permissionCodes.size());
            
            return ApiResponse.success(permissionCodes, "获取权限代码成功");
            
        } catch (Exception e) {
            log.error("获取用户权限代码失败，错误: {}", e.getMessage(), e);
            return ApiResponse.error("获取权限代码失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户角色代码列表
     * 
     * @param httpRequest HTTP请求对象
     * @return 角色代码列表
     */
    @GetMapping("/roles/codes")
    @Operation(summary = "获取用户角色代码", description = "获取当前登录用户的所有角色代码列表")
    public ApiResponse<List<String>> getUserRoleCodes(HttpServletRequest httpRequest) {
        
        try {
            // 从请求头中获取令牌并验证
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ApiResponse.error("未找到有效的访问令牌");
            }
            
            if (!jwtUtils.validateTokenFormat(token)) {
                return ApiResponse.error("令牌无效或已过期");
            }
            
            Long userId = jwtUtils.getUserIdFromToken(token);
            
            // 获取角色代码列表
            List<String> roleCodes = userService.getUserRoleCodes(userId);
            
            log.debug("获取用户角色代码成功，用户ID: {}, 角色数量: {}", userId, roleCodes.size());
            
            return ApiResponse.success(roleCodes, "获取角色代码成功");
            
        } catch (Exception e) {
            log.error("获取用户角色代码失败，错误: {}", e.getMessage(), e);
            return ApiResponse.error("获取角色代码失败: " + e.getMessage());
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
}