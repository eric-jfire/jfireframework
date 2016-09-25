package com.jfireframework.eventbus.handler;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.RowEventContext;

public class RowKeyHandlerContextImpl<T> extends AbstractEventHandlerContext<T>
{
    private final ConcurrentHashMap<Object, MPSCQueue<RowEventContext>> map = new ConcurrentHashMap<Object, MPSCQueue<RowEventContext>>();
    
    public RowKeyHandlerContextImpl(Enum<? extends Event<T>> event)
    {
        super(event);
    }
    
    @Override
    public void handle(EventContext eventContext, EventBus eventBus)
    {
        RowEventContext rowEventContext = (RowEventContext) eventContext;
        do
        {
            Object rowKey = rowEventContext.rowkey();
            MPSCQueue<RowEventContext> pre = map.get(rowKey);
            if (pre == null)
            {
                MPSCQueue<RowEventContext> inwork = new MPSCQueue<RowEventContext>();
                pre = map.putIfAbsent(rowKey, inwork);
                if (pre == null)
                {
                    inwork.offer(rowEventContext);
                    while ((rowEventContext = inwork.poll()) != null)
                    {
                        _handle(rowEventContext, eventBus);
                    }
                    map.remove(rowKey);
                    break;
                }
            }
            pre.offer(rowEventContext);
            if (pre == map.get(rowKey))
            {
                break;
            }
            else
            {
                if (rowEventContext.isFinished())
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
    
    private void _handle(EventContext eventContext, EventBus eventBus)
    {
        try
        {
            for (EventHandler<T> each : handlers)
            {
                each.handle(eventContext, eventBus);
            }
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
