package com.jfireframework.mvc.newbinder.field.array.extra;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.array.AbstractArrayField;
import com.jfireframework.mvc.newbinder.impl.ObjectDataBinder;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class ArrayObjectField extends AbstractArrayField
{
    private final ObjectDataBinder binder;
    private final Class<?>         ckass;
    
    public ArrayObjectField(Field field)
    {
        super(field);
        ckass = field.getType().getComponentType();
        binder = new ObjectDataBinder(ckass, "", null);
    }
    
    @Override
    protected Object buildFromArray(int size, List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected Object buildFromTree(int size, Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        Object[] array = (Object[]) Array.newInstance(ckass, size);
        for (Entry<String, ParamNode> each : set)
        {
            int tmp = Integer.valueOf(each.getKey());
            array[tmp] = binder.bind(request, (TreeValueNode) each.getValue(), response);
        }
        return array;
    }
    
}
