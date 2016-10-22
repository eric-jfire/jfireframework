package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class MPMCQueue<E> implements Queue<E>
{
    private volatile Node<E>    head;
    private volatile Node<E>    tail;
    private static final Unsafe unsafe     = ReflectUtil.getUnsafe();
    private static final long   tailOffset = ReflectUtil.getFieldOffset("tail", MPMCQueue.class);
    private static final long   headOffset = ReflectUtil.getFieldOffset("head", MPMCQueue.class);
    private final boolean       fair;
    private Sync<E>             sync       = new Sync<E>() {
                                               
                                               @Override
                                               protected E pull()
                                               {
                                                   return poll();
                                               }
                                           };
    
    public MPMCQueue()
    {
        this(false);
    }
    
    public MPMCQueue(boolean fair)
    {
        head = tail = new Node<E>(null);
        this.fair = fair;
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
        
        public void orderSetNext(Node<E> next)
        {
            unsafe.putOrderedObject(this, nextOffset, next);
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
            old.orderSetNext(insert_node);
            return true;
        }
        do
        {
            old = tail;
            if (unsafe.compareAndSwapObject(this, tailOffset, old, insert_node))
            {
                old.orderSetNext(insert_node);
                return true;
            }
        } while (true);
        
    }
    
    public void offerAndSignal(E o)
    {
        offer(o);
        sync.signal();
    }
    
    public E mastPull()
    {
        startFromHead: //
        for (Node<E> h = head, next = h.next; //
        ; //
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
        if (sync.isThreadOnWaiting() == false)
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
        return sync.take(time, unit);
    }
    
    private E enqueueAndWait()
    {
        return sync.take();
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
    
    @Override
    public int size()
    {
        return -1;
    }
    
    @Override
    public boolean isEmpty()
    {
        return head == tail;
    }
    
    @Override
    public boolean contains(Object o)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterator<E> iterator()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <T> T[] toArray(T[] a)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean add(E e)
    {
        offer(e);
        return true;
    }
    
    @Override
    public E remove()
    {
        return mastPull();
    }
    
    @Override
    public E element()
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E peek()
    {
        throw new UnsupportedOperationException();
    }
    
}
