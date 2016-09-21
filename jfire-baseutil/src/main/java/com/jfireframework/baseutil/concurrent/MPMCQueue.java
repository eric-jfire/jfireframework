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
    private static final long   tailWaiterOffset = ReflectUtil.getFieldOffset("tailWaiter", MPMCQueue.class);
    private static final long   headWaiterOffset = ReflectUtil.getFieldOffset("headWaiter", MPMCQueue.class);
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
    
    private void signalWaiter()
    {
        Waiter headNext = findHeadNext();
        if (headNext != null)
        {
            LockSupport.unpark(headNext.thread);
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
    
    private Waiter findHeadNext()
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
        Waiter headNext;
        long nanos = unit.toNanos(time);
        long t0 = System.nanoTime();
        do
        {
            // head之后的next是本线程设置的，所以这里直接获取。可以读取到就意味着确实是head节点的后继节点
            Waiter h = headWaiter;
            headNext = h.next;
            if (self == headNext)
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
                        return null;
                    }
                    t0 = System.nanoTime();
                }
                else
                {
                    /**
                     * 此时不能把h.next设置为null。否则如果其他线程正在执行findNextWaiter方法。
                     * 由于始终next都是null，就会成为死循环。
                     */
                    
                    unparkNext(h);
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
                    return null;
                }
                t0 = System.nanoTime();
            }
            if (Thread.currentThread().isInterrupted())
            {
                self.status = Waiter.CANCELED;
                headNext = headWaiter.next;
                if (headNext == self)
                {
                    unparkNext(h);
                    return null;
                }
                else
                {
                    return null;
                }
            }
        } while (true);
    }
    
    private E enqueueAndWait()
    {
        E result;
        Waiter self = enqueue();
        Waiter headNext;
        do
        {
            // head之后的next是本线程设置的，所以这里直接获取。可以读取到就意味着确实是head节点的后继节点
            Waiter h = headWaiter;
            headNext = h.next;
            if (self == headNext)
            {
                result = poll();
                if (result == null)
                {
                    LockSupport.park();
                }
                else
                {
                    /**
                     * 此时不能把h.next设置为null。否则如果其他线程正在执行findNextWaiter方法。
                     * 由于始终next都是null，就会成为死循环。
                     */
                    
                    unparkNext(h);
                    return result;
                }
            }
            else
            {
                LockSupport.park();
            }
            if (Thread.currentThread().isInterrupted())
            {
                self.status = Waiter.CANCELED;
                headNext = headWaiter.next;
                if (headNext == self)
                {
                    unparkNext(h);
                    return null;
                }
                else
                {
                    return null;
                }
            }
        } while (true);
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
    
    private void unparkNext(Waiter head)
    {
        Waiter self = head.next;
        do
        {
            Waiter next;
            for (next = findNextWaiter(self); next != null; next = findNextWaiter(self))
            {
                if (next.status == Waiter.CANCELED)
                {
                    self = next;
                }
                else
                {
                    break;
                }
            }
            /**
             * 1.上下代码之间，next节点可能就进入了cancel状态
             * 2.或者循环退出是因为next为null，但是上下代码之间，又有新节点加入
             */
            /**
             * cas的原因是因为unparknext方法可能会被多个线程进入。假设线程1是head之后的节点，在完成cas后失去cpu。
             * 此时第二个线程获得cpu，因为head节点变化了，故而也进入了unparknext。或者自己进入了canceled状态。
             * 因此会有多个线程进入unparkNext。
             * 
             */
            if (casHeadWaiter(head, self))
            {
                /**
                 * 在头结点重新确定之后，可能存在情况有：
                 * 1 原有的next节点在cas之前就进入cancel状态
                 * 2 原有的next节点在cas之后进入cancel状态.此时该next节点可能也会进入unparkNext这个方法内
                 * 3.1 原本没有next节点，现在有了next节点，且next节点状态正常
                 * 3.2 原本没有next节点，现在有了next节点，但next节点进入cancel状态
                 * 4 原本的next节点状态正常
                 * 5 原本的next节点被因为其他线程放入数据而被唤醒。此时该next节点也会进入unparkNext方法内
                 * 针对情况1，再次循环流程
                 * 针对情况2，重新发起循环流程，
                 * 针对情况3.1， 唤醒next节点的线程并且退出循环
                 * 针对情况3.2，再次循环流程
                 * 针对情况4，唤醒next节点线程 并且退出循环
                 * 针对情况5，再次循环
                 * 
                 */
                if (next != null || (next = findNextWaiter(self)) != null)
                {
                    if (next.status == Waiter.CANCELED)
                    {
                        continue;
                    }
                    else
                    {
                        LockSupport.unpark(next.thread);
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        } while (true);
    }
    
    private boolean casHeadWaiter(Waiter head, Waiter newHead)
    {
        return unsafe.compareAndSwapObject(this, headWaiterOffset, head, newHead);
    }
}
