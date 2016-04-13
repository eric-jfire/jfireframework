package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.CompletionHandler;
import com.jfireframework.jnet.common.exception.EndOfStreamException;
import com.jfireframework.jnet.server.server.ServerChannelInfo;

public class ChannelReadHandler implements CompletionHandler<Integer, ServerChannelInfo>
{
    @Override
    public void completed(Integer read, ServerChannelInfo channelInfo)
    {
        if (read == -1)
        {
            channelInfo.close(new EndOfStreamException());
            return;
        }
        channelInfo.ioBuf().addWriteIndex(read);
        channelInfo.doRead();
    }
    
    @Override
    public void failed(Throwable exc, ServerChannelInfo channelInfo)
    {
        channelInfo.close(exc);
    }
    
}
