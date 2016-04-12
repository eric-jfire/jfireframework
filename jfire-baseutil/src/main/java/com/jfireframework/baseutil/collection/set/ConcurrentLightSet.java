package com.jfireframework.baseutil.collection.set;

public class ConcurrentLightSet<T>
{
    private Node<T> head = new Node<T>(null, null);
    
    public void addValue(T value)
    {
        Node<T> secondNode = head.next();
        if (secondNode == null)
        {
            Node<T> node = new Node<T>(null, value);
            head.setNext(node);
        }
        else
        {
            Node<T> node = new Node<T>(secondNode, value);
            head.setNext(node);
        }
    }
    
    public void remove(Object value)
    {
        Node<T> pre = head;
        Node<T> target = head.next();
        while (target != null)
        {
            if (target.value() == value)
            {
                synchronized (pre)
                {
                    synchronized (target)
                    {
                        pre.setNext(target.next());
                        break;
                    }
                }
                
            }
            pre = target;
            target = target.next();
        }
    }
    
    public Node<T> getHead()
    {
        return head;
    }
}
