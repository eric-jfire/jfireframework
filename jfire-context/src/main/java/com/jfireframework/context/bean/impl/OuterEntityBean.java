package com.jfireframework.context.bean.impl;

import java.util.Map;

public class OuterEntityBean extends AbstractBean
{
    
    public OuterEntityBean(String beanName, Object entity)
    {
        singletonInstance = entity;
        this.beanName = beanName;
        canEnhance = false;
        canInject = true;
        prototype = false;
        type = entity.getClass();
        originType = type;
    }
    
    @Override
    public Object getInstance()
    {
        return singletonInstance;
    }
    
    @Override
    public Object getInstance(Map<String, Object> beanInstanceMap)
    {
        return singletonInstance;
    }
    
}
