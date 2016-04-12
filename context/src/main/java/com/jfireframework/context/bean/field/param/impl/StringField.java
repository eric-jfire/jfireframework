package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class StringField extends AbstractParamField
{
    
    public StringField(Field field, String value)
    {
        super(field, value);
        this.value = value;
    }
    
}
