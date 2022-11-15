package com.heartape.service;

import com.heartape.entity.User;
import com.heartape.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {
    @MockBean
    @SpyBean
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(userMapper.selectById(1)).thenReturn(new User(1, "jackson"));
    }

    @Test
    void getById() {
        assertEquals(userService.getById(1).getName(), "jackson");
    }
}