package com.heartape.controller;

import com.heartape.config.KafkaProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private AdminClient adminClient;

    @Autowired
    private KafkaProperties kafkaProperties;

    @PostMapping("/topic")
    public void create() {
        List<NewTopic> topics = kafkaProperties.getTopics()
                .stream()
                .map(KafkaProperties.Topic::create)
                .toList();
        adminClient.createTopics(topics);
    }

    @PostMapping("/message/single")
    public void single(@RequestBody String message) {
        for (int i = 0; i < 3; i++) {
            kafkaTemplate.send("topic.test.single", i, "test", message);
        }
    }

    @PostMapping("/message/batch")
    public void batch(@RequestBody String message) {
        for (int i = 0; i < 3; i++) {
            kafkaTemplate.send("topic.test.batch", message);
        }
    }
}
