package com.jfireframework.schedule.timer;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.schedule.trigger.Trigger;
import sun.misc.Unsafe;

public class Bucket
{
    class Node
    {
        final Trigger trigger;
        volatile Node next;
        
        public Node(Trigger trigger)
        {
            this.trigger = trigger;
        }
    }
    
    private volatile Node       head;
    private static final Unsafe unsafe = ReflectUtil.getUnsafe();
    private static final long   off    = ReflectUtil.getFieldOffset("head", Bucket.class);
    private final ExpireHandler expireHandler;
    
    public Bucket(ExpireHandler expireHandler)
    {
        this.expireHandler = expireHandler;
    }
    
    public void add(Trigger trigger)
    {
        Node node = new Node(trigger);
        node.next = head;
        if (unsafe.compareAndSwapObject(this, off, head, node))
        {
            return;
        }
        do
        {
            node.next = head;
            if (unsafe.compareAndSwapObject(this, off, head, node))
            {
                return;
            }
        } while (true);
    }
    
    public void expire()
    {
        long currentTime = System.currentTimeMillis();
        for (Node pred = head, now = pred; now != null;)
        {
            Trigger trigger = now.trigger;
            long left = trigger.deadline() - currentTime;
            if (left < 0)
            {
                expireHandler.expire(trigger);
                pred = now;
                now = now.next;
            }
            else
            {
                now = now.next;
                pred.next = now;
            }
        }
    }
}
