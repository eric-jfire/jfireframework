package com.jfireframework.mvc.binder.field.array.base;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.array.AbstractArrayField;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;

public class ArrayWByteField extends AbstractArrayField
{
    
    public ArrayWByteField(Field field)
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
        return buildByString(value);
    }
}
