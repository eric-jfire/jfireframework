package com.jfireframework.socket.test;

import java.nio.charset.Charset;
import java.util.concurrent.Future;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.HeapByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.client.AioClient;
import com.jfireframework.jnet2.common.channel.ChannelInitListener;
import com.jfireframework.jnet2.common.channel.JnetChannel;
import com.jfireframework.jnet2.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.handler.LengthPreHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.server.AioServer;
import com.jfireframework.jnet2.server.util.ServerConfig;

public class NewServerTest
{
    private Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    
    @Test
    public void test() throws Throwable
    {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(81);
        serverConfig.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel serverChannelInfo)
            {
                serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 1000));
                serverChannelInfo.setHandlers(new Loghandler(), new EchoHandler(), new LengthPreHandler(0, 4));
            }
            
        });
        AioServer aioServer = new AioServer(serverConfig);
        aioServer.start();
        AioClient aioClient = new AioClient(false);
        aioClient.setAddress("127.0.0.1").setPort(81);
        aioClient.setWriteHandlers(new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult client)
            {
                String val = (String) data;
                ByteBuf<?> buf = HeapByteBufPool.getInstance().get(10);
                buf.addWriteIndex(4);
                buf.put(val.getBytes());
                return buf;
            }
            
            @Override
            public Object catchException(Object data, InternalResult result)
            {
                // TODO Auto-generated method stub
                return null;
            }
        }, new LengthPreHandler(0, 4));
        aioClient.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel jnetChannel)
            {
                jnetChannel.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 1000));
                jnetChannel.setHandlers(new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult client)
                    {
                        ByteBuf<?> val = (ByteBuf<?>) data;
                        System.out.println(val);
                        byte[] src = val.toArray();
                        val.release();
                        logger.debug("线程名称");
                        return new String(src, Charset.forName("utf8"));
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                });
            }
        });
        aioClient.connect();
        Future<?> future = aioClient.write("你好");
        logger.debug((String) future.get());
        aioServer.stop();
    }
}
