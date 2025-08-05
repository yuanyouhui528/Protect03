package com.leadexchange.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器
 * 处理用户信息管理、权限管理等操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Tag(name = "用户管理", description = "用户信息管理相关接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // TODO: 实现用户管理相关接口
    // - GET /api/users/profile - 获取用户信息
    // - PUT /api/users/profile - 更新用户信息
    // - POST /api/users/change-password - 修改密码
    // - GET /api/users/list - 获取用户列表(管理员)
    // - PUT /api/users/{id}/status - 更新用户状态(管理员)
    // - GET /api/users/{id} - 获取指定用户信息(管理员)
}