package com.heartape;

import com.heartape.entity.User;
import com.heartape.mapper.UserMapper;
import com.heartape.proxy.ProxyFactory;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {

        ProxyFactory proxyFactory = new ProxyFactory();
        UserMapper userMapper = proxyFactory.create(UserMapper.class);
        // 调用
        int insert = userMapper.insert(new User(1, "dd"));
        System.out.println(insert);
    }


}