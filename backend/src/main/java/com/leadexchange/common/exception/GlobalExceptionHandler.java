package com.leadexchange.common.exception;

import com.leadexchange.common.result.Result;
import com.leadexchange.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.warn("业务异常: {} - {}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常
     * 
     * @param e 参数验证异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.warn("参数验证异常: {}", message);
        return Result.error(ResultCode.VALIDATION_ERROR.getCode(), message);
    }

    /**
     * 处理绑定异常
     * 
     * @param e 绑定异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.warn("绑定异常: {}", message);
        return Result.error(ResultCode.VALIDATION_ERROR.getCode(), message);
    }

    /**
     * 处理约束违反异常
     * 
     * @param e 约束违反异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        logger.warn("约束违反异常: {}", message);
        return Result.error(ResultCode.VALIDATION_ERROR.getCode(), message);
    }

    /**
     * 处理缺少请求参数异常
     * 
     * @param e 缺少请求参数异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String message = "缺少必需的请求参数: " + e.getParameterName();
        logger.warn("缺少请求参数异常: {}", message);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理方法参数类型不匹配异常
     * 
     * @param e 方法参数类型不匹配异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String message = "参数类型不匹配: " + e.getName() + " 应该是 " + e.getRequiredType().getSimpleName();
        logger.warn("参数类型不匹配异常: {}", message);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理HTTP请求方法不支持异常
     * 
     * @param e HTTP请求方法不支持异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String message = "不支持的请求方法: " + e.getMethod();
        logger.warn("HTTP请求方法不支持异常: {}", message);
        return Result.error(ResultCode.METHOD_NOT_ALLOWED.getCode(), message);
    }

    /**
     * 处理资源未找到异常
     * 
     * @param e 资源未找到异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String message = "请求的资源不存在: " + e.getRequestURL();
        logger.warn("资源未找到异常: {}", message);
        return Result.error(ResultCode.NOT_FOUND.getCode(), message);
    }

    /**
     * 处理文件上传大小超出限制异常
     * 
     * @param e 文件上传大小超出限制异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        String message = "文件大小超出限制: " + e.getMaxUploadSize() + " bytes";
        logger.warn("文件上传大小超出限制异常: {}", message);
        return Result.error(ResultCode.FILE_SIZE_EXCEEDED.getCode(), message);
    }

    /**
     * 处理认证异常
     * 
     * @param e 认证异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        logger.warn("认证异常: {}", e.getMessage());
        return Result.error(ResultCode.UNAUTHORIZED.getCode(), "认证失败");
    }

    /**
     * 处理凭据错误异常
     * 
     * @param e 凭据错误异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        logger.warn("凭据错误异常: {}", e.getMessage());
        return Result.error(ResultCode.USER_PASSWORD_ERROR.getCode(), "用户名或密码错误");
    }

    /**
     * 处理访问拒绝异常
     * 
     * @param e 访问拒绝异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        logger.warn("访问拒绝异常: {}", e.getMessage());
        return Result.error(ResultCode.FORBIDDEN.getCode(), "访问被拒绝");
    }

    /**
     * 处理运行时异常
     * 
     * @param e 运行时异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("运行时异常: ", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统运行时异常");
    }

    /**
     * 处理其他异常
     * 
     * @param e 异常
     * @param request HTTP请求
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        logger.error("系统异常: ", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }
}