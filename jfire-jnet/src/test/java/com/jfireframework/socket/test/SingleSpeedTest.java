package com.jfireframework.socket.test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBuf;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.jnet.client.AioClient;
import com.jfireframework.jnet.common.channel.ChannelInfo;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoderByHeap;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.handler.LengthPreHandler;
import com.jfireframework.jnet.common.result.InternalResult;
import com.jfireframework.jnet.server.server.AioServer;
import com.jfireframework.jnet.server.server.ServerConfig;
import com.jfireframework.jnet.server.server.WorkMode;

public class SingleSpeedTest
{
    private int    threadCount = 4;
    private int    sendCount   = 100000;
    private int    arraylength = 1024;
    private String ip          = "127.0.0.1";
    
    @Test
    public void test() throws Throwable
    {
        ServerConfig config = new ServerConfig();
        config.setWorkMode(WorkMode.SYNC);
        config.setSocketThreadSize(6);
        config.setAsyncThreadSize(4);
        config.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(ChannelInfo serverChannelInfo)
            {
                serverChannelInfo.setResultArrayLength(arraylength);
                serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 500));
                serverChannelInfo.setHandlers(new EchoHandler());
            }
        });
        config.setPort(8554);
        AioServer aioServer = new AioServer(config);
        aioServer.start();
        Thread[] threads = new Thread[threadCount];
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
            }, "测试线程" + i);
            threads[i].start();
        }
        Timewatch timewatch = new Timewatch();
        timewatch.start();
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].join();
        }
        timewatch.end();
        System.out.println("运行完毕:" + timewatch.getTotal());
    }
    
    public void connecttest() throws Throwable
    {
        AioClient client = new AioClient(false);
        client.setAddress(ip);
        client.setPort(8554);
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
                // ((Throwable) data).printStackTrace();
                return data;
            }
        }, new LengthPreHandler(0, 4));
        client.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(ChannelInfo channelInfo)
            {
                channelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoderByHeap(0, 4, 4, 500));
                channelInfo.setResultArrayLength(arraylength);
                channelInfo.setHandlers(new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult result) throws JnetException
                    {
                        // System.out.println("收到数据");
                        ByteBuf<?> buf = (ByteBuf<?>) data;
                        String value = null;
                        value = buf.readString();
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
            }
        });
        for (int i = 0; i < sendCount; i++)
        {
            try
            {
                client.connect().write("123456");
            }
            catch (Exception e)
            {
            }
        }
        Future<?> future = client.connect().write("987654321");
        try
        {
            Assert.assertEquals("987654321", (String) future.get(20, TimeUnit.SECONDS));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        client.close();
    }
}
