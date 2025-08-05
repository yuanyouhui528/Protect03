package com.leadexchange.config;

import com.leadexchange.common.utils.JwtUtils;
import com.leadexchange.security.JwtAuthenticationEntryPoint;
import com.leadexchange.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security 安全配置类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 密码编码器
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * 
     * @param authConfig 认证配置
     * @return AuthenticationManager
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * JWT认证过滤器
     * 
     * @return JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtils);
    }

    /**
     * CORS配置源
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.PATCH.name()
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization",
            "Content-Disposition"
        ));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求缓存时间
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 安全过滤器链配置
     * 
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF
            .csrf().disable()
            
            // 启用CORS
            .cors().configurationSource(corsConfigurationSource())
            
            .and()
            
            // 会话管理 - 无状态
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            .and()
            
            // 异常处理
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            
            .and()
            
            // 请求授权配置
            .authorizeHttpRequests(authz -> authz
                // 公开接口 - 不需要认证
                .antMatchers(
                    "/api/auth/**",           // 认证相关接口
                    "/api/public/**",        // 公开接口
                    "/api/captcha/**",       // 验证码接口
                    "/actuator/**",          // 监控端点
                    "/swagger-ui/**",        // Swagger UI
                    "/swagger-resources/**", // Swagger 资源
                    "/v2/api-docs",          // API 文档
                    "/v3/api-docs/**",       // OpenAPI 文档
                    "/webjars/**",           // WebJars 资源
                    "/favicon.ico",          // 网站图标
                    "/error"                 // 错误页面
                ).permitAll()
                
                // 静态资源 - 不需要认证
                .antMatchers(
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/fonts/**"
                ).permitAll()
                
                // OPTIONS请求 - 预检请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 管理员接口 - 需要ADMIN角色
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                
                // 用户管理接口 - 需要USER或ADMIN角色
                .antMatchers(
                    HttpMethod.GET, "/api/users/**"
                ).hasAnyRole("USER", "ADMIN")
                .antMatchers(
                    HttpMethod.POST, "/api/users/**",
                    HttpMethod.PUT, "/api/users/**",
                    HttpMethod.DELETE, "/api/users/**"
                ).hasRole("ADMIN")
                
                // 线索管理接口 - 需要认证
                .antMatchers("/api/leads/**").authenticated()
                
                // 交换管理接口 - 需要认证
                .antMatchers("/api/exchanges/**").authenticated()
                
                // 评级管理接口 - 需要认证
                .antMatchers("/api/ratings/**").authenticated()
                
                // 数据分析接口 - 需要认证
                .antMatchers("/api/analytics/**").authenticated()
                
                // 通知接口 - 需要认证
                .antMatchers("/api/notifications/**").authenticated()
                
                // 文件上传接口 - 需要认证
                .antMatchers("/api/files/**").authenticated()
                
                // 其他所有请求 - 需要认证
                .anyRequest().authenticated()
            );
        
        // 添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}