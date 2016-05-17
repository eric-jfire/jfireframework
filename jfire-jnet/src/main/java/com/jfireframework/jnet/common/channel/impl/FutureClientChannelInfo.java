package com.jfireframework.jnet.common.channel.impl;

import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;
import com.jfireframework.jnet.client.ResponseFuture;

public class FutureClientChannelInfo extends AbstractClientChannel
{
    private Thread           writeThread;
    private volatile boolean needUnpark = false;
    
    public void signal(Object obj, long cursor)
    {
        // 可以考虑一种很难出现的情况就是服务端收到一个请求的时候发送了两个响应。这样就会导致读取到不合适位置的future
        ResponseFuture future = (ResponseFuture) getDataVolatile(cursor);
        future.ready(obj, null);
        // 这个调用，否则添加的线程（调用addFuture方法的线程）被唤醒后，仍然会再次进入等待
        readCompleter.setCursor(cursor + 1);
        if (needUnpark)
        {
            // 这里设置为false，避免不必要的对一个线程进行unpark操作
            needUnpark = false;
            LockSupport.unpark(writeThread);
        }
    }
    
    public void signalAll(Throwable e, long cursor)
    {
        ResponseFuture future;
        while (cursor < writeCursor)
        {
            future = (ResponseFuture) getDataVolatile(cursor);
            future.ready(null, e);
            cursor += 1;
        }
        // 这个调用，否则添加的线程（调用addFuture方法的线程）被唤醒后，仍然会再次进入等待
        readCompleter.setCursor(cursor);
        if (needUnpark)
        {
            needUnpark = false;
            LockSupport.unpark(writeThread);
        }
        
    }
    
    public Future<?> addFuture()
    {
        while (writeCursor >= wrapPoint)
        {
            wrapPoint = readCompleter.cursor() + capacity;
            if (writeCursor < wrapPoint)
            {
                break;
            }
            // writeThread的赋值要在needUnpark的前面，这样才能保证其他线程中该属性的正确可见
            writeThread = Thread.currentThread();
            needUnpark = true;
            // 在设置needUnpark之后进行检查，以避免数据其实都已经被读取而没有线程可以unpark该线程
            wrapPoint = readCompleter.cursor() + capacity;
            if (writeCursor >= wrapPoint)
            {
                LockSupport.park();
            }
            else
            {
                break;
            }
        }
        // 客户端每一个future都代表一个结果，不可以被复用。因为外部环境需要保留所有的result
        ResponseFuture future = new ResponseFuture();
        putDataVolatile(future, writeCursor);
        writeCursor += 1;
        return future;
    }
}
