package com.jfireframework.jnet.server.CompletionHandler.weapon;

import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public interface WeaponWriteHandler extends CompletionHandler<Integer, ByteBuf<?>>
{
    
    /**
     * 将一个数据发送给写出处理器的对应位置执行写出动作。该方法实现于写出处理器存在容量的实现。
     * 
     * @param buf
     * @param index
     */
    public void write(ByteBuf<?> buf, long index);
    
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
