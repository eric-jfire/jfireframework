package com.jfireframework.eventbus.eventthread;

import java.util.IdentityHashMap;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.handler.EventHandlerContext;

/**
 * Created by linbin on 2016/9/19.
 */
public class FlexibleEventThreadImpl implements EventThread
{
    private final MPMCQueue<ApplicationEvent>                       eventQueue;
    private volatile Thread                                         ownerThread;
    private final IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap;
    private final IdleCount                                         idleCount;
    private final int                                               coreEventThreadNum;
    private final long                                              waitTime;
    private static final Logger                                     LOGGER = ConsoleLogFactory.getLogger();
    
    public FlexibleEventThreadImpl(
            MPMCQueue<ApplicationEvent> eventQueue, //
            IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap, //
            IdleCount idleCount, //
            int coreEventThreadNum, //
            long waitTime
    )
    {
        this.waitTime = waitTime;
        this.eventQueue = eventQueue;
        this.contextMap = contextMap;
        this.idleCount = idleCount;
        this.coreEventThreadNum = coreEventThreadNum;
        idleCount.add();
    }
    
    @Override
    public void run()
    {
        ownerThread = Thread.currentThread();
        while (true)
        {
            ApplicationEvent event = eventQueue.take(waitTime, TimeUnit.MILLISECONDS);
            if (event == null)
            {
                if (idleCount.nowIdleCount() > coreEventThreadNum)
                {
                    idleCount.reduce();
                    LOGGER.debug("事件线程:{}退出对事件的获取", Thread.currentThread().getName());
                    break;
                }
                else
                {
                    continue;
                }
            }
            idleCount.reduce();
            EventHandlerContext<?> context = contextMap.get(event.getEvent());
            if (context != null)
            {
                context.handle(event);
            }
            idleCount.add();
        }
    }
    
    @Override
    public void stop()
    {
        ownerThread.interrupt();
    }
}
