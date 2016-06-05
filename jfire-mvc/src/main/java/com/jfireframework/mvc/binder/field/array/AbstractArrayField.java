package com.jfireframework.mvc.binder.field.array;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.mvc.binder.field.impl.AbstractBinderField;

public abstract class AbstractArrayField extends AbstractBinderField
{
    protected String[]     requestParamNames;
    protected int          length;
    protected final String matchPrefix;
    protected final int    index;
    
    public AbstractArrayField(String prefix, Field field, Set<Class<?>> cycleSet)
    {
        super(prefix, field, cycleSet);
        matchPrefix = name + "[";
        index = matchPrefix.length();
    }
    
    @Override
    public Object setValue(HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response) throws InstantiationException, IllegalAccessException
    {
        Object _array = null;
        if (entity != null)
        {
            _array = unsafe.getObject(entity, offset);
        }
        for (Entry<String, String> entry : map.entrySet())
        {
            if (entry.getKey().startsWith(matchPrefix) && StringUtil.isNotBlank(entry.getValue()))
            {
                if (entity == null)
                {
                    entity = type.newInstance();
                    _array = unsafe.getObject(entity, offset);
                }
                String value = entry.getKey();
                int flag = Integer.parseInt(value.substring(index, value.indexOf("]", index)));
                setFlagValue(entry.getValue(), _array, flag, request, entity, map, response);
            }
        }
        return entity;
    }
    
    @Override
    protected void set(Object entity, String value)
    {
        throw new UnSupportException("代码不应该执行到这一句");
    }
    
    protected abstract void setFlagValue(String value, Object _array, int flag, HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response);
}
