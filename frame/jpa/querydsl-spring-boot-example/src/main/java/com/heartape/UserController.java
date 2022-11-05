package com.heartape;

import com.heartape.entity.Phone;
import com.heartape.entity.User;
import com.heartape.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/one")
    public User one(@RequestParam Integer id) {
        return userService.getOne(1);
    }

    @GetMapping("/join")
    public Phone join(@RequestParam Integer id) {
        return userService.getPhone(1);
    }
}
