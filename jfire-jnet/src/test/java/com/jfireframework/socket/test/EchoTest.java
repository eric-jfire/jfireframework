package com.jfireframework.socket.test;

import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.jnet.client.FutureClient;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoderByHeap;
import com.jfireframework.jnet.common.exception.EndOfStreamException;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.handler.LengthPreHandler;
import com.jfireframework.jnet.common.result.InternalResult;
import com.jfireframework.jnet.server.server.AioServer;
import com.jfireframework.jnet.server.server.ChannelInitListener;
import com.jfireframework.jnet.server.server.ServerChannelInfo;
import com.jfireframework.jnet.server.server.ServerConfig;

public class EchoTest

{
    @Test
    public void test() throws Throwable
    {
        ServerConfig config = new ServerConfig();
        config.setRingArraySize(2);
        config.setSocketThreadSize(8);
        config.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(ServerChannelInfo serverChannelInfo)
            {
                serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoderByHeap(0, 4, 4, 500));
                serverChannelInfo.setHandlers(new EchoHandler(), new EchoHandler2(), new LengthPreHandler(0, 4));
            }
        });
        config.setPort(8554);
        AioServer aioServer = new AioServer(config);
        aioServer.start();
        Thread[] threads = new Thread[8];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(new Runnable() {
                
                @Override
                public void run()
                {
                    try
                    {
                        connecttest();
                    }
                    catch (Throwable e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].join();
        }
        System.out.println("运行完毕");
        // Thread.sleep(1000);
        // client.close(new EndOfStreamException());
    }
    
    public void connecttest() throws Throwable
    {
        FutureClient client = new FutureClient();
        client.setAddress("127.0.0.1");
        client.setPort(8554).setFrameDecodec(new TotalLengthFieldBasedFrameDecoderByHeap(0, 4, 4, 500));
        client.setWriteHandlers(new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult result) throws JnetException
            {
                ByteBuf<?> buf = DirectByteBuf.allocate(100);
                buf.addWriteIndex(4);
                buf.writeString((String) data);
                return buf;
            }
            
            @Override
            public Object catchException(Object data, InternalResult result)
            {
                ((Throwable) data).printStackTrace();
                return data;
            }
        }, new LengthPreHandler(0, 4));
        client.setReadHandlers(new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult result) throws JnetException
            {
                // System.out.println("收到数据");
                ByteBuf<?> buf = (ByteBuf<?>) data;
                String value = null;
                buf.maskRead();
                // System.out.println(buf.readIndex(0).hexString());
                buf.resetRead();
                try
                {
                    value = buf.readString();
                }
                catch (Exception e)
                {
                    buf.readIndex(0);
                    System.out.println(buf.hexString());
                    // System.out.println(buf.toString());
                    // e.printStackTrace();
                }
                buf.release();
                return value;
            }
            
            @Override
            public Object catchException(Object data, InternalResult result)
            {
                // System.err.println("客户端");
                // ((Throwable) data).printStackTrace();
                return data;
            }
        });
        for (int i = 0; i < 100000; i++)
        {
            try
            {
                client.connect().write("123456");
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // System.out.println(i);
        }
        Future<?> future = client.connect().write("987654321");
        Assert.assertEquals("987654321", (String) future.get());
        // System.out.println("完成");
        // TimeUnit.SECONDS.sleep(1000);
        client.close(new EndOfStreamException());
    }
}
