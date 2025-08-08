package com.leadexchange.service.impl;

import com.leadexchange.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务实现类
 * 使用Redis存储验证码，实现发送频率限制和验证功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 构造器注入
    public SmsServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    // Redis键前缀
    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final String SMS_LIMIT_PREFIX = "sms:limit:";
    
    // 验证码配置
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRE_MINUTES = 5; // 验证码有效期5分钟
    private static final int SEND_LIMIT_SECONDS = 60; // 发送间隔60秒
    private static final int DAILY_LIMIT = 10; // 每日发送限制10次
    
    private final SecureRandom random = new SecureRandom();

    @Override
    public boolean sendRegisterCode(String phone) {
        return sendCode(phone, "register");
    }

    @Override
    public boolean sendLoginCode(String phone) {
        return sendCode(phone, "login");
    }

    @Override
    public boolean sendResetPasswordCode(String phone) {
        return sendCode(phone, "reset");
    }

    @Override
    public boolean verifyCode(String phone, String code, String type) {
        try {
            String key = SMS_CODE_PREFIX + type + ":" + phone;
            String storedCode = (String) redisTemplate.opsForValue().get(key);
            
            if (storedCode == null) {
                log.warn("验证码不存在或已过期，手机号: {}, 类型: {}", phone, type);
                return false;
            }
            
            boolean isValid = storedCode.equals(code);
            if (isValid) {
                // 验证成功后删除验证码
                redisTemplate.delete(key);
                log.info("验证码验证成功，手机号: {}, 类型: {}", phone, type);
            } else {
                log.warn("验证码验证失败，手机号: {}, 类型: {}, 输入码: {}", phone, type, code);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("验证码验证异常，手机号: {}, 类型: {}", phone, type, e);
            return false;
        }
    }

    @Override
    public boolean sendSms(String phone, String content) {
        try {
            // 检查发送限制
            if (!checkSendLimit(phone)) {
                log.warn("短信发送频率限制，手机号: {}", phone);
                return false;
            }
            
            // 调用第三方短信服务商发送短信
            boolean result = sendSmsToProvider(phone, content, "custom");
            
            if (result) {
                // 设置发送间隔限制
                String intervalKey = SMS_LIMIT_PREFIX + "interval:" + phone;
                redisTemplate.opsForValue().set(intervalKey, "1", Duration.ofSeconds(SEND_LIMIT_SECONDS));
                
                // 增加每日发送次数
                String dailyKey = SMS_LIMIT_PREFIX + "daily:" + phone;
                redisTemplate.opsForValue().increment(dailyKey);
                redisTemplate.expire(dailyKey, Duration.ofDays(1));
                
                log.info("短信发送成功，手机号: {}", phone);
            } else {
                log.error("短信发送失败，手机号: {}", phone);
            }
            
            return result;
        } catch (Exception e) {
            log.error("短信发送异常，手机号: {}", phone, e);
            return false;
        }
    }

    @Override
    public boolean checkSendLimit(String phone) {
        try {
            // 检查发送间隔限制
            String intervalKey = SMS_LIMIT_PREFIX + "interval:" + phone;
            Boolean hasInterval = redisTemplate.hasKey(intervalKey);
            if (Boolean.TRUE.equals(hasInterval)) {
                log.warn("发送过于频繁，请稍后再试，手机号: {}", phone);
                return false;
            }
            
            // 检查每日发送次数限制
            String dailyKey = SMS_LIMIT_PREFIX + "daily:" + phone;
            Integer dailyCount = (Integer) redisTemplate.opsForValue().get(dailyKey);
            if (dailyCount != null && dailyCount >= DAILY_LIMIT) {
                log.warn("今日发送次数已达上限，手机号: {}", phone);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("检查发送限制异常，手机号: {}", phone, e);
            return false;
        }
    }

    @Override
    public long getCodeExpireTime(String phone, String type) {
        try {
            String key = SMS_CODE_PREFIX + type + ":" + phone;
            Long expireTime = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expireTime != null ? expireTime : 0;
        } catch (Exception e) {
            log.error("获取验证码过期时间异常，手机号: {}, 类型: {}", phone, type, e);
            return 0;
        }
    }

    /**
     * 发送验证码的通用方法
     * @param phone 手机号
     * @param type 验证码类型
     * @return 是否发送成功
     */
    private boolean sendCode(String phone, String type) {
        try {
            // 检查发送限制
            if (!checkSendLimit(phone)) {
                return false;
            }
            
            // 生成验证码
            String code = generateCode();
            
            // 存储验证码到Redis
            String codeKey = SMS_CODE_PREFIX + type + ":" + phone;
            redisTemplate.opsForValue().set(codeKey, code, Duration.ofMinutes(CODE_EXPIRE_MINUTES));
            
            // 设置发送间隔限制
            String intervalKey = SMS_LIMIT_PREFIX + "interval:" + phone;
            redisTemplate.opsForValue().set(intervalKey, "1", Duration.ofSeconds(SEND_LIMIT_SECONDS));
            
            // 增加每日发送次数
            String dailyKey = SMS_LIMIT_PREFIX + "daily:" + phone;
            redisTemplate.opsForValue().increment(dailyKey);
            redisTemplate.expire(dailyKey, Duration.ofDays(1));
            
            // 实际发送短信（这里使用模拟发送）
            boolean sendResult = sendSmsToProvider(phone, code, type);
            
            if (sendResult) {
                log.info("验证码发送成功，手机号: {}, 类型: {}, 验证码: {}", phone, type, code);
            } else {
                log.error("验证码发送失败，手机号: {}, 类型: {}", phone, type);
                // 发送失败时删除已存储的验证码
                redisTemplate.delete(codeKey);
            }
            
            return sendResult;
        } catch (Exception e) {
            log.error("发送验证码异常，手机号: {}, 类型: {}", phone, type, e);
            return false;
        }
    }

    /**
     * 生成随机验证码
     * @return 6位数字验证码
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 调用第三方短信服务商发送短信
     * 这里使用模拟发送，实际项目中需要集成真实的短信服务商
     * 
     * @param phone 手机号
     * @param content 短信内容（验证码或自定义内容）
     * @param type 短信类型
     * @return 是否发送成功
     */
    private boolean sendSmsToProvider(String phone, String content, String type) {
        try {
            // 根据类型判断是验证码还是自定义内容
            if ("custom".equals(type)) {
                // 自定义内容短信
                log.info("[模拟短信] 发送给 {}: {}", phone, content);
            } else {
                // 验证码短信
                log.info("[模拟短信] 发送给 {}: 您的{}验证码是 {}，有效期{}分钟。", 
                        phone, getTypeDesc(type), content, CODE_EXPIRE_MINUTES);
            }
            
            // 模拟网络延迟
            Thread.sleep(100);
            
            // 模拟发送成功率（95%成功率）
            return random.nextInt(100) < 95;
        } catch (Exception e) {
            log.error("调用短信服务商异常，手机号: {}", phone, e);
            return false;
        }
    }

    /**
     * 获取验证码类型描述
     * @param type 验证码类型
     * @return 类型描述
     */
    private String getTypeDesc(String type) {
        switch (type) {
            case "register":
                return "注册";
            case "login":
                return "登录";
            case "reset":
                return "密码重置";
            default:
                return "验证";
        }
    }
}