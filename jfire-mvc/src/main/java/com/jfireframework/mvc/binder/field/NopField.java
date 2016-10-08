package com.jfireframework.mvc.binder.field;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.node.ParamNode;

public class NopField extends AbstractBinderField
{
    
    public NopField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        ;
    }
    
}
