package com.jfireframework.baseutil.concurrent;

import java.util.Collection;
import java.util.Iterator;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class MPMCQueue2<E>
{
    private volatile Node<E>    head;
    private volatile Node<E>    tail;
    private static final Unsafe unsafe     = ReflectUtil.getUnsafe();
    private static final long   tailOffset = ReflectUtil.getFieldOffset("tail", MPMCQueue2.class);
    private static final long   headOffset = ReflectUtil.getFieldOffset("head", MPMCQueue2.class);
    
    public MPMCQueue2()
    {
        head = tail = new Node<E>(null);
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
            unsafe.putOrderedObject(this, Node.nextOffset, null);
            return origin;
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
    
    public E poll()
    {
        for (Node<E> h = head, next = h.next, t = tail;; h = head, next = h.next)
        {
            if (h != t)
            {
                if (next == null)
                {
                    do
                    {
                        if ((next = h.next) != null)
                        {
                            break;
                        }
                    } while (h == head);
                    if (h != head)
                    {
                        continue;
                    }
                }
                if (unsafe.compareAndSwapObject(this, headOffset, h, next))
                {
                    return h.clear();
                }
                continue;
            }
            else if (h != (t = tail))
            {
                if (next == null)
                {
                    do
                    {
                        if ((next = h.next) != null)
                        {
                            break;
                        }
                    } while (h == head);
                    if (h != head)
                    {
                        continue;
                    }
                }
                if (unsafe.compareAndSwapObject(this, headOffset, h, next))
                {
                    return h.clear();
                }
                continue;
            }
            else
            {
                return null;
            }
        }
    }
    
}
