package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;
import com.jfireframework.mvc.binder.node.TreeValueNode;

public class EnumBinder implements DataBinder
{
    
    private final String         prefixName;
    private Map<String, Enum<?>> instances;
    
    @SuppressWarnings("unchecked")
    public EnumBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
        instances = (Map<String, Enum<?>>) ReflectUtil.getAllEnumInstances((Class<? extends Enum<?>>) ckass);
        Map<String, Enum<?>> tmp = new HashMap<String, Enum<?>>();
        for (Enum<?> each : instances.values())
        {
            tmp.put(String.valueOf(each.ordinal()), each);
        }
        instances.putAll(tmp);
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
            String value = ((StringValueNode) node).getValue();
            Enum<?> instance = instances.get(value);
            return instance;
        }
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
