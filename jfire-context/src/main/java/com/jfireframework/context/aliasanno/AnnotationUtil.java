package com.jfireframework.context.aliasanno;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;

public class AnnotationUtil
{
    private static final Map<Annotation, AnnoContext> aliasMap = new ConcurrentHashMap<Annotation, AnnoContext>(256);
    
    public static boolean isPresent(Class<? extends Annotation> annoType, Field field)
    {
        if (field.isAnnotationPresent(annoType))
        {
            return true;
        }
        return getAnnotation(annoType, field) != null;
    }
    
    public static boolean isPresent(Class<? extends Annotation> annoType, Class<?> target)
    {
        if (target.isAnnotationPresent(annoType) && target.isAnnotation() == false)
        {
            return true;
        }
        return getAnnotation(annoType, target) != null;
    }
    
    public static boolean isPresent(Class<? extends Annotation> annoType, Method method)
    {
        if (method.isAnnotationPresent(annoType))
        {
            return true;
        }
        return getAnnotation(annoType, method) != null;
    }
    
    public static <T extends Annotation> T getAnnotation(Class<T> annoType, Method method)
    {
        T anno = null;
        anno = method.getAnnotation(annoType);
        if (anno != null)
        {
            return anno;
        }
        return getAnnotation(annoType, method.getAnnotations());
    }
    
    public static <T extends Annotation> T getAnnotation(Class<T> annoType, Field field)
    {
        T anno = null;
        anno = field.getAnnotation(annoType);
        if (anno != null)
        {
            return anno;
        }
        return getAnnotation(annoType, field.getAnnotations());
    }
    
    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getAnnotation(Class<T> annoType, Annotation[] annotations)
    {
        for (Annotation each : annotations)
        {
            AnnoContext annoContext = aliasMap.get(each);
            if (annoContext == null)
            {
                annoContext = new AnnoContext(each);
                aliasMap.put(each, annoContext);
            }
            if (annoContext.isPresent(annoType))
            {
                return annoContext.getAnno(annoType);
            }
        }
        return null;
    }
    
    public static <T extends Annotation> T getAnnotation(Class<T> annotationType, Class<?> target)
    {
        if (target.isAnnotation())
        {
            return null;
        }
        T anno;
        anno = target.getAnnotation(annotationType);
        if (anno != null)
        {
            return anno;
        }
        return getAnnotation(annotationType, target.getAnnotations());
    }
    
    static class AnnoContext
    {
        private final Map<String, Object>              valueMap = new HashMap<String, Object>();
        private final Set<Class<? extends Annotation>> types    = new HashSet<Class<? extends Annotation>>();
        private final ClassLoader                      classLoader;
        
        public AnnoContext(Annotation annotation)
        {
            classLoader = annotation.annotationType().getClassLoader();
            fillAnnoValues(annotation);
        }
        
        private void fillAnnoValues(Annotation annotation)
        {
            types.add(annotation.annotationType());
            for (Method each : annotation.annotationType().getMethods())
            {
                if (each.getParameterTypes().length != 0)
                {
                    continue;
                }
                String name = null;
                Object value = null;
                if (each.isAnnotationPresent(AliasFor.class))
                {
                    AliasFor aliasFor = each.getAnnotation(AliasFor.class);
                    try
                    {
                        name = aliasFor.annotation().getName() + "." + aliasFor.annotation().getMethod(aliasFor.value()).getName();
                    }
                    catch (Exception e)
                    {
                        throw new UnSupportException(StringUtil.format("别名注解的属性不存在，请检查{}.{}中的别名是否拼写错误", each.getDeclaringClass().getName(), each.getName()), e);
                    }
                    try
                    {
                        value = each.invoke(annotation);
                    }
                    catch (Exception e)
                    {
                        throw new JustThrowException(e);
                    }
                }
                else
                {
                    name = each.getDeclaringClass().getName() + '.' + each.getName();
                    try
                    {
                        value = each.invoke(annotation);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw new JustThrowException(e);
                    }
                }
                String originName = each.getDeclaringClass().getName() + '.' + each.getName();
                if (valueMap.containsKey(originName))
                {
                    if (valueMap.containsKey(name) == false)
                    {
                        valueMap.put(name, valueMap.get(originName));
                    }
                }
                else
                {
                    if (valueMap.containsKey(name) == false)
                    {
                        valueMap.put(name, value);
                    }
                }
                
            }
            for (Annotation anno : annotation.annotationType().getDeclaredAnnotations())
            {
                if (anno instanceof Documented || anno instanceof Target || anno instanceof Retention || anno instanceof Inherited)
                {
                    continue;
                }
                fillAnnoValues(anno);
            }
        }
        
        public boolean isPresent(Class<? extends Annotation> type)
        {
            return types.contains(type);
        }
        
        @SuppressWarnings("unchecked")
        public <T extends Annotation> T getAnno(Class<T> type)
        {
            return (T) Proxy.newProxyInstance(classLoader, new Class<?>[] { type }, new aliasInvocationHandler());
        }
        
        class aliasInvocationHandler implements InvocationHandler
        {
            
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
                String name = method.getDeclaringClass().getName() + '.' + method.getName();
                return valueMap.get(name);
            }
        }
    }
    
}
