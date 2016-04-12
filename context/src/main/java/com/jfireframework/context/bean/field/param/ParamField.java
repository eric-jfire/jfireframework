package com.jfireframework.context.bean.field.param;

public interface ParamField
{
    /**
     * 将参数设置到要注入的对象中
     * 
     * @param entity
     */
    public void setParam(Object entity);
}
