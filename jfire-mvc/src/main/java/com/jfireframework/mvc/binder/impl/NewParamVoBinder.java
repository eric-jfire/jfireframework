package com.jfireframework.mvc.binder.impl;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.AbstractDataBinder;
import com.jfireframework.mvc.binder.field.BinderField;

public class NewParamVoBinder extends AbstractDataBinder
{
    private BinderField[] binderFields;
    
    public NewParamVoBinder(String paramName, Class<?> entityClass)
    {
        super(paramName, entityClass);
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
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void setBinderFields(BinderField[] binderFields)
    {
        this.binderFields = binderFields;
    }
    
}
