package com.leadexchange.service;

/**
 * 短信服务接口
 * 提供短信验证码发送和验证功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface SmsService {

    /**
     * 发送注册验证码
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendRegisterCode(String phone);

    /**
     * 发送登录验证码
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendLoginCode(String phone);

    /**
     * 发送密码重置验证码
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendResetPasswordCode(String phone);

    /**
     * 验证短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @param type 验证码类型（register/login/reset）
     * @return 是否验证成功
     */
    boolean verifyCode(String phone, String code, String type);
    
    /**
     * 发送短信
     * @param phone 手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    boolean sendSms(String phone, String content);

    /**
     * 检查发送频率限制
     * @param phone 手机号
     * @return 是否可以发送
     */
    boolean checkSendLimit(String phone);

    /**
     * 获取验证码剩余有效时间（秒）
     * @param phone 手机号
     * @param type 验证码类型
     * @return 剩余有效时间
     */
    long getCodeExpireTime(String phone, String type);
}