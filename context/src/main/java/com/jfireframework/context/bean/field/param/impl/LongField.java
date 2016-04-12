package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class LongField extends AbstractParamField
{
    
    public LongField(Field field, String value)
    {
        super(field, value);
        this.value = Long.valueOf(value);
    }
    
    @SuppressWarnings("restriction")
    public void setParam(Object entity)
    {
        unsafe.putLong(entity, offset, ((Long) value).longValue());
    }
}
