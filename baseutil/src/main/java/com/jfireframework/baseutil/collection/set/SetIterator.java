package com.jfireframework.baseutil.collection.set;

import java.util.Iterator;

public class SetIterator<T> implements Iterator<T>
{
    private Node<T>     node;
    private LightSet<T> set;
                        
    public SetIterator(LightSet<T> set)
    {
        this.set = set;
        node = set.getHead();
    }
    
    @Override
    public boolean hasNext()
    {
        if (node.next() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public T next()
    {
        node = node.next();
        return node.value();
    }
    
    @Override
    public void remove()
    {
        set.removeNode(node);
    }
    
}
