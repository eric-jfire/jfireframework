package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Resource;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.channel.ServerChannelInfo;
import com.jfireframework.jnet.common.result.ServerInternalResult;

@Resource
public class ChannelWriteHandler implements CompletionHandler<Integer, ServerInternalResult>
{
    private volatile AtomicLong cursor = new AtomicLong(0);
    
    public long cursor()
    {
        return cursor.get();
    }
    
    @Override
    public void completed(Integer writeTotal, ServerInternalResult result)
    {
        try
        {
            ByteBuf<?> buf = (ByteBuf<?>) result.getData();
            buf.addReadIndex(writeTotal);
            if (buf.remainRead() > 0)
            {
                result.getChannelInfo().write(result);
                return;
            }
            else
            {
                buf.release();
                ServerChannelInfo channelInfo = result.getChannelInfo();
                channelInfo.reStartRead();
                ServerInternalResult next = channelInfo.getResult(cursor.incrementAndGet());
                if (next != null)
                {
                    next.getChannelInfo().write(next);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(Thread.currentThread().getName());
            e.printStackTrace();
            result.getChannelInfo().close(e);
        }
    }
    
    @Override
    public void failed(Throwable exc, ServerInternalResult result)
    {
        ((ByteBuf<?>) result.getData()).release();
        result.getChannelInfo().close(exc);
    }
    
}
