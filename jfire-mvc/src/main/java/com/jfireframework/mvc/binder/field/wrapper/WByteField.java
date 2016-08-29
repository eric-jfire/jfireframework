package com.jfireframework.mvc.binder.field.wrapper;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.AbstractBinderField;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;

public class WByteField extends AbstractBinderField
{
    
    public WByteField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        String value = ((StringValueNode) node).getValue();
        Byte b = Byte.valueOf(value);
        unsafe.putObject(entity, offset, b);
    }
    
}
