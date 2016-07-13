package com.jfireframework.licp;

import java.util.IdentityHashMap;

public class ObjectCollect
{
    private Object[]                         objs     = new Object[20];
    private IdentityHashMap<Object, Integer> idMap    = new IdentityHashMap<Object, Integer>(256);
    private int                              sequence = 1;
    private final boolean                    cycleSupport;
    
    public ObjectCollect(boolean cycleSupport)
    {
        this.cycleSupport = cycleSupport;
    }
    
    public ObjectCollect()
    {
        cycleSupport = true;
    }
    
    public void putForDesc(Object obj)
    {
        if (sequence == objs.length)
        {
            Object[] tmp = new Object[objs.length * 2];
            System.arraycopy(objs, 0, tmp, 0, sequence);
            objs = tmp;
        }
        objs[sequence++] = obj;
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
        Integer result = idMap.get(obj);
        if (result == null)
        {
            if (sequence == objs.length)
            {
                Object[] tmp = new Object[objs.length * 2];
                System.arraycopy(objs, 0, tmp, 0, sequence);
                objs = tmp;
            }
            objs[sequence] = obj;
            idMap.put(obj, sequence++);
            return 0;
        }
        else
        {
            return result;
        }
    }
    
    public Object get(int id)
    {
        return objs[id];
    }
    
    public void clear()
    {
        sequence = 1;
        for (int i = 0; i < objs.length; i++)
        {
            objs[i] = null;
        }
        idMap.clear();
    }
    
}
