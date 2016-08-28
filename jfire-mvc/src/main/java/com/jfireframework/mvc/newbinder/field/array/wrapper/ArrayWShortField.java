package com.jfireframework.mvc.newbinder.field.array.wrapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.array.AbstractArrayField;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;

public class ArrayWShortField extends AbstractArrayField
{
    
    public ArrayWShortField(Field field)
    {
        super(field);
    }
    
    @Override
    protected Object buildFromArray(int size, List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        Short[] array = new Short[size];
        int index = 0;
        for (String each : values)
        {
            array[index] = Short.valueOf(each);
            index += 1;
        }
        return array;
    }
    
    @Override
    protected Object buildFromTree(int size, Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        Short[] array = new Short[size];
        for (Entry<String, ParamNode> each : set)
        {
            int tmp = Integer.valueOf(each.getKey());
            array[tmp] = Short.valueOf(((StringValueNode) each.getValue()).getValue());
        }
        return array;
    }
    
}
