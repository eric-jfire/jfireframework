package com.jfireframework.schedule.timer.bucket.impl;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.schedule.timer.ExpireHandler;
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
    
    public AbstractBucket(ExpireHandler expireHandler, Timer timer)
    {
        this.expireHandler = expireHandler;
        this.timer = timer;
    }
    
    @Override
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
    
}
