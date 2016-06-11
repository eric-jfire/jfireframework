package com.jfireframework.sql.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.field.impl.BooleanField;
import com.jfireframework.sql.field.impl.CalendarField;
import com.jfireframework.sql.field.impl.DateField;
import com.jfireframework.sql.field.impl.DoubleField;
import com.jfireframework.sql.field.impl.FloatField;
import com.jfireframework.sql.field.impl.IntField;
import com.jfireframework.sql.field.impl.IntegerField;
import com.jfireframework.sql.field.impl.LongField;
import com.jfireframework.sql.field.impl.StringField;
import com.jfireframework.sql.field.impl.TimeField;
import com.jfireframework.sql.field.impl.TimestampField;
import com.jfireframework.sql.field.impl.WBooleanField;
import com.jfireframework.sql.field.impl.WDoubleField;
import com.jfireframework.sql.field.impl.WFloatField;
import com.jfireframework.sql.field.impl.WLongField;

public class MapFieldBuilder
{
    private static final Map<Class<?>, Constructor<? extends MapField>> fieldMap = new HashMap<Class<?>, Constructor<? extends MapField>>();
    static
    {
        try
        {
            fieldMap.put(boolean.class, BooleanField.class.getConstructor(Field.class));
            fieldMap.put(Calendar.class, CalendarField.class.getConstructor(Field.class));
            fieldMap.put(java.util.Date.class, DateField.class.getConstructor(Field.class));
            fieldMap.put(Date.class, DateField.class.getConstructor(Field.class));
            fieldMap.put(double.class, DoubleField.class.getConstructor(Field.class));
            fieldMap.put(float.class, FloatField.class.getConstructor(Field.class));
            fieldMap.put(long.class, LongField.class.getConstructor(Field.class));
            fieldMap.put(int.class, IntField.class.getConstructor(Field.class));
            fieldMap.put(String.class, StringField.class.getConstructor(Field.class));
            fieldMap.put(Time.class, TimeField.class.getConstructor(Field.class));
            fieldMap.put(Timestamp.class, TimestampField.class.getConstructor(Field.class));
            fieldMap.put(Boolean.class, WBooleanField.class.getConstructor(Field.class));
            fieldMap.put(Double.class, WDoubleField.class.getConstructor(Field.class));
            fieldMap.put(Float.class, WFloatField.class.getConstructor(Field.class));
            fieldMap.put(Integer.class, IntegerField.class.getConstructor(Field.class));
            fieldMap.put(Long.class, WLongField.class.getConstructor(Field.class));
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
    public static MapField buildMapField(Field field)
    {
        Class<?> fieldType = field.getType();
        Constructor<?> constructor = fieldMap.get(fieldType);
        if (constructor != null)
        {
            try
            {
                return (MapField) constructor.newInstance(field);
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
        }
        else
        {
            Verify.error("属性{}.{}的类型尚未支持", field.getDeclaringClass(), field.getName());
            return null;
        }
    }
    
}
