package com.jfireframework.mvc.binder.field.array;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ArrayWLongField extends AbstractArrayField
{
    
    public ArrayWLongField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        super(prefix, field, cycleSet);
    }
    
    @Override
    protected void setFlagValue(String value, Object _array, int flag, HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response)
    {
        ((Long[]) _array)[flag] = Long.valueOf(value);
    }
    
}
