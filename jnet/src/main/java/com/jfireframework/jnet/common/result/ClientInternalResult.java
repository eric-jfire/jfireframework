package com.jfireframework.jnet.common.result;

import com.jfireframework.jnet.client.ClientChannelInfo;
import com.jfireframework.jnet.common.exception.SelfCloseException;

public class ClientInternalResult extends AbstractInternalResult
{
    private ClientChannelInfo channelInfo;
    
    public ClientInternalResult(Object data, ClientChannelInfo channelInfo, int index)
    {
        this.channelInfo = channelInfo;
        this.data = data;
        this.index = index;
    }
    
    public ClientChannelInfo getChannelInfo()
    {
        return channelInfo;
    }
    
    public void setChannelInfo(ClientChannelInfo channelInfo)
    {
        this.channelInfo = channelInfo;
    }
    
    @Override
    public void closeChannel(Throwable e)
    {
        channelInfo.close(e);
    }
    
    @Override
    public void closeChannel()
    {
        channelInfo.close(new SelfCloseException());
    }
    
}
