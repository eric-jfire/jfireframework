package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.DataBinderFactory;
import com.jfireframework.mvc.binder.ParamInfo;

public class ObjectBinderField extends AbstractBinderField
{
    private Class<?>   fieldType;
    private DataBinder dataBinder;
    
    public ObjectBinderField(String prefix, Field field, Set<Class<?>> set)
    {
        super(prefix, field, set);
        fieldType = field.getType();
        ParamInfo info = new ParamInfo();
        info.setPrefix(name);
        info.setEntityClass(fieldType);
        dataBinder = DataBinderFactory.build(info, set);
    }
    
    @SuppressWarnings("restriction")
    @Override
    public Object setValue(HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response) throws InstantiationException, IllegalAccessException
    {
        Object fieldEntity = dataBinder.binder(request, map, response);
        if (fieldEntity != null)
        {
            if (entity == null)
            {
                entity = type.newInstance();
            }
            unsafe.putObject(entity, offset, fieldEntity);
        }
        return entity;
    }
    
    @Override
    protected void set(Object entity, String value)
    {
        throw new UnSupportException("这里的代码不应该执行");
    }
    
}
