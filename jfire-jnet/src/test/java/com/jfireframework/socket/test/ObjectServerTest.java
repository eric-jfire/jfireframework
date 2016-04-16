package com.jfireframework.socket.test;

import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet.client.AioClient;
import com.jfireframework.jnet.common.channel.ChannelInfo;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet.common.exception.JnetException;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.handler.LengthPreHandler;
import com.jfireframework.jnet.common.result.InternalResult;
import com.jfireframework.jnet.server.server.AioServer;
import com.jfireframework.jnet.server.server.ServerConfig;

public class ObjectServerTest
{
    private Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    
    @Test
    public void test() throws Throwable
    {
        
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(81);
        serverConfig.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(ChannelInfo serverChannelInfo)
            {
                serverChannelInfo.setResultArrayLength(128);
                serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 1000));
                serverChannelInfo.setHandlers(new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult result) throws JnetException
                    {
                        ByteBuf<?> buf = (ByteBuf<?>) data;
                        Person person = new Person(buf.readString(), buf.readInt(), buf.readFloat());
                        logger.debug(person.toString());
                        buf.clear();
                        buf.addWriteIndex(4);
                        return buf;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                }, new LengthPreHandler(0, 4));
            }
        });
        AioServer aioServer = new AioServer(serverConfig);
        aioServer.start();
        AioClient aioClient = new AioClient();
        aioClient.setAsync(true);
        aioClient.setPort(81).setAddress("127.0.0.1");
        aioClient.setWriteHandlers(new DataHandler() {
            
            @Override
            public Object handle(Object data, InternalResult client)
            {
                if (data instanceof Person)
                {
                    Person person = (Person) data;
                    ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
                    buf.addWriteIndex(4);
                    buf.writeString(person.getName());
                    buf.writeInt(person.getAge());
                    buf.writeFloat(person.getWeight());
                    return buf;
                }
                else
                {
                    return null;
                }
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
            public void channelInit(ChannelInfo channelInfo)
            {
                channelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 1000));
                channelInfo.setHandlers(new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult result) throws JnetException
                    {
                        ByteBuf<?> buf = (ByteBuf<?>) data;
                        if (buf.remainRead() == 0)
                        {
                            System.out.println("没有信息");
                            return null;
                        }
                        System.out.println(new Person(buf.readString(), buf.readInt(), buf.readFloat()));
                        return null;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                });
                channelInfo.setResultArrayLength(128);
            }
        });
        aioClient.connect();
        aioClient.write(new Person("你好", 26, 56.56f), 0);
        Thread.sleep(2000);
        aioServer.stop();
    }
    
}
