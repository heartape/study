package com.heartape;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LuceneCrudTest {

    private final LuceneCrud luceneCrud = new LuceneCrud();

    @Test
    void insert() {
        luceneCrud.insert();
    }

    @Test
    void select() {
        luceneCrud.select();
    }
}