package com.jfireframework.baseutil.collection.buffer;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

public abstract class ByteBufPool<T>
{
    protected CacheSize[]       sizes;
    protected Queue<T>[]        memorys;
    protected Queue<ByteBuf<T>> bufHost;
    
    @SuppressWarnings("unchecked")
    public ByteBufPool()
    {
        int index = 0;
        List<CacheSize> tmp = new LinkedList<CacheSize>();
        for (int i = 1; i <= 7; i++)
        {
            tmp.add(new CacheSize(128 * i, index));
            index++;
        }
        for (int i = 1; i <= 511; i++)
        {
            tmp.add(new CacheSize(i * 1024 * 2, index));
            index++;
        }
        for (int i = 1; i <= 64; i++)
        {
            tmp.add(new CacheSize(i * 1024 * 1024, index));
            index++;
        }
        sizes = tmp.toArray(new CacheSize[0]);
        memorys = new Queue[sizes.length];
        for (int i = 0; i < sizes.length; i++)
        {
            memorys[i] = new LinkedTransferQueue<T>();
        }
    }
    
    protected abstract void expend(ByteBuf<T> buf, int need);
    
    public abstract ByteBuf<T> get(int size);
}
