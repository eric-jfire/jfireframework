package com.jfireframework.baseutil.concurrent;

public class SpscQueue<E>
{
    private final int          length;
    private final int          mask;
    private final Object[]     array;
    private CpuCachePadingLong get     = new CpuCachePadingLong(0);
    private CpuCachePadingLong put     = new CpuCachePadingLong(0);
    private long               wrapPut = 0;
    private long               wrapGet = 0;
    
    public SpscQueue()
    {
        this(1024);
    }
    
    public SpscQueue(int length)
    {
        this.length = length;
        mask = length - 1;
        array = new Object[length];
    }
    
    public void put(E e)
    {
        long tmp = put.value();
        if (tmp < wrapPut)
        {
            array[(int) (tmp & mask)] = e;
            put.orderSet(tmp + 1);
        }
        else
        {
            do
            {
                wrapPut = get.value() + length;
                if (tmp < wrapPut)
                {
                    array[(int) (tmp & mask)] = e;
                    put.orderSet(tmp + 1);
                    return;
                }
            } while (true);
        }
    }
    
    @SuppressWarnings("unchecked")
    public E get()
    {
        long tmp = get.value();
        if (tmp < wrapGet)
        {
            E result = (E) array[(int) (tmp & mask)];
            get.orderSet(tmp + 1);
            return result;
        }
        else
        {
            do
            {
                wrapGet = put.value();
                if (tmp < wrapGet)
                {
                    E result = (E) array[(int) (tmp & mask)];
                    get.orderSet(tmp + 1);
                    return result;
                }
            } while (true);
        }
    }
    
}
