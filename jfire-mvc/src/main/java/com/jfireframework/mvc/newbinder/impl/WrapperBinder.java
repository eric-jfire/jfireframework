package com.jfireframework.mvc.newbinder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.reflect.trans.Transfer;
import com.jfireframework.baseutil.reflect.trans.TransferFactory;
import com.jfireframework.mvc.newbinder.DataBinder;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class WrapperBinder implements DataBinder
{
    
    private final Transfer transfer;
    private final String   prefixName;
    
    public WrapperBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
        transfer = TransferFactory.get(ckass);
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        ParamNode node = treeValueNode.get(prefixName);
        if (node == null)
        {
            return null;
        }
        return transfer.trans(((StringValueNode) node).getValue());
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
