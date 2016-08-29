package com.jfireframework.mvc.newbinder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.trans.Transfer;
import com.jfireframework.baseutil.reflect.trans.TransferFactory;
import com.jfireframework.mvc.newbinder.DataBinder;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class BaseBinder implements DataBinder
{
    
    private final Transfer transfer;
    private final String   prefixName;
    
    public BaseBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
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
            throw new UnSupportException(StringUtil.format("参数为基本类型，页面必须要有传参，请检查传参名字是否是{}", prefixName));
        }
        return transfer.trans(((StringValueNode) node).getValue());
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
