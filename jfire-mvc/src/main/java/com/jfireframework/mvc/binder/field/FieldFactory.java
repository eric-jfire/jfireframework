package com.jfireframework.mvc.binder.field;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.annotation.MvcIgnore;
import com.jfireframework.mvc.binder.field.array.ArrayBooleanField;
import com.jfireframework.mvc.binder.field.array.ArrayDoubleField;
import com.jfireframework.mvc.binder.field.array.ArrayFloatField;
import com.jfireframework.mvc.binder.field.array.ArrayIntField;
import com.jfireframework.mvc.binder.field.array.ArrayIntegerField;
import com.jfireframework.mvc.binder.field.array.ArrayLongField;
import com.jfireframework.mvc.binder.field.array.ArrayObjectField;
import com.jfireframework.mvc.binder.field.array.ArrayStringField;
import com.jfireframework.mvc.binder.field.array.ArrayWBooleanField;
import com.jfireframework.mvc.binder.field.array.ArrayWDoubleField;
import com.jfireframework.mvc.binder.field.array.ArrayWFloatField;
import com.jfireframework.mvc.binder.field.array.ArrayWLongField;
import com.jfireframework.mvc.binder.field.impl.BooleanField;
import com.jfireframework.mvc.binder.field.impl.CalendarField;
import com.jfireframework.mvc.binder.field.impl.DateField;
import com.jfireframework.mvc.binder.field.impl.DoubleField;
import com.jfireframework.mvc.binder.field.impl.FloatField;
import com.jfireframework.mvc.binder.field.impl.IntField;
import com.jfireframework.mvc.binder.field.impl.IntegerField;
import com.jfireframework.mvc.binder.field.impl.LongField;
import com.jfireframework.mvc.binder.field.impl.ObjectBinderField;
import com.jfireframework.mvc.binder.field.impl.StringField;
import com.jfireframework.mvc.binder.field.impl.WBooleanField;
import com.jfireframework.mvc.binder.field.impl.WDoubleField;
import com.jfireframework.mvc.binder.field.impl.WFloatField;
import com.jfireframework.mvc.binder.field.impl.WLongField;

public class FieldFactory
{
    private static final ConcurrentHashMap<Class<?>, Constructor<?>> binderMap = new ConcurrentHashMap<>();
    static
    {
        try
        {
            binderMap.put(boolean.class, BooleanField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Calendar.class, CalendarField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(java.util.Date.class, DateField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Date.class, DateField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(double.class, DoubleField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(float.class, FloatField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Integer.class, IntegerField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(int.class, IntField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(long.class, LongField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(String.class, StringField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Boolean.class, WBooleanField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Double.class, WDoubleField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Float.class, WFloatField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Long.class, WLongField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(boolean[].class, ArrayBooleanField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(double[].class, ArrayDoubleField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(float[].class, ArrayFloatField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Integer[].class, ArrayIntegerField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(int[].class, ArrayIntField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(long[].class, ArrayLongField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(String[].class, ArrayStringField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Boolean[].class, ArrayWBooleanField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Double[].class, ArrayWDoubleField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Float[].class, ArrayWFloatField.class.getConstructor(String.class, Field.class, Set.class));
            binderMap.put(Long[].class, ArrayWLongField.class.getConstructor(String.class, Field.class, Set.class));
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public static BinderField[] build(String prefix, Class<?> entityClass, Set<Class<?>> cycleSet)
    {
        Field[] fields = ReflectUtil.getAllFields(entityClass);
        List<BinderField> list = new LinkedList<>();
        for (Field each : fields)
        {
            if (Modifier.isStatic(each.getModifiers()) || Modifier.isFinal(each.getModifiers()) || each.isAnnotationPresent(MvcIgnore.class) || List.class.isAssignableFrom(each.getType()) || Map.class.isAssignableFrom(each.getType()) || each.getType().equals(each.getDeclaringClass()))
            {
                continue;
            }
            Constructor<?> constructor = binderMap.get(each.getType());
            if (constructor != null)
            {
                try
                {
                    list.add((BinderField) constructor.newInstance(prefix, each, cycleSet));
                }
                catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    throw new JustThrowException(e);
                }
            }
            else
            {
                if (each.getType().isArray())
                {
                    Class<?> type = each.getType().getComponentType();
                    if (type.isArray())
                    {
                        throw new UnSupportException("支持一维数组");
                    }
                    list.add(new ArrayObjectField(prefix, each, cycleSet));
                }
                else
                {
                    list.add(new ObjectBinderField(prefix, each, cycleSet));
                }
            }
        }
        return list.toArray(new BinderField[list.size()]);
    }
}
