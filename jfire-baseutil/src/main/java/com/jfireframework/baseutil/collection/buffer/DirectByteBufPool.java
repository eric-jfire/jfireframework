package com.jfireframework.baseutil.collection.buffer;

import java.nio.ByteBuffer;
import java.util.Queue;

public class DirectByteBufPool extends ByteBufPool<ByteBuffer>
{
    
    private static volatile DirectByteBufPool INSTANCE;
    
    private DirectByteBufPool()
    {
        super();
        bufHost = queueFactory.newInstance();
    }
    
    public static DirectByteBufPool getInstance()
    {
        if (INSTANCE == null)
        {
            synchronized (HeapByteBufPool.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new DirectByteBufPool();
                }
            }
        }
        return INSTANCE;
    }
    
    @Override
    protected void expend(ByteBuf<ByteBuffer> buf, int need)
    {
        ByteBuffer tmp = null;
        Queue<ByteBuffer> host = null;
        for (CacheSize each : sizes)
        {
            if (each.biggerThan(need))
            {
                ByteBuffer buffer = memorys[each.index()].poll();
                if (buffer != null)
                {
                    tmp = buffer;
                    tmp.clear();
                }
                else
                {
                    tmp = ByteBuffer.allocateDirect(each.size());
                }
                host = memorys[each.index()];
                break;
            }
        }
        if (tmp == null)
        {
            tmp = ByteBuffer.allocateDirect(need);
        }
        ((DirectByteBuf) buf).changeToReadState();
        ByteBuffer src = buf.memory;
        tmp.put(src);
        buf.releaseMemOnly();
        buf.memHost = host;
        buf.memory = tmp;
        buf.readIndex(0);
        buf.writeIndex(tmp.position());
    }
    
    @Override
    public DirectByteBuf get(int size)
    {
        for (CacheSize each : sizes)
        {
            if (each.biggerThan(size))
            {
                ByteBuffer buffer = memorys[each.index()].poll();
                if (buffer != null)
                {
                    buffer.clear();
                }
                else
                {
                    buffer = ByteBuffer.allocateDirect(each.size());
                }
                DirectByteBuf buf = (DirectByteBuf) bufHost.poll();
                if (buf == null)
                {
                    buf = new DirectByteBuf(buffer, memorys[each.index()], bufHost);
                    return buf;
                }
                else
                {
                    buf.init(buffer, memorys[each.index()], bufHost);
                    return buf;
                }
                
            }
        }
        return new DirectByteBuf(ByteBuffer.allocateDirect(size), null, bufHost);
    }
    
}
