package com.jfireframework.mvc.binder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.annotation.CookieValue;
import com.jfireframework.mvc.annotation.HeaderValue;
import com.jfireframework.mvc.binder.impl.BaseBinder;
import com.jfireframework.mvc.binder.impl.CalendarBinder;
import com.jfireframework.mvc.binder.impl.CookieBinder;
import com.jfireframework.mvc.binder.impl.CustomVoBinder;
import com.jfireframework.mvc.binder.impl.DateBinder;
import com.jfireframework.mvc.binder.impl.HeaderBinder;
import com.jfireframework.mvc.binder.impl.HttpRequestBinder;
import com.jfireframework.mvc.binder.impl.HttpResponseBinder;
import com.jfireframework.mvc.binder.impl.HttpSessionBinder;
import com.jfireframework.mvc.binder.impl.ParamMapBinder;
import com.jfireframework.mvc.binder.impl.ServletContextBinder;
import com.jfireframework.mvc.binder.impl.SqlDateBinder;
import com.jfireframework.mvc.binder.impl.UploadBinder;

public class DataBinderFactory
{
    private static ConcurrentHashMap<Class<?>, Constructor<?>> constructorMap = new ConcurrentHashMap<>();
    
    static
    {
        try
        {
            constructorMap.put(boolean.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(Calendar.class, CalendarBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(java.util.Date.class, DateBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(Date.class, SqlDateBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(double.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(float.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(HttpServletRequest.class, HttpRequestBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(HttpServletResponse.class, HttpResponseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(HttpSession.class, HttpSessionBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(int.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(Integer.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(long.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(Map.class, ParamMapBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(ServletContext.class, ServletContextBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(String.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(UploadItem.class, UploadBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(Boolean.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(Double.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(Float.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
            constructorMap.put(Long.class, BaseBinder.class.getConstructor(ParamInfo.class, Set.class));
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    /**
     * 使用条件信息创建一个databinder实例。条件信息包含前缀和该条件的类型
     * 
     * @param info
     * @param cycleSet
     * @return
     */
    public static DataBinder build(ParamInfo info, Set<Class<?>> cycleSet)
    {
        if (cycleSet.contains(info.getEntityClass()))
        {
            return null;
        }
        Type type = info.getEntityClass();
        if (type instanceof ParameterizedType)
        {
            Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
            if (rawType == List.class)
            {
                Class<?> paramType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
                if (paramType.equals(UploadItem.class))
                {
                    return new UploadBinder(info, cycleSet);
                }
                else
                {
                    throw new UnSupportException("未支持的入参形式，请联系作者eric@jfire.cn");
                }
            }
            else
            {
                throw new UnSupportException("未支持的入参形式，请联系作者eric@jfire.cn");
            }
        }
        for (Annotation each : info.getAnnotations())
        {
            if (each instanceof HeaderValue)
            {
                return new HeaderBinder(info, cycleSet);
            }
            else if (each instanceof CookieValue)
            {
                return new CookieBinder(info, cycleSet);
            }
        }
        Constructor<?> constructor = constructorMap.get(type);
        if (constructor != null)
        {
            try
            {
                return (DataBinder) constructor.newInstance(info, cycleSet);
            }
            catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                throw new JustThrowException(e);
            }
        }
        else
        {
            return new CustomVoBinder(info, cycleSet);
        }
        
    }
    
}
