package com.jfireframework.jnet.server.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ServerInternalTask;

public class AsyncTaskCenter
{
    private final LinkedTransferQueue<ServerInternalTask> tasks  = new LinkedTransferQueue<>();
    private volatile boolean                              stoped = false;
    
    public AsyncTaskCenter(int threadSize)
    {
        ExecutorService pool = Executors.newFixedThreadPool(threadSize);
        for (int i = 0; i < threadSize; i++)
        {
            pool.submit(new ResultHandler());
        }
    }
    
    public void addTask(ServerInternalTask task)
    {
        tasks.add(task);
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
                    ServerInternalTask task = tasks.take();
                    try
                    {
                        if (task.getChannelInfo().isOpen() == false)
                        {
                            return;
                        }
                        // 储存中间结果
                        Object intermediateResult = task.getData();
                        DataHandler[] handlers = task.getChannelInfo().getHandlers();
                        for (int i = task.getIndex(); i < handlers.length;)
                        {
                            intermediateResult = handlers[i].handle(intermediateResult, task);
                            if (i == task.getIndex())
                            {
                                i++;
                                task.setIndex(i);
                            }
                            else
                            {
                                i = task.getIndex();
                            }
                        }
                        if (intermediateResult instanceof ByteBuf<?>)
                        {
                            task.setData(intermediateResult);
                            long version = task.version();
                            task.flowDone();
                            if (task.getChannelInfo().isOpen())
                            {
                                task.write(version);
                            }
                        }
                        else
                        {
                            
                        }
                    }
                    catch (Exception e)
                    {
                        task.getReadCompletionHandler().catchThrowable(e);
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
