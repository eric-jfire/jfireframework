package com.jfireframework.mvc.newbinder.field;

import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.ParamTreeNode;
import com.jfireframework.mvc.newbinder.StringValueNode;

public class IntField extends AbstractBinderField
{
    
    public IntField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamTreeNode node, Object entity)
    {
        String value = ((StringValueNode) node).getValue();
        unsafe.putInt(entity, offset, Integer.valueOf(value).intValue());
    }
    
}
