package com.jfireframework.jnet.common.channel;

import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.jnet.client.ResponseFuture;

public class FutureClientChannelInfo extends AbstractClientChannelInfo
{
    private volatile Thread writeThread;
    
    public void signal(Object obj, long cursor)
    {
        // 可以考虑一种很难出现的情况就是服务端收到一个请求的时候发送了两个响应。这样就会导致读取到不合适位置的future
        ResponseFuture future = (ResponseFuture) getResultVolatile(cursor);
        future.ready(obj, null);
        // 这个调用，否则添加的线程（调用addFuture方法的线程）被唤醒后，仍然会再次进入等待
        readCompleter.setCursor(cursor + 1);
        LockSupport.unpark(writeThread);
    }
    
    public void signalAll(Throwable e, long cursor)
    {
        ResponseFuture future;
        while (cursor < writeCursor)
        {
            future = (ResponseFuture) getResultVolatile(cursor);
            future.ready(null, e);
            cursor += 1;
        }
        // 这个调用，否则添加的线程（调用addFuture方法的线程）被唤醒后，仍然会再次进入等待
        readCompleter.setCursor(cursor);
        LockSupport.unpark(writeThread);
    }
    
    public Future<?> addFuture()
    {
        if (writeCursor >= wrapPoint)
        {
            // 在外面对writeThread进行赋值。这样其他想要unpark的才能保证是有机会的。如果是在while循环内部，在赋值前，真正park住的时候可能其他所有的内容都被读取完毕了。这样就没有线程可以unpark这个写入线程
            // 在外面保证了，如果进入while循环，其他线程unpark的时候writeThread是有值的。而避免一直进行非必要的unpark调用也是有一定好处的
            writeThread = Thread.currentThread();
            wrapPoint = readCompleter.cursor() + getEntryArraySize();
            while (writeCursor >= wrapPoint)
            {
                LockSupport.park();
                wrapPoint = readCompleter.cursor() + getEntryArraySize();
            }
            writeThread = null;
        }
        // 客户端每一个future都代表一个结果，不可以被复用。因为外部环境需要保留所有的result
        ResponseFuture future = new ResponseFuture();
        putResultVolatile(future, writeCursor);
        writeCursor += 1;
        return future;
    }
}
