package com.jfireframework.mvc.binder.field.array;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.DataBinderFactory;
import com.jfireframework.mvc.binder.ParamInfo;

public class ArrayObjectField extends AbstractArrayField
{
    private DataBinder[] dataBinders;
    
    public ArrayObjectField(String prefix, Field field, Set<Class<?>> set)
    {
        super(prefix, field);
        Class<?> fieldType = field.getType().getComponentType();
        dataBinders = new DataBinder[length];
        for (int i = 0; i < length; i++)
        {
            ParamInfo info = new ParamInfo();
            info.setPrefix(requestParamNames[i]);
            info.setEntityClass(fieldType);
            dataBinders[i] = DataBinderFactory.build(info, set);
        }
    }
    
    @SuppressWarnings("restriction")
    @Override
    public Object setValue(HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response) throws InstantiationException, IllegalAccessException
    {
        Object[] array = null;
        if (entity != null)
        {
            array = (Object[]) unsafe.getObject(entity, offset);
        }
        Set<String> paramNames = map.keySet();
        for (int i = 0; i < length; i++)
        {
            for (String each : paramNames)
            {
                if (each.startsWith(requestParamNames[i]))
                {
                    if (entity == null)
                    {
                        entity = type.newInstance();
                        array = (Object[]) unsafe.getObject(entity, offset);
                    }
                    array[i] = dataBinders[i].binder(request, map, null);
                }
            }
        }
        return entity;
    }
    
}
