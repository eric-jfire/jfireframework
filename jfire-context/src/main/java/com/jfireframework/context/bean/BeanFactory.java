package com.jfireframework.context.bean;

public interface BeanFactory
{
    /**
     * 给定一个class,返回其bean的表示形式
     * 
     * @param ckass
     * @return
     */
    public Bean parse(Class<?> ckass);
}
