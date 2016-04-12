package com.jfireframework.baseutil.collection.set;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * 轻量级的链表类
 * 
 * @author 林斌（eric@jfire.cn）
 *         
 * @param <T>
 */
public class LightSet<T> implements Iterable<T>
{
    private Node<T> head = new Node<T>(null, null);
    private int     size = 0;
    private Node<T> end  = null;
                         
    /**
     * 将数组中所有的数据加入set中
     * 
     * @param values
     */
    public void addAll(T[] values)
    {
        for (T each : values)
        {
            addValue(each);
        }
    }
    
    /**
     * 该set是否为空
     * 
     * @return
     */
    public boolean isEmpty()
    {
        return head.next() == null;
    }
    
    /**
     * 将另外一个set的数据加入自身中
     * 
     * @param set
     */
    public void addAll(LightSet<T> set)
    {
        if (set.size() == 0)
        {
            return;
        }
        if (end == null)
        {
            head.setNext(set.getHead().next());
            end = set.getEnd();
        }
        else
        {
            end.setNext(set.getHead().next());
            end = set.getEnd();
        }
        size += set.size();
    }
    
    public void addValue(T value)
    {
        if (end == null)
        {
            Node<T> node = new Node<T>(value);
            head.setNext(node);
            end = node;
        }
        else
        {
            Node<T> node = new Node<T>(value);
            end.setNext(node);
            end = node;
        }
        size++;
    }
    
    public void add(T value)
    {
        addValue(value);
    }
    
    public void removeValue(Object value)
    {
        Node<T> pre = head;
        Node<T> target = head.next();
        while (target != null)
        {
            if (target.value() == value)
            {
                if (end == target)
                {
                    end = pre;
                    pre.setNext(null);
                }
                else
                {
                    pre.setNext(target.next());
                }
                size--;
                break;
            }
            pre = target;
            target = target.next();
        }
    }
    
    public void removeNode(Node<T> node)
    {
        Node<T> pre = head;
        Node<T> target = head.next();
        while (target != null)
        {
            if (target == node)
            {
                if (target == end)
                {
                    end = pre;
                    pre.setNext(null);
                }
                else
                {
                    pre.setNext(target.next());
                }
                size--;
                break;
            }
            pre = target;
            target = target.next();
        }
    }
    
    public Node<T> getHead()
    {
        return head;
    }
    
    /**
     * 将链表内的数据以数组的形式返回
     * 
     * @return
     */
    public T[] toArray(Class<T> type)
    {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(type, size);
        int index = 0;
        Node<T> node = head;
        while ((node = node.next()) != null)
        {
            result[index] = node.value();
            index++;
        }
        return result;
    }
    
    public int size()
    {
        return size;
    }
    
    /**
     * 删除所有的内容
     */
    public void removeAll()
    {
        head.setNext(null);
        end = null;
        size = 0;
    }
    
    @Override
    public Iterator<T> iterator()
    {
        return new SetIterator<T>(this);
    }
    
    public Node<T> getEnd()
    {
        return end;
    }
    
}
