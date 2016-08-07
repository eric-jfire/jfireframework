package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.annotation.MvcRename;
import com.jfireframework.mvc.binder.field.BinderField;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractBinderField implements BinderField
{
    protected String        name;
    protected long          offset;
    protected static Unsafe unsafe = ReflectUtil.getUnsafe();
    protected Class<?>      type;
    
    public AbstractBinderField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        type = field.getDeclaringClass();
        String fieldName = field.isAnnotationPresent(MvcRename.class) ? field.getAnnotation(MvcRename.class).value() : field.getName();
        name = StringUtil.isNotBlank(prefix) ? prefix + '[' + fieldName + ']' : fieldName;
        offset = unsafe.objectFieldOffset(field);
        
    }
    
    @Override
    public Object setValue(HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response) throws InstantiationException, IllegalAccessException
    {
        String value = map.get(name);
        if (StringUtil.isNotBlank(value))
        {
            if (entity == null)
            {
                entity = type.newInstance();
            }
            set(entity, value);
        }
        return entity;
    }
    
    protected abstract void set(Object entity, String value);
}
