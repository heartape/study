package com.heartape.service;

import com.heartape.entity.User;
import com.heartape.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public User getById(Integer id){
        return userMapper.selectById(id);
    }
}
