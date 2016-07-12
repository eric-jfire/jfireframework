package com.jfireframework.licp;

public class ObjectCollect
{
    private Object[]      objs     = new Object[20];
    private int           sequence = 0;
    private final boolean cycleSupport;
    
    public ObjectCollect(boolean cycleSupport)
    {
        this.cycleSupport = cycleSupport;
    }
    
    public ObjectCollect()
    {
        cycleSupport = true;
    }
    
    /**
     * 放入一个对象，如果对象已经存在于收集器中，就放回具体的id,id从1开始。如果对象不存在，返回0
     * 
     * @param obj
     * @return
     */
    public int put(Object obj)
    {
        if (cycleSupport == false)
        {
            return 0;
        }
        for (int i = 0; i < sequence; i++)
        {
            if (objs[i] == obj)
            {
                return i + 1;
            }
        }
        if (sequence < objs.length)
        {
            objs[sequence] = obj;
        }
        else
        {
            Object[] tmp = new Object[objs.length * 2];
            System.arraycopy(objs, 0, tmp, 0, sequence);
            objs = tmp;
            objs[sequence] = obj;
        }
        sequence += 1;
        return 0;
    }
    
    public Object get(int id)
    {
        return objs[id - 1];
    }
    
    public void clear()
    {
        sequence = 0;
        for (int i = 0; i < objs.length; i++)
        {
            objs[i] = null;
        }
    }
    
}
