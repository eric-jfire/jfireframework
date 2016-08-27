package com.jfireframework.mvc.newbinder.field;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.newbinder.field.array.ArrayIntField;
import com.jfireframework.mvc.newbinder.field.array.ArrayObjectField;
import com.jfireframework.mvc.newbinder.field.array.ArrayWIntegerField;
import com.jfireframework.mvc.newbinder.field.base.FloatFiled;
import com.jfireframework.mvc.newbinder.field.base.IntField;
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
        if (type.isArray())
        {
            type = type.getComponentType();
            if (type == int.class)
            {
                return new ArrayIntField(field);
            }
            else if (type == Integer.class)
            {
                return new ArrayWIntegerField(field);
            }
            else
            {
                return new ArrayObjectField(field);
            }
        }
        else
        {
            if (type == String.class)
            {
                return new StringField(field);
            }
            else if (type == int.class)
            {
                return new IntField(field);
            }
            else if (type == float.class)
            {
                return new FloatFiled(field);
            }
            else if (type == Float.class)
            {
                return new WFloatField(field);
            }
            else if (type == Integer.class)
            {
                return new WIntegerField(field);
            }
            else if (type == Long.class)
            {
                return new WLongField(field);
            }
            else
            {
                return new ObjectField(field);
            }
        }
    }
}
