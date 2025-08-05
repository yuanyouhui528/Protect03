package com.leadexchange.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadexchange.common.result.Result;
import com.leadexchange.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT认证入口点
 * 处理未认证用户访问需要认证资源的情况
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理认证异常
     * 
     * @param request 请求
     * @param response 响应
     * @param authException 认证异常
     * @throws IOException IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        logger.warn("未认证用户尝试访问受保护资源: {} {}, 异常信息: {}", 
                   request.getMethod(), request.getRequestURI(), authException.getMessage());
        
        // 设置响应状态码
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 设置响应内容类型
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        // 设置响应头
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        
        // 构建错误响应
        Result<Void> result = Result.error(ResultCode.UNAUTHORIZED, "访问被拒绝，请先登录");
        
        // 写入响应
        String jsonResponse = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}