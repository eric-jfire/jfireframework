package com.jfireframework.jnet.server.CompletionHandler.x.capacity;

import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public interface WeaponWriteHandler extends CompletionHandler<Integer, ByteBuf<?>>
{
    
    /**
     * 读取处理器尝试发送一个数据供写出处理器进行处理
     * 
     * @param buf
     * @return
     */
    public boolean trySend(ByteBuf<?> buf);
    
    /**
     * 供用户主动推送一个消息给客户端
     * 
     * @param buf
     */
    public void push(ByteBuf<?> buf);
}
