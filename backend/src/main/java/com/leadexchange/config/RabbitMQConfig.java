package com.leadexchange.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * RabbitMQ配置类
 * 配置消息队列、交换机、绑定关系和消息转换器
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    // 交换机名称
    public static final String LEAD_EXCHANGE = "lead.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String ANALYTICS_EXCHANGE = "analytics.exchange";
    
    // 队列名称
    public static final String LEAD_CREATED_QUEUE = "lead.created.queue";
    public static final String LEAD_UPDATED_QUEUE = "lead.updated.queue";
    public static final String LEAD_EXCHANGED_QUEUE = "lead.exchanged.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String ANALYTICS_QUEUE = "analytics.queue";
    
    // 路由键
    public static final String LEAD_CREATED_ROUTING_KEY = "lead.created";
    public static final String LEAD_UPDATED_ROUTING_KEY = "lead.updated";
    public static final String LEAD_EXCHANGED_ROUTING_KEY = "lead.exchanged";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.send";
    public static final String ANALYTICS_ROUTING_KEY = "analytics.data";
    
    // 死信队列
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "dead.letter";

    /**
     * 消息转换器配置
     * 使用Jackson2JsonMessageConverter进行JSON序列化
     * 
     * @return MessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        logger.info("配置RabbitMQ消息转换器");
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate配置
     * 配置消息发送模板和重试机制
     * 
     * @param connectionFactory 连接工厂
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        
        // 配置重试机制
        RetryTemplate retryTemplate = new RetryTemplate();
        
        // 重试策略：最多重试3次
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        // 退避策略：指数退避
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000); // 初始间隔1秒
        backOffPolicy.setMultiplier(2.0); // 倍数
        backOffPolicy.setMaxInterval(10000); // 最大间隔10秒
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        rabbitTemplate.setRetryTemplate(retryTemplate);
        
        // 开启发送确认
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                logger.debug("消息发送成功: {}", correlationData);
            } else {
                logger.error("消息发送失败: {}, 原因: {}", correlationData, cause);
            }
        });
        
        // 开启返回确认
        rabbitTemplate.setReturnsCallback(returned -> {
            logger.error("消息返回: {}, 回复码: {}, 回复文本: {}, 交换机: {}, 路由键: {}",
                    returned.getMessage(), returned.getReplyCode(), returned.getReplyText(),
                    returned.getExchange(), returned.getRoutingKey());
        });
        
        logger.info("RabbitTemplate配置完成");
        return rabbitTemplate;
    }

    /**
     * 监听器容器工厂配置
     * 
     * @param connectionFactory 连接工厂
     * @return RabbitListenerContainerFactory
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        
        // 设置并发消费者数量
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        
        // 设置预取数量
        factory.setPrefetchCount(1);
        
        // 开启手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        
        logger.info("RabbitMQ监听器容器工厂配置完成");
        return factory;
    }

    // ==================== 交换机配置 ====================

    /**
     * 线索交换机
     */
    @Bean
    public TopicExchange leadExchange() {
        return ExchangeBuilder.topicExchange(LEAD_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 通知交换机
     */
    @Bean
    public TopicExchange notificationExchange() {
        return ExchangeBuilder.topicExchange(NOTIFICATION_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 数据分析交换机
     */
    @Bean
    public TopicExchange analyticsExchange() {
        return ExchangeBuilder.topicExchange(ANALYTICS_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DEAD_LETTER_EXCHANGE)
                .durable(true)
                .build();
    }

    // ==================== 队列配置 ====================

    /**
     * 线索创建队列
     */
    @Bean
    public Queue leadCreatedQueue() {
        return QueueBuilder.durable(LEAD_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 300000) // 5分钟TTL
                .build();
    }

    /**
     * 线索更新队列
     */
    @Bean
    public Queue leadUpdatedQueue() {
        return QueueBuilder.durable(LEAD_UPDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    /**
     * 线索交换队列
     */
    @Bean
    public Queue leadExchangedQueue() {
        return QueueBuilder.durable(LEAD_EXCHANGED_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    /**
     * 通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 600000) // 10分钟TTL
                .build();
    }

    /**
     * 数据分析队列
     */
    @Bean
    public Queue analyticsQueue() {
        return QueueBuilder.durable(ANALYTICS_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .withArgument("x-message-ttl", 1800000) // 30分钟TTL
                .build();
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    // ==================== 绑定配置 ====================

    /**
     * 线索创建队列绑定
     */
    @Bean
    public Binding leadCreatedBinding() {
        return BindingBuilder.bind(leadCreatedQueue())
                .to(leadExchange())
                .with(LEAD_CREATED_ROUTING_KEY);
    }

    /**
     * 线索更新队列绑定
     */
    @Bean
    public Binding leadUpdatedBinding() {
        return BindingBuilder.bind(leadUpdatedQueue())
                .to(leadExchange())
                .with(LEAD_UPDATED_ROUTING_KEY);
    }

    /**
     * 线索交换队列绑定
     */
    @Bean
    public Binding leadExchangedBinding() {
        return BindingBuilder.bind(leadExchangedQueue())
                .to(leadExchange())
                .with(LEAD_EXCHANGED_ROUTING_KEY);
    }

    /**
     * 通知队列绑定
     */
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    /**
     * 数据分析队列绑定
     */
    @Bean
    public Binding analyticsBinding() {
        return BindingBuilder.bind(analyticsQueue())
                .to(analyticsExchange())
                .with(ANALYTICS_ROUTING_KEY);
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }
}