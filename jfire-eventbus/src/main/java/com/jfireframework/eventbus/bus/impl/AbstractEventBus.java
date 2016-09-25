package com.jfireframework.eventbus.bus.impl;

import java.util.IdentityHashMap;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.MoreContextInfo;
import com.jfireframework.eventbus.eventcontext.impl.NormalEventContext;
import com.jfireframework.eventbus.eventcontext.impl.RowEventContextImpl;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.handler.EventHandlerContext;
import com.jfireframework.eventbus.handler.ParallelHandlerContextImpl;
import com.jfireframework.eventbus.handler.RowKeyHandlerContextImpl;
import com.jfireframework.eventbus.handler.SerialHandlerContextImpl;

public abstract class AbstractEventBus implements EventBus
{
    protected final MPMCQueue<EventContext>                           eventQueue = new MPMCQueue<EventContext>();
    protected final IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap = new IdentityHashMap<Event<?>, EventHandlerContext<?>>();
    protected static final Logger                                     LOGGER     = ConsoleLogFactory.getLogger();
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> void addHandler(EventHandler<T> eventHandler)
    {
        Enum<? extends Event<T>> event = eventHandler.interest();
        if (((Event<?>) event).parallelLevel() == null)
        {
            throw new IllegalArgumentException("事件：" + event.getClass() + "的parallelLevel()方法缺少返回值");
        }
        EventHandlerContext<T> context = (EventHandlerContext<T>) contextMap.get(event);
        if (context == null)
        {
            switch (((Event<?>) event).parallelLevel())
            {
                case PAEALLEL:
                    context = new ParallelHandlerContextImpl<T>(event);
                    break;
                case SERIAL:
                    context = new SerialHandlerContextImpl<T>(event);
                    break;
                case ROWKEY_SERIAL:
                    context = new RowKeyHandlerContextImpl<T>(event);
                    break;
            }
            contextMap.put((Event<?>) event, context);
        }
        context.addHandler(eventHandler);
    }
    
    @Override
    public void start()
    {
        IdentityHashMap<Event<?>, EventHandlerContext<?>> copy_contextMap = new IdentityHashMap<Event<?>, EventHandlerContext<?>>(contextMap.size());
        copy_contextMap.putAll(contextMap);
        for (EventHandlerContext<?> context : copy_contextMap.values())
        {
            context.endAdd();
        }
        
    }
    
    @Override
    public EventContext post(EventContext eventContext)
    {
        EventHandlerContext<?> eventHandlerContext = contextMap.get(eventContext.getEvent());
        if (eventHandlerContext == null)
        {
            throw new IllegalArgumentException(StringUtil.format("不存在事件:{}的处理器", eventContext.getEvent()));
        }
        ((MoreContextInfo) eventContext).setMoreInfo(eventHandlerContext, this, eventQueue);
        eventQueue.offerAndSignal(eventContext);
        return eventContext;
    }
    
    @Override
    public EventContext post(Object data, Enum<? extends Event<?>> event)
    {
        if (((Event<?>) event).parallelLevel() == ParallelLevel.ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法不能接受并行度为：ROWKEY_SERIAL的事件");
        }
        EventContext applicationEvent = new NormalEventContext(data, event);
        post(applicationEvent);
        return applicationEvent;
    }
    
    @Override
    public EventContext post(Object data, Enum<? extends Event<?>> event, Object rowkey)
    {
        if (((Event<?>) event).parallelLevel() != ParallelLevel.ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法只能接受并行度为：ROWKEY_SERIAL的事件");
        }
        EventContext eventContext = new RowEventContextImpl(data, event, rowkey);
        post(eventContext);
        return eventContext;
    }
}
