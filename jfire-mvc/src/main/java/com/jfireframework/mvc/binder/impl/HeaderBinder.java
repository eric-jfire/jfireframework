package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.trans.Transfer;
import com.jfireframework.baseutil.reflect.trans.TransferFactory;
import com.jfireframework.mvc.annotation.HeaderValue;
import com.jfireframework.mvc.binder.ParamInfo;

public class HeaderBinder extends AbstractDataBinder
{
    private final String   headerName;
    private final String   defaultValue;
    private final Transfer transfer;
    
    public HeaderBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
        String headerName = "";
        String defaultValue = "";
        for (Annotation each : info.getAnnotations())
        {
            if (each instanceof HeaderValue)
            {
                headerName = ((HeaderValue) each).value();
                defaultValue = ((HeaderValue) each).defaultValue();
                break;
            }
        }
        if (headerName.equals(""))
        {
            headerName = paramName;
        }
        if (defaultValue.equals(""))
        {
            defaultValue = null;
        }
        this.headerName = headerName;
        this.defaultValue = defaultValue;
        transfer = TransferFactory.get((Class<?>) info.getEntityClass());
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        String value = request.getHeader(headerName);
        if (StringUtil.isNotBlank(value))
        {
            return transfer.trans(value);
        }
        else if (StringUtil.isNotBlank(defaultValue))
        {
            return transfer.trans(defaultValue);
        }
        else
        {
            return null;
        }
    }
    
}
