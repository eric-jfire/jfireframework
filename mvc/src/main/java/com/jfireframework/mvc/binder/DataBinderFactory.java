package com.jfireframework.mvc.binder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.mvc.annotation.MvcIgnore;
import com.jfireframework.mvc.annotation.MvcRename;
import com.jfireframework.mvc.annotation.RequestParam;
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
import com.jfireframework.mvc.binder.impl.BooleanBinder;
import com.jfireframework.mvc.binder.impl.CalendarBinder;
import com.jfireframework.mvc.binder.impl.DateBinder;
import com.jfireframework.mvc.binder.impl.DoubleBinder;
import com.jfireframework.mvc.binder.impl.FloatBinder;
import com.jfireframework.mvc.binder.impl.HttpRequestBinder;
import com.jfireframework.mvc.binder.impl.HttpResponseBinder;
import com.jfireframework.mvc.binder.impl.HttpSessionBinder;
import com.jfireframework.mvc.binder.impl.IntBinder;
import com.jfireframework.mvc.binder.impl.IntegerBinder;
import com.jfireframework.mvc.binder.impl.LongBinder;
import com.jfireframework.mvc.binder.impl.NewParamVoBinder;
import com.jfireframework.mvc.binder.impl.ServletContextBinder;
import com.jfireframework.mvc.binder.impl.SqlDateBinder;
import com.jfireframework.mvc.binder.impl.StringBinder;
import com.jfireframework.mvc.binder.impl.UploadBinder;
import com.jfireframework.mvc.binder.impl.WBooleanBinder;
import com.jfireframework.mvc.binder.impl.WDoubleBinder;
import com.jfireframework.mvc.binder.impl.WFloatBinder;
import com.jfireframework.mvc.binder.impl.WLongBinder;

public class DataBinderFactory
{
    
    public static DataBinder[] build(Method method)
    {
        Type[] paramTypes = method.getGenericParameterTypes();
        if (paramTypes.length == 0)
        {
            return new DataBinder[0];
        }
        String[] paramNames = getParamNames(method);
        Annotation[][] annotations = method.getParameterAnnotations();
        method.getParameterAnnotations();
        DataBinder[] dataBinders = new DataBinder[paramNames.length];
        for (int i = 0; i < paramTypes.length; i++)
        {
            ParamInfo info = new ParamInfo();
            if (annotations[i].length > 0)
            {
                info.setRequestParam((RequestParam) annotations[i][0]);
            }
            info.setEntityClass(paramTypes[i]);
            info.setPrefix(paramNames[i]);
            dataBinders[i] = build(info, new HashSet<Class<?>>());
        }
        return dataBinders;
    }
    
    /**
     * 使用条件信息创建一个databinder实例。条件信息包含前缀和该条件的类型
     * 
     * @param info
     * @param set
     * @return
     */
    public static DataBinder build(ParamInfo info, Set<Class<?>> set)
    {
        if (set.contains(info.getEntityClass()))
        {
            return null;
        }
        Type type = info.getEntityClass();
        String paramName = info.getPrefix();
        if (type instanceof ParameterizedType)
        {
            Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
            if (List.class.isAssignableFrom(rawType))
            {
                Class<?> paramType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
                if (paramType.equals(UploadItem.class))
                {
                    return new UploadBinder(paramName, false);
                }
                else
                {
                    throw new RuntimeException("未支持的入参形式，请联系作者eric@jfire.cn");
                }
            }
            else
            {
                throw new RuntimeException("未支持的入参形式，请联系作者eric@jfire.cn");
            }
        }
        if (type.equals(Double.class))
        {
            return new WDoubleBinder(paramName);
        }
        if (type.equals(double.class))
        {
            return new DoubleBinder(paramName);
        }
        if (type.equals(UploadItem.class))
        {
            return new UploadBinder(info.getPrefix(), true);
        }
        if (type.equals(String.class))
        {
            return new StringBinder(paramName);
        }
        if (type.equals(Long.class))
        {
            return new WLongBinder(paramName);
        }
        if (type.equals(long.class))
        {
            return new LongBinder(paramName);
        }
        if (type.equals(int.class))
        {
            return new IntBinder(paramName);
        }
        if (type.equals(Integer.class))
        {
            return new IntegerBinder(paramName);
        }
        if (type.equals(Float.class))
        {
            return new WFloatBinder(paramName);
        }
        if (type.equals(float.class))
        {
            return new FloatBinder(paramName);
        }
        if (type.equals(Boolean.class))
        {
            return new WBooleanBinder(paramName);
        }
        if (type.equals(boolean.class))
        {
            return new BooleanBinder(paramName);
        }
        if (HttpServletRequest.class.isAssignableFrom((Class<?>) type))
        {
            return new HttpRequestBinder(paramName);
        }
        if (HttpServletResponse.class.isAssignableFrom((Class<?>) type))
        {
            return new HttpResponseBinder(paramName);
        }
        if (HttpSession.class.isAssignableFrom((Class<?>) type))
        {
            return new HttpSessionBinder(paramName);
        }
        if (ServletContext.class.isAssignableFrom((Class<?>) type))
        {
            return new ServletContextBinder(paramName);
        }
        if (type.equals(java.util.Date.class))
        {
            return new DateBinder(info.getRequestParam(), paramName);
        }
        if (type.equals(Date.class))
        {
            return new SqlDateBinder(info.getRequestParam(), paramName);
        }
        if (type.equals(Calendar.class))
        {
            return new CalendarBinder(info.getRequestParam(), paramName);
        }
        else
        {
            return buildParamVoBinder(info, set);
        }
        
    }
    
