package com.jfireframework.jnet2.server.CompletionHandler.weapon.capacity.sync;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet2.server.CompletionHandler.WeaponWriteHandler;

public interface WeaponCapacityWriteHandler extends WeaponWriteHandler
{
    
    /**
     * 将一个数据发送给写出处理器的对应位置执行写出动作。该方法实现于写出处理器存在容量的实现。
     * 
     * @param buf
     * @param index
     */
    public void write(ByteBuf<?> buf, long index);
    
    /**
     * 当前可以放入的数据位置。从0开始。如果返回-1，意味着当前没有位置可以放入数据
     * 
     * @return
     */
    public long availablePut();
    
    /**
     * 下一个写出的序号
     * 
     * @return
     */
    public long cursor();
}
