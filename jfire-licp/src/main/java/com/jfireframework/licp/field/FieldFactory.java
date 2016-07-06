package com.jfireframework.licp.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.licp.field.impl.BooleanField;
import com.jfireframework.licp.field.impl.ByteField;
import com.jfireframework.licp.field.impl.CharField;
import com.jfireframework.licp.field.impl.DoubleField;
import com.jfireframework.licp.field.impl.FloatField;
import com.jfireframework.licp.field.impl.IntField;
import com.jfireframework.licp.field.impl.IntegerField;
import com.jfireframework.licp.field.impl.LongField;
import com.jfireframework.licp.field.impl.ObjectField;
import com.jfireframework.licp.field.impl.ShortField;
import com.jfireframework.licp.field.impl.StringField;
import com.jfireframework.licp.field.impl.WBooleanField;
import com.jfireframework.licp.field.impl.WByteField;
import com.jfireframework.licp.field.impl.WCharField;
import com.jfireframework.licp.field.impl.WDoubleField;
import com.jfireframework.licp.field.impl.WFloatField;
import com.jfireframework.licp.field.impl.WShortField;
import com.jfireframework.licp.field.impl.WlongField;

public class FieldFactory
{
    private static final Map<Class<?>, Constructor<? extends CacheField>> map = new HashMap<Class<?>, Constructor<? extends CacheField>>();
    static
    {
        try
        {
            map.put(int.class, IntField.class.getConstructor(Field.class));
            map.put(short.class, ShortField.class.getConstructor(Field.class));
            map.put(byte.class, ByteField.class.getConstructor(Field.class));
            map.put(long.class, LongField.class.getConstructor(Field.class));
            map.put(float.class, FloatField.class.getConstructor(Field.class));
            map.put(double.class, DoubleField.class.getConstructor(Field.class));
            map.put(boolean.class, BooleanField.class.getConstructor(Field.class));
            map.put(char.class, CharField.class.getConstructor(Field.class));
            map.put(Integer.class, IntegerField.class.getConstructor(Field.class));
            map.put(Byte.class, WByteField.class.getConstructor(Field.class));
            map.put(Character.class, WCharField.class.getConstructor(Field.class));
            map.put(Boolean.class, WBooleanField.class.getConstructor(Field.class));
            map.put(Long.class, WlongField.class.getConstructor(Field.class));
            map.put(Float.class, WFloatField.class.getConstructor(Field.class));
            map.put(Short.class, WShortField.class.getConstructor(Field.class));
            map.put(Double.class, WDoubleField.class.getConstructor(Field.class));
            map.put(String.class, StringField.class.getConstructor(Field.class));
            
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public static final CacheField build(Field field)
    {
        Class<?> type = field.getType();
        Constructor<? extends CacheField> constructor = map.get(type);
        if (constructor != null)
        {
            try
            {
                return constructor.newInstance(field);
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
        }
        return new ObjectField(field);
    }
}
