package com.jfireframework.baseutil.concurrent.time;

public interface TimeoutHandler
{
    /**
     * 当时间任务过期时触发的操作。默认实现可以直接调用timeout的invoke接口。那么就意味着在时间线程中执行了超时操作。
     * 也可以将这些任务投递到其他的线程中进行处理
     * 
     * @param tasks
     */
    public void handle(Timeout timeout);
}
