package com.jfireframework.mvc.newbinder.field.list;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.impl.ObjectDataBinder;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class ListObjectField extends ListField
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
    protected List<?> buildFromArray(List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected List<?> buildFromTree(Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        List<Object> list = new ArrayList<Object>();
        for (Entry<String, ParamNode> each : set)
        {
            int index = Integer.valueOf(each.getKey());
            list.add(index, binder.bind(request, (TreeValueNode) each.getValue(), response));
        }
        return list;
    }
}
