package com.jfireframework.jnet.common.channel.impl;

import com.jfireframework.jnet.common.result.ServerInternalTask;

public class ServerChannel extends AbstractChannel
{
    @Override
    public void setDataArrayLength(int resultArrayLength)
    {
        super.setDataArrayLength(resultArrayLength);
        for (int i = 0; i < resultArrayLength; i++)
        {
            resultArray[i] = new ServerInternalTask();
        }
    }
}
