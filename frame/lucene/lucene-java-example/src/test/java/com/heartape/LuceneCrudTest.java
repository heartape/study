package com.heartape;

import org.junit.jupiter.api.Test;

import java.util.List;

class LuceneCrudTest {

    private final LuceneCrud luceneCrud = new LuceneCrud();

    @Test
    void insert() {
        luceneCrud.insert(new User(1L, "jackson", "the best singer", "experience"));
    }

    @Test
    void insertList() {
        List<User> userList = List.of(
                new User(1L, "jackson", "the best singer", "experience"),
                new User(2L, "jackson", "the best singer", "experience"),
                new User(3L, "jackson", "the best singer", "experience"),
                new User(4L, "jackson", "the best singer", "experience"),
                new User(11L, "jackson", "the best singer", "experience")
        );
        luceneCrud.insertList(userList);
    }

    @Test
    void select() {
        luceneCrud.select(1L);
    }

    @Test
    void selectByName() {
        luceneCrud.selectByName("jackson");
    }

    @Test
    void selectByNameSortById() {
        luceneCrud.selectByNameSortById("jackson", 2);
    }

    @Test
    void updateById() {
        luceneCrud.updateById(new User(1L, "jobs", "i am jobs", "experience"));
    }

    @Test
    void updateByName() {
        luceneCrud.updateByName(new User(6L, "jobs", "i am jobs", "experience"), "jackson");
    }
}