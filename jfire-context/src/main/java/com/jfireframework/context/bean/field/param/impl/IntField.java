package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class IntField extends AbstractParamField
{
    
    public IntField(Field field, String value)
    {
        super(field, value);
        this.value = Integer.valueOf(value);
    }
    
    @SuppressWarnings("restriction")
    public void setParam(Object entity)
    {
        unsafe.putInt(entity, offset, ((Integer) value).intValue());
    }
}
