package com.heartape.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "spring.rabbitmq.custom",name = "callback.enable",havingValue = "true")
public class RabbitCallbackConfig implements ConfirmCallback, ReturnsCallback {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * true:当交换机无法路由消息时，把消息返回给生产者
     * false:当交换机无法路由消息时，直接丢弃消息
     */
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
        rabbitTemplate.setMandatory(true);
    }

    /**
     * 交换机回调(消息发送到交换机)
     */
    @Override
    public void confirm(@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) {
        if (Objects.isNull(cause) || cause.isBlank()){
            cause = "未知";
        }
        if (Objects.nonNull(correlationData)) {
            String messageId = correlationData.getId();
            if (ack){
                log.info("\nconfirm回调>>>交换机收到消息\nmessageId:{}", messageId);
            } else {
                log.info("\nconfirm回调>>>交换机未收到消息\nmessageId:{}\n原因:{}", messageId, cause);
            }
        } else {
            log.info("\nconfirm回调>>>交换机未收到消息\nmessageId:{}\n原因:{}", "未知", cause);
        }
    }

    /**
     * 路由回调(消息无法路由)
     * 延时插件会导致一直调用该回调，需要处理
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info( "\n"+"returnedMessage回调:"
                + "\nMessageId:"+returnedMessage.getMessage().getMessageProperties().getHeader("spring_returned_message_correlation")
                + "\nRoutingKey:"+returnedMessage.getRoutingKey()
                + "\nExchange:"+returnedMessage.getExchange()
                + "\ncause:"+returnedMessage.getReplyText());
    }
}
