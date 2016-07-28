package com.jfireframework.jnet.server.CompletionHandler.x.capacity;

import java.nio.channels.CompletionHandler;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;

public interface WeaponWriteHandler extends CompletionHandler<Integer, ByteBuf<?>>
{
    
    /**
     * 尝试发送一个数据用以写出。返回是否成功
     * 
     * @param buf
     * @return
     */
    public boolean trySend(ByteBuf<?> buf);
    
}
