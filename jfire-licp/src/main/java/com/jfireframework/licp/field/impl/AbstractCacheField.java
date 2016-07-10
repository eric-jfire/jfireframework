package com.jfireframework.licp.field.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.licp.field.CacheField;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractCacheField implements CacheField
{
    protected static final Unsafe unsafe = ReflectUtil.getUnsafe();
    protected final long          offset;
    protected final boolean       finalField;
    protected final String        fieldName;
    
    public AbstractCacheField(Field field)
    {
        offset = unsafe.objectFieldOffset(field);
        if (Modifier.isFinal(field.getType().getModifiers()))
        {
            finalField = true;
        }
        else
        {
            finalField = false;
        }
        fieldName = field.getName();
    }
    
    public String getName()
    {
        return fieldName;
    }
}
