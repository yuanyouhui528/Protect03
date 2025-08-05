package com.leadexchange.security;

import com.leadexchange.common.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 * 从请求中提取JWT令牌并验证，设置Spring Security上下文
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 执行过滤逻辑
     * 
     * @param request 请求
     * @param response 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 从请求中获取JWT令牌
            String jwt = getJwtFromRequest(request);
            
            // 验证令牌并设置认证信息
            if (StringUtils.hasText(jwt) && jwtUtils.validateToken(jwt)) {
                // 从令牌中获取用户信息
                String username = jwtUtils.getUsernameFromToken(jwt);
                Long userId = jwtUtils.getUserIdFromToken(jwt);
                List<String> roles = jwtUtils.getRolesFromToken(jwt);
                List<String> authorities = jwtUtils.getAuthoritiesFromToken(jwt);
                
                if (StringUtils.hasText(username)) {
                    // 构建权限列表
                    List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    
                    // 添加角色权限（Spring Security角色需要ROLE_前缀）
                    roles.forEach(role -> {
                        if (!role.startsWith("ROLE_")) {
                            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                        } else {
                            grantedAuthorities.add(new SimpleGrantedAuthority(role));
                        }
                    });
                    
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                    
                    // 设置认证详情
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 将用户ID添加到认证详情中（可以在后续业务中使用）
                    if (userId != null) {
                        request.setAttribute("userId", userId);
                    }
                    
                    // 设置Spring Security上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.debug("JWT认证成功: username={}, userId={}, authorities={}", 
                               username, userId, grantedAuthorities);
                }
            } else if (StringUtils.hasText(jwt)) {
                logger.warn("JWT令牌验证失败: {}", jwt.substring(0, Math.min(jwt.length(), 20)) + "...");
            }
        } catch (Exception e) {
            logger.error("JWT认证过程中发生异常: {}", e.getMessage(), e);
            // 清除认证上下文
            SecurityContextHolder.clearContext();
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取JWT令牌
     * 
     * @param request HTTP请求
     * @return JWT令牌
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // 从Authorization头中获取令牌
        String bearerToken = request.getHeader(jwtUtils.getHeader());
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtUtils.getPrefix())) {
            // 移除Bearer前缀
            return bearerToken.substring(jwtUtils.getPrefix().length()).trim();
        }
        
        // 从请求参数中获取令牌（备用方案）
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }
        
        return null;
    }

    /**
     * 判断是否应该跳过此过滤器
     * 
     * @param request 请求
     * @return 是否跳过
     * @throws ServletException Servlet异常
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // 跳过公开接口
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/api/captcha/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/swagger-resources/") ||
               path.startsWith("/v2/api-docs") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/webjars/") ||
               path.equals("/favicon.ico") ||
               path.equals("/error") ||
               path.startsWith("/static/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/fonts/");
    }
}