package com.jfireframework.mvc.newbinder.field.extra;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.AbstractBinderField;
import com.jfireframework.mvc.newbinder.impl.ObjectDataBinder;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class ObjectField extends AbstractBinderField
{
    private final ObjectDataBinder binder;
    
    public ObjectField(Field field)
    {
        super(field);
        binder = new ObjectDataBinder(field.getType(), "", null);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        Object value = binder.bind(request, (TreeValueNode) node, response);
        unsafe.putObject(entity, offset, value);
    }
    
}
