package com.jfireframework.mvc.binder.field.extra;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.field.AbstractBinderField;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;

public class StringField extends AbstractBinderField
{
    
    public StringField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        StringValueNode realNode = (StringValueNode) node;
        unsafe.putObject(entity, offset, realNode.getValue());
    }
    
}
