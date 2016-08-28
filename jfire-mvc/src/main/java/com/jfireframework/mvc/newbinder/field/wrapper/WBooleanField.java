package com.jfireframework.mvc.newbinder.field.wrapper;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.AbstractBinderField;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;

public class WBooleanField extends AbstractBinderField
{
    
    public WBooleanField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        String value = ((StringValueNode) node).getValue();
        Boolean b = Boolean.valueOf(value);
        unsafe.putObject(entity, offset, b);
    }
    
}
