package com.jfireframework.eventbus.executor;

import java.util.LinkedList;
import java.util.List;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.handler.EventHandler;
import sun.misc.Unsafe;

public class ReadWriteExecutor implements EventHandlerExecutor
{
    static final int SHARED_SHIFT   = 1;
    static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
    static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
    
    public static int readLock(int c)
    {
        return c >>> SHARED_SHIFT;
    }
    
    public static int queueLock(int c)
    {
        return c & EXCLUSIVE_MASK;
    }
    
    /**
     * 读锁代表着当前的运行中非写操作。
     * queue锁则代表着持有了对queue的操作权。
     * 1.如果抢占queue锁成功，并且成功时读锁数量为0，则直接操作queue
     * 2.如果抢占queue锁成功，并且成功时读锁数量大于0，则将queue的操作权让渡给读锁持有者
     * 3.读锁持有者释放读锁成功时，如果释放后读锁数量为0并且queue锁被抢占，则可以操作queueu
     * 
     * @author linbin
     *
     */
    static class ReadQueueLock
    {
        private volatile int resourceCount = 0;
        static final Unsafe  unsafe        = ReflectUtil.getUnsafe();
        static final long    offset        = ReflectUtil.getFieldOffset("resourceCount", ReadQueueLock.class);
        
        public boolean compareAndSet(int expectedValue, int newValue)
        {
            return unsafe.compareAndSwapInt(this, offset, expectedValue, newValue);
        }
        
        int count()
        {
            return resourceCount;
        }
        
        public int lockRead()
        {
            int now = resourceCount;
            int t = now + SHARED_UNIT;
            if (compareAndSet(now, t))
            {
                return t;
            }
            for (;;)
            {
                now = resourceCount;
                t = now + SHARED_UNIT;
                if (compareAndSet(now, t))
                {
                    return t;
                }
            }
        }
        
        public int releaseQueueLock()
        {
            int now = resourceCount;
            int t = now - 1;
            if (compareAndSet(now, t))
            {
                return t;
            }
            for (;;)
            {
                now = resourceCount;
                t = now - 1;
                if (compareAndSet(now, t))
                {
                    return t;
                }
            }
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
    
    private ReadQueueLock                             readWriteLock = new ReadQueueLock();
    private final MPSCQueue<ReadWriteEventContext<?>> queue         = new MPSCQueue<ReadWriteEventContext<?>>();
    
    @Override
    public void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        ReadWriteEventContext<?> readWriteEventContext = (ReadWriteEventContext<?>) eventContext;
        if (readWriteEventContext.mode() == ReadWriteEventContext.READ)
        {
            if (readWriteEventContext.immediateInvoke() == false)
            {
                if (tryFastInvokeRead(readWriteEventContext, eventBus) == false)
                {
                    fullPathInvokeRead(readWriteEventContext, eventBus);
                }
            }
            else
            {
                _handle(readWriteEventContext, eventBus);
                int now = readWriteLock.releaseReadLock();
                if (readLock(now) == 0 && queueLock(now) > 0)
                {
                    tryHandleWriteSituation(eventBus);
                }
                else
                {
                    ;
                }
            }
        }
        else
        {
            queue.offer(readWriteEventContext);
            int now = readWriteLock.resourceCount;
            if (queueLock(now) == 0)
            {
                if (readWriteLock.compareAndSet(now, now + 1))
                {
                    if (readLock(now + 1) == 0)
                    {
                        tryHandleWriteSituation(eventBus);
                    }
                    else
                    {
                        ;
                    }
                }
                else
                {
                    for (;;)
                    {
                        if (queueLock(now) == 0)
                        {
                            if (readWriteLock.compareAndSet(now, now + 1))
                            {
                                if (readLock(now + 1) == 0)
                                {
                                    tryHandleWriteSituation(eventBus);
                                }
                                else
                                {
                                    return;
                                }
                            }
                            else
                            {
                                continue;
                            }
                        }
                        else
                        {
                            return;
                        }
                    }
                }
            }
            else
            {
                return;
            }
        }
    }
    
