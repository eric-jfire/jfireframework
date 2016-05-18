package com.jfireframework.baseutil.collection.buffer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HeapByteBufPool extends ByteBufPool<byte[]>
{
    private static volatile HeapByteBufPool INSTANCE;
    
    private HeapByteBufPool()
    {
        super();
        bufHost = new ConcurrentLinkedQueue<ByteBuf<byte[]>>();
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
                byte[] src = memorys[each.index()].poll();
                if (src == null)
                {
                    tmp = new byte[each.size()];
                }
                else
                {
                    tmp = src;
                }
                host = memorys[each.index()];
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
        buf.releaseMemOnly();
        buf.memHost = host;
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
                byte[] src = memorys[each.index()].poll();
                if (src == null)
                {
                    src = new byte[each.size()];
                }
                else
                {
                    ;
                }
                HeapByteBuf buf = (HeapByteBuf) bufHost.poll();
                if (buf == null)
                {
                    buf = new HeapByteBuf(src, memorys[each.index()], bufHost);
                    return buf;
                }
                else
                {
                    buf.init(src, memorys[each.index()], bufHost);
                    return buf;
                }
            }
        }
        return new HeapByteBuf(new byte[size], null, bufHost);
    }
    
}
