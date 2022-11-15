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

/**
 * {@code
 * @MockBean 表示所有方法均需要mock
 * @SpyBean 表示只有经过when显式声明的方法均需要mock
 * }
 */
@SpringBootTest
class UserServiceTest {
    // @MockBean
    @SpyBean
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(userMapper.selectById(1)).thenReturn(new User(1, "jackson"));
    }

    /**
     * 稍微复杂一点的场景：
     * UserMapper在UserService中被调用，在test类中mock UserMapper，依然可以对UserService生效
     */
    @Test
    void getById() {
        assertEquals(userMapper.selectById(1).getName(), "jackson");
        assertEquals(userService.getById(1).getName(), "jackson");
    }
}