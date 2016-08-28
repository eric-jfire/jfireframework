package com.jfireframework.mvc.newbinder.field.array.wrapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.array.AbstractArrayField;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;

public class ArrayWLongField extends AbstractArrayField
{

    public ArrayWLongField(Field field)
    {
        super(field);
    }

    @Override
    protected Object buildFromArray(int size, List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        Long[] array = new Long[size];
        int index = 0;
        for (String each : values)
        {
            array[index] = Long.valueOf(each);
            index += 1;
        }
        return array;
    }
    
    @Override
    protected Object buildFromTree(int size, Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        Long[] array = new Long[size];
        for (Entry<String, ParamNode> each : set)
        {
            int tmp = Integer.valueOf(each.getKey());
            array[tmp] = Long.valueOf(((StringValueNode) each.getValue()).getValue());
        }
        return array;
    }
    
}
