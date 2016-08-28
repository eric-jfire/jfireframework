package com.jfireframework.mvc.newbinder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class HttpServletRequestBinder implements DataBinder
{
    private final String prefixName;
    
    public HttpServletRequestBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        return request;
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
