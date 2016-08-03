package com.jfireframework.socket.test;

import com.jfireframework.jnet.server.AioServer;
import com.jfireframework.jnet.server.util.ServerConfig;
import com.jfireframework.jnet.server.util.WorkMode;

public class MainServer
{
    public static void main(String[] args)
    {
        ServerConfig config = new ServerConfig();
        // 服务端监听的端口
        config.setPort(81);
        config.setWorkMode(WorkMode.ASYNC);
        config.setInitListener(new myInitListener());
        // 设置包解码器。包解码器用来从tcp的数据流中截取出一个完整的tcp报文
        // 这个解码器是行解码器。使用换行符进行报文切割
        // 当然，开发者也可以根据自己的业务需求，自行定制包解码器。框架本身提供了4种最为常见的包解码器。
        // 使用上面的配置新建一个服务端对象
        AioServer aioServer = new AioServer(config);
        // 启动服务端
        aioServer.start();
    }
}
