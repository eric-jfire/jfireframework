package com.jfireframework.jnet.server.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.common.handler.DataHandler;
import com.jfireframework.jnet.common.result.ServerInternalTask;
import com.jfireframework.jnet.server.CompletionHandler.UnOrderedWriteCompletionHandler;

public class AsyncTaskCenter
{
    private final LinkedTransferQueue<ServerInternalTask> tasks  = new LinkedTransferQueue<>();
    private final LinkedTransferQueue<ServerInternalTask> res    = new LinkedTransferQueue<>();
    private volatile boolean                              stoped = false;
    
    public AsyncTaskCenter(int threadSize, WorkMode workMode)
    {
        ExecutorService pool = Executors.newFixedThreadPool(threadSize);
        for (int i = 0; i < threadSize; i++)
        {
            pool.submit(new TaskHandler(workMode));
        }
    }
    
    public ServerInternalTask askFor()
    {
        ServerInternalTask task = res.poll();
        if (task == null)
        {
            return new ServerInternalTask();
        }
        return task;
    }
    
    public void addTask(ServerInternalTask task)
    {
        tasks.add(task);
    }
    
    public void stop()
    {
        stoped = true;
    }
    
    class TaskHandler implements Runnable
    {
        private final WorkMode workMode;
        
        public TaskHandler(WorkMode workMode)
        {
            this.workMode = workMode;
        }
        
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
                            if (workMode == WorkMode.ASYNC_WITHOUT_ORDER)
                            {
                                UnOrderedWriteCompletionHandler writeCompletionHandler = (UnOrderedWriteCompletionHandler) task.getWriteCompletionHandler();
                                writeCompletionHandler.askToWrite((ByteBuf<?>) intermediateResult);
                                res.offer(task);
                            }
                            else
                            {
                                task.setData(intermediateResult);
                                long version = task.version();
                                task.flowDone();
                                if (task.getChannelInfo().isOpen())
                                {
                                    task.write(version);
                                }
                            }
                        }
                        else
                        {
                            
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
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
