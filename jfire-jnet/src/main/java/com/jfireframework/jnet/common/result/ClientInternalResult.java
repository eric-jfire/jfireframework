package com.jfireframework.jnet.common.result;

import com.jfireframework.jnet.common.channel.ClientChannelInfo;

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

    
}
