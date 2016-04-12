package com.jfireframework.baseutil.collection;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * buffer缓存池
 * 
 * @author Administrator
 *         
 */
public class ByteBufferPool
{
    private static ConcurrentHashMap<Integer, Queue<ByteBuffer>> bufferPool   = new ConcurrentHashMap<>();
    private static int[]                                         sizeStandard = new int[] { 1024, 2048, 3 * 1024, 4 * 1024, 10 * 1024, 20 * 1024, 50 * 1024, 100 * 1024, 1000 * 1024, 1500 * 1024, 2000 * 1024, 2500 * 1024, 3000 * 1024, 4000 * 1024, 5000 * 1024, 6000 * 1024, 7000 * 1024, 8000 * 1024 };
    private static int                                           sizeSum      = sizeStandard.length;
                                                                              
    static
    {
        for (int i = 0; i < sizeSum; i++)
        {
            bufferPool.put(sizeStandard[i], new ConcurrentLinkedQueue<ByteBuffer>());
        }
    }
    
    /**
     * 获取一个不小于size的bytebuffer
     * 
     * @param size
     * @return
     */
    public static ByteBuffer getBuffer(int size)
    {
        for (int i = 0; i < sizeSum; i++)
        {
            if (sizeStandard[i] >= size)
            {
                ByteBuffer need = bufferPool.get(sizeStandard[i]).poll();
                if (need == null)
                {
                    need = ByteBuffer.allocate(sizeStandard[i]);
                    return need;
                }
                else
                {
                    return need;
                }
            }
        }
        return ByteBuffer.allocate(size);
    }
    
    /**
     * 将一个正在进行写操作的buffer扩大容量到新的size。
     * 新的buffer的position位置与原buffer相同
     * 
     * @param size
     * @param src
     * @return
     */
    public static ByteBuffer expandToSize(int size, ByteBuffer src)
    {
        if (size > src.capacity())
        {
            ByteBuffer tmp = ByteBufferPool.getBuffer(size);
            int limit = src.flip().limit();
            tmp.put(src);
            ByteBufferPool.returnBuffer(src);
            tmp.position(limit);
            return tmp;
        }
        else
        {
            return src;
        }
    }
    
    /**
     * 将bytebuffer返回给缓存池，该操作会清空当前的buffer内容
     * 
     * @param buffer
     */
    public static void returnBuffer(ByteBuffer buffer)
    {
        int result = buffer.capacity();
        if (result <= sizeStandard[sizeSum - 1])
        {
            buffer.clear();
            bufferPool.get(result).offer(buffer);
        }
    }
    
    /**
     * 获取缓存池的大小
     * 
     * @return
     */
    public static int getBufferCacheSize()
    {
        int cacheSize = 0;
        for (Queue<ByteBuffer> each : bufferPool.values())
        {
            ByteBuffer buffer = each.peek();
            if (buffer != null)
            {
                cacheSize += buffer.capacity() * each.size();
            }
        }
        return cacheSize;
    }
    
    /**
     * 将所有的buffer都重置
     */
    public static void clearBufferCache()
    {
        for (Queue<ByteBuffer> each : bufferPool.values())
        {
            each.clear();
        }
    }
    
    /**
     * 获取缓存池的状态大小
     * 
     * @return
     */
    public static HashMap<String, String> getCacheStatus()
    {
        HashMap<String, String> cacheStatus = new HashMap<>();
        for (int i = 0; i < sizeSum; i++)
        {
            if (bufferPool.get(sizeStandard[i]).size() > 0)
            {
                cacheStatus.put(sizeStandard[i] / 1024 + "K", "total :" + bufferPool.get(sizeStandard[i]).size());
            }
        }
        return cacheStatus;
    }
}
