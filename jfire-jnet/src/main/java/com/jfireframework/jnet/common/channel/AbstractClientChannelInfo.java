package com.jfireframework.jnet.common.channel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.jfireframework.jnet.client.ClientReadCompleter;

public abstract class AbstractClientChannelInfo extends AbstractChannelInfo
{
    protected volatile long       writeCursor = 0;
    protected long                wrapPoint   = 0;
    protected ClientReadCompleter readCompleter;
    // private static final Logger logger = ConsoleLogFactory.getLogger();
    public static Future<Void>    NORESULT    = new Future<Void>() {
                                                  
                                                  @Override
                                                  public boolean cancel(boolean mayInterruptIfRunning)
                                                  {
                                                      return false;
                                                  }
                                                  
                                                  @Override
                                                  public boolean isCancelled()
                                                  {
                                                      return false;
                                                  }
                                                  
                                                  @Override
                                                  public boolean isDone()
                                                  {
                                                      return true;
                                                  }
                                                  
                                                  @Override
                                                  public Void get() throws InterruptedException, ExecutionException
                                                  {
                                                      return null;
                                                  }
                                                  
                                                  @Override
                                                  public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
                                                  {
                                                      return null;
                                                  }
                                              };
    
    public void setReadCompleter(ClientReadCompleter readCompleter)
    {
        this.readCompleter = readCompleter;
    }
    
    public boolean isOpen()
    {
        return openState == OPEN;
    }
    
    public abstract void signal(Object obj, long cursor);
    
    public abstract void signalAll(Throwable e, long cursor);
    
    public abstract Future<?> addFuture();
    
}
