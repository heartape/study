package com.heartape.proxy;

import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@AllArgsConstructor
public class UserProxy implements InvocationHandler {

    private Class<?> clazz;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DefaultBaseMapper baseMapper = new DefaultBaseMapper(clazz);
        Method methodBase = baseMapper.getClass().getMethod(method.getName(), method.getParameterTypes());
        return methodBase.invoke(baseMapper, args);
    }
}
