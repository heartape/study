package com.heartape;

import com.heartape.entity.Phone;
import com.heartape.entity.User;
import com.heartape.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QueryDSLApplicationTest {

    @Autowired
    private IUserService userService;

    @Test
    public void one() {
        User user = userService.getOne(1);
        System.out.println(user);
    }

    @Test
    public void leftJoin() {
        Phone phone = userService.getPhone(1);
        System.out.println(phone);
    }
}
