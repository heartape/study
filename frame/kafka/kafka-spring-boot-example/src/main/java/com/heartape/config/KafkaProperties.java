package com.heartape.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties("kafka")
public class KafkaProperties {

    private List<Topic> topics;

    @Getter
    @Setter
    public static class Topic {
        private String name;
        private Integer numPartitions;
        private Short replicationFactor;

        public NewTopic create(){
            return new NewTopic(name, numPartitions, replicationFactor);
        }
    }
}
