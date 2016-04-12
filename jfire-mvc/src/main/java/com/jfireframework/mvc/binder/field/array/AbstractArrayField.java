package com.jfireframework.mvc.binder.field.array;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import com.jfireframework.mvc.binder.field.impl.AbstractBinderField;

public abstract class AbstractArrayField extends AbstractBinderField
{
    protected String[] requestParamNames;
    protected int      length;
    
    @SuppressWarnings("restriction")
    public AbstractArrayField(String prefix, Field field)
    {
        super(prefix, field);
        try
        {
            Object entity = field.getDeclaringClass().newInstance();
            Object array = unsafe.getObject(entity, offset);
            length = Array.getLength(array);
            requestParamNames = new String[length];
            for (int i = 0; i < length; i++)
            {
                requestParamNames[i] = name + '[' + i + ']';
            }
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    
}
