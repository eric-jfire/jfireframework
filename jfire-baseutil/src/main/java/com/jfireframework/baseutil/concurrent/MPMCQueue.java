package com.jfireframework.baseutil.concurrent;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

public class MPMCQueue<E>
{
    private final CpuCachePadingRefence<Node<E>> head;
    private final CpuCachePadingRefence<Node<E>> tail;
    private static final Unsafe                  unsafe = ReflectUtil.getUnsafe();
    
    public MPMCQueue()
    {
        head = tail = new CpuCachePadingRefence<MPMCQueue.Node<E>>(new Node<E>(null));
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
    }
    
    public void clear()
    {
        Node<E> node = tail.get();
        head.set(node);
        node.clear();
    }
    
    public boolean offer(E o)
    {
        if (o == null)
        {
            throw new NullPointerException();
        }
        Node<E> insert_node = new Node<E>(o);
        Node<E> origin = tail.setAndReturnOrigin(insert_node);
        unsafe.putOrderedObject(origin, Node.nextOffset, insert_node);
        return true;
    }
    
    public E poll()
    {
        for (Node<E> h = head.get(), next, t = tail.get();; h = head.get(), next = h.next)
        {
            if (h != t)
            {
                next = h.next;
                if (next == null)
                {
                    do
                    {
                        if ((next = h.next) != null)
                        {
                            break;
                        }
                    } while (h == head.get());
                    if (h != head.get())
                    {
                        continue;
                    }
                }
                if (head.compareAndSwap(h, next))
                {
                    return next.clear();
                }
                continue;
            }
            else if (h != (t = tail.get()) )
            {
                next = h.next;
                if (next == null)
                {
                    do
                    {
                        if ((next = h.next) != null)
                        {
                            break;
                        }
                    } while (h == head.get());
                    if (h != head.get())
                    {
                        continue;
                    }
                }
                if (head.compareAndSwap(h, next))
                {
                    return next.clear();
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
