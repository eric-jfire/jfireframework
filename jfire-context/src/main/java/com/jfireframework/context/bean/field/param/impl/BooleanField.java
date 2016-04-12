package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class BooleanField extends AbstractParamField
{
    public BooleanField(Field field, String value)
    {
        super(field, value);
        this.value = Boolean.valueOf(value);
    }
    
    @SuppressWarnings("restriction")
    public void setParam(Object entity)
    {
        unsafe.putBoolean(entity, offset, (Boolean) value);
    }
}
