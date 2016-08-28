package com.jfireframework.mvc.newbinder.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.newbinder.field.array.ArrayObjectField;
import com.jfireframework.mvc.newbinder.field.base.BooleanField;
import com.jfireframework.mvc.newbinder.field.base.ByteField;
import com.jfireframework.mvc.newbinder.field.base.CharField;
import com.jfireframework.mvc.newbinder.field.base.DoubleField;
import com.jfireframework.mvc.newbinder.field.base.FloatFiled;
import com.jfireframework.mvc.newbinder.field.base.IntField;
import com.jfireframework.mvc.newbinder.field.base.LongField;
import com.jfireframework.mvc.newbinder.field.base.ShortField;
import com.jfireframework.mvc.newbinder.field.wrapper.WBooleanField;
import com.jfireframework.mvc.newbinder.field.wrapper.WByteField;
import com.jfireframework.mvc.newbinder.field.wrapper.WCharacterField;
import com.jfireframework.mvc.newbinder.field.wrapper.WDoubleField;
import com.jfireframework.mvc.newbinder.field.wrapper.WFloatField;
import com.jfireframework.mvc.newbinder.field.wrapper.WIntegerField;
import com.jfireframework.mvc.newbinder.field.wrapper.WLongField;
import com.jfireframework.mvc.newbinder.field.wrapper.WShortField;
import sun.misc.Unsafe;

public abstract class AbstractBinderField implements BinderField
{
    protected static final Unsafe                                            unsafe             = ReflectUtil.getUnsafe();
    protected final Field                                                    field;
    protected final long                                                     offset;
    protected final String                                                   name;
    protected static final Map<Class<?>, Constructor<? extends BinderField>> binderConstructors = new HashMap<Class<?>, Constructor<? extends BinderField>>();
    
    static
    {
        try
        {
            binderConstructors.put(int.class, IntField.class.getConstructor(Field.class));
            binderConstructors.put(short.class, ShortField.class.getConstructor(Field.class));
            binderConstructors.put(long.class, LongField.class.getConstructor(Field.class));
            binderConstructors.put(float.class, FloatFiled.class.getConstructor(Field.class));
            binderConstructors.put(double.class, DoubleField.class.getConstructor(Field.class));
            binderConstructors.put(byte.class, ByteField.class.getConstructor(Field.class));
            binderConstructors.put(char.class, CharField.class.getConstructor(Field.class));
            binderConstructors.put(boolean.class, BooleanField.class.getConstructor(Field.class));
            //
            binderConstructors.put(Integer.class, WIntegerField.class.getConstructor(Field.class));
            binderConstructors.put(Short.class, WShortField.class.getConstructor(Field.class));
            binderConstructors.put(Long.class, WLongField.class.getConstructor(Field.class));
            binderConstructors.put(Byte.class, WByteField.class.getConstructor(Field.class));
            binderConstructors.put(Double.class, WDoubleField.class.getConstructor(Field.class));
            binderConstructors.put(Float.class, WFloatField.class.getConstructor(Field.class));
            binderConstructors.put(Character.class, WCharacterField.class.getConstructor(Field.class));
            binderConstructors.put(Boolean.class, WBooleanField.class.getConstructor(Field.class));
        }
        catch (Exception e)
        {
            ;
        }
    }
    
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
        Constructor<?> constructor = binderConstructors.get(type);
        if (constructor != null)
        {
            try
            {
                return (BinderField) constructor.newInstance(field);
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
        }
        else
        {
            if (type.isArray())
            {
                return new ArrayObjectField(field);
            }
            else
            {
                return new ObjectField(field);
            }
        }
    }
}
