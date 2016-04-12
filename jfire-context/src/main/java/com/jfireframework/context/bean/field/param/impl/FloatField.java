package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class FloatField extends AbstractParamField
{
    
    public FloatField(Field field, String value)
    {
        super(field, value);
        this.value = Float.valueOf(value);
    }
    
    @SuppressWarnings("restriction")
    public void setParam(Object entity)
    {
        unsafe.putFloat(entity, offset, ((Float) value).floatValue());
    }
    
}
