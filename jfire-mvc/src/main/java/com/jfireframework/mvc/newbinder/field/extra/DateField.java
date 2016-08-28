package com.jfireframework.mvc.newbinder.field.extra;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.annotation.MvcDateParse;
import com.jfireframework.mvc.newbinder.field.AbstractBinderField;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;

public class DateField extends AbstractBinderField
{
    
    private final String pattern;
    
    public DateField(Field field)
    {
        super(field);
        if (field.isAnnotationPresent(MvcDateParse.class))
        {
            pattern = field.getAnnotation(MvcDateParse.class).date_format();
        }
        else
        {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        String value = ((StringValueNode) node).getValue();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try
        {
            Date date = format.parse(value);
            unsafe.putObject(entity, offset, date);
        }
        catch (ParseException e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
}
