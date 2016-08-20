package com.jfireframework.jnet2.server.CompletionHandler.weapon.single;

import java.nio.channels.CompletionHandler;
import com.jfireframework.jnet2.common.channel.impl.ServerChannel;

public interface SingleReadHandler extends CompletionHandler<Integer, ServerChannel>
{
    /**
     * 通知读取处理器，有空余的空间可以存储读取的数据。让处理器执行读取动作。
     * 注意：该方法必须为异步方法
     */
    public void notifyRead();
    
    /**
     * 读取处理器开始读取并且等待数据到达
     */
    public void readAndWait();
    
    public void catchThrowable(Throwable e);
}
