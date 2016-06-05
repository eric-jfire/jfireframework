package com.jfireframework.mvc.binder.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.mvc.annotation.MvcIgnore;
import com.jfireframework.mvc.annotation.MvcRename;
import com.jfireframework.mvc.binder.ParamInfo;
import com.jfireframework.mvc.binder.field.BinderField;
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

public class CustomVoBinder extends AbstractDataBinder
{
    private BinderField[] binderFields;
    
    public CustomVoBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
        
        String prefix = info.getPrefix();
        Field[] fields = ReflectUtil.getAllFields((Class<?>) info.getEntityClass());
        List<BinderField> list = new LinkedList<>();
        for (Field each : fields)
        {
            if (Modifier.isStatic(each.getModifiers()) || Modifier.isFinal(each.getModifiers()) || each.isAnnotationPresent(MvcIgnore.class) || List.class.isAssignableFrom(each.getType()) || Map.class.isAssignableFrom(each.getType()) || each.getType().equals(each.getDeclaringClass()))
            {
                continue;
            }
            if (each.getType().isArray())
            {
                Class<?> fieldType = each.getType().getComponentType();
                if (fieldType.equals(String.class))
                {
                    list.add(new ArrayStringField(prefix, each));
                }
                else if (fieldType.equals(Integer.class))
                {
                    list.add(new ArrayIntegerField(prefix, each));
                }
                else if (fieldType.equals(Long.class))
                {
                    list.add(new ArrayWLongField(prefix, each));
                }
                else if (fieldType.equals(Float.class))
                {
                    list.add(new ArrayWFloatField(prefix, each));
                }
                else if (fieldType.equals(Double.class))
                {
                    list.add(new ArrayWDoubleField(prefix, each));
                }
                else if (fieldType.equals(int.class))
                {
                    list.add(new ArrayIntField(prefix, each));
                }
                else if (fieldType.equals(long.class))
                {
                    list.add(new ArrayLongField(prefix, each));
                }
                else if (fieldType.equals(float.class))
                {
                    list.add(new ArrayFloatField(prefix, each));
                }
                else if (fieldType.equals(double.class))
                {
                    list.add(new ArrayDoubleField(prefix, each));
                }
                else if (fieldType.equals(Boolean.class))
                {
                    list.add(new ArrayWBooleanField(prefix, each));
                }
                else if (fieldType.equals(boolean.class))
                {
                    list.add(new ArrayBooleanField(prefix, each));
                }
                else
                {
                    Verify.False(fieldType.isArray(), "数据绑定只支持到二维数组,请检查{}.{}", each.getDeclaringClass(), each.getName());
                    list.add(new ArrayObjectField(prefix, each, cycleSet));
                }
            }
            else
            {
                Class<?> fieldType = each.getType();
                if (fieldType.equals(String.class))
                {
                    list.add(new StringField(prefix, each));
                }
                else if (fieldType.equals(Integer.class))
                {
                    list.add(new IntegerField(prefix, each));
                }
                else if (fieldType.equals(Float.class))
                {
                    list.add(new WFloatField(prefix, each));
                }
                else if (fieldType.equals(Long.class))
                {
                    list.add(new WLongField(prefix, each));
                }
                else if (fieldType.equals(Double.class))
                {
                    list.add(new WDoubleField(prefix, each));
                }
                else if (fieldType.equals(Boolean.class))
                {
                    list.add(new WBooleanField(prefix, each));
                }
                else if (fieldType.equals(int.class))
                {
                    list.add(new IntField(prefix, each));
                }
                else if (fieldType.equals(long.class))
                {
                    list.add(new LongField(prefix, each));
                }
                else if (fieldType.equals(float.class))
                {
                    list.add(new FloatField(prefix, each));
                }
                else if (fieldType.equals(double.class))
                {
                    list.add(new DoubleField(prefix, each));
                }
                else if (fieldType.equals(boolean.class))
                {
                    list.add(new BooleanField(prefix, each));
                }
                else if (fieldType.equals(Date.class))
                {
                    list.add(new DateField(prefix, each));
                }
                else if (fieldType.equals(java.util.Date.class))
                {
                    list.add(new DateField(prefix, each));
                }
                else if (fieldType.equals(Calendar.class))
                {
                    list.add(new CalendarField(prefix, each));
                }
                else
                {
                    String fieldName = each.isAnnotationPresent(MvcRename.class) ? each.getAnnotation(MvcRename.class).value() : each.getName();
                    String nestedPrefix = StringUtil.isNotBlank(prefix) ? prefix + '.' + fieldName : fieldName;
                    list.add(new ObjectBinderField(nestedPrefix, each, cycleSet));
                }
            }
        }
        binderFields = list.toArray(new BinderField[list.size()]);
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        try
        {
            Object entity = null;
            for (BinderField each : binderFields)
            {
                entity = each.setValue(request, entity, map, response);
            }
            return entity;
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public void setBinderFields(BinderField[] binderFields)
    {
        this.binderFields = binderFields;
    }
    
}
