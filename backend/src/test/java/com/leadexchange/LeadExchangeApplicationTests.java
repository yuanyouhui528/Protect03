package com.leadexchange;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 应用程序启动测试
 * 验证Spring Boot应用能够正常启动
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class LeadExchangeApplicationTests {

    /**
     * 测试应用上下文加载
     */
    @Test
    void contextLoads() {
        // 如果Spring上下文能够成功加载，此测试就会通过
        // 这验证了所有的配置类和依赖注入都是正确的
    }
}