    /**
     * 创建一个自定义对象的绑定器。
     * 
     * @param info
     * @param cycleSet 循环检测set
     * @return
     */
    private static NewParamVoBinder buildParamVoBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        String prefix = info.getPrefix();
        Class<?> entityClass = (Class<?>) info.getEntityClass();
        LightSet<BinderField> set = new LightSet<>();
        initFields(prefix, entityClass, set, cycleSet);
        NewParamVoBinder binder = new NewParamVoBinder(info.getPrefix(), entityClass);
        binder.setBinderFields(set.toArray(BinderField.class));
        return binder;
    }
    
    /**
     * 将类中的属性生成binderfield
     * 
     * @param prefix
     * @param entityClass
     * @param set
     * @param cycleSet
     */
    private static void initFields(String prefix, Class<?> entityClass, LightSet<BinderField> set, Set<Class<?>> cycleSet)
    {
        Field[] fields = ReflectUtil.getAllFields(entityClass);
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
                    set.add(new ArrayStringField(prefix, each));
                }
                else if (fieldType.equals(Integer.class))
                {
                    set.add(new ArrayIntegerField(prefix, each));
                }
                else if (fieldType.equals(Long.class))
                {
                    set.add(new ArrayWLongField(prefix, each));
                }
                else if (fieldType.equals(Float.class))
                {
                    set.add(new ArrayWFloatField(prefix, each));
                }
                else if (fieldType.equals(Double.class))
                {
                    set.add(new ArrayWDoubleField(prefix, each));
                }
                else if (fieldType.equals(int.class))
                {
                    set.add(new ArrayIntField(prefix, each));
                }
                else if (fieldType.equals(long.class))
                {
                    set.add(new ArrayLongField(prefix, each));
                }
                else if (fieldType.equals(float.class))
                {
                    set.add(new ArrayFloatField(prefix, each));
                }
                else if (fieldType.equals(double.class))
                {
                    set.add(new ArrayDoubleField(prefix, each));
                }
                else if (fieldType.equals(Boolean.class))
                {
                    set.add(new ArrayWBooleanField(prefix, each));
                }
                else if (fieldType.equals(boolean.class))
                {
                    set.add(new ArrayBooleanField(prefix, each));
                }
                else
                {
                    Verify.False(fieldType.isArray(), "数据绑定只支持到二维数组,请检查{}.{}", each.getDeclaringClass(), each.getName());
                    set.add(new ArrayObjectField(prefix, each, cycleSet));
                }
            }
            else
            {
                Class<?> fieldType = each.getType();
                if (fieldType.equals(String.class))
                {
                    set.add(new StringField(prefix, each));
                }
                else if (fieldType.equals(Integer.class))
                {
                    set.add(new IntegerField(prefix, each));
                }
                else if (fieldType.equals(Float.class))
                {
                    set.add(new WFloatField(prefix, each));
                }
                else if (fieldType.equals(Long.class))
                {
                    set.add(new WLongField(prefix, each));
                }
                else if (fieldType.equals(Double.class))
                {
                    set.add(new WDoubleField(prefix, each));
                }
                else if (fieldType.equals(Boolean.class))
                {
                    set.add(new WBooleanField(prefix, each));
                }
                else if (fieldType.equals(int.class))
                {
                    set.add(new IntField(prefix, each));
                }
                else if (fieldType.equals(long.class))
                {
                    set.add(new LongField(prefix, each));
                }
                else if (fieldType.equals(float.class))
                {
                    set.add(new FloatField(prefix, each));
                }
                else if (fieldType.equals(double.class))
                {
                    set.add(new DoubleField(prefix, each));
                }
                else if (fieldType.equals(boolean.class))
                {
                    set.add(new BooleanField(prefix, each));
                }
                else if (fieldType.equals(Date.class))
                {
                    set.add(new DateField(prefix, each));
                }
                else if (fieldType.equals(java.util.Date.class))
                {
                    set.add(new DateField(prefix, each));
                }
                else if (fieldType.equals(Calendar.class))
                {
                    set.add(new CalendarField(prefix, each));
                }
                else
                {
                    String fieldName = each.isAnnotationPresent(MvcRename.class) ? each.getAnnotation(MvcRename.class).value() : each.getName();
                    String nestedPrefix = StringUtil.isNotBlank(prefix) ? prefix + '.' + fieldName : fieldName;
                    set.add(new ObjectBinderField(nestedPrefix, each, cycleSet));
                }
            }
        }
        
    }
    
    /**
     * 获取方法的参数名称数组，如果没有注解则使用形参名称，如果有，则该参数采用注解的名称
     * 
     * @param method
     * @return
     */
    private static String[] getParamNames(Method method)
    {
        String[] paramNames = AopUtil.getParamNames(method);
        Annotation[][] annos = method.getParameterAnnotations();
        for (int i = 0; i < annos.length; i++)
        {
            if (annos[i].length == 0)
            {
                continue;
            }
            else
            {
                RequestParam param = (RequestParam) annos[i][0];
                paramNames[i] = param.value();
            }
        }
        return paramNames;
    }
    
}
