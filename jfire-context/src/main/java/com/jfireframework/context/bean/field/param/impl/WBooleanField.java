package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class WBooleanField extends AbstractParamField
{
    
    public WBooleanField(Field field, String value)
    {
        super(field, value);
        this.value = Boolean.valueOf(value);
    }
    
}
