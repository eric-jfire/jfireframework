package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public interface AcceptHandler extends CompletionHandler<AsynchronousSocketChannel, Object>
{
    /**
     * 停止接入
     */
    public void stop();
}
