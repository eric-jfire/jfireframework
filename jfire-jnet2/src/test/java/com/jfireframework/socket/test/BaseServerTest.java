package com.jfireframework.socket.test;

import java.util.concurrent.Future;
import org.junit.Test;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.collection.buffer.DirectByteBufPool;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.jnet2.client.AioClient;
import com.jfireframework.jnet2.common.channel.ChannelInitListener;
import com.jfireframework.jnet2.common.channel.JnetChannel;
import com.jfireframework.jnet2.common.decodec.LineBasedFrameDecodec;
import com.jfireframework.jnet2.common.exception.JnetException;
import com.jfireframework.jnet2.common.handler.DataHandler;
import com.jfireframework.jnet2.common.result.InternalResult;
import com.jfireframework.jnet2.server.AioServer;
import com.jfireframework.jnet2.server.util.AcceptMode;
import com.jfireframework.jnet2.server.util.PushMode;
import com.jfireframework.jnet2.server.util.ServerConfig;
import com.jfireframework.jnet2.server.util.WorkMode;

public class BaseServerTest
{
    private Logger logger = ConsoleLogFactory.getLogger(ConsoleLogFactory.DEBUG);
    
    @Test
    public void test() throws Throwable
    {
        ServerConfig config = new ServerConfig();
        // 服务端监听的端口
        config.setPort(81);
        config.setSocketThreadSize(4);
        config.setChannelCapacity(64);
        config.setAcceptMode(AcceptMode.weapon_single);
        config.setWorkMode(WorkMode.SYNC);
        config.setPushMode(PushMode.OFF);
        config.setInitListener(new myInitListener());
        // 设置包解码器。包解码器用来从tcp的数据流中截取出一个完整的tcp报文
        // 这个解码器是行解码器。使用换行符进行报文切割
        // 当然，开发者也可以根据自己的业务需求，自行定制包解码器。框架本身提供了4种最为常见的包解码器。
        // 使用上面的配置新建一个服务端对象
        AioServer aioServer = new AioServer(config);
        // 启动服务端
        aioServer.start();
        AioClient aioClient = new AioClient(false);
        aioClient.setAddress("127.0.0.1").setPort(81);
        aioClient.setWriteHandlers(
                new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult result) throws JnetException
                    {
                        String value = (String) data;
                        ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
                        buf.writeString(value);
                        buf.put((byte) '\r');
                        buf.put((byte) '\n');
                        return buf;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                }
        );
        aioClient.setInitListener(
                new ChannelInitListener() {
                    
                    @Override
                    public void channelInit(JnetChannel jnetChannel)
                    {
                        jnetChannel.setFrameDecodec(new LineBasedFrameDecodec(1000));
                        jnetChannel.setHandlers(
                                new DataHandler() {
                                    
                                    @Override
                                    public Object handle(Object data, InternalResult result) throws JnetException
                                    {
                                        ByteBuf<?> buf = (ByteBuf<?>) data;
                                        String value = buf.readString();
//                                        System.out.println("收到数据:" + value);
                                        buf.release();
                                        return value;
                                    }
                                    
                                    @Override
                                    public Object catchException(Object data, InternalResult result)
                                    {
                                        Throwable e = (Throwable) data;
                                        System.err.println("感知到关闭");
                                        e.printStackTrace();
                                        return null;
                                    }
                                }
                        );
                    }
                }
        );
        // 使用对应的参数链接服务端
        aioClient.connect();
        for (int i = 0; i < 1; i++)
        {
            Future<?> future = aioClient.write("你好，这里是客户端");
            // future.get();
        }
        Future<?> future = aioClient.write("你好，这里是客户端~~~~~");
        System.out.println("+++" + future.get());
        // 服务端关闭
        aioServer.stop();
    }
}

class myInitListener implements ChannelInitListener
{
    
    // 当通道被建立的时候触发
    @Override
    public void channelInit(JnetChannel serverChannelInfo)
    {
        serverChannelInfo.setFrameDecodec(new LineBasedFrameDecodec(1000));
        // 可以设置通道的读取超时时长。默认为3000毫秒
        serverChannelInfo.setReadTimeout(3000);
        // 可以设置通道无数据的读取等待时长。默认为30分钟
        serverChannelInfo.setWaitTimeout(1000 * 60 * 30);
        // 设置数据流的处理器，这步是最重要的，也是业务逻辑的所在。每一个通道的处理器都是通过这种方式新建而来。所以其中的数据都是针对该通道的
        serverChannelInfo.setHandlers(
                new DataHandler() {
                    
                    // 这样的数据都是只针对该通道的。因此这种结构很方便用来做登录拦截之类的
                    private String loginName;
                    
                    // data是上一个处理器传递过来的数据，返回值是要给下一个处理器的数据。如果是最开头的处理器，则data就是包解码器解码出来的一个完整报文
                    @Override
                    public Object handle(Object data, InternalResult result) throws JnetException
                    {
                        ByteBuf<?> buf = (ByteBuf<?>) data;
//                         System.out.println("收到消息:" + buf.readString());
                        buf.release();
                        return "客户端你好，我收到消息了";
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        Throwable e = (Throwable) data;
                        e.printStackTrace();
                        return null;
                    }
                }, new DataHandler() {
                    
                    @Override
                    public Object handle(Object data, InternalResult result) throws JnetException
                    {
                        // 这里的data就是上一个处理器返回的数据了。处理器之间的顺序就是在代码中他们初始化的顺序
                        String value = (String) data;
                        // 从内存缓存池中获取一个内存缓存区用来写出数据
                        ByteBuf<?> buf = DirectByteBufPool.getInstance().get(100);
                        buf.writeString(value);
                        buf.put((byte) '\r');
                        buf.put((byte) '\n');
                        // 末尾的处理器一定要返回ByteBuf类型的数据。这样框架就会自动将这个数据通过socket发送出去
                        return buf;
                    }
                    
                    @Override
                    public Object catchException(Object data, InternalResult result)
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                }
        );
    }
    
}
