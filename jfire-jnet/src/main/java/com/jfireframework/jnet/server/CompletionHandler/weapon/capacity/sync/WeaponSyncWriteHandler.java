package com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.sync;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.jnet.server.CompletionHandler.weapon.capacity.WeaponWriteHandler;

public interface WeaponSyncWriteHandler extends WeaponWriteHandler
{
    public void write(ByteBuf<?> buf, long index);
    
    public long availablePut();
}
