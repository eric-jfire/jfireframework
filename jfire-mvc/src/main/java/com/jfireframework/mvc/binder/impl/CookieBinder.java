package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.annotation.CookieValue;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.TreeValueNode;
import com.jfireframework.mvc.binder.transfer.Transfer;
import com.jfireframework.mvc.binder.transfer.TransferFactory;

public class CookieBinder implements DataBinder
{
    private final String cookieName;
    private final String defaultValue;
    private Transfer<?>  transfer;
    private final String prefixName;
    
    public CookieBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
        String cookieName = "";
        String defaultValue = "";
        for (Annotation each : annotations)
        {
            if (each instanceof CookieValue)
            {
                cookieName = ((CookieValue) each).value();
                defaultValue = ((CookieValue) each).defaultValue();
                break;
            }
        }
        if (cookieName.equals(""))
        {
            cookieName = prefixName;
        }
        if (defaultValue.equals(""))
        {
            defaultValue = null;
        }
        this.cookieName = cookieName;
        this.defaultValue = defaultValue;
        transfer = TransferFactory.build(ckass);
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        Cookie[] cookies = request.getCookies();
        Cookie target = null;
        for (Cookie each : cookies)
        {
            if (each.getName().equals(cookieName))
            {
                target = each;
                break;
            }
        }
        if (target != null)
        {
            return transfer.trans(target.getValue());
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
