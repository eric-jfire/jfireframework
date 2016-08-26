package com.jfireframework.mvc.newbinder.field;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.ParamTreeNode;
import com.jfireframework.mvc.newbinder.TreeValueNode;
import com.jfireframework.mvc.newbinder.impl.ObjectDataBinder;

public class ObjectField extends AbstractBinderField
{
    private final ObjectDataBinder binder;
    
    public ObjectField(Field field)
    {
        super(field);
        binder = new ObjectDataBinder(field.getType(), name);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamTreeNode node, Object entity)
    {
        Object value = binder.binder(request, (TreeValueNode) node, response);
        unsafe.putObject(entity, offset, value);
    }
    
}
