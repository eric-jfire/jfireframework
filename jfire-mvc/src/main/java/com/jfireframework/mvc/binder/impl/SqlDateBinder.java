package com.jfireframework.mvc.binder.impl;

import java.lang.annotation.Annotation;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.annotation.MvcDateParse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;
import com.jfireframework.mvc.binder.node.TreeValueNode;

public class SqlDateBinder implements DataBinder
{
    private final String prefixName;
    private final String pattern;
    
    public SqlDateBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
        String t_pattern = "yyyy-MM-dd";
        for (Annotation each : annotations)
        {
            if (each instanceof MvcDateParse)
            {
                t_pattern = ((MvcDateParse) each).date_format();
            }
        }
        pattern = t_pattern;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        ParamNode node = treeValueNode.get(prefixName);
        if (node != null)
        {
            String value = ((StringValueNode) node).getValue();
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            try
            {
                return new Date(format.parse(value).getTime());
            }
            catch (ParseException e)
            {
                throw new JustThrowException(e);
            }
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
