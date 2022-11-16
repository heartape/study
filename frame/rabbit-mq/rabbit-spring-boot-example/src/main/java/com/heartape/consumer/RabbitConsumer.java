package com.heartape.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RabbitConsumer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "consumer-queue",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange",value = "deadLetter"),
                            @Argument(name = "x-dead-letter-routing-key",value = "deadLetter"),
                            @Argument(name = "x-message-ttl",value = "5000",type = "java.lang.Integer")}),
            exchange = @Exchange("consumer-exchange"),
            key = "consumer-key"))
    public void consumer(Object o, Channel channel, Message message) throws IOException {
        printInfo(o, message);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag,false);
        channel.basicNack(deliveryTag,false,false);
        channel.basicReject(deliveryTag,false);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "dead-queue"),
            exchange = @Exchange("dead-exchange"),
            key = "dead-key"))
    public void dead(Object o, Channel channel, Message message) throws IOException {
        printInfo(o, message);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag,false);
        channel.basicNack(deliveryTag,false,false);
        channel.basicReject(deliveryTag,false);
    }

    /**
     * 参数打印
     * @param o 数据
     * @param message 消息参数
     */
    private void printInfo(Object o, Message message){
        String messageId = message.getMessageProperties().getMessageId();
        String queue = message.getMessageProperties().getConsumerQueue();
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String exchange = message.getMessageProperties().getReceivedExchange();
        log.info("""
                从rabbit接收消息:
                MessageId:{}
                Queue:{}
                RoutingKey:{}
                Exchange:{}
                Data:{}
                """,
                messageId, queue, routingKey, exchange, o);
    }
}
