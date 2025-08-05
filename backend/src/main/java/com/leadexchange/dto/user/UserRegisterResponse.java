package com.leadexchange.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户注册响应DTO
 * 用于返回用户注册成功后的基本信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户注册响应")
public class UserRegisterResponse {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 手机号（脱敏）
     */
    @Schema(description = "手机号（脱敏）", example = "138****8000")
    private String phone;

    /**
     * 邮箱（脱敏）
     */
    @Schema(description = "邮箱（脱敏）", example = "zha***@example.com")
    private String email;

    /**
     * 真实姓名（脱敏）
     */
    @Schema(description = "真实姓名（脱敏）", example = "张*")
    private String realName;

    /**
     * 企业名称
     */
    @Schema(description = "企业名称", example = "北京科技有限公司")
    private String companyName;

    /**
     * 用户状态
     */
    @Schema(description = "用户状态（1-正常，0-禁用）", example = "1")
    private Integer status;

    /**
     * 认证状态
     */
    @Schema(description = "认证状态（1-已认证，0-未认证）", example = "0")
    private Integer verified;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    /**
     * 注册成功提示信息
     */
    @Schema(description = "注册成功提示信息", example = "注册成功，请等待管理员审核")
    private String message;

    /**
     * 是否需要邮箱验证
     */
    @Schema(description = "是否需要邮箱验证", example = "false")
    private Boolean needEmailVerification;

    /**
     * 是否需要管理员审核
     */
    @Schema(description = "是否需要管理员审核", example = "true")
    private Boolean needAdminApproval;

    /**
     * 创建成功响应
     * @param userId 用户ID
     * @param username 用户名
     * @param phone 手机号
     * @param email 邮箱
     * @param realName 真实姓名
     * @param companyName 企业名称
     * @param createTime 创建时间
     * @return 注册响应
     */
    public static UserRegisterResponse success(Long userId, String username, String phone, 
                                             String email, String realName, String companyName, 
                                             LocalDateTime createTime) {
        return UserRegisterResponse.builder()
                .userId(userId)
                .username(username)
                .phone(maskPhone(phone))
                .email(maskEmail(email))
                .realName(maskRealName(realName))
                .companyName(companyName)
                .status(1)
                .verified(0)
                .createTime(createTime)
                .message("注册成功，请等待管理员审核")
                .needEmailVerification(email != null && !email.isEmpty())
                .needAdminApproval(true)
                .build();
    }

    /**
     * 手机号脱敏处理
     * @param phone 原始手机号
     * @return 脱敏后的手机号
     */
    private static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏处理
     * @param email 原始邮箱
     * @return 脱敏后的邮箱
     */
    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 3) {
            return username.charAt(0) + "***@" + domain;
        } else {
            return username.substring(0, 3) + "***@" + domain;
        }
    }

    /**
     * 真实姓名脱敏处理
     * @param realName 原始姓名
     * @return 脱敏后的姓名
     */
    private static String maskRealName(String realName) {
        if (realName == null || realName.length() <= 1) {
            return realName;
        }
        return realName.charAt(0) + "*".repeat(realName.length() - 1);
    }

    /**
     * 创建Builder实例
     * @return Builder实例
     */
    public static UserRegisterResponseBuilder builder() {
        return new UserRegisterResponseBuilder();
    }

    /**
     * UserRegisterResponse的Builder类
     */
    public static class UserRegisterResponseBuilder {
        private Long userId;
        private String username;
        private String phone;
        private String email;
        private String realName;
        private String companyName;
        private Integer status;
        private Integer verified;
        private LocalDateTime createTime;
        private String message;
        private Boolean needEmailVerification;
        private Boolean needAdminApproval;

        public UserRegisterResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserRegisterResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserRegisterResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserRegisterResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserRegisterResponseBuilder realName(String realName) {
            this.realName = realName;
            return this;
        }

        public UserRegisterResponseBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public UserRegisterResponseBuilder status(Integer status) {
            this.status = status;
            return this;
        }

        public UserRegisterResponseBuilder verified(Integer verified) {
            this.verified = verified;
            return this;
        }

        public UserRegisterResponseBuilder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public UserRegisterResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public UserRegisterResponseBuilder needEmailVerification(Boolean needEmailVerification) {
            this.needEmailVerification = needEmailVerification;
            return this;
        }

        public UserRegisterResponseBuilder needAdminApproval(Boolean needAdminApproval) {
            this.needAdminApproval = needAdminApproval;
            return this;
        }

        public UserRegisterResponse build() {
            UserRegisterResponse response = new UserRegisterResponse();
            response.userId = this.userId;
            response.username = this.username;
            response.phone = this.phone;
            response.email = this.email;
            response.realName = this.realName;
            response.companyName = this.companyName;
            response.status = this.status;
            response.verified = this.verified;
            response.createTime = this.createTime;
            response.message = this.message;
            response.needEmailVerification = this.needEmailVerification;
            response.needAdminApproval = this.needAdminApproval;
            return response;
        }
    }
}