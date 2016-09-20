package com.jfireframework.baseutil.concurrent;

import java.util.concurrent.locks.LockSupport;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class MPMCQueue<E>
{
    private volatile Node<E>    head;
    private volatile Node<E>    tail;
    private static final Unsafe unsafe           = ReflectUtil.getUnsafe();
    private static final long   tailOffset       = ReflectUtil.getFieldOffset("tail", MPMCQueue.class);
    private static final long   headOffset       = ReflectUtil.getFieldOffset("head", MPMCQueue.class);
    private volatile Waiter     headWaiter;
    private volatile Waiter     tailWaiter;
    private static final long   tailWaiterOffset = ReflectUtil.getFieldOffset("tailWaiter", MPMCQueue.class);
    
    static class Waiter
    {
        private final Thread      thread;
        // 通过HB关系来维持该属性的可见性
        private volatile Waiter   next;
        private volatile Waiter   pre;
        private volatile int      status;
        private static final int  WAITING   = 1;
        private static final int  CANCELED  = 2;
        private static final long preOffset = ReflectUtil.getFieldOffset("pre", Waiter.class);
        
        public Waiter(Thread thread)
        {
            this.thread = thread;
            status = WAITING;
        }
        
        public void setPre(Waiter pre)
        {
            unsafe.putObject(this, preOffset, pre);
        }
    }
    
    public MPMCQueue()
    {
        head = tail = new Node<E>(null);
        headWaiter = tailWaiter = new Waiter(null);
    }
    
    private Waiter enqueue()
    {
        Waiter newTail = new Waiter(Thread.currentThread());
        Waiter oldTail = tailWaiter;
        newTail.setPre(oldTail);
        if (unsafe.compareAndSwapObject(this, tailWaiterOffset, oldTail, newTail))
        {
            oldTail.next = newTail;
            return newTail;
        }
        for (;;)
        {
            oldTail = tailWaiter;
            newTail.setPre(oldTail);
            if (unsafe.compareAndSwapObject(this, tailWaiterOffset, oldTail, newTail))
            {
                oldTail.next = newTail;
                return newTail;
            }
        }
    }
    
    private void signalWaiter()
    {
        Waiter headNext = fetchNextWaiter();
        if (headNext != null)
        {
            LockSupport.unpark(headNext.thread);
        }
        
    }
    
    private Waiter fetchNextWaiter()
    {
        Waiter headNext;
        Waiter h = headWaiter;
        for (h = headWaiter; h != tailWaiter; h = headWaiter)
        {
            while ((headNext = h.next) == null && h == headWaiter)
            {
                ;
            }
            if (h == headWaiter)
            {
                return headNext;
            }
        }
        return null;
    }
    
    private static class Node<E>
    {
        private volatile E        value;
        private volatile Node<E>  next;
        private static final long valueOffset = ReflectUtil.getFieldOffset("value", Node.class);
        private static final long nextOffset  = ReflectUtil.getFieldOffset("next", Node.class);
        
        public Node(E value)
        {
            unsafe.putObject(this, valueOffset, value);
        }
        
        public E clear()
        {
            E origin = value;
            unsafe.putObject(this, Node.valueOffset, null);
            return origin;
        }
        
        public void unlink()
        {
            unsafe.putObject(this, Node.nextOffset, null);
        }
    }
    
    public void clear()
    {
    }
    
    public boolean offer(E o)
    {
        if (o == null)
        {
            throw new NullPointerException();
        }
        Node<E> insert_node = new Node<E>(o);
        Node<E> old = tail;
        if (unsafe.compareAndSwapObject(this, tailOffset, old, insert_node))
        {
            unsafe.putOrderedObject(old, Node.nextOffset, insert_node);
            return true;
        }
        do
        {
            old = tail;
            if (unsafe.compareAndSwapObject(this, tailOffset, old, insert_node))
            {
                unsafe.putOrderedObject(old, Node.nextOffset, insert_node);
                return true;
            }
        } while (true);
        
    }
    
    public void offerAndSignal(E o)
    {
        offer(o);
        signalWaiter();
    }
    
    public E poll()
    {
        startFromHead: //
        for (Node<E> h = head, next = h.next, t = tail; //
                h != t || h != (t = tail); //
                h = head, next = h.next)
        {
            if (next == null)
            {
                for (next = h.next; h == head; next = h.next)
                {
                    if (next == null && (next = h.next) == null)
                    {
                        continue;
                    }
                    else
                    {
                        ;
                    }
                    if (unsafe.compareAndSwapObject(this, headOffset, h, next))
                    {
                        h.unlink();
                        return next.clear();
                    }
                    else
                    {
                        continue startFromHead;
                    }
                }
            }
            else
            {
                if (unsafe.compareAndSwapObject(this, headOffset, h, next))
                {
                    h.unlink();
                    return next.clear();
                }
                else
                {
                    ;
                }
            }
        }
        return null;
    }
    
    /**
     * 阻塞的获取一个元素。如果没有元素，则一直阻塞等待
     * 
     * @return
     */
    public E take()
    {
        E result = poll();
        Waiter self;
        if (result == null)
        {
            self = enqueue();
            Waiter headNext;
            do
            {
                headNext = headWaiter.next;
                if (self == headNext)
                {
                    result = poll();
                    if (result == null)
                    {
                        LockSupport.park();
                        if (Thread.currentThread().isInterrupted())
                        {
                            self.status = Waiter.CANCELED;
                            return null;
                        }
                    }
                    else
                    {
                        headWaiter = self;
                        headNext = fetchNextWaiter();
                        if (headNext != null)
                        {
                            LockSupport.unpark(headNext.thread);
                        }
                        return result;
                    }
                }
                else
                {
                    LockSupport.park();
                }
            } while (true);
        }
        return result;
    }
    
}
