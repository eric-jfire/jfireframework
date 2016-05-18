package com.jfireframework.baseutil.verify;

import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.VerifyException;

public final class Verify
{
    public static void Null(Object target, String msg, Object... params)
    {
        if (target != null)
        {
            throw new VerifyException(StringUtil.format(msg, params));
        }
    }
    
    public static void notNull(Object target, String msg, Object... params)
    {
        if (target == null)
        {
            throw new VerifyException(StringUtil.format(msg, params));
        }
    }
    
    public static void False(boolean target, String msg, Object... params)
    {
        if (target)
        {
            throw new VerifyException(StringUtil.format(msg, params));
        }
    }
    
    public static void True(boolean target, String msg, Object... params)
    {
        if (target == false)
        {
            throw new VerifyException(StringUtil.format(msg, params));
        }
    }
    
    /**
     * 对象target必须是类type的实例，否则抛出异常
     * 
     * @param target
     * @param type
     * @param msg
     * @param params
     */
    public static void matchType(Object target, Class<?> type, String msg, Object... params)
    {
        if (target.getClass() != type)
        {
            throw new VerifyException(StringUtil.format(msg, params));
        }
    }
    
    /**
     * 两个对象需要相等，否则抛出异常
     * 
     * @param o1
     * @param o2
     * @param msg
     * @param params
     */
    public static void equal(Object o1, Object o2, String msg, Object... params)
    {
        if (o1.equals(o2) == false)
        {
            throw new VerifyException(StringUtil.format(msg, params));
        }
    }
    
    public static void error(String msg, Object... params)
    {
        throw new VerifyException(StringUtil.format(msg, params));
    }
    
    public static void exist(Object entity, String msg, Object... params)
    {
        if (entity == null)
        {
            throw new VerifyException(StringUtil.format(msg, params));
        }
    }
}
