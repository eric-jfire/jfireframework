package com.jfireframework.jnet.client;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.decodec.FrameDecodec;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ClientInternalResult;

/**
 * 客户端工具类，注意，该客户端是非线程安全类，其方法不可多线程运行
 * 
 * @author linbin
 *         
 */
public abstract class AioClient
{
    protected ClientChannelInfo        clientChannelInfo;
    protected long                     readTimeout       = 3000;
    // 默认的超时等待时间是30分钟
    protected long                     waitTimeout       = 1000 * 60 * 30;
    protected String                   address;
    protected int                      port;
    protected AsynchronousChannelGroup channelGroup;
    protected FrameDecodec             frameDecodec;
    protected DataHandler[]            readHandlers;
    protected DataHandler[]            writeHandlers;
    protected volatile boolean         connectState      = UNCONNECTED;
    public static final boolean        UNCONNECTED       = false;
    public static final boolean        CONNECTED         = true;
    public static Future<Void>         NORESULT          = new Future<Void>() {
                                                             
                                                             @Override
                                                             public boolean cancel(boolean mayInterruptIfRunning)
                                                             {
                                                                 return false;
                                                             }
                                                             
                                                             @Override
                                                             public boolean isCancelled()
                                                             {
                                                                 return false;
                                                             }
                                                             
                                                             @Override
                                                             public boolean isDone()
                                                             {
                                                                 return true;
                                                             }
                                                             
                                                             @Override
                                                             public Void get() throws InterruptedException, ExecutionException
                                                             {
                                                                 return null;
                                                             }
                                                             
                                                             @Override
                                                             public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
                                                             {
                                                                 return null;
                                                             }
                                                         };
                                                         
    public AioClient setReadTimeout(long readTimeout)
    {
        this.readTimeout = readTimeout;
        return this;
    }
    
    public AioClient setWaitTimeout(long waitTimeout)
    {
        this.waitTimeout = waitTimeout;
        return this;
    }
    
    public AioClient setAddress(String address)
    {
        this.address = address;
        return this;
    }
    
    public AioClient setPort(int port)
    {
        this.port = port;
        return this;
    }
    
    public AioClient setChannelGroup(AsynchronousChannelGroup channelGroup)
    {
        this.channelGroup = channelGroup;
        return this;
    }
    
    public AioClient setReadHandlers(DataHandler... readHandlers)
    {
        this.readHandlers = readHandlers;
        return this;
    }
    
    public AioClient setWriteHandlers(DataHandler... writeHandlers)
    {
        this.writeHandlers = writeHandlers;
        return this;
    }
    
    public AioClient setFrameDecodec(FrameDecodec frameDecodec)
    {
        this.frameDecodec = frameDecodec;
        return this;
    }
    
    public boolean isConnectState()
    {
        return connectState;
    }
    
    public AioClient setConnectState(boolean connectState)
    {
        this.connectState = connectState;
        return this;
    }
    
    public AioClient connect() throws Throwable
    {
        if (connectState == UNCONNECTED)
        {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(channelGroup);
            socketChannel.connect(new InetSocketAddress(address, port)).get();
            connectState = CONNECTED;
            clientChannelInfo = new ClientChannelInfo(this, frameDecodec, socketChannel, readTimeout, waitTimeout, readHandlers);
            clientChannelInfo.readAndWait();
        }
        return this;
    }
    
    /**
     * 将一个对象写出并且返回一个future。该future表明的是服务端对该请求报文的响应报文的处理结果
     * 
     * @param object
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws JnetException
     */
    public Future<?> write(Object object) throws Throwable
    {
        return write(object, 0);
    }
    
    /**
     * 将一个对象写出并且指定开始处理时的handler顺序，然后返回一个future。该future表明的是服务端对该请求报文的响应报文的处理结果
     * 
     * @param object
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws JnetException
     */
    public Future<?> write(Object data, int index) throws Throwable
    {
        try
        {
            if (connectState == UNCONNECTED)
            {
                throw new InterruptedException("链接已经中断，请重新链接后再发送信息");
            }
            ClientInternalResult internalResult = new ClientInternalResult(data, clientChannelInfo, index);
            for (int i = index; i < writeHandlers.length;)
            {
                data = writeHandlers[i].handle(data, internalResult);
                if (i == internalResult.getIndex())
                {
                    i++;
                    internalResult.setIndex(i);
                }
                else
                {
                    i = internalResult.getIndex();
                }
                
            }
            if (data instanceof ByteBuf<?>)
            {
                Future<?> result = buildFuture();
                ByteBuf<?> buf = (ByteBuf<?>) data;
                int writed = 0;
                while (buf.remainRead() > 0)
                {
                    writed = clientChannelInfo.socketChannel().write(((ByteBuf<?>) data).nioBuffer()).get();
                    buf.addReadIndex(writed);
                }
                buf.release();
                return result;
            }
            else
            {
                return NORESULT;
            }
        }
        catch (Exception e)
        {
            Object tmp = e;
            ClientInternalResult result = new ClientInternalResult(e, clientChannelInfo, 0);
            for (DataHandler each : writeHandlers)
            {
                tmp = each.catchException(tmp, result);
            }
            if (tmp instanceof Throwable)
            {
                throw (Throwable) tmp;
            }
            else
            {
                throw e;
            }
        }
    }
    
    protected abstract Future<?> buildFuture();
    
    public void close(Throwable throwable)
    {
        ClientChannelInfo tmp = clientChannelInfo;
        connectState = UNCONNECTED;
        tmp.close(throwable);
    }
    
    public void close()
    {
        ClientChannelInfo tmp = clientChannelInfo;
        connectState = UNCONNECTED;
        tmp.close();
    }
}
