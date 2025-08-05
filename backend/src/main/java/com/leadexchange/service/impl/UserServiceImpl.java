package com.leadexchange.service.impl;

import com.leadexchange.domain.user.User;
import com.leadexchange.dto.user.UserRegisterRequest;
import com.leadexchange.dto.user.UserRegisterResponse;
import com.leadexchange.repository.user.UserRepository;
import com.leadexchange.service.SmsService;
import com.leadexchange.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 实现用户管理相关的业务逻辑
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;

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

    // TODO: 实现用户登录方法
    // TODO: 实现用户信息更新方法
    // TODO: 实现密码修改方法
    // TODO: 实现用户状态管理方法
    // TODO: 实现用户权限查询方法
}