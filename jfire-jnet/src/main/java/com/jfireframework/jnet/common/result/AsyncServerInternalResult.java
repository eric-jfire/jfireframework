package com.jfireframework.jnet.common.result;

import com.jfireframework.jnet.server.CompletionHandler.async.AsyncReadCompletionHandler;
import com.jfireframework.jnet.server.CompletionHandler.async.AsyncWriteCompletionHandler;

public class AsyncServerInternalResult extends AbstractInternalResult
{
    private AsyncReadCompletionHandler  readCompletionHandler;
    private AsyncWriteCompletionHandler writeCompletionHandler;
    
    public AsyncReadCompletionHandler getReadCompletionHandler()
    {
        return readCompletionHandler;
    }
    
    public void setReadCompletionHandler(AsyncReadCompletionHandler readCompletionHandler)
    {
        this.readCompletionHandler = readCompletionHandler;
    }
    
    public AsyncWriteCompletionHandler getWriteCompletionHandler()
    {
        return writeCompletionHandler;
    }
    
    public void setWriteCompletionHandler(AsyncWriteCompletionHandler writeCompletionHandler)
    {
        this.writeCompletionHandler = writeCompletionHandler;
    }
    
}
