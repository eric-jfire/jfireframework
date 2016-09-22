package com.jfireframework.baseutil.concurrent;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

/**
 * Created by 林斌 on 2016/9/10.
 */
public class MPSCQueue<E>
{
    private volatile MPSCNode<E> head;
    private volatile MPSCNode<E> tail;
    private static final Unsafe  unsafe = ReflectUtil.getUnsafe();
    private static final long    offset = ReflectUtil.getFieldOffset("tail", MPSCQueue.class);
    
    public MPSCQueue()
    {
        head = tail = new MPSCNode<E>(null);
    }
    
    public E poll()
    {
        if (head != tail)
        {
            MPSCNode<E> nextNode = head.next;
            if (nextNode != null)
            {
                unsafe.putObject(head, MPSCNode.nextOff, null);
                head = nextNode;
                return nextNode.value;
            }
            while ((nextNode = head.next) == null)
            {
                ;
            }
            unsafe.putObject(head, MPSCNode.nextOff, null);
            head = nextNode;
            return nextNode.value;
        }
        else
        {
            return null;
        }
    }
    
    private final static class MPSCNode<E>
    {
        private final E              value;
        private volatile MPSCNode<E> next;
        private static final long    nextOff = ReflectUtil.getFieldOffset("next", MPSCNode.class);
        
        public MPSCNode(E value)
        {
            this.value = value;
        }
        
    }
    
    public boolean isEmpty()
    {
        return head == tail;
    }
    
    public void clear()
    {
        head = tail;
    }
    
    public boolean offer(E o)
    {
        if (o == null)
        {
            throw new NullPointerException();
        }
        
        MPSCNode<E> new_tail = new MPSCNode<E>(o);
        MPSCNode<E> old_tail = tail;
        if (unsafe.compareAndSwapObject(this, offset, old_tail, new_tail))
        {
            old_tail.next = new_tail;
            return true;
        }
        while (true)
        {
            old_tail = tail;
            if (unsafe.compareAndSwapObject(this, offset, old_tail, new_tail))
            {
                old_tail.next = new_tail;
                return true;
            }
        }
    }
    
}
