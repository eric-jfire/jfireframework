package com.jfireframework.mvc.newbinder.field.array.base;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.array.AbstractArrayField;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;

public class ArrayCharField extends AbstractArrayField
{
    
    public ArrayCharField(Field field)
    {
        super(field);
    }
    
    @Override
    protected Object buildFromArray(int size, List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        char[] array = new char[size];
        int index = 0;
        for (String each : values)
        {
            array[index] = each.charAt(0);
            index += 1;
        }
        return array;
    }
    
    @Override
    protected Object buildFromTree(int size, Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        char[] array = new char[size];
        for (Entry<String, ParamNode> each : set)
        {
            int tmp = Integer.valueOf(each.getKey());
            array[tmp] = ((StringValueNode) each.getValue()).getValue().charAt(0);
        }
        return array;
    }
    
}
