package com.heartape.proxy;

import com.heartape.processor.OrderProcessor;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class OrderInterceptor implements MethodInterceptor {

    private OrderProcessor orderProcessor;

    /**
     *
     * @param o orderProcessor的代理对象
     * @param method orderProcessor的方法
     * @param objects 参数
     * @param methodProxy orderProcessor的代理对象的方法
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("proxy");

        // 反射调用方法
        // method.invoke(orderProcessor, objects);

        // 代理调用方法，无反射
        // 会执行super.process()
        // methodProxy.invoke(orderProcessor, objects);

        // 代理调用父类方法，无反射
        // 会执行代理类生成的增强process()
        return methodProxy.invokeSuper(o, objects);
    }
}
