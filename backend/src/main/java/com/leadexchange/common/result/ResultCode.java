package com.leadexchange.common.result;

/**
 * 响应结果码枚举
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum ResultCode {

    // 成功状态码
    SUCCESS(200, "操作成功"),

    // 客户端错误状态码 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    VALIDATION_ERROR(422, "参数验证失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // 服务器错误状态码 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),

    // 业务错误状态码 1xxx
    // 用户相关错误 10xx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_PASSWORD_ERROR(1004, "用户密码错误"),
    USER_LOGIN_EXPIRED(1005, "用户登录已过期"),
    USER_PERMISSION_DENIED(1006, "用户权限不足"),
    USER_PHONE_EXISTS(1007, "手机号已存在"),
    USER_EMAIL_EXISTS(1008, "邮箱已存在"),

    // 线索相关错误 11xx
    LEAD_NOT_FOUND(1101, "线索不存在"),
    LEAD_ALREADY_EXISTS(1102, "线索已存在"),
    LEAD_STATUS_ERROR(1103, "线索状态错误"),
    LEAD_PERMISSION_DENIED(1104, "线索权限不足"),
    LEAD_DUPLICATE_DETECTED(1105, "检测到重复线索"),
    LEAD_RATING_ERROR(1106, "线索评级失败"),
    LEAD_AUDIT_FAILED(1107, "线索审核失败"),
    LEAD_OFFLINE_ERROR(1108, "线索下架失败"),

    // 交换相关错误 12xx
    EXCHANGE_NOT_FOUND(1201, "交换记录不存在"),
    EXCHANGE_INSUFFICIENT_BALANCE(1202, "交换余额不足"),
    EXCHANGE_SELF_NOT_ALLOWED(1203, "不能与自己交换"),
    EXCHANGE_STATUS_ERROR(1204, "交换状态错误"),
    EXCHANGE_VALUE_ERROR(1205, "交换价值计算错误"),
    EXCHANGE_APPROVAL_FAILED(1206, "交换审批失败"),
    EXCHANGE_CANCEL_FAILED(1207, "交换取消失败"),
    EXCHANGE_TIMEOUT(1208, "交换超时"),

    // 评级相关错误 13xx
    RATING_CALCULATION_ERROR(1301, "评级计算错误"),
    RATING_WEIGHT_ERROR(1302, "评级权重配置错误"),
    RATING_THRESHOLD_ERROR(1303, "评级阈值配置错误"),
    RATING_DATA_INCOMPLETE(1304, "评级数据不完整"),

    // 通知相关错误 14xx
    NOTIFICATION_SEND_FAILED(1401, "通知发送失败"),
    NOTIFICATION_TEMPLATE_NOT_FOUND(1402, "通知模板不存在"),
    NOTIFICATION_CHANNEL_ERROR(1403, "通知渠道错误"),

    // 数据分析相关错误 15xx
    ANALYTICS_DATA_ERROR(1501, "分析数据错误"),
    ANALYTICS_CALCULATION_ERROR(1502, "分析计算错误"),
    ANALYTICS_EXPORT_ERROR(1503, "分析数据导出错误"),

    // 文件相关错误 16xx
    FILE_UPLOAD_ERROR(1601, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(1602, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(1603, "文件大小超出限制"),
    FILE_NOT_FOUND(1604, "文件不存在"),
    FILE_DELETE_ERROR(1605, "文件删除失败"),

    // 第三方服务错误 17xx
    SMS_SEND_ERROR(1701, "短信发送失败"),
    EMAIL_SEND_ERROR(1702, "邮件发送失败"),
    PAYMENT_ERROR(1703, "支付失败"),
    EXTERNAL_API_ERROR(1704, "外部API调用失败"),

    // 缓存相关错误 18xx
    CACHE_ERROR(1801, "缓存操作失败"),
    CACHE_KEY_NOT_FOUND(1802, "缓存键不存在"),
    CACHE_EXPIRED(1803, "缓存已过期"),

    // 消息队列相关错误 19xx
    MQ_SEND_ERROR(1901, "消息发送失败"),
    MQ_CONSUME_ERROR(1902, "消息消费失败"),
    MQ_CONNECTION_ERROR(1903, "消息队列连接失败"),

    // 搜索相关错误 20xx
    SEARCH_ERROR(2001, "搜索失败"),
    SEARCH_INDEX_ERROR(2002, "搜索索引错误"),
    SEARCH_QUERY_ERROR(2003, "搜索查询错误");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态消息
     */
    private final String message;

    /**
     * 构造函数
     * 
     * @param code 状态码
     * @param message 状态消息
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码
     * 
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取状态消息
     * 
     * @return 状态消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 根据状态码获取枚举
     * 
     * @param code 状态码
     * @return 结果码枚举
     */
    public static ResultCode getByCode(Integer code) {
        for (ResultCode resultCode : values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }

    @Override
    public String toString() {
        return "ResultCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}