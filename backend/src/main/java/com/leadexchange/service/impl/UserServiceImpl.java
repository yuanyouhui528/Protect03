package com.leadexchange.service.impl;

import com.leadexchange.domain.user.User;
import com.leadexchange.domain.user.Role;
import com.leadexchange.domain.user.Permission;
import com.leadexchange.dto.user.*;
import com.leadexchange.repository.user.*;
import com.leadexchange.service.SmsService;
import com.leadexchange.service.UserService;
import com.leadexchange.common.utils.JwtUtils;
import com.leadexchange.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 用户服务实现类
 * 实现用户管理相关的业务逻辑
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;

    /**
     * 构造器注入依赖
     */
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                          PermissionRepository permissionRepository, UserRoleRepository userRoleRepository,
                          RolePermissionRepository rolePermissionRepository, PasswordEncoder passwordEncoder,
                          SmsService smsService, JwtUtils jwtUtils, RedisUtils redisUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.smsService = smsService;
        this.jwtUtils = jwtUtils;
        this.redisUtils = redisUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterResponse register(UserRegisterRequest request) {
        log.info("开始用户注册，用户名: {}, 手机号: {}", request.getUsername(), request.getPhone());
        
        try {
            // 1. 参数验证
            validateRegisterRequest(request);
            
            // 2. 验证短信验证码
            if (!smsService.verifyCode(request.getPhone(), request.getSmsCode(), "register")) {
                throw new RuntimeException("验证码错误或已过期");
            }
            
            // 3. 检查用户名是否已存在
            if (userRepository.existsByUsername(request.getUsername(), null) > 0) {
                throw new RuntimeException("用户名已存在");
            }
            
            // 4. 检查手机号是否已注册
            if (userRepository.existsByPhone(request.getPhone(), null) > 0) {
                throw new RuntimeException("手机号已注册");
            }
            
            // 5. 检查邮箱是否已注册（如果提供了邮箱）
            if (StringUtils.hasText(request.getEmail()) && 
                userRepository.existsByEmail(request.getEmail(), null) > 0) {
                throw new RuntimeException("邮箱已注册");
            }
            
            // 6. 创建用户实体
            User user = createUserFromRequest(request);
            
            // 7. 保存用户到数据库
            userRepository.insert(user);
            
            // 8. 构建注册响应
            UserRegisterResponse response = UserRegisterResponse.success(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getEmail(),
                user.getRealName(),
                user.getCompanyName(),
                user.getCreateTime()
            );
            
            log.info("用户注册成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
            return response;
            
        } catch (Exception e) {
            log.error("用户注册失败，用户名: {}, 手机号: {}, 错误: {}", 
                     request.getUsername(), request.getPhone(), e.getMessage(), e);
            throw new RuntimeException("注册失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证注册请求参数
     * @param request 注册请求
     */
    private void validateRegisterRequest(UserRegisterRequest request) {
        // 验证密码一致性
        if (!request.isPasswordMatch()) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 验证协议同意状态
        if (!request.isAgreeAllTerms()) {
            throw new RuntimeException("必须同意用户协议和隐私政策");
        }
        
        // 验证手机号格式
        if (!isValidPhone(request.getPhone())) {
            throw new RuntimeException("手机号格式不正确");
        }
        
        // 验证邮箱格式（如果提供了邮箱）
        if (StringUtils.hasText(request.getEmail()) && !isValidEmail(request.getEmail())) {
            throw new RuntimeException("邮箱格式不正确");
        }
    }

    /**
     * 根据注册请求创建用户实体
     * @param request 注册请求
     * @return 用户实体
     */
    private User createUserFromRequest(UserRegisterRequest request) {
        User user = new User();
        
        // 基本信息
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRealName(request.getRealName());
        
        // 企业信息
        user.setCompanyName(request.getCompanyName());
        user.setPosition(request.getPosition());
        user.setLocation(request.getLocation());
        user.setIndustry(request.getIndustry());
        user.setCompanySize(request.getCompanySize());
        
        // 状态信息
        user.setStatus(1); // 正常状态
        user.setVerified(0); // 未认证
        user.setLoginCount(0);
        
        // 审计信息
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setDeleted(0);
        user.setVersion(1);
        
        return user;
    }

    /**
     * 验证手机号格式
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 验证邮箱格式
     * @param email 邮箱
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginResponse login(UserLoginRequest request) {
        log.info("开始用户登录，用户名: {}", request.getUsername());
        
        try {
            // 1. 查找用户（支持用户名、手机号、邮箱登录）
            User user = findUserByUsernameOrPhoneOrEmail(request.getUsername());
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 2. 验证用户状态
            if (user.getStatus() != 1) {
                throw new RuntimeException("用户账号已被禁用");
            }
            
            // 3. 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            
            // 4. 获取用户角色和权限
            List<String> roles = getUserRoleCodes(user.getId());
            List<String> permissions = getUserPermissionCodes(user.getId());
            
            // 5. 生成JWT令牌
            String accessToken = jwtUtils.generateToken(user.getId(), user.getUsername(), 
                String.join(",", roles), String.join(",", permissions));
            String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getUsername());
            Long expiresIn = jwtUtils.getExpirationTime();
            
            // 6. 缓存用户信息到Redis
            cacheUserInfo(user, accessToken, refreshToken);
            
            // 7. 更新登录信息
            updateLoginInfo(user, request.getIpAddress());
            
            // 8. 构建登录响应
            UserLoginResponse response = UserLoginResponse.success(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getEmail(),
                user.getCompanyName(),
                accessToken,
                refreshToken,
                expiresIn,
                roles,
                permissions,
                request.getIpAddress()
            );
            
            log.info("用户登录成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
            return response;
            
        } catch (Exception e) {
            log.error("用户登录失败，用户名: {}, 错误: {}", request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("登录失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean logout(Long userId, String token) {
        log.info("开始用户登出，用户ID: {}", userId);
        
        try {
            // 1. 将令牌加入黑名单
            String tokenKey = "token:blacklist:" + token;
            Long expiration = jwtUtils.getTokenExpiration(token);
            if (expiration != null && expiration > System.currentTimeMillis()) {
                long ttl = (expiration - System.currentTimeMillis()) / 1000;
                redisUtils.setex(tokenKey, "1", (int) ttl);
            }
            
            // 2. 清除用户缓存信息
            String userCacheKey = "user:session:" + userId;
            redisUtils.del(userCacheKey);
            
            // 3. 清除用户权限缓存
            String permissionCacheKey = "user:permissions:" + userId;
            redisUtils.del(permissionCacheKey);
            
            log.info("用户登出成功，用户ID: {}", userId);
            return true;
            
        } catch (Exception e) {
            log.error("用户登出失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        log.info("开始刷新JWT令牌");
        
        try {
            // 1. 验证刷新令牌
            if (!jwtUtils.validateRefreshToken(request.getRefreshToken())) {
                throw new RuntimeException("刷新令牌无效或已过期");
            }
            
            // 2. 从刷新令牌中提取用户信息
            Long userId = jwtUtils.getUserIdFromRefreshToken(request.getRefreshToken());
            String username = jwtUtils.getUsernameFromRefreshToken(request.getRefreshToken());
            
            // 3. 验证用户状态
            User user = userRepository.selectById(userId);
            if (user == null || user.getStatus() != 1) {
                throw new RuntimeException("用户不存在或已被禁用");
            }
            
            // 4. 获取用户角色和权限
            List<String> roles = getUserRoleCodes(userId);
            List<String> authorities = getUserPermissionCodes(userId);
            
            // 5. 生成新的访问令牌
            String newAccessToken = jwtUtils.generateToken(userId, username, 
                String.join(",", roles), String.join(",", authorities));
            Long expiresIn = jwtUtils.getExpirationTime();
            
            // 6. 可选：生成新的刷新令牌（令牌轮换）
            String newRefreshToken = jwtUtils.generateRefreshToken(userId, username);
            
            // 7. 将旧的刷新令牌加入黑名单
            String oldTokenKey = "refresh_token:blacklist:" + request.getRefreshToken();
            Long expiration = jwtUtils.getRefreshTokenExpiration(request.getRefreshToken());
            if (expiration != null && expiration > System.currentTimeMillis()) {
                long ttl = (expiration - System.currentTimeMillis()) / 1000;
                redisUtils.setex(oldTokenKey, "1", (int) ttl);
            }
            
            // 8. 构建刷新响应
            TokenRefreshResponse response = TokenRefreshResponse.success(
                newAccessToken,
                newRefreshToken,
                expiresIn,
                userId,
                username
            );
            
            log.info("JWT令牌刷新成功，用户ID: {}", userId);
            return response;
            
        } catch (Exception e) {
            log.error("JWT令牌刷新失败，错误: {}", e.getMessage(), e);
            throw new RuntimeException("令牌刷新失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public UserPermissionResponse getUserPermissions(Long userId) {
        log.info("获取用户权限信息，用户ID: {}", userId);
        
        try {
            // 1. 获取用户信息
            User user = userRepository.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 2. 获取用户角色
            List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
            List<Role> roles = roleIds.isEmpty() ? List.of() : roleRepository.selectBatchIds(roleIds);
            
            // 3. 获取角色权限
            List<Long> permissionIds = roleIds.isEmpty() ? List.of() : 
                rolePermissionRepository.findPermissionIdsByRoleIds(roleIds);
            List<Permission> permissions = permissionIds.isEmpty() ? List.of() : 
                permissionRepository.selectBatchIds(permissionIds);
            
            // 4. 构建角色信息
            List<UserPermissionResponse.RoleInfo> roleInfos = roles.stream()
                .map(role -> {
                    UserPermissionResponse.RoleInfo roleInfo = new UserPermissionResponse.RoleInfo();
                    roleInfo.setRoleId(role.getId());
                    roleInfo.setRoleCode(role.getRoleCode());
                    roleInfo.setRoleName(role.getRoleName());
                    roleInfo.setDescription(role.getDescription());
                    return roleInfo;
                })
                .collect(java.util.stream.Collectors.toList());
            
            // 5. 构建权限信息
            List<UserPermissionResponse.PermissionInfo> permissionInfos = permissions.stream()
                .map(permission -> {
                    UserPermissionResponse.PermissionInfo permissionInfo = new UserPermissionResponse.PermissionInfo();
                    permissionInfo.setPermissionId(permission.getId());
                    permissionInfo.setPermissionCode(permission.getPermissionCode());
                    permissionInfo.setPermissionName(permission.getPermissionName());
                    permissionInfo.setType(permission.getPermissionType());
                    permissionInfo.setDescription(permission.getDescription());
                    return permissionInfo;
                })
                .collect(java.util.stream.Collectors.toList());
            
            // 6. 提取权限代码列表
            List<String> permissionCodes = permissions.stream()
                .map(Permission::getPermissionCode)
                .toList();
            
            // 7. 构建响应
            UserPermissionResponse response = UserPermissionResponse.success(
                userId,
                user.getUsername(),
                roleInfos,
                permissionInfos,
                permissionCodes
            );
            
            log.info("获取用户权限信息成功，用户ID: {}, 角色数: {}, 权限数: {}", 
                    userId, roles.size(), permissions.size());
            return response;
            
        } catch (Exception e) {
            log.error("获取用户权限信息失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("获取权限信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        try {
            // 先从缓存中查找
            String cacheKey = "user:permissions:" + userId;
            List<String> cachedPermissions = redisUtils.getList(cacheKey, String.class);
            
            if (cachedPermissions != null && !cachedPermissions.isEmpty()) {
                return cachedPermissions.contains(permissionCode);
            }
            
            // 缓存中没有，从数据库查询
            List<String> permissions = getUserPermissionCodes(userId);
            
            // 缓存权限列表（缓存30分钟）
            redisUtils.setex(cacheKey, permissions, 1800);
            
            return permissions.contains(permissionCode);
            
        } catch (Exception e) {
            log.error("检查用户权限失败，用户ID: {}, 权限代码: {}, 错误: {}", 
                     userId, permissionCode, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        try {
            // 1. 获取用户角色ID列表
            List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
            if (roleIds.isEmpty()) {
                return List.of();
            }
            
            // 2. 获取角色权限ID列表
            List<Long> permissionIds = rolePermissionRepository.findPermissionIdsByRoleIds(roleIds);
            if (permissionIds.isEmpty()) {
                return List.of();
            }
            
            // 3. 获取权限信息
            List<Permission> permissions = permissionRepository.selectBatchIds(permissionIds);
            
            // 4. 提取权限代码
            return permissions.stream()
                .map(Permission::getPermissionCode)
                .distinct()
                .toList();
                
        } catch (Exception e) {
            log.error("获取用户权限代码失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<String> getUserRoleCodes(Long userId) {
        try {
            // 1. 获取用户角色ID列表
            List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
            if (roleIds.isEmpty()) {
                return List.of();
            }
            
            // 2. 获取角色信息
            List<Role> roles = roleRepository.selectBatchIds(roleIds);
            
            // 3. 提取角色代码
            return roles.stream()
                .map(Role::getRoleCode)
                .toList();
                
        } catch (Exception e) {
            log.error("获取用户角色代码失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * 根据用户名、手机号或邮箱查找用户
     * @param identifier 用户标识（用户名、手机号或邮箱）
     * @return 用户实体
     */
    private User findUserByUsernameOrPhoneOrEmail(String identifier) {
        // 先尝试用户名查找
        User user = userRepository.findByUsername(identifier);
        if (user != null) {
            return user;
        }
        
        // 再尝试手机号查找
        user = userRepository.findByPhone(identifier);
        if (user != null) {
            return user;
        }
        
        // 最后尝试邮箱查找
        return userRepository.findByEmail(identifier);
    }
    
    /**
     * 缓存用户信息到Redis
     * @param user 用户实体
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     */
    private void cacheUserInfo(User user, String accessToken, String refreshToken) {
        try {
            String userCacheKey = "user:session:" + user.getId();
            String userInfo = String.format("{\"userId\":%d,\"username\":\"%s\",\"accessToken\":\"%s\",\"refreshToken\":\"%s\"}",
                user.getId(), user.getUsername(), accessToken, refreshToken);
            
            // 缓存用户会话信息（缓存时间与JWT令牌过期时间一致）
            Long expiresIn = jwtUtils.getExpirationTime();
            redisUtils.setex(userCacheKey, userInfo, expiresIn.intValue());
            
        } catch (Exception e) {
            log.warn("缓存用户信息失败，用户ID: {}, 错误: {}", user.getId(), e.getMessage());
        }
    }
    
    /**
     * 更新用户登录信息
     * @param user 用户实体
     * @param ipAddress IP地址
     */
    private void updateLoginInfo(User user, String ipAddress) {
        try {
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setLoginCount(user.getLoginCount() + 1);
            updateUser.setLastLoginTime(LocalDateTime.now());
            updateUser.setLastLoginIp(ipAddress);
            updateUser.setUpdateTime(LocalDateTime.now());
            
            userRepository.updateById(updateUser);
            
        } catch (Exception e) {
            log.warn("更新用户登录信息失败，用户ID: {}, 错误: {}", user.getId(), e.getMessage());
        }
    }
    
    // TODO: 实现用户信息更新方法
    // TODO: 实现密码修改方法
    // TODO: 实现用户状态管理方法
}