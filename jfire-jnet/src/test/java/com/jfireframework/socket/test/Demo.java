package com.jfireframework.socket.test;

import com.jfireframework.jnet.common.channel.ChannelInitListener;
import com.jfireframework.jnet.common.channel.JnetChannel;
import com.jfireframework.jnet.common.decodec.TotalLengthFieldBasedFrameDecoder;
import com.jfireframework.jnet.server.AioServer;
import com.jfireframework.jnet.server.util.AcceptMode;
import com.jfireframework.jnet.server.util.ExecutorMode;
import com.jfireframework.jnet.server.util.ServerConfig;
import com.jfireframework.jnet.server.util.WorkMode;

public class Demo
{
    public static void main(String[] args)
    {
        ServerConfig config = new ServerConfig();
        config.setAcceptMode(AcceptMode.weapon_single);
        config.setSocketThreadSize(100);
        config.setAsyncCapacity(1024);
        config.setChannelCapacity(16);
        config.setWorkMode(WorkMode.SYNC_WITH_ORDER);
        config.setExecutorMode(ExecutorMode.FIX);
        config.setAsyncThreadSize(100);
        config.setInitListener(new ChannelInitListener() {
            
            @Override
            public void channelInit(JnetChannel serverChannelInfo)
            {
                serverChannelInfo.setFrameDecodec(new TotalLengthFieldBasedFrameDecoder(0, 4, 4, 500));
                serverChannelInfo.setHandlers(new EchoHandler());
            }
        });
        config.setPort(5566);
        AioServer aioServer = new AioServer(config);
        aioServer.start();
        aioServer.waitForShutdown();
    }
}
