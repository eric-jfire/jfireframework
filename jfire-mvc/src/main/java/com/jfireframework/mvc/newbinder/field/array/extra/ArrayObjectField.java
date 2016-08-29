package com.jfireframework.mvc.newbinder.field.array.extra;

import java.lang.reflect.Field;
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
