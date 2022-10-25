package com.heartape.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MyKafkaListener {

    @KafkaListener(topics = "test", groupId = "testGroup")
    public void listen(@Payload(required = false) ConsumerRecord<String, String> payload,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(value = KafkaHeaders.RECEIVED_MESSAGE_KEY,required = false) String key) {
        System.out.println(payload);
    }
}
