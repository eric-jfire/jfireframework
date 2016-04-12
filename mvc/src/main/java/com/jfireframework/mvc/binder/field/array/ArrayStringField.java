package com.jfireframework.mvc.binder.field.array;

import java.lang.reflect.Field;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("restriction")
public class ArrayStringField extends AbstractArrayField
{
    public ArrayStringField(String prefix, Field field)
    {
        super(prefix, field);
    }
    
    @Override
    public Object setValue(HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response) throws InstantiationException, IllegalAccessException
    {
        String[] array = null;
        if (entity != null)
        {
            array = (String[]) unsafe.getObject(entity, offset);
        }
        String value;
        for (int i = 0; i < length; i++)
        {
            value = map.get(requestParamNames[i]);
            if (value != null)
            {
                if (entity == null)
                {
                    entity = type.newInstance();
                    array = (String[]) unsafe.getObject(entity, offset);
                }
                array[i] = value;
            }
        }
        return entity;
    }
    
}
