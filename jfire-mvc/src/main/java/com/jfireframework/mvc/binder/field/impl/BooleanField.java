package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.util.Set;

public class BooleanField extends AbstractBinderField
{
    
    public BooleanField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        super(prefix, field, cycleSet);
    }
    
    @Override
    protected void set(Object entity, String value)
    {
        unsafe.putBoolean(entity, offset, Boolean.parseBoolean(value));
    }
    
}
