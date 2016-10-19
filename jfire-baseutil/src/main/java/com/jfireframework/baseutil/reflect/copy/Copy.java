package com.jfireframework.baseutil.reflect.copy;

import java.lang.reflect.ParameterizedType;

public abstract class Copy<S, D>
{
    private Class<S> source;
    private Class<D> destination;
    
    @SuppressWarnings("unchecked")
    public Copy()
    {
        ParameterizedType tmp = (ParameterizedType) (this.getClass().getGenericSuperclass());
        source = (Class<S>) tmp.getActualTypeArguments()[0];
        destination = (Class<D>) tmp.getActualTypeArguments()[1];
    }
    
    public CopyUtil<S, D> instance()
    {
        return new CopyUtilImpl<S, D>(source, destination);
    }
}
