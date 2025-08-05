package com.leadexchange.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * CORS跨域配置类
 * 配置跨域资源共享策略
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.exposed-headers:Authorization,Content-Type,X-Total-Count}")
    private String[] exposedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    /**
     * 配置全局CORS策略
     * 
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .exposedHeaders(exposedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
        
        logger.info("全局CORS配置完成 - 允许的源: {}, 允许的方法: {}, 最大缓存时间: {}秒",
                Arrays.toString(allowedOrigins), Arrays.toString(allowedMethods), maxAge);
    }

    /**
     * 配置CORS配置源
     * 用于Spring Security的CORS配置
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 设置允许的源
        if (allowedOrigins.length == 1 && "*".equals(allowedOrigins[0])) {
            configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        } else {
            configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        }
        
        // 设置允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        
        // 设置允许的请求头
        if (allowedHeaders.length == 1 && "*".equals(allowedHeaders[0])) {
            configuration.setAllowedHeaders(Collections.singletonList("*"));
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        }
        
        // 设置暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders));
        
        // 是否允许携带凭证
        configuration.setAllowCredentials(allowCredentials);
        
        // 预检请求的缓存时间
        configuration.setMaxAge(maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        logger.info("CORS配置源创建完成");
        return source;
    }

    /**
     * 配置CORS过滤器
     * 提供更细粒度的CORS控制
     * 
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsFilter corsFilter = new CorsFilter(corsConfigurationSource());
        logger.info("CORS过滤器配置完成");
        return corsFilter;
    }
}