package com.jfireframework.jnet2.server.CompletionHandler;

import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public interface WeaponWriteHandler extends CompletionHandler<Integer, ByteBuf<?>>
{
    
    /**
     * 将一个数据发送给写出处理器执行写出动作
     * 
     * @param buf
     */
    public void write(ByteBuf<?> buf);
    
    /**
     * 供用户主动推送一个消息给客户端
     * 
     * @param buf
     */
    public void push(ByteBuf<?> buf);
}
