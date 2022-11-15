package com.heartape.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 生成测试类后添加assertEquals方法
 */
class MathUtilsTest {
    @BeforeAll
    static void start() {
    }

    @AfterAll
    static void end() {
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void plus() {
        assertEquals(MathUtils.plus(2, 3), 5);
    }

    @Test
    void minus() {
        assertEquals(MathUtils.minus(2, 3), -1);
    }
}