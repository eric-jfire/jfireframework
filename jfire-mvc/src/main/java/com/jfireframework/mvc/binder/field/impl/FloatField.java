package com.jfireframework.mvc.binder.field.impl;

import java.lang.reflect.Field;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;

public class FloatField extends AbstractBinderField
{
    
    public FloatField(String prefix, Field field)
    {
        super(prefix, field);
    }
    
    @SuppressWarnings("restriction")
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
            unsafe.putFloat(entity, offset, Float.valueOf(value));
        }
        return entity;
    }
    
}
