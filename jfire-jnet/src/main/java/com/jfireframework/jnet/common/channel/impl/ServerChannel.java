package com.jfireframework.jnet.common.channel.impl;

import java.nio.channels.AsynchronousSocketChannel;

public class ServerChannel extends AbstractChannel
{

    public ServerChannel(AsynchronousSocketChannel socketChannel)
    {
        super(socketChannel);
    }
}
