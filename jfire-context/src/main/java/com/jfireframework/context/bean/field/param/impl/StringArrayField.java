package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class StringArrayField extends AbstractParamField
{
    
    public StringArrayField(Field field, String value)
    {
        super(field, value);
        this.value = value.split(",");
    }
    
}
