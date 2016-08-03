package com.jfireframework.context.event.impl;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.jfireframework.baseutil.disruptor.Disruptor;
import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.waitstrategy.BlockWaitStrategy;
import com.jfireframework.baseutil.disruptor.waitstrategy.ParkWaitStrategy;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;
import com.jfireframework.context.event.ApplicationEvent;
import com.jfireframework.context.event.EventHandler;
import com.jfireframework.context.event.EventPublisher;

@Resource(name = "eventPublisher")
public class EventPublisherImpl implements EventPublisher
{
    private Disruptor          disruptor;
    @Resource
    private List<EventHandler> _handlers  = new LinkedList<EventHandler>();
    private String             strategy   = "park";
    private int                capacity   = 0;
    private int                threadSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
    
    @PostConstruct
    public void init()
    {
        if (capacity <= 0)
        {
            return;
        }
        IdentityHashMap<Class<?>, Integer> idMap = new IdentityHashMap<Class<?>, Integer>();
        EventHandler[][][] handlers = transfer(idMap);
        int tmp = 1;
        while (tmp < capacity)
        {
            tmp <<= 1;
        }
        capacity = tmp;
        Thread[] threads = new Thread[threadSize];
        EntryAction[] actions = new EntryAction[threads.length];
        for (int i = 0; i < actions.length; i++)
        {
            actions[i] = new EventAction(handlers, idMap);
        }
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(actions[i]);
        }
        WaitStrategy waitStrategy = new BlockWaitStrategy();
        if ("park".equals(strategy))
        {
            waitStrategy = new ParkWaitStrategy(threads, actions);
        }
        else if ("block".equals(strategy))
        {
            waitStrategy = new BlockWaitStrategy();
        }
        disruptor = new Disruptor(1024, actions, threads, waitStrategy);
    }
    
    private EventHandler[][][] transfer(IdentityHashMap<Class<?>, Integer> map)
    {
        int sequence = 0;
        for (EventHandler each : _handlers)
        {
            if (map.containsKey(each.type().getClass()))
            {
                continue;
            }
            map.put(each.type().getClass(), sequence++);
        }
        int[][] count = new int[map.size()][];
        for (int i = 0; i < count.length; i++)
        {
            count[i] = new int[0];
        }
        for (EventHandler each : _handlers)
        {
            sequence = map.get(each.type().getClass());
            int index = each.type().ordinal();
            if (count[sequence].length <= index)
            {
                int[] tmp = new int[index + 1];
                System.arraycopy(count[sequence], 0, tmp, 0, count[sequence].length);
                count[sequence] = tmp;
            }
            count[sequence][index] += 1;
        }
        EventHandler[][][] handlers = new EventHandler[map.size()][][];
        for (int i = 0; i < map.size(); i++)
        {
            handlers[i] = new EventHandler[count[i].length][];
            for (int j = 0; j < handlers[i].length; j++)
            {
                handlers[i][j] = new EventHandler[count[i][j]];
            }
        }
        for (EventHandler each : _handlers)
        {
            sequence = map.get(each.type().getClass());
            int index = each.type().ordinal();
            EventHandler[] tmp = handlers[sequence][index];
            for (int j = 0; j < tmp.length; j++)
            {
                if (tmp[j] == null)
                {
                    tmp[j] = each;
                    break;
                }
            }
        }
        return handlers;
    }
    
    @Override
    public void publish(Object data, Enum<?> type)
    {
        if (disruptor == null)
        {
            throw new NullPointerException("请正确初始化事件发布器");
        }
        ApplicationEvent applicationEvent = new ApplicationEvent();
        applicationEvent.setData(data);
        applicationEvent.setType(type);
        disruptor.publish(applicationEvent);
    }
    
}
