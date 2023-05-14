package com.heartape;

import com.heartape.mapper.BookMapper;
import com.heartape.entity.Book;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Testcontainers
public class MybatisPlusTest {

    private static final Logger LOG = LoggerFactory.getLogger(MybatisPlusTest.class);

    private static final String TEST = "test";

    @Autowired
    private BookMapper bookMapper;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:5.7.34")
            // .withConfigurationOverride("docker/my.cnf")
            .withDatabaseName(TEST)
            .withUsername(TEST)
            .withPassword(TEST)
            .withInitScript("docker/book.sql")
            .withLogConsumer(new Slf4jLogConsumer(LOG));

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        mysql.start();
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
    }

    @Test
    public void testSimplePutAndGet() {
        Book book = new Book(null, "百年孤独");
        bookMapper.insert(book);
        String title = bookMapper.selectById(book.getId()).getTitle();
        assertEquals(book.getTitle(), title);
    }

}
