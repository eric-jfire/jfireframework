package com.jfireframework.jnet.common.result;

import com.jfireframework.jnet.common.channel.impl.AbstractClientChannel;
import com.jfireframework.jnet.common.channel.impl.FutureClientChannelInfo;

public class ClientInternalResult extends AbstractInternalTask
{
    private AbstractClientChannel channelInfo;
    
    public void init(Object data, AbstractClientChannel channelInfo, int index)
    {
        this.channelInfo = channelInfo;
        this.data = data;
        this.index = index;
    }
    
    public AbstractClientChannel getChannelInfo()
    {
        return channelInfo;
    }
    
    public void setChannelInfo(FutureClientChannelInfo channelInfo)
    {
        this.channelInfo = channelInfo;
    }
    
}
