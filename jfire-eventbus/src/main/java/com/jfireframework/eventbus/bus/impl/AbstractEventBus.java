package com.jfireframework.eventbus.bus.impl;

import java.util.IdentityHashMap;
import com.jfireframework.baseutil.concurrent.MPMCQueue;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.EventConfig;
import com.jfireframework.eventbus.event.ParallelLevel;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.eventcontext.ReadWriteEventContext;
import com.jfireframework.eventbus.eventcontext.impl.NormalEventContext;
import com.jfireframework.eventbus.eventcontext.impl.ReadWriteEventContextImpl;
import com.jfireframework.eventbus.eventcontext.impl.RowEventContextImpl;
import com.jfireframework.eventbus.executor.EventHandlerExecutor;
import com.jfireframework.eventbus.executor.EventSerialHandlerExecutor;
import com.jfireframework.eventbus.executor.ParallelHandlerExecutor;
import com.jfireframework.eventbus.executor.ReadWriteExecutor;
import com.jfireframework.eventbus.executor.RowKeyHandlerExecutor;
import com.jfireframework.eventbus.executor.TypeRowKeySerialHandlerExecutor;
import com.jfireframework.eventbus.executor.TypeSerialHandlerExecutor;
import com.jfireframework.eventbus.handler.EventHandler;
import com.jfireframework.eventbus.handler.HandlerCombination;

public abstract class AbstractEventBus implements EventBus
{
    protected final MPMCQueue<EventContext<?>>                                         eventQueue          = new MPMCQueue<EventContext<?>>();
    protected final IdentityHashMap<Enum<? extends EventConfig>, HandlerCombination>   combinationMap      = new IdentityHashMap<Enum<? extends EventConfig>, HandlerCombination>();
    protected final IdentityHashMap<Enum<? extends EventConfig>, EventHandlerExecutor> executorMap         = new IdentityHashMap<Enum<? extends EventConfig>, EventHandlerExecutor>();
    private final IdentityHashMap<Class<?>, EventHandlerExecutor>                      typeSerialMap       = new IdentityHashMap<Class<?>, EventHandlerExecutor>();
    private final IdentityHashMap<Class<?>, EventHandlerExecutor>                      typeRowKeySerialMap = new IdentityHashMap<Class<?>, EventHandlerExecutor>();
    private final IdentityHashMap<Class<?>, EventHandlerExecutor>                      readWriteMap        = new IdentityHashMap<Class<?>, EventHandlerExecutor>();
    protected static final Logger                                                      LOGGER              = ConsoleLogFactory.getLogger();
    
    @Override
    public void addHandler(EventHandler<?, ?> eventHandler)
    {
        Enum<? extends EventConfig> event = eventHandler.interest();
        Verify.notNull(((EventConfig) event).parallelLevel(), "事件：{}的parallelLevel()方法缺少返回值", event.getClass());
        HandlerCombination combination = combinationMap.get(event);
        if (combination == null)
        {
            combination = new HandlerCombination();
            combinationMap.put(event, combination);
        }
        combination.addHandler(eventHandler);
        if (executorMap.containsKey(event) == false)
        {
            EventHandlerExecutor executor = null;
            switch (((EventConfig) event).parallelLevel())
            {
                case PAEALLEL:
                    executor = new ParallelHandlerExecutor();
                    break;
                case ROWKEY_SERIAL:
                    executor = new RowKeyHandlerExecutor();
                    break;
                case EVENT_SERIAL:
                    executor = new EventSerialHandlerExecutor();
                    break;
                case TYPE_SERIAL:
                    executor = typeSerialMap.get(event.getClass());
                    if (executor == null)
                    {
                        executor = new TypeSerialHandlerExecutor();
                        typeSerialMap.put(event.getClass(), executor);
                    }
                    break;
                case TYPE_ROWKEY_SERIAL:
                    executor = typeRowKeySerialMap.get(event.getClass());
                    if (executor == null)
                    {
                        executor = new TypeRowKeySerialHandlerExecutor();
                        typeRowKeySerialMap.put(event.getClass(), executor);
                    }
                    break;
                case RW_EVENT_READ:
                    // 直接走到下面，因为两个的逻辑是一样的
                    ;
                case RW_EVENT_WRITE:
                    executor = readWriteMap.get(event.getClass());
                    if (executor == null)
                    {
                        executor = new ReadWriteExecutor();
                        readWriteMap.put(event.getClass(), executor);
                    }
                    break;
            }
            executorMap.put(event, executor);
        }
    }
    
    @Override
    public void start()
    {
        for (HandlerCombination each : combinationMap.values())
        {
            each.sort();
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event)
    {
        if (((EventConfig) event).parallelLevel() == ParallelLevel.ROWKEY_SERIAL || ((EventConfig) event).parallelLevel() == ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法不能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
        EventConfig config = (EventConfig) event;
        if (config.parallelLevel() == ParallelLevel.RW_EVENT_READ)
        {
            EventContext<T> eventContext = new ReadWriteEventContextImpl(ReadWriteEventContext.READ, data, event, combinationMap.get(event).combination(), executorMap.get(event), this);
            post(eventContext);
            return eventContext;
        }
        else if (config.parallelLevel() == ParallelLevel.RW_EVENT_WRITE)
        {
            EventContext<T> eventContext = new ReadWriteEventContextImpl(ReadWriteEventContext.WRITE, data, event, combinationMap.get(event).combination(), executorMap.get(event), this);
            post(eventContext);
            return eventContext;
        }
        else
        {
            
            EventContext<T> eventContext = new NormalEventContext(data, event, combinationMap.get(event).combination(), executorMap.get(event), this);
            post(eventContext);
            return eventContext;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> EventContext<T> post(Object data, Enum<? extends EventConfig> event, Object rowkey)
    {
        if (((EventConfig) event).parallelLevel() != ParallelLevel.ROWKEY_SERIAL && ((EventConfig) event).parallelLevel() != ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法只能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
        EventContext<T> eventContext = new RowEventContextImpl(data, event, combinationMap.get(event).combination(), executorMap.get(event), this, rowkey);
        post(eventContext);
        return eventContext;
    }
    
    @Override
    public void post(EventContext<?> eventContext)
    {
        eventQueue.offerAndSignal(eventContext);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event, Object rowkey)
    {
        if (((EventConfig) event).parallelLevel() != ParallelLevel.ROWKEY_SERIAL && ((EventConfig) event).parallelLevel() != ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法只能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
        EventContext<T> eventContext = new RowEventContextImpl(data, event, combinationMap.get(event).combination(), executorMap.get(event), this, rowkey);
        eventContext.executor().handle(eventContext, this);
        eventContext.await();
        return eventContext;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> EventContext<T> syncPost(Object data, Enum<? extends EventConfig> event)
    {
        if (((EventConfig) event).parallelLevel() == ParallelLevel.ROWKEY_SERIAL || ((EventConfig) event).parallelLevel() == ParallelLevel.TYPE_ROWKEY_SERIAL)
        {
            throw new IllegalArgumentException("该方法不能接受并行度为：ROWKEY_SERIAL或TYPE_ROWKEY_SERIAL的事件");
        }
        EventContext<T> eventContext = new NormalEventContext(data, event, combinationMap.get(event).combination(), executorMap.get(event), this);
        eventContext.executor().handle(eventContext, this);
        eventContext.await();
        return eventContext;
    }
}
