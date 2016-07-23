package com.jfireframework.context.bean;

public interface BeanBuilder
{
    public <T> Bean parse(Class<T> ckass);
}
