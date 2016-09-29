package com.jfireframework.schedule.timer.bucket.impl;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.schedule.handler.ExpireHandler;
import com.jfireframework.schedule.timer.Timer;
import com.jfireframework.schedule.timer.bucket.Bucket;
import com.jfireframework.schedule.trigger.Trigger;
import sun.misc.Unsafe;

public abstract class AbstractBucket implements Bucket
{
    class Node
    {
        final Trigger trigger;
        // 全程通过hb关系保证可见
        Node          next;
        
        public Node(Trigger trigger)
        {
            this.trigger = trigger;
        }
    }
    
    protected volatile Node       head;
    protected static final Unsafe unsafe = ReflectUtil.getUnsafe();
    protected static final long   off    = ReflectUtil.getFieldOffset("head", AbstractBucket.class);
    protected final ExpireHandler expireHandler;
    protected final Timer         timer;
    protected final long          tickDuration_mills;
    
    public AbstractBucket(ExpireHandler expireHandler, Timer timer, long tickDuration_mills)
    {
        this.expireHandler = expireHandler;
        this.timer = timer;
        this.tickDuration_mills = tickDuration_mills;
    }
    
    @Override
    public void add(Trigger trigger)
    {
        if (trigger.isCanceled())
        {
            return;
        }
        Node node = new Node(trigger);
        Node h = head;
        node.next = h;
        if (unsafe.compareAndSwapObject(this, off, h, node))
        {
            return;
        }
        do
        {
            h = head;
            node.next = h;
            if (unsafe.compareAndSwapObject(this, off, h, node))
            {
                return;
            }
        } while (true);
    }
    
    protected Node takeHead()
    {
        Node h = head;
        if (unsafe.compareAndSwapObject(this, off, h, null))
        {
            return h;
        }
        do
        {
            h = head;
            if (unsafe.compareAndSwapObject(this, off, h, null))
            {
                return h;
            }
        } while (true);
    }
}
