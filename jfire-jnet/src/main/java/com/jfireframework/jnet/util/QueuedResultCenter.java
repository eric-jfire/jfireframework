package com.jfireframework.jnet.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ServerInternalResult;

public class QueuedResultCenter
{
    private final LinkedTransferQueue<ServerInternalResult> results = new LinkedTransferQueue<>();
    private volatile boolean                                stoped  = false;
    
    public QueuedResultCenter(int threadSize)
    {
        ExecutorService pool = Executors.newFixedThreadPool(threadSize);
        for (int i = 0; i < threadSize; i++)
        {
            pool.submit(new ResultHandler());
        }
    }
    
    public LinkedTransferQueue<ServerInternalResult> getResults()
    {
        return results;
    }
    
    public void stop()
    {
        stoped = true;
    }
    
    class ResultHandler implements Runnable
    {
        
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    if (stoped)
                    {
                        break;
                    }
                    ServerInternalResult result = results.take();
                    try
                    {
                        if (result.getChannelInfo().isOpen() == false)
                        {
                            return;
                        }
                        // 储存中间结果
                        Object intermediateResult = result.getData();
                        DataHandler[] handlers = result.getChannelInfo().getHandlers();
                        for (int i = result.getIndex(); i < handlers.length;)
                        {
                            intermediateResult = handlers[i].handle(intermediateResult, result);
                            if (i == result.getIndex())
                            {
                                i++;
                                result.setIndex(i);
                            }
                            else
                            {
                                i = result.getIndex();
                            }
                        }
                        if (intermediateResult instanceof ByteBuf<?>)
                        {
                            result.setData(intermediateResult);
                            long version = result.version();
                            result.flowDone();
                            if (result.getChannelInfo().isOpen())
                            {
                                result.write(version);
                            }
                        }
                        else
                        {
                            
                        }
                    }
                    catch (Exception e)
                    {
                        result.getReadCompletionHandler().catchThrowable(e);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
        
    }
}
