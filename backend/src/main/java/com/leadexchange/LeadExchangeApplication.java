package com.leadexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 招商线索流通Web应用主启动类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {
        ElasticsearchRepositoriesAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class,
        ElasticsearchRestClientAutoConfiguration.class,
        MybatisPlusAutoConfiguration.class
})
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = {
    "com.leadexchange.repository"
})
public class LeadExchangeApplication {

    /**
     * 应用程序入口点
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(LeadExchangeApplication.class, args);
        System.out.println("\n" +
                "  _                    _   _____          _                            \n" +
                " | |                  | | |  ___|        | |                           \n" +
                " | |     ___  __ _  __| | | |____  _____ | |__   __ _ _ __   __ _  ___  \n" +
                " | |    / _ \\/ _` |/ _` | |  __\\ \\/ / __|| '_ \\ / _` | '_ \\ / _` |/ _ \\ \n" +
                " | |___|  __/ (_| | (_| | | |___>  < (__ | | | | (_| | | | | (_| |  __/ \n" +
                " \\_____/\\___|\\__,_|\\__,_| \\____/_/\\_\\___||_| |_|\\__,_|_| |_|\\__, |\\___| \n" +
                "                                                              __/ |     \n" +
                "                                                             |___/      \n" +
                "\n招商线索流通Web应用后端服务启动成功！\n");
    }

}