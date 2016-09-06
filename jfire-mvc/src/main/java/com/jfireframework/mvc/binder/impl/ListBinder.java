package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.ArrayNode;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;
import com.jfireframework.mvc.binder.node.TreeValueNode;

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
    
    public static final ListBinder valueOf(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        if (ckass == Integer.class)
        {
            return new ListIntegerBinder(ckass, prefixName, annotations);
        }
        else if (ckass == Short.class)
        {
            return new ListShortBinder(ckass, prefixName, annotations);
        }
        else if (ckass == Long.class)
        {
            return new ListLongBinder(ckass, prefixName, annotations);
        }
        else if (ckass == Float.class)
        {
            return new ListFloatBinder(ckass, prefixName, annotations);
        }
        else if (ckass == Double.class)
        {
            return new ListDoubleBinder(ckass, prefixName, annotations);
        }
        else if (ckass == Boolean.class)
        {
            return new ListBooleanBinder(ckass, prefixName, annotations);
        }
        else if (ckass == String.class)
        {
            return new ListStringBinder(ckass, prefixName, annotations);
        }
        else
        {
            return new ListObjectBinder(ckass, prefixName, annotations);
        }
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
    
    static class ListIntegerBinder extends ListBinder
    {
        
        public ListIntegerBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return Integer.valueOf(str);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return Integer.valueOf(value);
        }
    }
    
    static class ListBooleanBinder extends ListBinder
    {
        
        public ListBooleanBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return Long.valueOf(str);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return Long.valueOf(value);
        }
    }
    
    static class ListShortBinder extends ListBinder
    {
        
        public ListShortBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return Short.valueOf(str);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return Short.valueOf(value);
        }
    }
    
    static class ListLongBinder extends ListBinder
    {
        
        public ListLongBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return Long.valueOf(str);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return Long.valueOf(value);
        }
    }
    
    static class ListFloatBinder extends ListBinder
    {
        
        public ListFloatBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return Float.valueOf(str);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return Float.valueOf(value);
        }
    }
    
    static class ListDoubleBinder extends ListBinder
    {
        
        public ListDoubleBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return Double.valueOf(str);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return Double.valueOf(value);
        }
    }
    
    static class ListStringBinder extends ListBinder
    {
        
        public ListStringBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return str;
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return value;
        }
    }
    
    static class ListObjectBinder extends ListBinder
    {
        private final ObjectDataBinder binder;
        
        public ListObjectBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
            binder = new ObjectDataBinder(ckass, "", annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            throw new UnsupportedOperationException();
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            return binder.bind(request, (TreeValueNode) node, response);
        }
        
    }
}
