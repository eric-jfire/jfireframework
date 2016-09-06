package com.jfireframework.socket.test;

import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.client.AioClient;
import com.jfireframework.jnet2.common.channel.ChannelInitListener;
import com.jfireframework.jnet2.common.channel.JnetChannel;
import com.jfireframework.jnet2.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet2.common.exception.JnetException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.handler.LengthPreHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.server.AioServer;
import com.jfireframework.jnet2.server.util.ServerConfig;

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
            public void channelInit(JnetChannel serverChannelInfo)
            {
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
        AioClient aioClient = new AioClient(true);
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
            public void channelInit(JnetChannel jnetChannel)
            {
                jnetChannel.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 1000));
                jnetChannel.setHandlers(new DataHandler() {
                    
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
            }
        });
        aioClient.connect();
        aioClient.write(new Person("你好", 26, 56.56f), 0);
        Thread.sleep(2000);
        aioServer.stop();
    }
    
}
