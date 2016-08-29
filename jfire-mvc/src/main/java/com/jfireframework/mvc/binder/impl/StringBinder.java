package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;
import com.jfireframework.mvc.binder.node.TreeValueNode;

public class StringBinder implements DataBinder
{
    
    private final String prefixName;
    
    public StringBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        ParamNode node = treeValueNode.get(prefixName);
        if (node == null)
        {
            return null;
        }
        else
        {
            return ((StringValueNode) node).getValue();
        }
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
