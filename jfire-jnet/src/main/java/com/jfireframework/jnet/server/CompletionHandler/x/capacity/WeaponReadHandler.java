package com.jfireframework.jnet.server.CompletionHandler.x.capacity;

import java.nio.channels.CompletionHandler;
import com.jfireframework.jnet.common.channel.impl.ServerChannel;

public interface WeaponReadHandler extends CompletionHandler<Integer, ServerChannel>
{
    
    /**
     * 通知读取处理器，有空余的空间可以存储读取的数据。让处理器执行读取动作。
     * 注意：该方法必须为异步方法
     */
    public void notifyRead();
    
    public void catchThrowable(Throwable e);
}
