package com.heartape.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MyKafkaListener {

    /**
     * errorHandler = {@link  org.springframework.kafka.listener.KafkaListenerErrorHandler}
     *
     * containerFactory = {@link org.springframework.kafka.config.KafkaListenerContainerFactory}
     */
    @KafkaListener(
            id = "example",
            clientIdPrefix = "example-batch",
            groupId = "test",
            topics = "test-batch",
            errorHandler = "kafkaErrorHandler",
            containerFactory = "",
            batch = "true"
    )
    public void batch(List<String> list) {
        log.info("接收{}条消息", list.size());
        for (String s : list) {
            log.debug(s);
        }
    }

    /**
     * @param payloads 批量数据集合
     */
    public void batchRecord(@Payload List<ConsumerRecord<?, String>> payloads) {
        log.info("接收{}条消息", payloads.size());
        for (ConsumerRecord<?, ?> payload : payloads) {
            log.info("partition:{} - batch接收 - {}", payload.partition(), payload.value());
        }
    }

    public void single(@Payload ConsumerRecord<?, String> payload,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp) {
        log.info("partition:{} - single接收 - {}", partition, payload.value());
    }
}
