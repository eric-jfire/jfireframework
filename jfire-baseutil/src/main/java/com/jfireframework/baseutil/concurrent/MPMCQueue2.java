package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class MPMCQueue2<E> implements Queue<E>
{
    private volatile Node<E>    head;
    private volatile Node<E>    tail;
    private static final Unsafe unsafe     = ReflectUtil.getUnsafe();
    private static final long   tailOffset = ReflectUtil.getFieldOffset("tail", MPMCQueue2.class);
    private static final long   headOffset = ReflectUtil.getFieldOffset("head", MPMCQueue2.class);
    private final boolean       fair;
    private Sync<E>             sync       = new Sync<E>() {
                                               
                                               @Override
                                               protected E get()
                                               {
                                                   return poll();
                                               }
                                           };
    private static final int    HOPS       = 2;
    
    public MPMCQueue2()
    {
        this(false);
    }
    
    public MPMCQueue2(boolean fair)
    {
        Node<E> n = new Node<E>(null);
        head = tail = n;
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
        
        public boolean casNext(Node<E> next)
        {
            return unsafe.compareAndSwapObject(this, nextOffset, null, next);
        }
        
        public boolean casValue(E value)
        {
            return unsafe.compareAndSwapObject(this, valueOffset, value, null);
        }
    }
    
    @Override
    public void clear()
    {
    }
    
    private void casHead(Node<E> originHead, Node<E> newHead)
    {
        unsafe.compareAndSwapObject(this, headOffset, originHead, newHead);
    }
    
    @Override
    public boolean offer(E o)
    {
        if (o == null)
        {
            throw new NullPointerException();
        }
        Node<E> insert_node = new Node<E>(o);
        Node<E> t = tail;
        Node<E> p = t;
        if (p.next == null && p.casNext(insert_node))
        {
            return true;
        }
        p = p.next;
        for (int i = 0;; i++)
        {
            if (p.next == null && p.casNext(insert_node))
            {
                if (i > HOPS)
                {
                    unsafe.compareAndSwapObject(this, tailOffset, t, insert_node);
                }
                return true;
            }
            else
            {
                p = p.next;
            }
        }
    }
    
    public void offerAndSignal(E o)
    {
        offer(o);
        sync.signal();
    }
    
    @Override
    public E poll()
    {
        Node<E> h = head;
        Node<E> n = h.next;
        for (int i = 0; n != null; i++)
        {
            E value = n.value;
            if (value != null && n.casValue(value))
            {
                if (i > HOPS)
                {
                    casHead(h, n);
                }
                return value;
            }
            n = n.next;
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
        Node<E> h = head;
        for (Node<E> n = h.next; n != null; n = n.next)
        {
            if (n.value != null)
            {
                return true;
            }
        }
        return false;
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
        return poll();
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
