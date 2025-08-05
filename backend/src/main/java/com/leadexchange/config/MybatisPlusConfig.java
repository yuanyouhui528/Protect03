package com.leadexchange.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 配置类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Configuration
public class MybatisPlusConfig {

    private static final Logger logger = LoggerFactory.getLogger(MybatisPlusConfig.class);

    /**
     * MyBatis Plus 拦截器配置
     * 
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置请求的页面大于最大页后操作，true调回到首页，false继续请求
        paginationInterceptor.setOverflow(false);
        // 单页分页条数限制，默认无限制
        paginationInterceptor.setMaxLimit(1000L);
        // 优化count sql
        paginationInterceptor.setOptimizeJoin(true);
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        // 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        
        logger.info("MyBatis Plus 拦截器配置完成");
        return interceptor;
    }

    /**
     * 元数据对象处理器
     * 自动填充创建时间、更新时间、创建人、更新人等字段
     * 
     * @return MetaObjectHandler
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new CustomMetaObjectHandler();
    }

    /**
     * 自定义元数据对象处理器
     */
    @Component
    public static class CustomMetaObjectHandler implements MetaObjectHandler {

        private static final Logger logger = LoggerFactory.getLogger(CustomMetaObjectHandler.class);

        /**
         * 插入时自动填充
         * 
         * @param metaObject 元数据对象
         */
        @Override
        public void insertFill(MetaObject metaObject) {
            logger.debug("开始插入填充...");
            
            LocalDateTime now = LocalDateTime.now();
            Long currentUserId = getCurrentUserId();
            
            // 填充创建时间
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
            
            // 填充更新时间
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
            
            // 填充创建人ID
            if (currentUserId != null) {
                this.strictInsertFill(metaObject, "createBy", Long.class, currentUserId);
                this.strictInsertFill(metaObject, "updateBy", Long.class, currentUserId);
            }
            
            // 填充删除标记
            this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            
            // 填充版本号
            this.strictInsertFill(metaObject, "version", Integer.class, 1);
            
            logger.debug("插入填充完成: createTime={}, createBy={}", now, currentUserId);
        }

        /**
         * 更新时自动填充
         * 
         * @param metaObject 元数据对象
         */
        @Override
        public void updateFill(MetaObject metaObject) {
            logger.debug("开始更新填充...");
            
            LocalDateTime now = LocalDateTime.now();
            Long currentUserId = getCurrentUserId();
            
            // 填充更新时间
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
            
            // 填充更新人ID
            if (currentUserId != null) {
                this.strictUpdateFill(metaObject, "updateBy", Long.class, currentUserId);
            }
            
            logger.debug("更新填充完成: updateTime={}, updateBy={}", now, currentUserId);
        }

        /**
         * 获取当前登录用户ID
         * 
         * @return 用户ID
         */
        private Long getCurrentUserId() {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() 
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                    
                    // 从认证对象中获取用户ID（需要在JWT过滤器中设置）
                    Object details = authentication.getDetails();
                    if (details instanceof org.springframework.security.web.authentication.WebAuthenticationDetails) {
                        // 这里可以从请求属性中获取用户ID
                        // 在JwtAuthenticationFilter中已经设置了userId属性
                        return null; // 暂时返回null，实际项目中需要从上下文获取
                    }
                }
            } catch (Exception e) {
                logger.warn("获取当前用户ID失败: {}", e.getMessage());
            }
            return null;
        }
    }
}