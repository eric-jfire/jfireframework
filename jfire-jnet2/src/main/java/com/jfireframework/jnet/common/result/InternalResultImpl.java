package com.jfireframework.jnet.common.result;

import com.jfireframework.jnet.common.channel.JnetChannel;

public final class InternalResultImpl implements InternalResult
{
    protected Object      data;
    protected int         index;
    protected JnetChannel jnetChannel;
    
    public Object getData()
    {
        return data;
    }
    
    public void setData(Object data)
    {
        this.data = data;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public void setIndex(int index)
    {
        this.index = index;
    }
    
    public JnetChannel getChannelInfo()
    {
        return jnetChannel;
    }
    
    public void setChannelInfo(JnetChannel jnetChannel)
    {
        this.jnetChannel = jnetChannel;
    }
    
    @Override
    public void closeChannel()
    {
        jnetChannel.closeChannel();
    }
    
}
