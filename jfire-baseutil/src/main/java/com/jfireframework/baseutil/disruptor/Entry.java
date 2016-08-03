package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class Entry
{
    
    private static final Unsafe unsafe          = ReflectUtil.getUnsafe();
    private static long         takeStateoffset = ReflectUtil.getFieldOffset("takeState", Entry.class);
    public static final int     FREEFORUSE      = 0;
    public static final int     TAKEN           = 1;
    private volatile int        takeState;
    public long                 p1, p2, p3, p4, p5, p6, p7;
    public int                  p8;
    private Object              data;
    public long                 p9, p10, p11, p12, p13, p14, p15;
    
    /**
     * 设置新的数据，会将状态刷新为freeforuse
     * 
     * @param actionType
     * @param command
     * @param serverChannelInfo
     */
    public void setNewData(Object data)
    {
        this.data = data;
        takeState = FREEFORUSE;
    }
    
    public long shouleNotUse()
    {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10 + p11 + p12 + p13 + p14 + p15;
    }
    
    /**
     * 获取该消息的使用权，true代表成功，false代表失败
     * 
     * @return
     */
    public boolean take()
    {
        if (takeState == FREEFORUSE)
        {
            return unsafe.compareAndSwapInt(this, takeStateoffset, FREEFORUSE, TAKEN);
        }
        else
        {
            return false;
        }
    }
    
    public Object getData()
    {
        return data;
    }
}
