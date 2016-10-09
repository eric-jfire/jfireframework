package com.jfireframework.mvc.binder.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.annotation.MvcRename;
import com.jfireframework.mvc.binder.field.array.base.ArrayWBooleanField;
import com.jfireframework.mvc.binder.field.array.base.ArrayWByteField;
import com.jfireframework.mvc.binder.field.array.base.ArrayWCharacterField;
import com.jfireframework.mvc.binder.field.array.base.ArrayWDoubleField;
import com.jfireframework.mvc.binder.field.array.base.ArrayWFloatField;
import com.jfireframework.mvc.binder.field.array.base.ArrayWIntegerField;
import com.jfireframework.mvc.binder.field.array.base.ArrayWLongField;
import com.jfireframework.mvc.binder.field.array.base.ArrayWShortField;
import com.jfireframework.mvc.binder.field.array.extra.ArrayEnumField;
import com.jfireframework.mvc.binder.field.array.extra.ArrayObjectField;
import com.jfireframework.mvc.binder.field.array.extra.ArrayStringField;
import com.jfireframework.mvc.binder.field.base.BooleanField;
import com.jfireframework.mvc.binder.field.base.ByteField;
import com.jfireframework.mvc.binder.field.base.CharField;
import com.jfireframework.mvc.binder.field.base.DoubleField;
import com.jfireframework.mvc.binder.field.base.FloatFiled;
import com.jfireframework.mvc.binder.field.base.IntField;
import com.jfireframework.mvc.binder.field.base.LongField;
import com.jfireframework.mvc.binder.field.base.ShortField;
import com.jfireframework.mvc.binder.field.extra.DateField;
import com.jfireframework.mvc.binder.field.extra.EnumField;
import com.jfireframework.mvc.binder.field.extra.ListField;
import com.jfireframework.mvc.binder.field.extra.ObjectField;
import com.jfireframework.mvc.binder.field.extra.SqlDateField;
import com.jfireframework.mvc.binder.field.extra.StringField;
import com.jfireframework.mvc.binder.field.wrapper.WBooleanField;
import com.jfireframework.mvc.binder.field.wrapper.WByteField;
import com.jfireframework.mvc.binder.field.wrapper.WCharacterField;
import com.jfireframework.mvc.binder.field.wrapper.WDoubleField;
import com.jfireframework.mvc.binder.field.wrapper.WFloatField;
import com.jfireframework.mvc.binder.field.wrapper.WIntegerField;
import com.jfireframework.mvc.binder.field.wrapper.WLongField;
import com.jfireframework.mvc.binder.field.wrapper.WShortField;
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
            binderConstructors.put(java.sql.Date.class, SqlDateField.class.getConstructor(Field.class));
            //
            binderConstructors.put(int[].class, ArrayWIntegerField.class.getConstructor(Field.class));
            binderConstructors.put(boolean[].class, ArrayWBooleanField.class.getConstructor(Field.class));
            binderConstructors.put(char[].class, ArrayWCharacterField.class.getConstructor(Field.class));
            binderConstructors.put(short[].class, ArrayWShortField.class.getConstructor(Field.class));
            binderConstructors.put(long[].class, ArrayWLongField.class.getConstructor(Field.class));
            binderConstructors.put(float[].class, ArrayWFloatField.class.getConstructor(Field.class));
            binderConstructors.put(double[].class, ArrayWDoubleField.class.getConstructor(Field.class));
            binderConstructors.put(byte[].class, ArrayWByteField.class.getConstructor(Field.class));
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
    
    @Override
    public String getName()
    {
        return name;
    }
    
    public final static BinderField build(Field field)
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
                if (Enum.class.isAssignableFrom(type.getComponentType()))
                {
                    return new ArrayEnumField(field);
                }
                else
                {
                    return new ArrayObjectField(field);
                }
            }
            else if (List.class.isAssignableFrom(type))
            {
                return ListField.valueOf(field);
            }
            else if (Set.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type))
            {
                return new NopField(field);
            }
            else if (Enum.class.isAssignableFrom(type))
            {
                return new EnumField(field);
            }
            else
            {
                return new ObjectField(field);
            }
        }
    }
}
