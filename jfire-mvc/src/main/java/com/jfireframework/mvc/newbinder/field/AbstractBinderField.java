package com.jfireframework.mvc.newbinder.field;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public abstract class AbstractBinderField implements BinderField
{
    protected static final Unsafe unsafe = ReflectUtil.getUnsafe();
    protected final Field         field;
    protected final long          offset;
    protected final String        name;
    
    public AbstractBinderField(Field field)
    {
        this.field = field;
        name = field.getName();
        offset = unsafe.objectFieldOffset(field);
    }
    
    public String getName()
    {
        return name;
    }
    
    public static BinderField build(Field field)
    {
        Class<?> type = field.getType();
        if (type == String.class)
        {
            return new StringField(field);
        }
        else if (type == int.class)
        {
            return new IntField(field);
        }
        else
        {
            return null;
        }
    }
}
