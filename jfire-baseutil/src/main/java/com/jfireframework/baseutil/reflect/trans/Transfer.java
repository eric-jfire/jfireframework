package com.jfireframework.baseutil.reflect.trans;

import com.jfireframework.baseutil.reflect.ReflectUtil;

import sun.misc.Unsafe;

public interface Transfer
{
    public static final Unsafe unsafe = ReflectUtil.getUnsafe();
    
    public Object trans(String value);
    
    public void setValue(Object entity, long offset, String value);
}
