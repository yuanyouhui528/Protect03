package com.leadexchange.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

/**
 * Swagger配置类
 * 配置API文档生成和JWT认证
 * 在H2测试环境下禁用以避免初始化问题
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Configuration
@Profile("!h2")
public class SwaggerConfig {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:招商线索流通平台}")
    private String applicationName;

    /**
     * 配置OpenAPI文档
     * 
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {

        // JWT安全方案
        SecurityScheme jwtSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("JWT认证")
                .description("请在请求头中添加JWT令牌：Authorization: Bearer <token>");

        // 安全要求
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("JWT认证");

        // 服务器配置
        List<Server> servers = Arrays.asList(
                new Server().url("http://localhost:" + serverPort).description("本地开发环境"),
                new Server().url("https://api.leadexchange.com").description("生产环境")
        );

        // 联系信息
        Contact contact = new Contact()
                .name("开发团队")
                .email("dev@leadexchange.com")
                .url("https://www.leadexchange.com");

        // 许可证信息
        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        // API信息
        Info apiInfo = new Info()
                .title(applicationName + " API文档")
                .description("招商线索流通平台后端API接口文档\n\n" +
                        "## 功能模块\n" +
                        "- **用户管理**: 用户注册、登录、权限管理\n" +
                        "- **线索管理**: 线索发布、编辑、审核、搜索\n" +
                        "- **评级引擎**: 线索质量评级算法\n" +
                        "- **交换引擎**: 线索价值交换机制\n" +
                        "- **数据分析**: 业务数据统计分析\n" +
                        "- **通知服务**: 多渠道消息通知\n\n" +
                        "## 认证方式\n" +
                        "本API使用JWT Bearer Token进行身份认证。\n" +
                        "请在请求头中添加：`Authorization: Bearer <your-jwt-token>`\n\n" +
                        "## 响应格式\n" +
                        "所有API响应都遵循统一的JSON格式：\n" +
                        "```json\n" +
                        "{\n" +
                        "  \"code\": 200,\n" +
                        "  \"message\": \"操作成功\",\n" +
                        "  \"data\": {},\n" +
                        "  \"timestamp\": \"2024-01-01T00:00:00Z\"\n" +
                        "}\n" +
                        "```\n\n" +
                        "## 错误码说明\n" +
                        "- **200**: 操作成功\n" +
                        "- **400**: 请求参数错误\n" +
                        "- **401**: 未授权访问\n" +
                        "- **403**: 权限不足\n" +
                        "- **404**: 资源不存在\n" +
                        "- **500**: 服务器内部错误")
                .version("1.0.0")
                .contact(contact)
                .license(license);

        OpenAPI openAPI = new OpenAPI()
                .info(apiInfo)
                .servers(servers)
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("JWT认证", jwtSecurityScheme));

        logger.info("Swagger OpenAPI配置完成，访问地址: http://localhost:{}/swagger-ui/index.html", serverPort);
        return openAPI;
    }
}