package com.leadexchange.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 数据验证工具类
 * 提供常用的数据格式验证功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class ValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    // 正则表达式常量
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{4,20}$";
    private static final String COMPANY_CODE_REGEX = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";

    // 编译后的正则表达式模式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(ID_CARD_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
    private static final Pattern COMPANY_CODE_PATTERN = Pattern.compile(COMPANY_CODE_REGEX);

    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱地址
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        boolean isValid = EMAIL_PATTERN.matcher(email.trim()).matches();
        logger.debug("邮箱格式验证: {} -> {}", email, isValid);
        return isValid;
    }

    /**
     * 验证手机号格式
     * 
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        boolean isValid = PHONE_PATTERN.matcher(phone.trim()).matches();
        logger.debug("手机号格式验证: {} -> {}", phone, isValid);
        return isValid;
    }

    /**
     * 验证身份证号格式
     * 
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return false;
        }
        
        String trimmedIdCard = idCard.trim().toUpperCase();
        if (!ID_CARD_PATTERN.matcher(trimmedIdCard).matches()) {
            return false;
        }
        
        // 验证身份证校验位
        boolean isValid = validateIdCardChecksum(trimmedIdCard);
        logger.debug("身份证格式验证: {} -> {}", idCard, isValid);
        return isValid;
    }

    /**
     * 验证密码强度
     * 要求：8-20位，包含大小写字母和数字
     * 
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        boolean isValid = PASSWORD_PATTERN.matcher(password).matches();
        logger.debug("密码强度验证: {} -> {}", "***", isValid);
        return isValid;
    }

    /**
     * 验证用户名格式
     * 要求：4-20位，只能包含字母、数字和下划线
     * 
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        boolean isValid = USERNAME_PATTERN.matcher(username.trim()).matches();
        logger.debug("用户名格式验证: {} -> {}", username, isValid);
        return isValid;
    }

    /**
     * 验证统一社会信用代码
     * 
     * @param companyCode 统一社会信用代码
     * @return 是否有效
     */
    public static boolean isValidCompanyCode(String companyCode) {
        if (!StringUtils.hasText(companyCode)) {
            return false;
        }
        
        String trimmedCode = companyCode.trim().toUpperCase();
        if (!COMPANY_CODE_PATTERN.matcher(trimmedCode).matches()) {
            return false;
        }
        
        // 验证统一社会信用代码校验位
        boolean isValid = validateCompanyCodeChecksum(trimmedCode);
        logger.debug("统一社会信用代码验证: {} -> {}", companyCode, isValid);
        return isValid;
    }

    /**
     * 验证字符串长度范围
     * 
     * @param str 字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是否在范围内
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength <= 0;
        }
        int length = str.length();
        boolean isValid = length >= minLength && length <= maxLength;
        logger.debug("字符串长度验证: 长度={}, 范围=[{},{}] -> {}", length, minLength, maxLength, isValid);
        return isValid;
    }

    /**
     * 验证数值范围
     * 
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @return 是否在范围内
     */
    public static boolean isValidRange(Number value, Number min, Number max) {
        if (value == null) {
            return false;
        }
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        boolean isValid = val >= minVal && val <= maxVal;
        logger.debug("数值范围验证: 值={}, 范围=[{},{}] -> {}", val, minVal, maxVal, isValid);
        return isValid;
    }

    /**
     * 验证身份证校验位
     * 
     * @param idCard 身份证号
     * @return 校验位是否正确
     */
    private static boolean validateIdCardChecksum(String idCard) {
        try {
            // 权重因子
            int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
            // 校验码
            char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
            
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                sum += Character.getNumericValue(idCard.charAt(i)) * weights[i];
            }
            
            int mod = sum % 11;
            char expectedCheckCode = checkCodes[mod];
            char actualCheckCode = idCard.charAt(17);
            
            return expectedCheckCode == actualCheckCode;
        } catch (Exception e) {
            logger.error("身份证校验位验证失败", e);
            return false;
        }
    }

    /**
     * 验证统一社会信用代码校验位
     * 
     * @param companyCode 统一社会信用代码
     * @return 校验位是否正确
     */
    private static boolean validateCompanyCodeChecksum(String companyCode) {
        try {
            // 代码字符集
            String codeSet = "0123456789ABCDEFGHJKLMNPQRTUWXY";
            // 权重因子
            int[] weights = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
            
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                char c = companyCode.charAt(i);
                int value = codeSet.indexOf(c);
                if (value == -1) {
                    return false;
                }
                sum += value * weights[i];
            }
            
            int mod = 31 - (sum % 31);
            if (mod == 31) {
                mod = 0;
            }
            
            char expectedCheckCode = codeSet.charAt(mod);
            char actualCheckCode = companyCode.charAt(17);
            
            return expectedCheckCode == actualCheckCode;
        } catch (Exception e) {
            logger.error("统一社会信用代码校验位验证失败", e);
            return false;
        }
    }
}