package com.jfireframework.licp;

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
}
