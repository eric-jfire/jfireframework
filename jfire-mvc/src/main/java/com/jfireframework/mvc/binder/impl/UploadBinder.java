package com.jfireframework.mvc.binder.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.AbstractDataBinder;
import com.jfireframework.mvc.binder.ParamInfo;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;

public class UploadBinder extends AbstractDataBinder
{
    private final boolean singleItem;
    
    public UploadBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
        Type type = info.getEntityClass();
        if (type instanceof ParameterizedType)
        {
            singleItem = false;
        }
        else
        {
            singleItem = true;
        }
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        if (singleItem)
        {
            Object value = request.getAttribute(UploadInterceptor.uploadFileList);
            if (value == null)
            {
                return null;
            }
            else
            {
                if (((List<?>) value).size() == 0)
                {
                    return null;
                }
                else
                {
                    return ((List<?>) value).get(0);
                }
            }
        }
        else
        {
            return request.getAttribute(UploadInterceptor.uploadFileList);
        }
    }
    
}
