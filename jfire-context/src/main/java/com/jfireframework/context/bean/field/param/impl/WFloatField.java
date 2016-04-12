package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class WFloatField extends AbstractParamField
{
    
    public WFloatField(Field field, String value)
    {
        super(field, value);
        this.value = Float.valueOf(value);
    }
    
}
