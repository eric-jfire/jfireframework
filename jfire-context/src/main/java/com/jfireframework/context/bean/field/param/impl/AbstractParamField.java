package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.context.bean.field.param.ParamField;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractParamField implements ParamField
{
    protected long   offset;
    protected Unsafe unsafe = ReflectUtil.getUnsafe();
    protected Object value;
    
    public AbstractParamField(Field field, String value)
    {
        offset = unsafe.objectFieldOffset(field);
    }
    
    public void setParam(Object entity)
    {
        unsafe.putObject(entity, offset, value);
    }
}
