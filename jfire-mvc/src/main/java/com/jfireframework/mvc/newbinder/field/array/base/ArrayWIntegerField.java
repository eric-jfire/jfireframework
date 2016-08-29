package com.jfireframework.mvc.newbinder.field.array.base;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.array.AbstractArrayField;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;

public class ArrayWIntegerField extends AbstractArrayField
{
    
    public ArrayWIntegerField(Field field)
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
        return buildByString(value);
    }
}
