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
    
    public static int readLocks(int c)
    {
        return c >>> SHARED_SHIFT;
    }
    
    public static boolean queueLocks(int c)
    {
        return (c & EXCLUSIVE_MASK) == 1;
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
        private volatile int state  = 0;
        static final Unsafe  unsafe = ReflectUtil.getUnsafe();
        static final long    offset = ReflectUtil.getFieldOffset("state", ReadQueueLock.class);
        
        boolean lockQueue(int now)
        {
            return unsafe.compareAndSwapInt(this, offset, now, now + 1);
        }
        
        boolean lockRead(int now)
        {
            return unsafe.compareAndSwapInt(this, offset, now, now + SHARED_UNIT);
        }
        
        boolean cas(int expectedValue, int newValue)
        {
            return unsafe.compareAndSwapInt(this, offset, expectedValue, newValue);
        }
        
        public int lockManyRead(int size)
        {
            int now = state;
            int t = now + (size << SHARED_SHIFT);
            if (cas(now, t))
            {
                return t;
            }
            for (;;)
            {
                now = state;
                t = now + (size << SHARED_SHIFT);
                if (cas(now, t))
                {
                    return t;
                }
            }
        }
        
        public int releaseQueueLock()
        {
            int now = state;
            int t = now - 1;
            if (cas(now, t))
            {
                return t;
            }
            for (;;)
            {
                now = state;
                t = now - 1;
                if (cas(now, t))
                {
                    return t;
                }
            }
        }
        
        public int releaseReadLock()
        {
            int now = state;
            int t = now - SHARED_UNIT;
            if (cas(now, t))
            {
                return t;
            }
            for (;;)
            {
                now = state;
                t = now - SHARED_UNIT;
                if (cas(now, t))
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
                if (readLocks(now) == 0 && queueLocks(now))
                {
                    handleQueue(eventBus);
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
            int now = readWriteLock.state;
            if (queueLocks(now) == false)
            {
                if (readWriteLock.lockQueue(now))
                {
                    if (readLocks(now + 1) == 0)
                    {
                        handleQueue(eventBus);
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
                        if (queueLocks(now) == false)
                        {
                            if (readWriteLock.lockQueue(now))
                            {
                                if (readLocks(now + 1) == 0)
                                {
                                    handleQueue(eventBus);
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
        int pred = readWriteLock.state;
        if (queueLocks(pred))
        {
            queue.offer(readWriteEventContext);
            int now = readWriteLock.state;
            if (now == pred)
            {
                return true;
            }
            else
            {
                if (queueLocks(now) == false)
                {
                    if (readWriteLock.lockQueue(now))
                    {
                        if (readLocks(now + 1) == 0)
                        {
                            handleQueue(eventBus);
                        }
                        else
                        {
                            ;
                        }
                        return true;
                    }
                    else
                    {
                        now = readWriteLock.state;
                        if (queueLocks(now))
                        {
                            return true;
                        }
                        for (;;)
                        {
                            now = readWriteLock.state;
                            if (queueLocks(now) == false)
                            {
                                if (readWriteLock.lockQueue(now))
                                {
                                    if (readLocks(now + 1) == 0)
                                    {
                                        handleQueue(eventBus);
                                    }
                                    else
                                    {
                                        return true;
                                    }
                                }
                                else
                                {
                                    continue;
                                }
                            }
                            else
                            {
                                return true;
                            }
                        }
                    }
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            if (readWriteLock.lockRead(pred))
            {
                _handle(readWriteEventContext, eventBus);
                int now = readWriteLock.releaseReadLock();
                if (readLocks(now) == 0 && queueLocks(now))
                {
                    handleQueue(eventBus);
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
            now = readWriteLock.state;
            if (queueLocks(now))
            {
                queue.offer(readWriteEventContext);
                break;
            }
            else if (readWriteLock.lockRead(now))
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
        if (invoked)
        {
            now = readWriteLock.releaseReadLock();
            if (readLocks(now) == 0 && queueLocks(now))
            {
                handleQueue(eventBus);
            }
            else
            {
                ;
            }
        }
        else
        {
            now = readWriteLock.state;
            if (queueLocks(now) == false)
            {
                if (readWriteLock.lockQueue(now))
                {
                    if (readLocks(now + 1) == 0)
                    {
                        handleQueue(eventBus);
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
                        now = readWriteLock.state;
                        if (queueLocks(now) == false)
                        {
                            if (readWriteLock.lockQueue(now))
                            {
                                if (readLocks(now + 1) == 0)
                                {
                                    handleQueue(eventBus);
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
    
    private void handleQueue(EventBus eventBus)
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
                        list.add(pollEvent);
                    }
                    else
                    {
                        break;
                    }
                }
                readWriteLock.lockManyRead(list.size());
                for (EventContext<?> each : list)
                {
                    eventBus.post(each);
                }
                // 这里就完成了控制权的让渡。等到最后一个共享操作完成后由那个线程继续后面的流程
                return;
            }
        }
        // 如果queue中全都是写操作才会走到这里
        readWriteLock.releaseQueueLock();
        if (queue.isEmpty())
        {
            return;
        }
        else
        {
            int now = readWriteLock.state;
            if (queueLocks(now) == false && readWriteLock.lockQueue(now) && readLocks(now + 1) == 0)
            {
                handleQueue(eventBus);
            }
            else
            {
                for (;;)
                {
                    now = readWriteLock.state;
                    if (queueLocks(now) == false)
                    {
                        if (readWriteLock.lockQueue(now))
                        {
                            if (readLocks(now + 1) == 0)
                            {
                                handleQueue(eventBus);
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
