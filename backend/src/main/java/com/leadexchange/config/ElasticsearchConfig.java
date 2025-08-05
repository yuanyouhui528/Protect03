package com.leadexchange.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// 移除AbstractElasticsearchConfiguration导入，避免版本兼容性问题
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;

/**
 * Elasticsearch配置类
 * 配置Elasticsearch客户端连接和操作模板
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.elasticsearch.enabled", havingValue = "true", matchIfMissing = false)
// @EnableElasticsearchRepositories(basePackages = "com.leadexchange.repository.elasticsearch") // 条件性禁用
public class ElasticsearchConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

    @Value("${spring.elasticsearch.rest.uris:http://localhost:9200}")
    private String[] elasticsearchUris;

    @Value("${spring.elasticsearch.rest.username:}")
    private String username;

    @Value("${spring.elasticsearch.rest.password:}")
    private String password;

    @Value("${spring.elasticsearch.rest.connection-timeout:5000}")
    private int connectionTimeout;

    @Value("${spring.elasticsearch.rest.read-timeout:30000}")
    private int readTimeout;

    @Value("${spring.elasticsearch.rest.connection-request-timeout:5000}")
    private int connectionRequestTimeout;

    @Value("${spring.elasticsearch.rest.max-conn-total:100}")
    private int maxConnTotal;

    @Value("${spring.elasticsearch.rest.max-conn-per-route:10}")
    private int maxConnPerRoute;

    private RestHighLevelClient restHighLevelClient;

    /**
     * 配置Elasticsearch客户端
     * 
     * @return RestHighLevelClient
     */
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        if (restHighLevelClient != null) {
            return restHighLevelClient;
        }

        // 解析主机地址
        HttpHost[] httpHosts = new HttpHost[elasticsearchUris.length];
        for (int i = 0; i < elasticsearchUris.length; i++) {
            String uri = elasticsearchUris[i];
            try {
                // 简单解析URI
                String[] parts = uri.replace("http://", "").replace("https://", "").split(":");
                String host = parts[0];
                int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9200;
                String scheme = uri.startsWith("https") ? "https" : "http";
                httpHosts[i] = new HttpHost(host, port, scheme);
                logger.info("配置Elasticsearch节点: {}://{}:{}", scheme, host, port);
            } catch (Exception e) {
                logger.error("解析Elasticsearch URI失败: {}", uri, e);
                throw new RuntimeException("Invalid Elasticsearch URI: " + uri, e);
            }
        }

        // 创建RestClient构建器
        RestClientBuilder builder = RestClient.builder(httpHosts);

        // 配置认证
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                // 配置连接池
                httpClientBuilder.setMaxConnTotal(maxConnTotal);
                httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
                return httpClientBuilder;
            });
            
            logger.info("配置Elasticsearch认证: 用户名={}", username);
        } else {
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                // 配置连接池
                httpClientBuilder.setMaxConnTotal(maxConnTotal);
                httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
                return httpClientBuilder;
            });
        }

        // 配置请求超时
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectionTimeout);
            requestConfigBuilder.setSocketTimeout(readTimeout);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            return requestConfigBuilder;
        });

        // 配置失败监听器
        builder.setFailureListener(new RestClient.FailureListener() {
            // 移除@Override注解，避免编译错误
            public void onFailure(HttpHost host) {
                logger.error("Elasticsearch节点失败: {}", host);
            }
        });

        restHighLevelClient = new RestHighLevelClient(builder);
        
        logger.info("Elasticsearch客户端配置完成，连接超时: {}ms, 读取超时: {}ms, 最大连接数: {}",
                connectionTimeout, readTimeout, maxConnTotal);
        
        return restHighLevelClient;
    }

    /**
     * 配置Elasticsearch操作模板
     * 
     * @return ElasticsearchOperations
     */
    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        ElasticsearchRestTemplate template = new ElasticsearchRestTemplate(elasticsearchClient());
        logger.info("ElasticsearchRestTemplate配置完成");
        return template;
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void destroy() {
        if (restHighLevelClient != null) {
            try {
                restHighLevelClient.close();
                logger.info("Elasticsearch客户端连接已关闭");
            } catch (Exception e) {
                logger.error("关闭Elasticsearch客户端连接失败", e);
            }
        }
    }
}