package com.jfireframework.jnet.common.util;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import com.jfireframework.baseutil.resource.ResourceCloseCallback;

public class ChannelCloseCallback implements ResourceCloseCallback<AsynchronousSocketChannel>
{
    public static final ChannelCloseCallback instance = new ChannelCloseCallback();
    
    private ChannelCloseCallback()
    {
    }
    
    @Override
    public void onClose(AsynchronousSocketChannel socketChannel)
    {
        try
        {
            socketChannel.shutdownOutput();
            socketChannel.shutdownInput();
            socketChannel.close();
        }
        catch (IOException e)
        {
            ;
        }
    }
    
}
