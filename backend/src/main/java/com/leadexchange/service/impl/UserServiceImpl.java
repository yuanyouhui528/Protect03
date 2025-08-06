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
import java.util.Optional;

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
    // 暂时注释掉其他依赖，简化启动测试
    // private final RoleRepository roleRepository;
    // private final PermissionRepository permissionRepository;
    // private final UserRoleRepository userRoleRepository;
    // private final RolePermissionRepository rolePermissionRepository;
    // private final PasswordEncoder passwordEncoder;
    // private final SmsService smsService;
    // private final JwtUtils jwtUtils;
    // private final RedisUtils redisUtils;

    /**
     * 构造器注入依赖
     */
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        // 暂时注释掉其他依赖注入
        // this.roleRepository = roleRepository;
        // this.permissionRepository = permissionRepository;
        // this.userRoleRepository = userRoleRepository;
        // this.rolePermissionRepository = rolePermissionRepository;
        // this.passwordEncoder = passwordEncoder;
        // this.smsService = smsService;
        // this.jwtUtils = jwtUtils;
        // this.redisUtils = redisUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterResponse register(UserRegisterRequest request) {
        log.info("开始用户注册，用户名: {}, 手机号: {}", request.getUsername(), request.getPhone());
        
        try {
            // 暂时简化注册逻辑，只做基本的用户创建
            // 1. 基本参数验证
            if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPhone())) {
                throw new RuntimeException("用户名和手机号不能为空");
            }
            
            // 2. 检查用户名是否已存在
            if (userRepository.existsByUsernameAndDeleted(request.getUsername(), 0)) {
                throw new RuntimeException("用户名已存在");
            }
            
            // 3. 创建用户实体
            User user = createUserFromRequest(request);
            
            // 4. 保存用户到数据库
            userRepository.save(user);
            
            // 5. 构建注册响应
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
        user.setPassword(request.getPassword()); // 暂时不加密，简化测试
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
            // TODO: 暂时简化登录逻辑，等待完整实现
            // 1. 查找用户（支持用户名、手机号、邮箱登录）
            User user = findUserByUsernameOrPhoneOrEmail(request.getUsername());
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 2. 验证用户状态
            if (user.getStatus() != 1) {
                throw new RuntimeException("用户账号已被禁用");
            }
            
            // 3. 简化密码验证（暂时直接比较明文）
            if (!request.getPassword().equals(user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            
            // 4. 获取用户角色和权限（模拟数据）
            List<String> roles = getUserRoleCodes(user.getId());
            List<String> permissions = getUserPermissionCodes(user.getId());
            
            // 5. 生成模拟JWT令牌
            String accessToken = "mock_access_token_" + user.getId();
            String refreshToken = "mock_refresh_token_" + user.getId();
            Long expiresIn = 3600L; // 1小时
            
            // 6. 更新登录信息（简化版）
            user.setLoginCount(user.getLoginCount() + 1);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(request.getIpAddress());
            userRepository.save(user);
            
            // 7. 构建登录响应
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
            
            log.info("用户登录成功（模拟），用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
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
            // TODO: 暂时简化登出逻辑，等待完整实现
            log.info("用户登出成功（模拟），用户ID: {}", userId);
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
            // TODO: 暂时简化令牌刷新逻辑，等待完整实现
            // 模拟从刷新令牌中提取用户ID
            String refreshToken = request.getRefreshToken();
            if (!refreshToken.startsWith("mock_refresh_token_")) {
                throw new RuntimeException("刷新令牌无效");
            }
            
            Long userId = Long.parseLong(refreshToken.replace("mock_refresh_token_", ""));
            
            // 验证用户状态
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getStatus() != 1) {
                throw new RuntimeException("用户不存在或已被禁用");
            }
            
            // 生成新的模拟令牌
            String newAccessToken = "mock_access_token_" + userId;
            String newRefreshToken = "mock_refresh_token_" + userId;
            Long expiresIn = 3600L; // 1小时
            
            // 构建刷新响应
            TokenRefreshResponse response = TokenRefreshResponse.success(
                newAccessToken,
                newRefreshToken,
                expiresIn,
                userId,
                user.getUsername()
            );
            
            log.info("JWT令牌刷新成功（模拟），用户ID: {}", userId);
            return response;
            
        } catch (Exception e) {
            log.error("JWT令牌刷新失败，错误: {}", e.getMessage(), e);
            throw new RuntimeException("令牌刷新失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public UserPermissionResponse getUserPermissions(Long userId) {
        log.info("获取用户权限信息，用户ID: {}", userId);
        
        // TODO: 暂时返回模拟数据，等待完整实现
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 返回基础权限信息
            UserPermissionResponse response = UserPermissionResponse.success(
                userId,
                user.getUsername(),
                List.of(), // 空角色列表
                List.of(), // 空权限列表
                List.of()  // 空权限代码列表
            );
            
            log.info("获取用户权限信息成功（模拟数据），用户ID: {}", userId);
            return response;
            
        } catch (Exception e) {
            log.error("获取用户权限信息失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("获取权限信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        try {
            // TODO: 暂时返回true，等待完整实现
            log.info("检查用户权限（模拟），用户ID: {}, 权限代码: {}", userId, permissionCode);
            return true;
            
        } catch (Exception e) {
            log.error("检查用户权限失败，用户ID: {}, 权限代码: {}, 错误: {}", 
                     userId, permissionCode, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        try {
            // TODO: 暂时返回基础权限列表，等待完整实现
            log.info("获取用户权限代码（模拟），用户ID: {}", userId);
            return List.of("user:read", "user:write");
                
        } catch (Exception e) {
            log.error("获取用户权限代码失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<String> getUserRoleCodes(Long userId) {
        try {
            // TODO: 暂时返回基础角色列表，等待完整实现
            log.info("获取用户角色代码（模拟），用户ID: {}", userId);
            return List.of("USER");
                
        } catch (Exception e) {
            log.error("获取用户角色代码失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public User getUserById(Long userId) {
        log.info("根据用户ID获取用户信息，用户ID: {}", userId);
        try {
            return userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            log.error("获取用户信息失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 根据用户名、手机号或邮箱查找用户
     * @param identifier 用户标识（用户名、手机号或邮箱）
     * @return 用户实体
     */
    private User findUserByUsernameOrPhoneOrEmail(String identifier) {
        // 先尝试用户名查找
        Optional<User> userOpt = userRepository.findByUsernameAndDeleted(identifier, 0);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        
        // 再尝试手机号查找
        userOpt = userRepository.findByPhoneAndDeleted(identifier, 0);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        
        // 最后尝试邮箱查找
        userOpt = userRepository.findByEmailAndDeleted(identifier, 0);
        return userOpt.orElse(null);
    }
    
    // TODO: 实现缓存用户信息到Redis的方法
    // TODO: 实现更新用户登录信息的方法
    
    // TODO: 实现用户信息更新方法
    // TODO: 实现密码修改方法
    // TODO: 实现用户状态管理方法
}