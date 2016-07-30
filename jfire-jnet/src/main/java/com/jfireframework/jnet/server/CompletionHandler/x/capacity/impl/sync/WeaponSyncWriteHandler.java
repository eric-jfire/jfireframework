package com.jfireframework.jnet.server.CompletionHandler.x.capacity.impl.sync;

import com.jfireframework.jnet.server.CompletionHandler.x.capacity.WeaponWriteHandler;

public interface WeaponSyncWriteHandler extends WeaponWriteHandler
{
    public boolean availablePut();
}
