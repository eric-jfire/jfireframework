package com.jfireframework.licp;

import java.util.IdentityHashMap;

public class ObjectCollect
{
    private Object[]                         objs     = new Object[256];
    private IdentityHashMap<Object, Integer> idMap    = new IdentityHashMap<Object, Integer>(256);
    // 下一个对象写入的位置
    private int                              sequence = 1;
    // 是否支持循环引用。开启的情况下，才可以序列化存在循环引用的对象。遇到相同对象不再序列化而是写入一个序号。
    // 所以如果整个对象图中有比较多的重复对象，用这个方法也可以节省性能。
    // 但如果没有这样的需求的情况下，关闭支持反而可以提升性能。Licp中默认是开启的
    private final boolean                    cycleSupport;
    
    public ObjectCollect(boolean cycleSupport)
    {
        this.cycleSupport = cycleSupport;
    }
    
    public void putForDesc(Object obj)
    {
        if (cycleSupport)
        {
            if (sequence == objs.length)
            {
                Object[] tmp = new Object[objs.length * 2];
                System.arraycopy(objs, 0, tmp, 0, sequence);
                objs = tmp;
            }
            objs[sequence++] = obj;
        }
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
        if (cycleSupport)
        {
            for (int i = 0; i < sequence; i++)
            {
                objs[i] = null;
            }
            sequence = 1;
            idMap.clear();
        }
    }
    
}
