package com.jfireframework.jnet.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.channel.ClientChannel;
import com.jfireframework.jnet.common.channel.impl.AsyncClientChannelInfo;
import com.jfireframework.jnet.common.channel.impl.FutureClientChannelInfo;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.InternalResult;
import com.jfireframework.jnet.common.result.InternalResultImpl;

/**
 * 客户端工具类，注意，该客户端是非线程安全类，其方法不可多线程运行
 * 
 * @author linbin
 * 
 */
public class AioClient
{
    private ClientChannel            clientChannel;
    private String                   address;
    private int                      port;
    private AsynchronousChannelGroup channelGroup;
    private DataHandler[]            writeHandlers;
    private ChannelInitListener      initListener;
    private final boolean            async;
    private final InternalResult     internalResult = new InternalResultImpl();
    private int                      capacity       = 16;
    
    public AioClient(boolean async)
    {
        this.async = async;
    }
    
    public boolean isAsync()
    {
        return async;
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
    
    public void setInitListener(ChannelInitListener initListener)
    {
        this.initListener = initListener;
    }
    
    public AioClient setChannelGroup(AsynchronousChannelGroup channelGroup)
    {
        this.channelGroup = channelGroup;
        return this;
    }
    
    public AioClient setWriteHandlers(DataHandler... writeHandlers)
    {
        this.writeHandlers = writeHandlers;
        return this;
    }
    
    public AioClient connect() throws Throwable
    {
        if (clientChannel == null || clientChannel.isOpen() == false)
        {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(channelGroup);
            socketChannel.connect(new InetSocketAddress(address, port)).get(30, TimeUnit.SECONDS);
            if (async == true)
            {
                clientChannel = new AsyncClientChannelInfo();
            }
            else
            {
                clientChannel = new FutureClientChannelInfo(capacity);
            }
            clientChannel.setChannel(socketChannel);
            initListener.channelInit(clientChannel);
            Verify.notNull(clientChannel.getFrameDecodec(), "没有设置framedecodec");
            Verify.notNull(clientChannel.getHandlers(), "没有设置Datahandler");
            ClientReadCompleter clientReadCompleter = new ClientReadCompleter(this, clientChannel);
            clientReadCompleter.readAndWait();
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
            if (clientChannel == null || clientChannel.isOpen() == false)
            {
                throw new InterruptedException("链接已经中断，请重新链接后再发送信息");
            }
            internalResult.setIndex(0);
            internalResult.setChannelInfo(clientChannel);
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
                Future<?> result = clientChannel.addFuture();
                ByteBuffer buffer = ((ByteBuf<?>) data).nioBuffer();
                while (buffer.hasRemaining())
                {
                    clientChannel.getSocketChannel().write(buffer).get();
                }
                ((ByteBuf<?>) data).release();
                return result;
            }
            else
            {
                return AsyncClientChannelInfo.NORESULT;
            }
        }
        catch (Exception e)
        {
            Object tmp = e;
            internalResult.setIndex(0);
            internalResult.setChannelInfo(clientChannel);
            internalResult.setData(e);
            for (DataHandler each : writeHandlers)
            {
                tmp = each.catchException(tmp, internalResult);
            }
            close();
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
    
    public void close()
    {
        clientChannel.closeChannel();
    }
    
}
