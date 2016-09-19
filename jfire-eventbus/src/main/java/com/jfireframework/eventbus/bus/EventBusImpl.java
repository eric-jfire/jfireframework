package com.jfireframework.eventbus.bus;

import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.BlockWaitStrategy;
import com.jfireframework.baseutil.disruptor.waitstrategy.ParkWaitStrategy;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.event.EventType;
import com.jfireframework.eventbus.handler.*;

import java.util.IdentityHashMap;

public class EventBusImpl implements EventBus {
    private final int                                               capacity;
    private final int                                               threadSize;
    private final String                                            strategy;
    private final IdentityHashMap<Event<?>, EventHandlerContext<?>> contextMap = new IdentityHashMap<Event<?>, EventHandlerContext<?>>();
    private Disruptor                                               disruptor;
    
    public EventBusImpl(int capacity)
    {
        this(capacity, Runtime.getRuntime().availableProcessors() * 2 + 1, "park");
    }
    
    public EventBusImpl(int capacity, int threadSize, String strategy)
    {
        if (capacity <= 0)
        {
            throw new IllegalArgumentException();
        }
        int tmp = 1;
        while (tmp < capacity)
        {
            tmp <<= 1;
        }
        capacity = tmp;
        this.capacity = capacity;
        this.threadSize = threadSize;
        this.strategy = strategy;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> void addHandler(EventHandler<T> eventHandler)
    {
        Enum<? extends Event<T>> event = eventHandler.interest();
        EventHandlerContext<T> context = (EventHandlerContext<T>) contextMap.get(event);
        if (context == null)
        {
            switch (((Event<?>) event).type())
            {
                case PAEALLEL:
                    context = new ParallelHandlerContextImpl<T>(event);
                    break;
                case SERIAL:
                    context = new SerialHandlerContextImpl<T>(event);
                    break;
                case ROWID_SERIAL:
                    context = new RowidSerialHandlerContextImpl<T>(event);
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
        EntryAction[] actions = new EntryAction[threadSize];
        for (int i = 0; i < actions.length; i++)
        {
            actions[i] = new EventAction(copy_contextMap);
        }
        Thread[] threads = new Thread[threadSize];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(actions[i]);
        }
        WaitStrategy waitStrategy = new BlockWaitStrategy();
        if ("park".equals(strategy))
        {
            waitStrategy = new ParkWaitStrategy(threads);
        }
        else if ("block".equals(strategy))
        {
            waitStrategy = new BlockWaitStrategy();
        }
        disruptor = new Disruptor(capacity, actions, threads, waitStrategy);
    }
    
    @Override
    public void stop()
    {
        disruptor.stop();
    }
    
    @Override
    public ApplicationEvent post(ApplicationEvent event)
    {
        disruptor.publish(event);
        return event;
    }
    
    @Override
    public ApplicationEvent post(Object data, Enum<? extends Event<?>> event)
    {
        if (((Event<?>) event).type() == EventType.ROWID_SERIAL)
        {
            throw new IllegalArgumentException();
        }
        ApplicationEvent applicationEvent = new ApplicationEvent(data, event, -1);
        post(applicationEvent);
        return applicationEvent;
    }
}