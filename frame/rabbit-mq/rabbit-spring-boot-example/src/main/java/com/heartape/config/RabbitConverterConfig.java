package com.heartape.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConverterConfig {

    /**
     * 使用Jackson2JsonMessageConverter消息转换器
     * 发送消息时也必须以json格式
     * spring.rabbitmq.custom.converter=json
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.rabbitmq.custom",name = "converter",havingValue = "json")
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
