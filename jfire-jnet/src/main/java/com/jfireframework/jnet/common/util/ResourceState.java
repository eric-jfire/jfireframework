package com.jfireframework.jnet.common.util;

import java.util.concurrent.atomic.AtomicInteger;

public class ResourceState
{
    private static final int    OPEN  = 1;
    private static final int    CLOSE = 2;
    private final AtomicInteger state = new AtomicInteger(OPEN);
    
    public boolean isOpen()
    {
        return state.intValue() == OPEN;
    }
    
    /**
     * 执行关闭资源的动作。只有资源处于open状态才可以执行关闭动作。并且通过cas抢占，保证关闭动作只会执行一次。
     * 如果成功执行关闭动作，返回true。否则返回false
     * 
     * @return
     */
    public boolean close()
    {
        if (state.intValue() == OPEN && state.compareAndSet(OPEN, CLOSE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
