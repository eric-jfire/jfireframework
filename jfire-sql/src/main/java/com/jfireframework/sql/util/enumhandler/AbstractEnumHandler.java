package com.jfireframework.sql.util.enumhandler;

import com.jfireframework.sql.annotation.EnumBoundHandler;

public abstract class AbstractEnumHandler<T> implements EnumHandler<T>
{
    protected final Class<? extends Enum<?>> entityClass;
    
    public AbstractEnumHandler(Class<? extends Enum<?>> ckass)
    {
        entityClass = ckass;
    }
    
    public static Class<? extends EnumHandler<?>> getEnumBoundHandler(Class<? extends Enum<?>> ckass)
    {
        if (ckass.isAnnotationPresent(EnumBoundHandler.class))
        {
            return ckass.getAnnotation(EnumBoundHandler.class).value();
        }
        else
        {
            return EnumStringHandler.class;
        }
    }
}
