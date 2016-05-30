package com.jfireframework.context.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;

public class AnnotationUtil
{
    private static Map<Class<?>, AliasAnno> typeMap = new HashMap<Class<?>, AnnotationUtil.AliasAnno>();
    
    public static boolean isAnnotationPresent(Class<? extends Annotation> annotationType, Class<?> target)
    {
        Annotation result = getAnnotation(annotationType, target);
        return result != null;
    }
    
    private static AliasAnno getAliasAnno(Class<? extends Annotation> annotationType)
    {
        AliasAnno result = typeMap.get(annotationType);
        if (result != null)
        {
            return result;
        }
        boolean hasAlias = false;
        for (Method each : annotationType.getMethods())
        {
            if (each.isAnnotationPresent(AliasFor.class))
            {
                hasAlias = true;
                break;
            }
        }
        if (hasAlias == false)
        {
            return null;
        }
        result = new AliasAnno(annotationType);
        typeMap.put(annotationType, result);
        return result;
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
            AliasAnno aliasAnno = getAliasAnno(each.annotationType());
            if (aliasAnno != null && aliasAnno.originType() == annotationType)
            {
                return (T) aliasAnno.build(each);
            }
        }
        return null;
    }
    
    static class AliasAnno
    {
        private final Annotation                  origin;
        private final Class<? extends Annotation> originType;
        
        public AliasAnno(Class<? extends Annotation> annoType)
        {
            Annotation origin = null;
            for (Method each : annoType.getMethods())
            {
                if (each.isAnnotationPresent(AliasFor.class))
                {
                    AliasFor aliasFor = each.getAnnotation(AliasFor.class);
                    if (annoType.isAnnotationPresent(aliasFor.annotation()))
                    {
                        origin = annoType.getAnnotation(aliasFor.annotation());
                    }
                }
            }
            this.origin = origin;
            originType = origin.annotationType();
        }
        
        public Annotation getOrigin()
        {
            return origin;
        }
        
        public Class<? extends Annotation> originType()
        {
            return originType;
        }
        
        public Annotation build(Annotation self)
        {
            return (Annotation) Proxy.newProxyInstance(self.annotationType().getClassLoader(), new Class<?>[] { origin.annotationType() }, new aliasInvocationHandler(self, origin));
        }
        
        class aliasInvocationHandler implements InvocationHandler
        {
            private final Map<String, Object> map = new HashMap<String, Object>();
            
            public aliasInvocationHandler(Annotation annotation, Annotation origin)
            {
                try
                {
                    for (Method each : origin.annotationType().getMethods())
                    {
                        if (each.getParameterCount() == 0)
                        {
                            map.put(each.getName(), each.invoke(origin));
                        }
                    }
                    Map<String, Object> tmp = new HashMap<String, Object>();
                    for (Method each : annotation.annotationType().getMethods())
                    {
                        if (each.isAnnotationPresent(AliasFor.class) && each.getParameterCount() == 0)
                        {
                            AliasFor aliasFor = each.getAnnotation(AliasFor.class);
                            String name = aliasFor.value();
                            Object value = each.invoke(annotation);
                            if (tmp.put(name, value) != null)
                            {
                                throw new UnSupportException(StringUtil.format(StringUtil.format("同一个注解内，别名不能重复指向，请检查{}", annotation)));
                            }
                        }
                    }
                    map.putAll(tmp);
                }
                catch (Exception e)
                {
                    throw new JustThrowException(e);
                }
                
            }
            
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
            {
                System.out.println(method.getName());
                return map.get(method.getName());
            }
            
        }
    }
}
