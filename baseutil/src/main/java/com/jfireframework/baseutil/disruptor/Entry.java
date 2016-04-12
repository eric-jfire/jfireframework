package com.jfireframework.baseutil.disruptor;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Entry
{
    private static Unsafe   unsafe          = ReflectUtil.getUnsafe();
    private volatile int    takeState;
    private static long     takeStateoffset = ReflectUtil.getFieldOffset("takeState", Entry.class);
    public static final int FREEFORUSE      = 0;
    public static final int TAKEN           = 1;
    private volatile Object data;
                            
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
