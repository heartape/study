package com.heartape;

import com.heartape.processor.OrderProcessor;
import com.heartape.proxy.OrderInterceptor;
import net.sf.cglib.proxy.Enhancer;

/**
 * 因为新版java的module功能，需要添加jvm参数：
 * --add-opens java.base/java.lang=ALL-UNNAMED
 * --add-opens java.base/sun.net.util=ALL-UNNAMED
 */
public class Main {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(OrderProcessor.class);
        enhancer.setCallback(new OrderInterceptor());
        OrderProcessor orderProcessor = (OrderProcessor) enhancer.create();
        int process = orderProcessor.process(1);
        System.out.println(process);
    }
}