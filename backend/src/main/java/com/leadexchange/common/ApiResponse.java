package com.leadexchange.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一API响应结果类
 * 用于包装所有API接口的响应数据
 * 
 * @param <T> 响应数据类型
 * @author AI Assistant
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "统一API响应结果")
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     */
    @Schema(description = "响应状态码", example = "200")
    private Integer code;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息", example = "操作成功")
    private String message;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private T data;

    /**
     * 响应时间戳
     */
    @Schema(description = "响应时间戳")
    private LocalDateTime timestamp;

    /**
     * 请求追踪ID
     */
    @Schema(description = "请求追踪ID")
    private String traceId;

    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @param message 响应消息
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建成功响应（无数据，默认消息）
     * 
     * @return 成功响应
     */
    public static ApiResponse<Void> success() {
        return success(null, "操作成功");
    }
    
    /**
     * 创建成功响应（无数据）
     * 
     * @param message 响应消息
     * @return 成功响应
     */
    public static ApiResponse<Void> success(String message) {
        return success(null, message);
    }

    /**
     * 创建泛型错误响应
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message, null);
    }

    /**
     * 创建错误响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return 错误响应
     */
    public static ApiResponse<Void> error(Integer code, String message) {
        return ApiResponse.<Void>builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建错误响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param data 错误数据
     * @param <T> 数据类型
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 判断是否成功
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }

    /**
     * 判断是否失败
     * 
     * @return 是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 创建Builder实例
     * @param <T> 数据类型
     * @return Builder实例
     */
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<T>();
    }

    /**
     * ApiResponse的Builder类
     * @param <T> 数据类型
     */
    public static class ApiResponseBuilder<T> {
        private Integer code;
        private String message;
        private T data;
        private LocalDateTime timestamp;
        private String traceId;

        public ApiResponseBuilder<T> code(Integer code) {
            this.code = code;
            return this;
        }

        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiResponseBuilder<T> traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public ApiResponse<T> build() {
            ApiResponse<T> response = new ApiResponse<T>();
            response.code = this.code;
            response.message = this.message;
            response.data = this.data;
            response.timestamp = this.timestamp;
            response.traceId = this.traceId;
            return response;
        }
    }
}