package com.jfireframework.jnet.common.result;

import com.jfireframework.jnet.common.channel.AbstractClientChannelInfo;
import com.jfireframework.jnet.common.channel.FutureClientChannelInfo;

public class ClientInternalResult extends AbstractInternalTask
{
    private AbstractClientChannelInfo channelInfo;
    
    public void init(Object data, AbstractClientChannelInfo channelInfo, int index)
    {
        this.channelInfo = channelInfo;
        this.data = data;
        this.index = index;
    }
    
    public AbstractClientChannelInfo getChannelInfo()
    {
        return channelInfo;
    }
    
    public void setChannelInfo(FutureClientChannelInfo channelInfo)
    {
        this.channelInfo = channelInfo;
    }
    
}
