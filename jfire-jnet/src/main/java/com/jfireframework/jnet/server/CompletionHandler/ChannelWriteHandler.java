package com.jfireframework.jnet.server.CompletionHandler;

import java.nio.channels.CompletionHandler;
import javax.annotation.Resource;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.result.ServerInternalResult;
import com.jfireframework.jnet.server.server.ServerChannelInfo;

@Resource
public class ChannelWriteHandler implements CompletionHandler<Integer, ServerInternalResult>
{
    private volatile long cursor = 0;
    
    public long cursor()
    {
        return cursor;
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
                // 不能在sendOne之前尝试获取。否则的话，获取为空，而sendOne之前又有了数据，那么sendOne之后没有发送数据，也没有给其他线程发送数据
                // channelInfo.sendOne();
                cursor++;
                ServerInternalResult next = channelInfo.getResult(cursor);
                // 上面的方法调用后，后续的result可能会由另外的线程直接就执行了
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
