package com.heartape.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/single")
    public void single(@RequestBody String message) {
        for (int i = 0; i < 3; i++) {
            kafkaTemplate.send("topic.test.single", message);
        }
    }

    @PostMapping("/batch")
    public void batch(@RequestBody String message) {
        for (int i = 0; i < 3; i++) {
            kafkaTemplate.send("topic.test.batch", message);
        }
    }
}
