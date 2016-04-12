package com.jfireframework.mvc.binder.field.array;

import java.lang.reflect.Field;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;

@SuppressWarnings("restriction")
public class ArrayWBooleanField extends AbstractArrayField
{
    public ArrayWBooleanField(String prefix, Field field)
    {
        super(prefix, field);
    }
    
    @Override
    public Object setValue(HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response) throws InstantiationException, IllegalAccessException
    {
        Boolean[] array = null;
        if (entity != null)
        {
            array = (Boolean[]) unsafe.getObject(entity, offset);
        }
        String value = null;
        for (int i = 0; i < length; i++)
        {
            value = map.get(requestParamNames[i]);
            if (StringUtil.isNotBlank(value))
            {
                if (entity == null)
                {
                    entity = type.newInstance();
                    array = (Boolean[]) unsafe.getObject(entity, offset);
                }
                array[i] = Boolean.valueOf(value);
            }
        }
        return entity;
    }
    
}
