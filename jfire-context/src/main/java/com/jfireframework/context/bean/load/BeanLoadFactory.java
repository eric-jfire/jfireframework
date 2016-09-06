package com.jfireframework.context.bean.load;

public interface BeanLoadFactory
{
    /**
     * 根据类获得对应的对象
     * 
     * @param ckass
     * @return
     */
    public <T, E extends T> E load(Class<T> ckass);
}
