package com.jfireframework.jnet.common.channel.impl;

import com.jfireframework.jnet.common.result.ServerInternalTask;

public class ServerChannel extends AbstractChannel
{
    @Override
    public void setCapacity(int capacity)
    {
        super.setCapacity(capacity);
        for (int i = 0; i < capacity; i++)
        {
            resultArray[i] = new ServerInternalTask();
        }
    }
}
