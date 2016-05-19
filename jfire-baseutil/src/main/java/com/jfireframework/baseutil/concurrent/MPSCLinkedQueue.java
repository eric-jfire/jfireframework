package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class MPSCLinkedQueue<E> implements Queue<E>
{
    private final CpuCachePadingRefence<MPSCNode<E>> headRef;
    private final CpuCachePadingRefence<MPSCNode<E>> tailRef;
    private static final RuntimeException            UNSUPPOR_RUNTIME_EXCEPTION = new RuntimeException("不支持的操作");
    
    public MPSCLinkedQueue()
    {
        MPSCNode<E> flag = new MPSCNode<E>(null);
        headRef = new CpuCachePadingRefence<MPSCLinkedQueue.MPSCNode<E>>(flag);
        tailRef = new CpuCachePadingRefence<MPSCLinkedQueue.MPSCNode<E>>(flag);
    }
    
    @Override
    public int size()
    {
        MPSCNode<E> pre = headRef.get();
        MPSCNode<E> end = tailRef.get();
        if (pre == end)
        {
            return 0;
        }
        int count = 0;
        while (pre != end)
        {
            while (pre.next() == null)
            {
                ;
            }
            pre = pre.next;
            count += 1;
        }
        return count;
    }
    
    @Override
    public boolean isEmpty()
    {
        return headRef.get() == tailRef.get();
    }
    
    @Override
    public boolean contains(Object o)
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public Iterator<E> iterator()
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public Object[] toArray()
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public <T> T[] toArray(T[] a)
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public boolean remove(Object o)
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public boolean containsAll(Collection<?> c)
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        for (E each : c)
        {
            offer(each);
        }
        return true;
    }
    
    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public void clear()
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public boolean add(E e)
    {
        return offer(e);
    }
    
    @Override
    public boolean offer(E value)
    {
        if (value == null)
        {
            throw new NullPointerException("value");
        }
        final MPSCNode<E> newTail = new MPSCNode<E>(value);
        MPSCNode<E> oldTail = tailRef.getAndSet(newTail);
        oldTail.setNext(newTail);
        return true;
    }
    
    @Override
    public E remove()
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public E poll()
    {
        MPSCNode<E> nextNode = peekNode();
        if (nextNode == null)
        {
            return null;
        }
        MPSCNode<E> oldHead = headRef.get();
        headRef.set(nextNode);
        oldHead.unlink();
        return nextNode.returnAndClear();
    }
    
    @Override
    public E element()
    {
        throw UNSUPPOR_RUNTIME_EXCEPTION;
    }
    
    @Override
    public E peek()
    {
        MPSCNode<E> node = peekNode();
        if (node == null)
        {
            return null;
        }
        return node.value();
    }
    
    private MPSCNode<E> peekNode()
    {
        MPSCNode<E> head = headRef.get();
        MPSCNode<E> next = head.next();
        if (next == null && head != tailRef.get())
        {
            do
            {
                next = head.next();
            } while (next == null);
        }
        return next;
    }
    
    private final static class MPSCNode<E>
    {
        private E                                                            value;
        private volatile MPSCNode<E>                                         next;
        @SuppressWarnings("rawtypes")
        private static final UnsafeReferenceFieldUpdater<MPSCNode, MPSCNode> nextUpdater = new UnsafeReferenceFieldUpdater<MPSCNode, MPSCNode>(MPSCNode.class, "next");
        
        public MPSCNode(E value)
        {
            this.value = value;
        }
        
        public E value()
        {
            return value;
        }
        
        public MPSCNode<E> next()
        {
            return next;
        }
        
        public void setNext(MPSCNode<E> newNext)
        {
            nextUpdater.orderSet(this, newNext);
        }
        
        public void unlink()
        {
            nextUpdater.orderSet(this, null);
        }
        
        public E returnAndClear()
        {
            E result = value;
            value = null;
            return result;
        }
    }
}
