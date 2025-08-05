package com.leadexchange.service;

import com.leadexchange.dto.user.UserRegisterRequest;
import com.leadexchange.dto.user.UserRegisterResponse;

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
    
    // TODO: 定义用户登录方法
    // UserLoginResponse login(UserLoginRequest request);
    
    // TODO: 定义用户信息更新方法
    // UserInfoResponse updateUserInfo(Long userId, UserUpdateRequest request);
    
    // TODO: 定义密码修改方法
    // boolean changePassword(Long userId, ChangePasswordRequest request);
    
    // TODO: 定义用户状态管理方法
    // boolean updateUserStatus(Long userId, Integer status);
    
    // TODO: 定义用户权限查询方法
    // List<String> getUserPermissions(Long userId);
    
    // TODO: 定义用户列表查询方法
    // PageResult<UserListItem> getUserList(UserQueryRequest request);
}