package com.heartape.producer;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Component
public class RabbitProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private IdGenerator idGenerator;

    public void produce(String exchange, String routingKey, Object message){
        produce(exchange, routingKey, message, 0,0);
    }

    /**
     * 向rabbitMQ发布消息
     * @param exchange:交换机
     * @param routingKey:路由
     * @param o:消息
     * @param time:延时时间
     * @param priority:队列优先级
     */
    public void produce(String exchange, String routingKey, Object o, int time, int priority){
        String id = idGenerator.generate();
        MessagePostProcessor messagePostProcessor = message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setContentEncoding(StandardCharsets.UTF_8.name());
            messageProperties.setMessageId(id);
            if (time != 0){
                messageProperties.setDelay(time);
            }
            if (priority != 0){
                messageProperties.setPriority(priority);
            }
            // messageProperties.setHeader("xxx", "xxx");
            return message;
        };
        CorrelationData correlationData = new CorrelationData(id);
        rabbitTemplate.convertAndSend(exchange, routingKey, o, messagePostProcessor, correlationData);
    }
}
