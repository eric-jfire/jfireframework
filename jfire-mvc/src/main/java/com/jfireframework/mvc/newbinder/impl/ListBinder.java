package com.jfireframework.mvc.newbinder.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.mvc.newbinder.DataBinder;
import com.jfireframework.mvc.newbinder.node.ArrayNode;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public abstract class ListBinder implements DataBinder
{
    protected final String   prefixName;
    protected final Class<?> ckass;
    
    public ListBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
        this.ckass = ckass;
        Verify.False(prefixName.equals(""), "数组绑定，参数必须有名称");
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        ParamNode node = treeValueNode.get(prefixName);
        if (node instanceof ArrayNode)
        {
            ArrayNode arrayNode = (ArrayNode) node;
            return buildFromArray(arrayNode.getArray().size(), arrayNode.getArray(), request, response);
        }
        else if (node instanceof TreeValueNode)
        {
            TreeValueNode new_treeValueNode = (TreeValueNode) node;
            return buildFromTree(new_treeValueNode.entrySet(), request, response);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
    protected Object buildFromArray(int size, List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        List<Object> list = new ArrayList<Object>();
        int index = 0;
        for (String each : values)
        {
            list.add(index, buildByString(each));
            index += 1;
        }
        return list;
    }
    
    protected abstract Object buildByString(String str);
    
    protected Object buildFromTree(Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        List<Object> list = new ArrayList<Object>();
        for (Entry<String, ParamNode> each : set)
        {
            int index = Integer.valueOf(each.getKey());
            list.add(index, buildByNode(each.getValue(), request, response));
        }
        return list;
    }
    
    protected abstract Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response);
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
