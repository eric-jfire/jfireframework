package com.jfireframework.baseutil.collection.buffer;

import java.util.Queue;

public class HeapByteBufPool extends ByteBufPool<byte[]>
{
    private static volatile HeapByteBufPool INSTANCE;
    
    private HeapByteBufPool()
    {
    }
    
    public static HeapByteBufPool getInstance()
    {
        if (INSTANCE == null)
        {
            synchronized (HeapByteBufPool.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new HeapByteBufPool();
                }
            }
        }
        return INSTANCE;
    }
    
    @Override
    protected void expend(ByteBuf<byte[]> buf, int need)
    {
        byte[] tmp = null;
        Queue<byte[]> host = null;
        for (CacheSize each : sizes)
        {
            if (each.biggerThan(need))
            {
                byte[] src = arrays[each.index()].poll();
                if (src == null)
                {
                    tmp = new byte[each.size()];
                }
                else
                {
                    tmp = src;
                }
                host = arrays[each.index()];
                break;
            }
        }
        if (tmp == null)
        {
            tmp = new byte[need];
        }
        byte[] src = buf.memory;
        int length = buf.remainRead();
        System.arraycopy(src, buf.readIndex, tmp, 0, length);
        buf.release();
        buf.host = host;
        buf.memory = tmp;
        buf.readIndex(0);
        buf.writeIndex(length);
    }
    
    @Override
    public HeapByteBuf get(int size)
    {
        for (CacheSize each : sizes)
        {
            if (each.biggerThan(size))
            {
                byte[] src = arrays[each.index()].poll();
                if (src == null)
                {
                    return new HeapByteBuf(new byte[each.size()], arrays[each.index()]);
                }
                else
                {
                    return new HeapByteBuf(src, arrays[each.index()]);
                }
            }
        }
        return new HeapByteBuf(new byte[size], null);
    }
    
}