    private boolean tryFastInvokeRead(ReadWriteEventContext<?> readWriteEventContext, EventBus eventBus)
    {
        int pred = readWriteLock.resourceCount;
        if (queueLock(pred) != 0)
        {
            queue.offer(readWriteEventContext);
            int now = readWriteLock.resourceCount;
            if (now == pred)
            {
                ;
            }
            else
            {
                if (queueLock(now) == 0)
                {
                    if (readWriteLock.compareAndSet(now, now + 1))
                    {
                        if (readLock(now + 1) == 0)
                        {
                            tryHandleWriteSituation(eventBus);
                        }
                        else
                        {
                            ;
                        }
                    }
                    else
                    {
                        for (;;)
                        {
                            now = readWriteLock.resourceCount;
                            if (queueLock(now) == 0)
                            {
                                if (readWriteLock.compareAndSet(now, now + 1))
                                {
                                    if (readLock(now + 1) == 0)
                                    {
                                        tryHandleWriteSituation(eventBus);
                                    }
                                    else
                                    {
                                        break;
                                    }
                                }
                                else
                                {
                                    continue;
                                }
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                else
                {
                    ;
                }
            }
            return true;
        }
        else
        {
            if (readWriteLock.compareAndSet(pred, pred + SHARED_UNIT))
            {
                _handle(readWriteEventContext, eventBus);
                int now = readWriteLock.releaseReadLock();
                if (readLock(now) == 0 && queueLock(now) > 0)
                {
                    tryHandleWriteSituation(eventBus);
                }
                else
                {
                    ;
                }
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    private void fullPathInvokeRead(ReadWriteEventContext<?> readWriteEventContext, EventBus eventBus)
    {
        int now;
        boolean invoked = false;
        for (;;)
        {
            now = readWriteLock.resourceCount;
            if (queueLock(now) != 0)
            {
                queue.offer(readWriteEventContext);
                break;
            }
            else
            {
                if (readWriteLock.compareAndSet(now, now + SHARED_UNIT))
                {
                    _handle(readWriteEventContext, eventBus);
                    invoked = true;
                    break;
                }
                else
                {
                    continue;
                }
            }
        }
        if (invoked)
        {
            now = readWriteLock.releaseReadLock();
            if (readLock(now) == 0 && queueLock(now) > 0)
            {
                tryHandleWriteSituation(eventBus);
            }
            else
            {
                ;
            }
        }
        else
        {
            now = readWriteLock.resourceCount;
            if (queueLock(now) == 0)
            {
                if (readWriteLock.compareAndSet(now, now + 1))
                {
                    if (readLock(now + 1) == 0)
                    {
                        tryHandleWriteSituation(eventBus);
                    }
                    else
                    {
                        ;
                    }
                }
                else
                {
                    for (;;)
                    {
                        now = readWriteLock.resourceCount;
                        if (queueLock(now) == 0)
                        {
                            if (readWriteLock.compareAndSet(now, now + 1))
                            {
                                if (readLock(now + 1) == 0)
                                {
                                    tryHandleWriteSituation(eventBus);
                                }
                                else
                                {
                                    break;
                                }
                            }
                            else
                            {
                                continue;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }
            else
            {
                ;
            }
        }
    }
    
    private void tryHandleWriteSituation(EventBus eventBus)
    {
        ReadWriteEventContext<?> pollEvent;
        while ((pollEvent = queue.peek()) != null)
        {
            if (pollEvent.mode() == ReadWriteEventContext.WRITE)
            {
                pollEvent = queue.poll();
                _handle(pollEvent, eventBus);
            }
            else
            {
                List<EventContext<?>> list = new LinkedList<EventContext<?>>();
                while ((pollEvent = queue.peek()) != null)
                {
                    if (pollEvent.mode() == ReadWriteEventContext.READ)
                    {
                        pollEvent = queue.poll();
                        pollEvent.setImmediateMode();
                        readWriteLock.lockRead();
                        list.add(pollEvent);
                    }
                    else
                    {
                        break;
                    }
                }
                for (EventContext<?> each : list)
                {
                    eventBus.post(each);
                }
                return;
            }
        }
        readWriteLock.releaseQueueLock();
        if (queue.isEmpty())
        {
            return;
        }
        else
        {
            int now = readWriteLock.resourceCount;
            if (queueLock(now) == 0 && readWriteLock.compareAndSet(now, now + 1) && readLock(now + 1) == 0)
            {
                tryHandleWriteSituation(eventBus);
            }
            else
            {
                for (;;)
                {
                    now = readWriteLock.resourceCount;
                    if (queueLock(now) == 0)
                    {
                        if (readWriteLock.compareAndSet(now, now + 1))
                        {
                            if (readLock(now + 1) == 0)
                            {
                                tryHandleWriteSituation(eventBus);
                            }
                            else
                            {
                                break;
                            }
                        }
                        else
                        {
                            continue;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }
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
