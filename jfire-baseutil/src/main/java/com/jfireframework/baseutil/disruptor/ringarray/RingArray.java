package com.jfireframework.baseutil.disruptor.ringarray;

import com.jfireframework.baseutil.disruptor.Entry;
import com.jfireframework.baseutil.disruptor.waitstrategy.WaitStrategyStopException;

public interface RingArray
{
    
    public long next();
    
    public Entry entryAt(long cursor);
    
    public void publish(long cursor);
    
    public void publish(Object data);
    
    /**
     * 尝试发布一个数据。
     * 如果发布成功，也就是说数据得到了可以写入的next位置，就将数据写入并且将该位置发布。此时该位置的数据处于freeForTake状态。所以这个位置上的数据是可以被使用的。
     * 如果发布失败，也就是说尝试获取的next位置没办法立即被写入。那么直接将该位置发布。因为这个位置上的数据处于taked状态，所以所有的处理器都会忽略掉它，前往下一个。
     * 
     * @param data
     * @return 发布成功返回true，发布失败返回false
     */
    public boolean tryPublish(Object data);
    
    public boolean isAvailable(long cursor);
    
    public long cursor();
    
    public void waitFor(long cursor) throws WaitStrategyStopException;
    
    public void stop();
    
}
