package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.util.Set;

public class DoubleField extends AbstractBinderField
{
    public DoubleField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        super(prefix, field, cycleSet);
    }
    
    @Override
    protected void set(Object entity, String value)
    {
        unsafe.putDouble(entity, offset, Double.parseDouble(value));
    }
    
}
