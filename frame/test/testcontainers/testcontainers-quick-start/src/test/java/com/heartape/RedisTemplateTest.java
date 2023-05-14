package com.heartape;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RedisTemplateTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:6.2.12-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        redis.start();
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @Test
    public void testSimplePutAndGet() {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        opsForValue.set("test", "example");
        String value = opsForValue.get("test");
        assertEquals(value,"example");
    }

}
