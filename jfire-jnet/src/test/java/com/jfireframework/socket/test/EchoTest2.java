package com.jfireframework.socket.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import org.junit.Test;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.jnet.common.channel.ChannelInfo;
import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet.server.server.AioServer;
import com.jfireframework.jnet.server.server.ServerConfig;
import com.jfireframework.jnet.server.server.WorkMode;

public class EchoTest2
{
    private int threadCount = 16;
    private int sendCount   = 100000;
    private int arraylength = 1024 * 4;
    
    @Test
    public void test() throws Throwable
    {
        ServerConfig config = new ServerConfig();
        config.setWorkMode(WorkMode.SYNC);
        config.setRingArraySize(1024 * 4);
        config.setSocketThreadSize(8);
        config.setHandlerThreadSize(2);
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
        // Thread.sleep(1000);
        // client.close(new EndOfStreamException());
    }
    
    public void connecttest() throws Throwable
    {
        final Socket socket = new Socket("127.0.0.1", 8554);
        byte[] src = new byte[10];
        src[0] = 0x00;
        src[1] = 0x00;
        src[2] = 0x00;
        src[3] = 0x0a;
        System.arraycopy("123456".getBytes(Charset.forName("utf8")), 0, src, 4, 6);
        OutputStream os = socket.getOutputStream();
        new Thread(new Runnable() {
            public void run()
            {
                byte[] tmp = new byte[500];
                InputStream inputStream = null;
                try
                {
                    inputStream = socket.getInputStream();
                }
                catch (IOException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                while (true)
                {
                    try
                    {
                        if (inputStream.read(tmp) == -1)
                        {
                            break;
                        }
                    }
                    catch (IOException e)
                    {
                    }
                }
            }
        }).start();
        for (int i = 0; i < sendCount; i++)
        {
            try
            {
                os.write(src);
            }
            catch (Exception e)
            {
                System.out.println(Thread.currentThread().getName() + "," + i);
                e.printStackTrace();
                break;
            }
            // System.out.println(i);
        }
        socket.close();
    }
}
