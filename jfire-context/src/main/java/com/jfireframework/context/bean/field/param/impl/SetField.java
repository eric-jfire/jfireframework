package com.jfireframework.context.bean.field.param.impl;

import java.awt.geom.Ellipse2D;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import com.jfireframework.baseutil.exception.UnSupportException;

public class SetField extends AbstractParamField
{
    private final SetType setType;
    private String ;
    public SetField(Field field, String value)
    {
        super(field, value);
        Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        if (type instanceof Class<?>)
        {
            if (type == String.class)
            {
                setType = SetType.STRING;
            }
            else if (type == Integer.class)
            {
                setType = SetType.INTEGER;
            }
            else if (type == Long.class)
            {
                setType = SetType.LONG;
            }
            else if (type == Float.class)
            {
                setType = SetType.FLOAT;
            }
            else if (type == Double.class)
            {
                setType = SetType.DOUBLE;
            }
            else{
                throw new UnSupportException("目前Set注入只支持String,Integer,Long,Float,Double");
            }
        }
        else
        {
            throw new UnSupportException("Set注入，必须指明注入类型，而不能使用问号");
        }
    }
    
    public void setParam(Object entity)
    {
        Set<?> set = (Set<?>) unsafe.getObject(entity, offset);
    }
    
    enum SetType
    {
        STRING, INTEGER, LONG, FLOAT, DOUBLE;
    }
}
