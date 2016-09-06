package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
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

public abstract class ArrayBinder implements DataBinder
{
    protected final Class<?> ckass;
    protected final String   prefixName;
    
    public ArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.ckass = ckass;
        this.prefixName = prefixName;
        Verify.False(prefixName.equals(""), "数组绑定，参数必须有名称");
    }
    
    public static final ArrayBinder valueOf(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        if (ckass.isArray() && ckass.getComponentType().isArray() == false)
        {
            ckass = ckass.getComponentType();
            if (ckass == Integer.class || ckass == int.class)
            {
                return new IntegerArrayBinder(ckass, prefixName, annotations);
            }
            else if (ckass == short.class || ckass == Short.class)
            {
                return new ShortArrayBinder(ckass, prefixName, annotations);
            }
            else if (ckass == long.class || ckass == Long.class)
            {
                return new LongArrayBinder(ckass, prefixName, annotations);
            }
            else if (ckass == boolean.class || ckass == Boolean.class)
            {
                return new BooleanArrayBinder(ckass, prefixName, annotations);
            }
            else if (ckass == Float.class || ckass == float.class)
            {
                return new FloatArrayBinder(ckass, prefixName, annotations);
            }
            else if (ckass == double.class || ckass == Double.class)
            {
                return new DoubleArrayBinder(ckass, prefixName, annotations);
            }
            else if (ckass == String.class)
            {
                return new StringArrayBinder(ckass, prefixName, annotations);
            }
            else
            {
                return new ObjectArrayBinder(ckass, prefixName, annotations);
            }
        }
        else
        {
            throw new UnsupportedOperationException();
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
            int max = 0;
            for (String each : new_treeValueNode.keySet())
            {
                int tmp = Integer.valueOf(each);
                if (max < tmp)
                {
                    max = tmp;
                }
            }
            return buildFromTree(max + 1, new_treeValueNode.entrySet(), request, response);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
    
    protected Object buildFromArray(int size, List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        Object array = Array.newInstance(ckass, size);
        int index = 0;
        for (String each : values)
        {
            Array.set(array, index, buildByString(each));
            index += 1;
        }
        return array;
    }
    
    protected abstract Object buildByString(String str);
    
    protected Object buildFromTree(int size, Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        Object array = Array.newInstance(ckass, size);
        for (Entry<String, ParamNode> each : set)
        {
            int index = Integer.valueOf(each.getKey());
            Array.set(array, index, buildByNode(each.getValue(), request, response));
        }
        return array;
    }
    
    protected abstract Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response);
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
    static class IntegerArrayBinder extends ArrayBinder
    {
        
        public IntegerArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
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
    
    static class LongArrayBinder extends ArrayBinder
    {
        
        public LongArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
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
    
    static class BooleanArrayBinder extends ArrayBinder
    {
        
        public BooleanArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
        {
            super(ckass, prefixName, annotations);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return Boolean.valueOf(str);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return Boolean.valueOf(value);
        }
    }
    
    static class ShortArrayBinder extends ArrayBinder
    {
        
        public ShortArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
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
    
    static class FloatArrayBinder extends ArrayBinder
    {
        
        public FloatArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
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
    
    static class DoubleArrayBinder extends ArrayBinder
    {
        
        public DoubleArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
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
    
    static class StringArrayBinder extends ArrayBinder
    {
        
        public StringArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
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
            return ((StringValueNode) node).getValue();
        }
    }
    
    static class ObjectArrayBinder extends ArrayBinder
    {
        private final ObjectDataBinder binder;
        
        public ObjectArrayBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
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
            TreeValueNode treeValueNode = (TreeValueNode) node;
            return binder.bind(request, treeValueNode, response);
        }
        
    }
}
