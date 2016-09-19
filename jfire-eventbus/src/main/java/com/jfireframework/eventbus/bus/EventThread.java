package com.jfireframework.eventbus.bus;

import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.eventbus.event.ApplicationEvent;
import sun.misc.Unsafe;

/**
 * Created by linbin on 2016/9/19.
 */
public class EventThread implements Runnable
{
    private final MPMCQueue<ApplicationEvent> eventQueue;
    private static final int                  IDLE         = 0;
    private static final int                  WORK         = 1;
    private volatile int                      status       = WORK;
    private static final Unsafe               unsafe       = ReflectUtil.getUnsafe();
    private static final long                 statusOffset = ReflectUtil.getFieldOffset("status", EventThread.class);
    
    public EventThread(MPMCQueue<ApplicationEvent> eventQueue)
    {
        this.eventQueue = eventQueue;
    }
    
    @Override
    public void run()
    {
        while (status == WORK)
        {
            
        }
    }
}
