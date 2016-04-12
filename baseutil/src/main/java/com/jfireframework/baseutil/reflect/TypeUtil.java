package com.jfireframework.baseutil.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeUtil<T>
{
    private Type type;
    
    public TypeUtil()
    {
        ParameterizedType tmp = (ParameterizedType) (this.getClass().getGenericSuperclass());
        type = tmp.getActualTypeArguments()[0];
    }
    
    public Type getType()
    {
        return type;
    }
    
}
