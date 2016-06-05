package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.util.Set;

public class StringField extends AbstractBinderField
{
    
    public StringField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        super(prefix, field, cycleSet);
    }
    
    @Override
    protected void set(Object entity, String value)
    {
        unsafe.putObject(entity, offset, value);
    }
    
}
