package com.jfireframework.eventbus.executor;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.RowEventContext;
import com.jfireframework.eventbus.handler.EventHandler;
import sun.misc.Unsafe;

public class TypeRowKeySerialHandlerExecutor implements EventHandlerExecutor
{
    private final ConcurrentHashMap<Object, RowBucket> map = new ConcurrentHashMap<Object, RowBucket>();
    
    static class RowBucket
    {
        public static final int                     IN_WORK        = 1;
        public static final int                     END_OF_WORK    = -1;
        public static final int                     SENDING_LEFT   = -2;
        public static final int                     END_OF_SENDING = -3;
        private volatile int                        status         = IN_WORK;
        private final MPSCQueue<RowEventContext<?>> eventQueue     = new MPSCQueue<RowEventContext<?>>();
        private static final long                   offset         = ReflectUtil.getFieldOffset("status", RowBucket.class);
        private static final Unsafe                 unsafe         = ReflectUtil.getUnsafe();
        
        public boolean takeControlOfSendingLeft()
        {
            int now = status;
            if (now == END_OF_WORK || now == END_OF_SENDING)
            {
                return unsafe.compareAndSwapInt(this, offset, now, SENDING_LEFT);
            }
            else
            {
                return false;
            }
        }
    }
    
    @Override
    public void handle(EventContext<?> eventContext, EventBus eventBus)
    {
        RowEventContext<?> rowEventContext = (RowEventContext<?>) eventContext;
        Object rowKey = rowEventContext.rowkey();
        RowBucket rowBucket = map.get(rowKey);
        if (rowBucket != null)
        {
            rowBucket.eventQueue.offer(rowEventContext);
            trySendLeft(rowBucket, eventBus);
        }
        else
        {
            rowBucket = new RowBucket();
            RowBucket pre = map.putIfAbsent(rowKey, rowBucket);
            if (pre == null)
            {
                rowBucket.eventQueue.offer(rowEventContext);
                while ((rowEventContext = rowBucket.eventQueue.poll()) != null)
                {
                    _handle(rowEventContext, eventBus);
                }
                map.remove(rowKey);
                rowBucket.status = RowBucket.END_OF_WORK;
                trySendLeft(rowBucket, eventBus);
            }
            else
            {
                pre.eventQueue.offer(rowEventContext);
                trySendLeft(pre, eventBus);
            }
        }
    }
    
    private void trySendLeft(RowBucket rowBucket, EventBus eventBus)
    {
        EventContext<?> rowEventContext;
        int status = rowBucket.status;
        if (status == RowBucket.IN_WORK || status == RowBucket.SENDING_LEFT)
        {
            return;
        }
        do
        {
            status = rowBucket.status;
            if (
                (status == RowBucket.END_OF_WORK && rowBucket.takeControlOfSendingLeft()) //
                        || (status == RowBucket.END_OF_SENDING && rowBucket.eventQueue.isEmpty() == false && rowBucket.takeControlOfSendingLeft())
            )
            {
                while ((rowEventContext = rowBucket.eventQueue.poll()) != null)
                {
                    eventBus.post(rowEventContext);
                }
                rowBucket.status = RowBucket.END_OF_SENDING;
                if (rowBucket.eventQueue.isEmpty())
                {
                    break;
                }
                else
                {
                    continue;
                }
            }
        } while (true);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void _handle(RowEventContext<?> eventContext, EventBus eventBus)
    {
        try
        {
            Object trans = eventContext.getEventData();
            for (EventHandler each : eventContext.combinationHandlers())
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
