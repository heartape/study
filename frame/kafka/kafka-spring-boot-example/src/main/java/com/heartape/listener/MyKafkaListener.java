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
     *
     * @param payloads 批量数据集合
     */
    @KafkaListener(
            id = "topic-test-batch",
            clientIdPrefix = "clientId",
            groupId = "test.batch",
            topics = "topic.test.batch",
            errorHandler = "",
            containerFactory = "",
            batch = "true"
    )
    public void batch(@Payload List<ConsumerRecord<?, ?>> payloads) {
        log.info("接收{}条消息", payloads.size());
        for (ConsumerRecord<?, ?> payload : payloads) {
            log.info("partition:{} - batch接收 - {}", payload.partition(), payload.value());
        }
    }

    // 手动指定分区，只有有需要时才会
    // @KafkaListener(
    //         id = "topic-test-single",
    //         groupId = "test.single",
    //         topicPartitions = {
    //                 @TopicPartition(topic = "topic.test.single", partitions = {"0", "1", "2"})
    //         }
    // )
    @KafkaListener(
            id = "topic-test-single",
            groupId = "test.single",
            topics = "topic.test.single"
    )
    public void single(@Payload ConsumerRecord<?, ?> payload,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp) {
        log.info("partition:{} - single接收 - {}", partition, payload.value());
    }
}
