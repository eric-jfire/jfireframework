package com.jfireframework.mvc.binder.field.extra;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.AbstractBinderField;
import com.jfireframework.mvc.binder.impl.ObjectDataBinder;
import com.jfireframework.mvc.binder.node.ArrayNode;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;
import com.jfireframework.mvc.binder.node.TreeValueNode;

public abstract class ListField extends AbstractBinderField
{
    
    public final static ListField valueOf(Field field)
    {
        Class<?> arguType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        if (arguType == Integer.class)
        {
            return new ListIntegerField(field);
        }
        else if (arguType == Long.class)
        {
            return new ListLongField(field);
        }
        else if (arguType == Short.class)
        {
            return new ListShortField(field);
        }
        else if (arguType == Boolean.class)
        {
            return new ListBooleanField(field);
        }
        else if (arguType == Float.class)
        {
            return new ListFloatField(field);
        }
        else if (arguType == Double.class)
        {
            return new ListDoubleField(field);
        }
        else if (arguType == Character.class)
        {
            return new ListCharacterField(field);
        }
        else if (arguType == Byte.class)
        {
            return new ListByteField(field);
        }
        else if (arguType == String.class)
        {
            return new ListStringField(field);
        }
        else
        {
            return new ListObjectField(field);
        }
    }
    
    public ListField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        if (node instanceof ArrayNode)
        {
            ArrayNode arrayNode = (ArrayNode) node;
            unsafe.putObject(entity, offset, buildFromArray(arrayNode.getArray(), request, response));
        }
        else if (node instanceof TreeValueNode)
        {
            TreeValueNode treeValueNode = (TreeValueNode) node;
            unsafe.putObject(entity, offset, buildFromTree(treeValueNode.entrySet(), request, response));
        }
    }
    
    private List<?> buildFromArray(List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        List<Object> list = new ArrayList<Object>();
        for (String each : values)
        {
            list.add(buildByString(each));
        }
        return list;
    }
    
    protected abstract Object buildByString(String str);
    
    private List<?> buildFromTree(Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
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
    
    static class ListBooleanField extends ListField
    {
        
        public ListBooleanField(Field field)
        {
            super(field);
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
    
    static class ListByteField extends ListField
    {
        
        public ListByteField(Field field)
        {
            super(field);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return Byte.valueOf(str);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return Byte.valueOf(value);
        }
        
    }
    
    static class ListCharacterField extends ListField
    {
        
        public ListCharacterField(Field field)
        {
            super(field);
        }
        
        @Override
        protected Object buildByString(String str)
        {
            return str.charAt(0);
        }
        
        @Override
        protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
        {
            String value = ((StringValueNode) node).getValue();
            return value.charAt(0);
        }
        
    }
    
    static class ListDoubleField extends ListField
    {
        
        public ListDoubleField(Field field)
        {
            super(field);
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
    
    static class ListFloatField extends ListField
    {
        
        public ListFloatField(Field field)
        {
            super(field);
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
    
    static class ListIntegerField extends ListField
    {
        
        public ListIntegerField(Field field)
        {
            super(field);
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
    
    static class ListLongField extends ListField
    {
        
        public ListLongField(Field field)
        {
            super(field);
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
    
    static class ListObjectField extends ListField
    {
        private final ObjectDataBinder binder;
        private final Class<?>         ckass;
        
        public ListObjectField(Field field)
        {
            super(field);
            ckass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            binder = new ObjectDataBinder(ckass, "", null);
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
    
    static class ListShortField extends ListField
    {
        
        public ListShortField(Field field)
        {
            super(field);
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
    
    static class ListStringField extends ListField
    {
        
        public ListStringField(Field field)
        {
            super(field);
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
    
}
