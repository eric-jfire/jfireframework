package com.jfireframework.eventbus.handler;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.eventbus.event.ApplicationEvent;
import com.jfireframework.eventbus.event.Event;

/**
 * Created by 林斌 on 2016/9/11.
 */
public class RowidSerialHandlerContextImpl<T> extends AbstractEventHandlerContext<T>
{
    public RowidSerialHandlerContextImpl(Enum<? extends Event<T>> event)
    {
        super(event);
    }
    
    private ConcurrentHashMap<Integer, MPSCQueue<ApplicationEvent>> map = new ConcurrentHashMap<Integer, MPSCQueue<ApplicationEvent>>(128);
    
    @Override
    public void handle(ApplicationEvent applicationEvent)
    {
        do
        {
            int id = applicationEvent.id();
            MPSCQueue<ApplicationEvent> queue = new MPSCQueue<ApplicationEvent>();
            queue.offer(applicationEvent);
            MPSCQueue<ApplicationEvent> pre = map.putIfAbsent(id, queue);
            if (pre == null)
            {
                while ((applicationEvent = queue.poll()) != null)
                {
                    _handle(applicationEvent);
                }
                map.remove(id);
                break;
            }
            else
            {
                pre.offer(applicationEvent);
                // 由于不会往map中放入相同的queue，所以如果判断为真，则意味着数据可以被处理
                if (map.get(id) == pre)
                {
                    break;
                }
                else
                {
                    applicationEvent = pre.poll();
                    // 如果还存在数据，这些数据不会被处理，所以进入循环，发起相同的程序流程
                    if (applicationEvent != null)
                    {
                        continue;
                    }
                    else
                    {
                        break;
                    }
                }
            }
        } while (true);
    }
    
    private void _handle(ApplicationEvent applicationEvent)
    {
        for (EventHandler<T> each : handlers)
        {
            each.handle(applicationEvent);
        }
        applicationEvent.signal();
    }
    
}
