package com.jfireframework.baseutil.disruptor.ringarray;

import com.jfireframework.baseutil.disruptor.EntryAction;
import com.jfireframework.baseutil.disruptor.Sequence;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategy;

public class SimpleMultRingArray extends AbstractMultRingArray
{
    private Sequence preAdd = new Sequence();
    
    public SimpleMultRingArray(int size, WaitStrategy waitStrategy, EntryAction[] actions)
    {
        super(size, waitStrategy, actions);
    }
    
    @Override
    public void publish(long cursor)
    {
        this.cursor.casSet(cursor - 1, cursor);
        waitStrategy.signallBlockwaiting();
    }
    
    @Override
    public boolean isAvailable(long cursor)
    {
        if (cursor <= this.cursor.value())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    protected long getNext()
    {
        return preAdd.next();
    }
    
}
