package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.trans.Transfer;
import com.jfireframework.baseutil.reflect.trans.TransferFactory;
import com.jfireframework.mvc.annotation.HeaderValue;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.TreeValueNode;

public class HeaderBinder implements DataBinder
{
    
    private final String   headerName;
    private final String   defaultValue;
    private final Transfer transfer;
    private final String   prefixName;
    
    public HeaderBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
        String headerName = "";
        String defaultValue = "";
        for (Annotation each : annotations)
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
            headerName = prefixName;
        }
        if (defaultValue.equals(""))
        {
            defaultValue = null;
        }
        this.headerName = headerName;
        this.defaultValue = defaultValue;
        transfer = TransferFactory.get(ckass);
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
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
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
