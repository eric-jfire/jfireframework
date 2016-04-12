package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class WLongField extends AbstractParamField
{
    
    public WLongField(Field field, String value)
    {
        super(field, value);
        this.value = Long.valueOf(value);
    }
    
}
