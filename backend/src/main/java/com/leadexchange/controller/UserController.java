package com.leadexchange.controller;

import com.leadexchange.common.result.Result;
import com.leadexchange.dto.user.UserRegisterRequest;
import com.leadexchange.dto.user.UserRegisterResponse;
import com.leadexchange.service.SmsService;
import com.leadexchange.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * 用户管理控制器
 * 提供用户相关的REST API接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户信息管理相关接口")
public class UserController {

    private final UserService userService;
    private final SmsService smsService;
    
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
}