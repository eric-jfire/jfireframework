package com.jfireframework.context.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;

public class AnnotationUtil
{
    private static final Map<Annotation, AliasAnno> aliasMap = new HashMap<Annotation, AnnotationUtil.AliasAnno>();
    
    public static boolean isAnnotationPresent(Class<? extends Annotation> annotationType, Class<?> target)
    {
        Annotation result = getAnnotation(annotationType, target);
        return result != null;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(Class<T> annotationType, Class<?> target)
    {
        T anno;
        anno = target.getAnnotation(annotationType);
        if (anno != null)
        {
            return anno;
        }
        for (Annotation each : target.getAnnotations())
        {
            AliasAnno aliasAnno = aliasMap.get(each);
            if (aliasAnno == null)
            {
                aliasAnno = new AliasAnno(each, new HashMap<String, Object>());
                aliasMap.put(each, aliasAnno);
            }
            if (aliasAnno.rootType() == annotationType)
            {
                return (T) aliasAnno.target();
            }
        }
        return null;
    }
    
    static class AliasAnno
    {
        private final Annotation                  target;
        private final Class<? extends Annotation> rootType;
        
        public AliasAnno(Annotation anno, final Map<String, Object> valueMap)
        {
            Annotation superAnno = null;
            for (Method each : anno.annotationType().getMethods())
            {
                if (each.isAnnotationPresent(AliasFor.class))
                {
                    AliasFor aliasFor = each.getAnnotation(AliasFor.class);
                    String name;
                    try
                    {
                        name = aliasFor.annotation().getName() + "." + aliasFor.annotation().getMethod(aliasFor.value()).getName();
                    }
                    catch (Exception e)
                    {
                        throw new UnSupportException(StringUtil.format("别名注解的属性不存在，请检查{}.{}中的别名是否拼写错误", each.getDeclaringClass().getName(), each.getName()), e);
                    }
                    Object value;
                    try
                    {
                        value = each.invoke(anno);
                    }
                    catch (Exception e)
                    {
                        throw new JustThrowException(e);
                    }
                    
                    String originName = each.getDeclaringClass().getName() + "." + each.getName();
                    if (valueMap.containsKey(originName))
                    {
                        valueMap.put(name, valueMap.get(originName));
                    }
                    else
                    {
                        valueMap.put(name, value);
                    }
                    if (anno.annotationType().isAnnotationPresent(aliasFor.annotation()))
                    {
                        superAnno = anno.annotationType().getAnnotation(aliasFor.annotation());
                    }
                }
                else if (each.getParameterCount() == 0)
                {
                    String name = each.getDeclaringClass().getName() + "." + each.getName();
                    try
                    {
                        valueMap.putIfAbsent(name, each.invoke(anno));
                    }
                    catch (Exception e)
                    {
                        throw new JustThrowException(e);
                    }
                }
            }
            if (superAnno != null)
            {
                new AliasAnno(superAnno, valueMap);
            }
            rootType = getRoot(anno.annotationType());
            target = (Annotation) Proxy.newProxyInstance(anno.annotationType().getClassLoader(), new Class<?>[] { getRoot(anno.annotationType()) }, new aliasInvocationHandler(valueMap));
            
        }
        
        public Annotation target()
        {
            return target;
        }
        
        public Class<? extends Annotation> rootType()
        {
            return rootType;
        }
        
        private Class<? extends Annotation> getRoot(Class<? extends Annotation> type)
        {
            Class<? extends Annotation> superAnno = null;
            for (Method each : type.getMethods())
            {
                if (each.isAnnotationPresent(AliasFor.class))
                {
                    AliasFor aliasFor = each.getAnnotation(AliasFor.class);
                    if (type.isAnnotationPresent(aliasFor.annotation()))
                    {
                        superAnno = aliasFor.annotation();
                        break;
                    }
                }
            }
            if (superAnno != null)
            {
                return getRoot(superAnno);
            }
            else
            {
                return type;
            }
        }
        
        class aliasInvocationHandler implements InvocationHandler
        {
            private final Map<String, Object> map = new HashMap<String, Object>();
            
            public aliasInvocationHandler(Map<String, Object> valueMap)
            {
                for (Entry<String, Object> entry : valueMap.entrySet())
                {
                    String name = entry.getKey();
                    name = name.substring(name.lastIndexOf('.') + 1);
                    map.put(name, entry.getValue());
                }
            }
            
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
                return map.get(method.getName());
            }
        }
    }
}
