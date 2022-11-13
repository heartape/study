package com.heartape.proxy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

public class ProxyFactory {
    @SuppressWarnings("unchecked")
    public  <T extends BaseMapper<?>> T create(Class<T> mapperClass) throws ClassNotFoundException {
        // 父接口
        ParameterizedType parameterizedType = (ParameterizedType) mapperClass.getGenericInterfaces()[0];
        // 泛型
        Class<?> clazz = Class.forName(parameterizedType.getActualTypeArguments()[0].getTypeName());
        // 代理对象
        return (T) Proxy.newProxyInstance(
                mapperClass.getClassLoader(),
                new Class[]{mapperClass},
                new UserProxy(clazz));
    }
}
