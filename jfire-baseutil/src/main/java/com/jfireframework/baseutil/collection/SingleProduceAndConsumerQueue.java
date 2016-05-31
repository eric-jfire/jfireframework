package com.jfireframework.baseutil.collection;

import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

/**
 * 单生产者消费者队列，可以允许一个生产者和一个消费者同时并发。但是不能多个消费者或者多个生产者。也就是说一个时刻最多只能有两个线程，一个是生产者一个是消费者
 * 
 * @author 林斌
 * 
 * @param <T>
 */
public class SingleProduceAndConsumerQueue<T>
{
    
    private static class Node<T>
    {
        T                value;
        volatile Node<T> next;
        static long      offset   = ReflectUtil.getFieldOffset("next", Node.class);
        static long      valueOff = ReflectUtil.getFieldOffset("value", Node.class);
        
        public Node(T value)
        {
            // this.value = value;
            unsafe.putObject(this, valueOff, value);
        }
        
        public void setNext(Node<T> next)
        {
            // unsafe.putObject(this, offset, next);
            // this.next = next;
            unsafe.putOrderedObject(this, offset, next);
        }
        
    }
    
    private volatile Node<T> head;
    private volatile Node<T> tail;
    private static Unsafe    unsafe = ReflectUtil.getUnsafe();
    
    public SingleProduceAndConsumerQueue()
    {
        head = tail = new Node<T>(null);
    }
    
    public void add(T value)
    {
        Node<T> insert = new Node<T>(value);
        if (tail.next == null)
        {
            // tail.setNext(insert);
            tail.next = insert;
        }
        else
        {
            tail.next.setNext(insert);
            tail = insert;
        }
    }
    
    public T poll()
    {
        Node<T> p = head.next;
        if (p == null)
        {
            return null;
        }
        else
        {
            head = p;
            return p.value;
        }
    }
    
    /**
     * 取出最上面的元素但是不移除
     * 
     * @return
     */
    public T peek()
    {
        return peek(0);
    }
    
    /**
     * 获取某一个序号的元素但是不移除。序号从0开始递增
     * 
     * @param index
     * @return
     */
    public T peek(int index)
    {
        Node<T> p = head.next;
        if (p == null)
        {
            return null;
        }
        else
        {
            for (int i = index; i > 0; i--)
            {
                if (p != null)
                {
                    p = p.next;
                }
                else
                {
                    return null;
                }
            }
            if (p == null)
            {
                return null;
            }
            else
            {
                return p.value;
            }
        }
    }
    
    public boolean isTop(T value)
    {
        Node<T> p = head.next;
        if (p != null)
        {
            return p.value == value;
        }
        else
        {
            return false;
        }
    }
    
    public int size()
    {
        int size = 0;
        Node<T> p = head.next;
        while (p != null)
        {
            size++;
            p = p.next;
        }
        return size;
    }
}
