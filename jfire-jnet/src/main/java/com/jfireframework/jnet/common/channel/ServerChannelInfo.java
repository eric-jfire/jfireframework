package com.jfireframework.jnet.common.channel;

import com.jfireframework.jnet.common.result.ServerInternalResult;

public class ServerChannelInfo extends AbstractChannelInfo
{
    @Override
    public void setResultArrayLength(int resultArrayLength)
    {
        super.setResultArrayLength(resultArrayLength);
        for (int i = 0; i < resultArrayLength; i++)
        {
            resultArray[i] = new ServerInternalResult(0, null, null, null, null, i);
        }
    }
}
