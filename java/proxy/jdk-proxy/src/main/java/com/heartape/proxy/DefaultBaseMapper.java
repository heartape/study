package com.heartape.proxy;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultBaseMapper implements BaseMapper {

    private Class<?> clazz;

    @Override
    public int insert(Object entity) {
        System.out.println(clazz.getName());
        System.out.println(entity);
        // jdbc操作
        return 1;
    }
}
