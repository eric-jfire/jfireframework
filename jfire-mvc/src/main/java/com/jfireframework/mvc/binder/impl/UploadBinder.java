package com.jfireframework.mvc.binder.impl;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.AbstractDataBinder;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;

public class UploadBinder extends AbstractDataBinder
{
    private boolean singleItem = false;
    
    public UploadBinder(String paramName, boolean singleItem)
    {
        super(paramName);
        this.singleItem = singleItem;
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
