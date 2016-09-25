package com.jfireframework.eventbus.forkjoin;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import com.jfireframework.eventbus.Print;
import com.jfireframework.eventbus.bus.EventBus;
import com.jfireframework.eventbus.event.Event;
import com.jfireframework.eventbus.eventcontext.EventContext;
import com.jfireframework.eventbus.handler.EventHandler;

public class PrintForkHandler implements EventHandler<Print>
{
    private boolean fork = true;
    
    public PrintForkHandler(boolean fork)
    {
        this.fork = fork;
    }
    
    @Override
    public int getOrder()
    {
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void handle(EventContext eventContext, EventBus eventBus)
    {
        Queue<String> queue = (Queue<String>) eventContext.getEventData();
        if (fork)
        {
            if (queue.size() > 2)
            {
                int left = queue.size() / 2;
                Queue<String> one = new ArrayBlockingQueue<String>(100);
                for (int i = 0; i < left; i++)
                {
                    one.offer(queue.poll());
                }
                EventContext leftTask = eventBus.post(one, Print.one);
                one = new ArrayBlockingQueue<String>(100);
                for (; queue.isEmpty() == false;)
                {
                    one.offer(queue.poll());
                }
                EventContext rightTask = eventBus.post(one, Print.one);
                leftTask.join();
                rightTask.join();
            }
            else
            {
                while (queue.isEmpty() == false)
                {
                    System.out.println(Thread.currentThread().getName() + "打印：" + queue.poll());
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        else
        {
            while (queue.isEmpty() == false)
            {
                System.out.println(Thread.currentThread().getName() + "打印：" + queue.poll());
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public Enum<? extends Event<Print>> interest()
    {
        return Print.one;
    }
    
}
