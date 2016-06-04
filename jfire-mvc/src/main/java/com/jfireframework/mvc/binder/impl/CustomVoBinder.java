package com.jfireframework.mvc.binder.impl;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.binder.AbstractDataBinder;
import com.jfireframework.mvc.binder.ParamInfo;
import com.jfireframework.mvc.binder.field.BinderField;

public class CustomVoBinder extends AbstractDataBinder
{
    private BinderField[] binderFields;
    
    public CustomVoBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        try
        {
            Object entity = null;
            for (BinderField each : binderFields)
            {
                entity = each.setValue(request, entity, map, response);
            }
            return entity;
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public void setBinderFields(BinderField[] binderFields)
    {
        this.binderFields = binderFields;
    }
    
}
