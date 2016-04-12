package com.jfireframework.baseutil.collection.set;

public class Node<T>
{
    private volatile Node<T> next;
    private T                value;
                             
    public Node(T value)
    {
        this.value = value;
    }
    
    public Node(Node<T> next, T value)
    {
        this.next = next;
        this.value = value;
    }
    
    public Node<T> next()
    {
        return next;
    }
    
    public void setNext(Node<T> next)
    {
        this.next = next;
    }
    
    public T value()
    {
        return value;
    }
    
    public void setValue(T value)
    {
        this.value = value;
    }
}
