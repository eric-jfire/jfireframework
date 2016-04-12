package com.jfireframework.jnet.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ClientInternalResult;

public class ClientChannelInfo
{
    public static final Throwable     CLOSE_FOR_NOTHING = new Throwable();
    private DataHandler[]             readHandlers;
    private final DirectByteBuf       ioBuf             = DirectByteBuf.allocate(100);
    private final Queue<Future<?>>    futures           = new ConcurrentLinkedQueue<>();
    private final Lock                lock              = new ReentrantLock();
    private AsynchronousSocketChannel socketChannel;
    private ClientReadCompleter       readCompleter;
    protected long                    readTimeout       = 3000;
    // 默认的超时等待时间是30分钟
    protected long                    waitTimeout       = 1000 * 60 * 30;
    private AioClient                 aioClient;
                                      
    public ClientChannelInfo(AioClient aioClient, FrameDecodec frameDecodec, AsynchronousSocketChannel socketChannel, long readTimeout, long waitTimeout, DataHandler... handlers)
    {
        this.aioClient = aioClient;
        this.socketChannel = socketChannel;
        this.readHandlers = handlers;
        readCompleter = new ClientReadCompleter(frameDecodec, handlers);
    }
    
    public DirectByteBuf ioBuf()
    {
        return ioBuf;
    }
    
    public void continueRead()
    {
        socketChannel.read(getReadBuffer(), readTimeout, TimeUnit.MILLISECONDS, this, readCompleter);
    }
    
    public void readAndWait()
    {
        socketChannel.read(getReadBuffer(), waitTimeout, TimeUnit.MILLISECONDS, this, readCompleter);
    }
    
    private ByteBuffer getReadBuffer()
    {
        ioBuf.compact();
        ByteBuffer ioBuffer = ioBuf.nioBuffer();
        ioBuffer.position(ioBuffer.limit()).limit(ioBuffer.capacity());
        return ioBuffer;
    }
    
    public void close()
    {
        close(CLOSE_FOR_NOTHING);
    }
    
    public void close(Throwable e)
    {
        Object tmp = e;
        ClientInternalResult result = new ClientInternalResult(e, this, 0);
        for (DataHandler each : readHandlers)
        {
            tmp = each.catchException(tmp, result);
        }
        try
        {
            aioClient.setConnectState(AioClient.UNCONNECTED);
            socketChannel.close();
            ioBuf.release();
        }
        catch (IOException e1)
        {
            e.printStackTrace();
        }
        if (e == null)
        {
            e = CLOSE_FOR_NOTHING;
        }
        releaseAllFuture(e);
    }
    
    public void popOneFuture(Object obj)
    {
        ResponseFuture future = (ResponseFuture) futures.poll();
        future.result = obj;
        lock.lock();
        try
        {
            // hasResponse是一个condition
            future.hasResponse.signal();
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public void releaseAllFuture(Throwable e)
    {
        lock.lock();
        ResponseFuture future;
        try
        {
            do
            {
                future = (ResponseFuture) futures.poll();
                if (future != null)
                {
                    future.e = e;
                    future.hasResponse.signal();
                }
            } while (future != null);
        }
        finally
        {
            lock.unlock();
        }
    }
    
    public AsynchronousSocketChannel socketChannel()
    {
        return socketChannel;
    }
    
    public Future<?> addFuture()
    {
        ResponseFuture future = new ResponseFuture(lock, lock.newCondition());
        futures.add(future);
        return future;
    }
}
