package com.jfireframework.mvc.newbinder.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.annotation.MvcRename;
import com.jfireframework.mvc.newbinder.field.array.base.ArrayBooleanField;
import com.jfireframework.mvc.newbinder.field.array.base.ArrayByteField;
import com.jfireframework.mvc.newbinder.field.array.base.ArrayCharField;
import com.jfireframework.mvc.newbinder.field.array.base.ArrayDoubleField;
import com.jfireframework.mvc.newbinder.field.array.base.ArrayFloatField;
import com.jfireframework.mvc.newbinder.field.array.base.ArrayIntField;
import com.jfireframework.mvc.newbinder.field.array.base.ArrayLongField;
import com.jfireframework.mvc.newbinder.field.array.base.ArrayShortField;
import com.jfireframework.mvc.newbinder.field.array.extra.ArrayObjectField;
import com.jfireframework.mvc.newbinder.field.array.extra.ArrayStringField;
import com.jfireframework.mvc.newbinder.field.array.wrapper.ArrayWBooleanField;
import com.jfireframework.mvc.newbinder.field.array.wrapper.ArrayWByteField;
import com.jfireframework.mvc.newbinder.field.array.wrapper.ArrayWCharacterField;
import com.jfireframework.mvc.newbinder.field.array.wrapper.ArrayWDoubleField;
import com.jfireframework.mvc.newbinder.field.array.wrapper.ArrayWFloatField;
import com.jfireframework.mvc.newbinder.field.array.wrapper.ArrayWIntegerField;
import com.jfireframework.mvc.newbinder.field.array.wrapper.ArrayWLongField;
import com.jfireframework.mvc.newbinder.field.array.wrapper.ArrayWShortField;
import com.jfireframework.mvc.newbinder.field.base.BooleanField;
import com.jfireframework.mvc.newbinder.field.base.ByteField;
import com.jfireframework.mvc.newbinder.field.base.CharField;
import com.jfireframework.mvc.newbinder.field.base.DoubleField;
import com.jfireframework.mvc.newbinder.field.base.FloatFiled;
import com.jfireframework.mvc.newbinder.field.base.IntField;
import com.jfireframework.mvc.newbinder.field.base.LongField;
import com.jfireframework.mvc.newbinder.field.base.ShortField;
import com.jfireframework.mvc.newbinder.field.extra.DateField;
import com.jfireframework.mvc.newbinder.field.extra.ObjectField;
import com.jfireframework.mvc.newbinder.field.extra.StringField;
import com.jfireframework.mvc.newbinder.field.list.ListBooleanField;
import com.jfireframework.mvc.newbinder.field.list.ListByteField;
import com.jfireframework.mvc.newbinder.field.list.ListCharacterField;
import com.jfireframework.mvc.newbinder.field.list.ListDoubleField;
import com.jfireframework.mvc.newbinder.field.list.ListFloatField;
import com.jfireframework.mvc.newbinder.field.list.ListIntegerField;
import com.jfireframework.mvc.newbinder.field.list.ListLongField;
import com.jfireframework.mvc.newbinder.field.list.ListObjectField;
import com.jfireframework.mvc.newbinder.field.list.ListShortField;
import com.jfireframework.mvc.newbinder.field.list.ListStringField;
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
            //
            binderConstructors.put(String.class, StringField.class.getConstructor(Field.class));
            binderConstructors.put(String[].class, ArrayStringField.class.getConstructor(Field.class));
            binderConstructors.put(Date.class, DateField.class.getConstructor(Field.class));
            //
            binderConstructors.put(int[].class, ArrayIntField.class.getConstructor(Field.class));
            binderConstructors.put(boolean[].class, ArrayBooleanField.class.getConstructor(Field.class));
            binderConstructors.put(char[].class, ArrayCharField.class.getConstructor(Field.class));
            binderConstructors.put(short[].class, ArrayShortField.class.getConstructor(Field.class));
            binderConstructors.put(long[].class, ArrayLongField.class.getConstructor(Field.class));
            binderConstructors.put(float[].class, ArrayFloatField.class.getConstructor(Field.class));
            binderConstructors.put(double[].class, ArrayDoubleField.class.getConstructor(Field.class));
            binderConstructors.put(byte[].class, ArrayByteField.class.getConstructor(Field.class));
            //
            binderConstructors.put(Integer[].class, ArrayWIntegerField.class.getConstructor(Field.class));
            binderConstructors.put(Boolean[].class, ArrayWBooleanField.class.getConstructor(Field.class));
            binderConstructors.put(Character[].class, ArrayWCharacterField.class.getConstructor(Field.class));
            binderConstructors.put(Short[].class, ArrayWShortField.class.getConstructor(Field.class));
            binderConstructors.put(Long[].class, ArrayWLongField.class.getConstructor(Field.class));
            binderConstructors.put(Float[].class, ArrayWFloatField.class.getConstructor(Field.class));
            binderConstructors.put(Double[].class, ArrayWDoubleField.class.getConstructor(Field.class));
            binderConstructors.put(Byte[].class, ArrayWByteField.class.getConstructor(Field.class));
            //
        }
        catch (Exception e)
        {
            ;
        }
    }
    
    public AbstractBinderField(Field field)
    {
        this.field = field;
        if (field.isAnnotationPresent(MvcRename.class))
        {
            name = field.getAnnotation(MvcRename.class).value();
        }
        else
        {
            name = field.getName();
        }
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
            else if (List.class.isAssignableFrom(type))
            {
                Class<?> arguType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                if (arguType == Integer.class)
                {
                    return new ListIntegerField(field);
                }
                else if (arguType == Long.class)
                {
                    return new ListLongField(field);
                }
                else if (arguType == Short.class)
                {
                    return new ListShortField(field);
                }
                else if (arguType == Boolean.class)
                {
                    return new ListBooleanField(field);
                }
                else if (arguType == Float.class)
                {
                    return new ListFloatField(field);
                }
                else if (arguType == Double.class)
                {
                    return new ListDoubleField(field);
                }
                else if (arguType == Character.class)
                {
                    return new ListCharacterField(field);
                }
                else if (arguType == Byte.class)
                {
                    return new ListByteField(field);
                }
                else if (type == String.class)
                {
                    return new ListStringField(field);
                }
                else
                {
                    return new ListObjectField(field);
                }
            }
            else
            {
                return new ObjectField(field);
            }
        }
    }
}
