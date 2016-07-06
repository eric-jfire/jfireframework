package com.jfireframework.licp;

import java.util.HashMap;
import java.util.IdentityHashMap;

public class ObjectCollect
{
    private IdentityHashMap<Object, Integer> objectMap = new IdentityHashMap<Object, Integer>();
    private HashMap<Integer, Object>         idMap     = new HashMap<Integer, Object>();
    private int                              sequence  = 0;
    private final boolean                    cycleSupport;
    
    public ObjectCollect(boolean cycleSupport)
    {
        this.cycleSupport = cycleSupport;
    }
    
    public ObjectCollect()
    {
        this(true);
    }
    
    /**
     * 放入一个对象，如果对象已经存在于收集器中，就放回具体的id。否则返回null
     * 
     * @param obj
     * @return
     */
    public Integer put(Object obj)
    {
        if (cycleSupport == false)
        {
            return null;
        }
        Integer result = objectMap.get(obj);
        if (result == null)
        {
            objectMap.put(obj, sequence);
            sequence += 1;
            return null;
        }
        else
        {
            return result;
        }
    }
    
    public Object get(Integer id)
    {
        return idMap.get(id);
    }
    
    public void clear()
    {
        sequence = 0;
        idMap.clear();
        objectMap.clear();
    }
    
}
