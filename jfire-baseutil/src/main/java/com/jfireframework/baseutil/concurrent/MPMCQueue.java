package com.jfireframework.baseutil.concurrent;

import java.util.concurrent.TimeUnit;
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
    private static final long   headWaiterOffset = ReflectUtil.getFieldOffset("headWaiter", MPMCQueue.class);
    private static final long   tailWaiterOffset = ReflectUtil.getFieldOffset("tailWaiter", MPMCQueue.class);
    private final boolean       fair;
    
    static class Waiter
    {
        private final Thread     thread;
        // 通过HB关系来维持该属性的可见性
        private volatile Waiter  next;
        private volatile int     status;
        private static final int WAITING  = 1;
        private static final int CANCELED = 2;
        
        public Waiter(Thread thread)
        {
            this.thread = thread;
            status = WAITING;
        }
        
    }
    
    public MPMCQueue()
    {
        this(false);
    }
    
    public MPMCQueue(boolean fair)
    {
        head = tail = new Node<E>(null);
        headWaiter = tailWaiter = new Waiter(null);
        this.fair = fair;
    }
    
    private Waiter enqueue()
    {
        Waiter newTail = new Waiter(Thread.currentThread());
        Waiter oldTail = tailWaiter;
        if (unsafe.compareAndSwapObject(this, tailWaiterOffset, oldTail, newTail))
        {
            oldTail.next = newTail;
            return newTail;
        }
        for (;;)
        {
            oldTail = tailWaiter;
            if (unsafe.compareAndSwapObject(this, tailWaiterOffset, oldTail, newTail))
            {
                oldTail.next = newTail;
                return newTail;
            }
        }
    }
    
    private Waiter findNextWaiter(Waiter waiter)
    {
        if (waiter == tailWaiter)
        {
            return null;
        }
        Waiter next = waiter.next;
        if (next != null)
        {
            return next;
        }
        while ((next = waiter.next) == null)
        {
            ;
        }
        return next;
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
        Waiter h = headWaiter;
        Waiter next = findNextWaiter(h);
        if (next != null)
        {
            LockSupport.unpark(next.thread);
        }
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
    
    private E fairTake(long time, TimeUnit unit)
    {
        if (headWaiter == tailWaiter)
        {
            E result = poll();
            if (result == null)
            {
                if (time == -1)
                {
                    return enqueueAndWait();
                }
                else
                {
                    return enqueueAndWait(time, unit);
                }
            }
            else
            {
                return result;
            }
        }
        else
        {
            if (time == -1)
            {
                return enqueueAndWait();
            }
            else
            {
                return enqueueAndWait(time, unit);
            }
        }
    }
    
    private E enqueueAndWait(long time, TimeUnit unit)
    {
        E result;
        Waiter self = enqueue();
        long nanos = unit.toNanos(time);
        long t0 = System.nanoTime();
        do
        {
            // head之后的next是本线程设置的，所以这里直接获取。可以读取到就意味着确实是head节点的后继节点
            if (self == headWaiter.next)
            {
                result = poll();
                if (result == null)
                {
                    if (nanos < 1000)
                    {
                        for (int i = 0; i < 1000; i++)
                        {
                            ;
                        }
                    }
                    else
                    {
                        LockSupport.parkNanos(nanos);
                    }
                    nanos -= System.nanoTime() - t0;
                    if (nanos < 0)
                    {
                        cancelWaiter(self);
                        return null;
                    }
                    t0 = System.nanoTime();
                }
                else
                {
                    headWaiter = self;
                    unparkNext(self);
                    return result;
                }
            }
            else
            {
                if (nanos < 1000)
                {
                    for (int i = 0; i < 1000; i++)
                    {
                        ;
                    }
                }
                else
                {
                    LockSupport.parkNanos(nanos);
                }
                nanos -= System.nanoTime() - t0;
                if (nanos < 0)
                {
                    cancelWaiter(self);
                    return null;
                }
                t0 = System.nanoTime();
            }
            if (Thread.currentThread().isInterrupted())
            {
                cancelWaiter(self);
                return null;
            }
        } while (true);
    }
    
    private E enqueueAndWait()
    {
        E result;
        Waiter self = enqueue();
        do
        {
            if (self == headWaiter.next)
            {
                result = poll();
                if (result == null)
                {
                    LockSupport.park();
                }
                else
                {
                    headWaiter = self;
                    unparkNext(self);
                    return result;
                }
            }
            else
            {
                LockSupport.park();
            }
            if (Thread.currentThread().isInterrupted())
            {
                cancelWaiter(self);
                return null;
            }
        } while (true);
    }
    
    private void cancelWaiter(Waiter waiter)
    {
        waiter.status = Waiter.CANCELED;
        Waiter h = headWaiter;
        if (h.next == waiter && casHead(h, waiter))
        {
            unparkNext(waiter);
        }
    }
    
    private E unfairTake(long time, TimeUnit unit)
    {
        E result = poll();
        if (result == null)
        {
            if (time == -1)
            {
                return enqueueAndWait();
            }
            else
            {
                return enqueueAndWait(time, unit);
            }
        }
        else
        {
            return result;
        }
    }
    
    /**
     * 阻塞的获取一个元素。如果没有元素，则一直阻塞等待
     * 
     * @return
     */
    public E take()
    {
        if (fair)
        {
            return fairTake(-1, null);
        }
        else
        {
            return unfairTake(-1, null);
        }
    }
    
    public E take(long time, TimeUnit unit)
    {
        if (fair)
        {
            return fairTake(time, unit);
        }
        else
        {
            return unfairTake(time, unit);
        }
    }
    
    /**
     * 唤醒后续节点。注意，这里的入口head节点就是当前的headWaiter
     * 
     * @param head
     */
    private void unparkNext(Waiter head)
    {
        Waiter next = findNextWaiter(head);
        if (next == null)
        {
            return;
        }
        // 如果后续节点状态此时是等待，则直接唤醒
        else if (next.status == Waiter.WAITING)
        {
            LockSupport.unpark(next.thread);
            return;
        }
        else
        {
            do
            {
                Waiter pred;
                do
                {
                    pred = next;
                    next = findNextWaiter(pred);
                } while (next != null && next.status == Waiter.CANCELED && head == headWaiter);
                /**
                 * 在头结点未变化的情况下，找到距离头节点最近的一个非cancel状态节点。
                 */
                /**
                 * 如果头节点发生了变化，意味着其他线程取得了控制权，则后续行为由其他线程完成。本线程可以退出了
                 */
                if (head == headWaiter && casHead(head, pred))
                {
                    /**
                     * 如果成功的设置了新的头结点。则尝试唤醒头结点的后继节点
                     */
                    head = pred;
                    next = findNextWaiter(pred);
                    if (next == null)
                    {
                        return;
                    }
                    else if (next.status == Waiter.WAITING)
                    {
                        LockSupport.unpark(next.thread);
                    }
                    else
                    {
                        continue;
                    }
                }
                else
                {
                    return;
                }
            } while (true);
        }
    }
    
    private boolean casHead(Waiter origin, Waiter newHead)
    {
        return unsafe.compareAndSwapObject(this, headWaiterOffset, origin, newHead);
    }
    
}
