package com.jfireframework.baseutil.resource;

import java.util.concurrent.atomic.AtomicInteger;

public class ResourceCloseAgent<T>
{
    private static final int               OPEN  = 0;
    private static final int               CLOSE = 1;
    private final AtomicInteger            state = new AtomicInteger(OPEN);
    private final T                        resource;
    private final ResourceCloseCallback<T> callback;
    
    public ResourceCloseAgent(T resource, ResourceCloseCallback<T> callback)
    {
        this.resource = resource;
        this.callback = callback;
    }
    
    public boolean isOpen()
    {
        return state.intValue() == OPEN;
    }
    
    /**
     * 执行资源的关闭动作。如果资源尚未关闭才能尝试关闭。如果成功抢夺到关闭的权利，执行关闭并且返回true。其余情况返回false
     * 
     * @return
     */
    public boolean close()
    {
        if (state.intValue() == OPEN && state.compareAndSet(OPEN, CLOSE))
        {
            if (callback != null)
            {
                callback.onClose(resource);
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
