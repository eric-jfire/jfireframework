package com.jfireframework.licp;

import com.jfireframework.baseutil.exception.JustThrowException;

public class Licp
{
    private ObjectCollect   collect  = new ObjectCollect();
    private ClassNoRegister register = new ClassNoRegister();
    public static final int NULL     = 0;
    public static final int EXIST    = 1;
    
    public Licp(boolean cycleSupport)
    {
        if (cycleSupport)
        {
            collect = new ObjectCollect();
        }
    }
    
    public int addClassNo(Class<?> type)
    {
        return register.registerTemporary(type);
    }
    
    public Class<?> loadClass(String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (ClassNotFoundException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public Class<?> loadClass(int classNo)
    {
        return register.getType(classNo);
    }
    
    public int indexOf(Class<?> type)
    {
        return register.indexOf(type);
    }
}
