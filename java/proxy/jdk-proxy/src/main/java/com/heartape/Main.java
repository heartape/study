package com.heartape;

import com.heartape.entity.User;
import com.heartape.mapper.UserMapper;
import com.heartape.proxy.UserProxy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {

        // 父接口
        ParameterizedType parameterizedType = (ParameterizedType) UserMapper.class.getGenericInterfaces()[0];
        // 泛型
        Class<?> clazz = Class.forName(parameterizedType.getActualTypeArguments()[0].getTypeName());
        // 代理对象
        UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(
                UserMapper.class.getClassLoader(),
                new Class[]{UserMapper.class},
                new UserProxy(clazz));
        int insert = userMapper.insert(new User(1, "dd"));
        System.out.println(insert);
    }
}