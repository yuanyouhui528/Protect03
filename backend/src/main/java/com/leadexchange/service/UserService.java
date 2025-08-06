package com.leadexchange.service;

import com.leadexchange.dto.user.*;

import java.util.List;

/**
 * 用户服务接口
 * 定义用户管理相关的业务操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册响应
     */
    UserRegisterResponse register(UserRegisterRequest request);
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    UserLoginResponse login(UserLoginRequest request);
    
    /**
     * 用户登出
     * @param userId 用户ID
     * @param token 访问令牌
     * @return 是否成功
     */
    boolean logout(Long userId, String token);
    
    /**
     * 刷新JWT令牌
     * @param request 刷新令牌请求
     * @return 刷新令牌响应
     */
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    
    /**
     * 获取用户权限信息
     * @param userId 用户ID
     * @return 用户权限响应
     */
    UserPermissionResponse getUserPermissions(Long userId);
    
    /**
     * 检查用户是否拥有指定权限
     * @param userId 用户ID
     * @param permissionCode 权限代码
     * @return 是否拥有权限
     */
    boolean hasPermission(Long userId, String permissionCode);
    
    /**
     * 获取用户权限代码列表
     * @param userId 用户ID
     * @return 权限代码列表
     */
    List<String> getUserPermissionCodes(Long userId);
    
    /**
     * 获取用户角色代码列表
     * @param userId 用户ID
     * @return 角色代码列表
     */
    List<String> getUserRoleCodes(Long userId);
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户实体
     */
    com.leadexchange.domain.user.User getUserById(Long userId);
    
    // TODO: 定义用户信息更新方法
    // UserInfoResponse updateUserInfo(Long userId, UserUpdateRequest request);
    
    // TODO: 定义密码修改方法
    // boolean changePassword(Long userId, ChangePasswordRequest request);
    
    // TODO: 定义用户状态管理方法
    // boolean updateUserStatus(Long userId, Integer status);
    
    // TODO: 定义用户列表查询方法
    // PageResult<UserListItem> getUserList(UserQueryRequest request);
}