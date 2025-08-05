package com.leadexchange.common.exception;

import com.leadexchange.common.result.ResultCode;

/**
 * 业务异常类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 构造函数
     */
    public BusinessException() {
        super();
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param resultCode 结果码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param cause 原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param resultCode 结果码枚举
     * @param cause 原因
     */
    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置错误码
     * 
     * @param code 错误码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取错误消息
     * 
     * @return 错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 设置错误消息
     * 
     * @param message 错误消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}