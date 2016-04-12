package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class IntegerField extends AbstractParamField
{
    
    public IntegerField(Field field, String value)
    {
        super(field, value);
        this.value = Integer.valueOf(value);
    }
    
}
