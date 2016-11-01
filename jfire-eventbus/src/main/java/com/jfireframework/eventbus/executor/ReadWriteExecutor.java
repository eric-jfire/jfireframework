package com.jfireframework.eventbus.executor;

import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.handler.EventHandler;
import sun.misc.Unsafe;

public class ReadWriteExecutor implements EventHandlerExecutor
{
    static final int SHARED_SHIFT   = 16;
    static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
    static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
    static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
    
    public static int sharedCount(int c)
    {
        return c >>> SHARED_SHIFT;
    }
    
    public static int exclusiveCount(int c)
    {
        return c & EXCLUSIVE_MASK;
    }
    
    static class ReadWriteLock
    {
        private volatile int resourceCount = 0;
        static final Unsafe  unsafe        = ReflectUtil.getUnsafe();
        static final long    offset        = ReflectUtil.getFieldOffset("resourceCount", ReadWriteLock.class);
        
        public boolean compareAndSet(int expectedValue, int newValue)
        {
            return unsafe.compareAndSwapInt(this, offset, expectedValue, newValue);
        }
        
        int count()
        {
            return resourceCount;
        }
        
        public int releaseReadLock()
        {
            int now = resourceCount;
            int t = now - SHARED_UNIT;
            if (compareAndSet(now, t))
            {
                return t;
            }
            for (;;)
            {
                now = resourceCount;
                t = now - SHARED_UNIT;
                if (compareAndSet(now, t))
                {
                    return t;
                }
            }
        }
    }
    
    private ReadWriteLock                             readWriteLock = new ReadWriteLock();
    private final MPSCQueue<ReadWriteEventContext<?>> events        = new MPSCQueue<ReadWriteEventContext<?>>();
    
    @Override
    public void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        ReadWriteEventContext<?> readWriteEventContext = (ReadWriteEventContext<?>) eventContext;
        if (readWriteEventContext.mode() == ReadWriteEventContext.READ)
        {
            if (readWriteEventContext.immediateInvoke() == false)
            {
                int now = readWriteLock.resourceCount;
                if (exclusiveCount(now) != 0)
                {
                    events.offer(readWriteEventContext);
                }
                else
                {
                    int s = sharedCount(now);
                    if (s == MAX_COUNT)
                    {
                        throw new UnSupportException("无法支持这么多锁");
                    }
                    if (readWriteLock.compareAndSet(now, now + SHARED_UNIT))
                    {
                        _handle(readWriteEventContext, eventBus);
                        now = readWriteLock.releaseReadLock();
                        if (sharedCount(now) == 0)
                        {
                            
                        }
                    }
                }
            }
        }
        else
        {
            
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void _handle(EventContext<?> eventContext, EventBus eventBus)
    {
        try
        {
            EventHandler<?, ?>[] handlers = eventContext.combinationHandlers();
            Object trans = eventContext.getEventData();
            for (EventHandler each : handlers)
            {
                trans = each.handle(trans, eventBus);
            }
            eventContext.setResult(trans);
        }
        catch (Throwable e)
        {
            eventContext.setThrowable(e);
        }
        finally
        {
            eventContext.signal();
        }
    }
}
